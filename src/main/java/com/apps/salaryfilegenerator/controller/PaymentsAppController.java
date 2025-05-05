package com.apps.salaryfilegenerator.controller;

import com.apps.salaryfilegenerator.dto.PasteEventDataObject;
import com.apps.salaryfilegenerator.entity.*;
import com.apps.salaryfilegenerator.service.*;
import com.apps.salaryfilegenerator.exception.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class PaymentsAppController {

    @Value("${default.bank.code}")
    private String defaultBankCode;

    @Autowired
    private EmployerService employerService;

    @Autowired
    private SimplifiedEmployeeService simplifiedEmployeeService;

    @Autowired
    private WpsEmployeeService wpsEmployeeService;

    @Autowired
    private MinistriesEmployeeService ministriesEmployeeService;

    @Autowired
    private WpsPdfGenerationService wpsPdfGenerationService;

    @Autowired
    private SimplifiedPdfGenerationService simplifiedPdfGenerationService;

    @Autowired
    private MinistriesPdfGenerationService ministriesPdfGenerationService;

    @Autowired
    private WpsExcelGenerationService wpsExcelGenerationService;

    @Autowired
    private MinistriesExcelGenerationService ministriesExcelGenerationService;

    @Autowired
    private PasteEventDataService pasteEventDataService;

    @Autowired
    private FormService formService;

    //forms
    @GetMapping("/forms")
    public List<Form> getForms() {
        return formService.getAll();
    }

    // employer
    @GetMapping("/employers/{formType}")
    public Employer getEmployerRecord(@PathVariable("formType") String formType) {
        if(formType.equalsIgnoreCase("wps")){
            return employerService.getWpsEmployer();
        } else if(formType.equalsIgnoreCase("simplified")){
            return employerService.getSimplifiedEmployer();
        }else if(formType.equalsIgnoreCase("ministries")){
            return employerService.getMinistriesEmployer();
        }else {
            return null;
        }
    }

    @PutMapping("/employers")
    public Employer saveEmployer(@RequestBody Employer employer) {
        employerService.add(employer);
        //updates the same employer object
        return employer;
    }

    // employees
    @GetMapping("/employees/{formType}")
    public List<?> getEmployees(@PathVariable("formType") String formType) {
        if(formType.equalsIgnoreCase("wps")){
            return wpsEmployeeService.getAll();
        } else if(formType.equalsIgnoreCase("simplified")){
            return simplifiedEmployeeService.getAll();
        }else if(formType.equalsIgnoreCase("ministries")){
            return ministriesEmployeeService.getAll();
        }else {
            return null;
        }
    }

    // add extra records
    @PostMapping("/employees/{formType}/addRecords/{count}")
    public ResponseEntity<?> addExtraRecords(@PathVariable("formType") String formType, @PathVariable("count") int count) {

        if (formType.equalsIgnoreCase("wps")) {
            long numberOfRecords = wpsEmployeeService.getCount();
            if(numberOfRecords%count == 0){
                for(int i =0; i<count; i++){
                    wpsEmployeeService.add(new WpsEmployee(null, null, defaultBankCode, "C", null, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
                }
            } else {
                for(int i =0; i<(count-(numberOfRecords%count)); i++){
                    wpsEmployeeService.add(new WpsEmployee(null, null, defaultBankCode, "C", null, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
                }
            }
        } else if(formType.equalsIgnoreCase("simplified")){
            long numberOfRecords = simplifiedEmployeeService.getCount();
            if(numberOfRecords%50 == 0){
                for(int i =0; i<50; i++){
                    simplifiedEmployeeService.add(new SimplifiedEmployee(null, null, defaultBankCode, BigDecimal.ZERO, ""));
                }
            } else {
                for(int i =0; i<(50-(numberOfRecords%50)); i++){
                    simplifiedEmployeeService.add(new SimplifiedEmployee(null, null, defaultBankCode, BigDecimal.ZERO, ""));
                }
            }
        }else if(formType.equalsIgnoreCase("ministries")){
            long numberOfRecords = ministriesEmployeeService.getCount();
            if(numberOfRecords%50 == 0){
                for(int i =0; i<50; i++){
                    ministriesEmployeeService.add(new MinistriesEmployee(null, null, defaultBankCode, null, BigDecimal.ZERO, BigDecimal.ZERO, ""));
                }
            } else {
                for(int i =0; i<(50-(numberOfRecords%50)); i++){
                    ministriesEmployeeService.add(new MinistriesEmployee(null, null, defaultBankCode, null, BigDecimal.ZERO, BigDecimal.ZERO, ""));
                }
            }
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Form type was not found!");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Extra records added successfully!");
    }

    // delete incomplete records
    @DeleteMapping("/employees/{formType}/incomplete")
    public ResponseEntity<?> deleteIncompleteRecords(@PathVariable("formType") String formType) {

        if (formType.equalsIgnoreCase("wps")) {
            List<WpsEmployee> employees = wpsEmployeeService.findIncompleteRecords();
            if (employees.isEmpty()) {
                return new ResponseEntity<>(new CustomResponse("204", "No records were found."),HttpStatus.OK);
            } else {
                try {
                    for (WpsEmployee employee : employees) {
                        wpsEmployeeService.deleteById(employee.getId());
                    }
                } catch (Exception exc) {
                    return ResponseEntity.status(500).body("error while deleting incomplete records");
                }
                return new ResponseEntity<>(new CustomResponse("200", "Incomplete records deleted successfully."),HttpStatus.OK);
            }
        } else if (formType.equalsIgnoreCase("simplified")) {
            List<SimplifiedEmployee> employees = simplifiedEmployeeService.findIncompleteRecords();
            if (employees.isEmpty()) {
                return new ResponseEntity<>(new CustomResponse("204", "No records were found."),HttpStatus.OK);
            } else {
                try {
                    for (SimplifiedEmployee employee : employees) {
                        simplifiedEmployeeService.deleteById(employee.getId());
                    }
                } catch (Exception exc) {
                    return ResponseEntity.status(500).body("error while deleting incomplete records");
                }
                return new ResponseEntity<>(new CustomResponse("200", "Incomplete records deleted successfully."),HttpStatus.OK);
            }
        } else if (formType.equalsIgnoreCase("ministries")) {
            List<MinistriesEmployee> employees = ministriesEmployeeService.findIncompleteRecords();
            if (employees.isEmpty()) {
                return new ResponseEntity<>(new CustomResponse("204", "No records were found."),HttpStatus.OK);
            } else {
                try {
                    for (MinistriesEmployee employee : employees) {
                        ministriesEmployeeService.deleteById(employee.getId());
                    }
                } catch (Exception exc) {
                    return ResponseEntity.status(500).body("error while deleting incomplete records");
                }
                return new ResponseEntity<>(new CustomResponse("200", "Incomplete records deleted successfully."),HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(new CustomResponse("200", "Incomplete records deleted successfully."),HttpStatus.OK);
        }

    }

    // paste event handler
    @PostMapping("/employees/pasteHandler")
    public ResponseEntity<?> handlePasteEventObject(@RequestBody PasteEventDataObject payloadObj) {
        String formType = payloadObj.getFormType();

        if (formType.equalsIgnoreCase("simplified")) {
            return pasteEventDataService.handleSimplifiedPasteEventObject(payloadObj);
        } else if (formType.equalsIgnoreCase("wps")) {
            return pasteEventDataService.handleWpsPasteEventObject(payloadObj);
        } else if (formType.equalsIgnoreCase("ministries")) {
            return pasteEventDataService.handleMinistriesPasteEventObject(payloadObj);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid form type!");
        }

    }

    // count
    @GetMapping("/employees/{formType}/count")
    public long getmEmployeesCount(@PathVariable("formType") String formType) {

        if (formType.equalsIgnoreCase("simplified")) {
            return ministriesEmployeeService.getCount();
        } else if (formType.equalsIgnoreCase("wps")) {
            return wpsEmployeeService.getCount();
        } else if (formType.equalsIgnoreCase("ministries")) {
            return ministriesEmployeeService.getCount();
        } else {
            return 0l;
        }

    }

    // total
    @GetMapping("/employees/{formType}/total")
    public BigDecimal getEmployeesTotalAmount(@PathVariable("formType") String formType) {

        if (formType.equalsIgnoreCase("simplified")) {
            return simplifiedEmployeeService.getTotalAmount();
        } else if (formType.equalsIgnoreCase("wps")) {
            return wpsEmployeeService.getTotalAmount();
        } else if (formType.equalsIgnoreCase("ministries")) {
            return ministriesEmployeeService.getTotalAmount();
        } else {
            return BigDecimal.ZERO;
        }

    }

    // dashboard
    @GetMapping("/dashboard/{formType}")
    public Map<String, Object> getDashboardData(@PathVariable("formType") String formType) {
        List<String> valuesArray = new ArrayList<>();
        Map<String, Object> stringObjectMap = new HashMap<>();

        if (formType.equalsIgnoreCase("simplified")) {
            stringObjectMap.put("numberOfRecords", simplifiedEmployeeService.getCount());
            stringObjectMap.put("totalAmount", simplifiedEmployeeService.getTotalAmount());
        } else if (formType.equalsIgnoreCase("wps")) {
            stringObjectMap.put("numberOfRecords", wpsEmployeeService.getCount());
            stringObjectMap.put("totalAmount", wpsEmployeeService.getTotalAmount());
        } else if (formType.equalsIgnoreCase("ministries")) {
            stringObjectMap.put("numberOfRecords", ministriesEmployeeService.getCount());
            stringObjectMap.put("totalAmount", ministriesEmployeeService.getTotalAmount());
        }

        return stringObjectMap;
    }

    // simplified
    @PutMapping("/employees/simplified")
    public SimplifiedEmployee addSimplifiedEmployee(@RequestBody SimplifiedEmployee employee) {
        //System.out.println(">>>>>>>>>>>>" + employee);
        return simplifiedEmployeeService.add(employee);
    }

    @PostMapping("/employees/simplified")
    public void addNewSimplifiedEmployee(@RequestBody SimplifiedEmployee employee) {
        //System.out.println(">>>>>>>>>>>>" + employee);
        simplifiedEmployeeService.add(employee);
    }

    // wps
    @PutMapping("/employees/wps")
    public WpsEmployee addWpsEmployee(@RequestBody WpsEmployee employee) {
        return wpsEmployeeService.add(employee);
    }

    @PostMapping("/employees/wps")
    public void addNewWpsEmployee(@RequestBody WpsEmployee employee) {
        wpsEmployeeService.add(employee);
    }

    // ministries
    @PutMapping("/employees/ministries")
    public MinistriesEmployee addMinistriesEmployee(@RequestBody MinistriesEmployee employee) {
        //System.out.println(">>>>>>>>>>>>" + employee);
        return ministriesEmployeeService.add(employee);
    }

    @PostMapping("/employees/ministries")
    public void addNewMinistriesEmployee(@RequestBody MinistriesEmployee employee) {
        //System.out.println(">>>>>>>>>>>>" + employee);
        ministriesEmployeeService.add(employee);
    }

    // delete all
    @DeleteMapping("/employees/{formType}/all")
    public ResponseEntity<?> deleteAllEmployees(@PathVariable("formType") String formType) {
        if (formType.equalsIgnoreCase("simplified")) {
            simplifiedEmployeeService.deleteAll();
        } else if (formType.equalsIgnoreCase("wps")) {
            wpsEmployeeService.deleteAll();
        } else if (formType.equalsIgnoreCase("ministries")) {
            ministriesEmployeeService.deleteAll();
        } else {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok().build();
    }

    // delete selected records
    @DeleteMapping("/employees/selected/{formType}/{toBeDeletedList}")
    public ResponseEntity<?> deleteMultiSimplifiedEmployees(@PathVariable("formType") String formType, @PathVariable("toBeDeletedList") int[] itemsList) {

        if (formType.equalsIgnoreCase("simplified")) {
            for (int id : itemsList) {
                simplifiedEmployeeService.deleteById(id);
            }
        } else if (formType.equalsIgnoreCase("wps")) {
            for (int id : itemsList) {
                wpsEmployeeService.deleteById(id);
            }
        } else if (formType.equalsIgnoreCase("ministries")) {
            for (int id : itemsList) {
                ministriesEmployeeService.deleteById(id);
            }
        } else {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.ok().build();
    }

    // generate pdf file
    @GetMapping("/pdf/{formType}")
    public ResponseEntity<?> generatePdf(@PathVariable("formType") String formType) {

        if (formType.equalsIgnoreCase("simplified")) {
            try {
                CustomResponse customResponse =  simplifiedPdfGenerationService.generatePdfFile();
                return new ResponseEntity<>(customResponse, HttpStatus.OK);
            } catch (Exception e) {
                System.out.println(e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check server logs");
            }
        } else if (formType.equalsIgnoreCase("wps")) {
            try {
                CustomResponse customResponse =  wpsPdfGenerationService.generatePdfFile();
                return new ResponseEntity<>(customResponse, HttpStatus.OK);
            } catch (Exception e) {
                System.out.println(e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check server logs");
            }
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check server logs");
        }

    }

    // generate excel file
    @GetMapping("/excel/{formType}")
    public ResponseEntity<?> generateExcel(@PathVariable("formType") String formType) {
        if (formType.equalsIgnoreCase("ministries")) {
            try {
                CustomResponse customResponse =  ministriesExcelGenerationService.generateExcelFile();
                return new ResponseEntity<>(customResponse, HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check server logs");
            }
        } else if (formType.equalsIgnoreCase("wps")) {
            try {
                CustomResponse customResponse =  wpsExcelGenerationService.generateExcelFile();
                return new ResponseEntity<>(customResponse, HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check server logs");
            }
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check server logs");
        }
    }

    /*
    // for testing purposes only
    @PostMapping("/employees/simplified/bulk/{recordsCount}")
    public ResponseEntity<?> createMultiSimplifiedEmployee(@PathVariable("recordsCount") int recordsCount) {
        for (int i = 0; i < recordsCount; i++) {
            simplifiedEmployeeService.add(new SimplifiedEmployee("Hamza Hamdan", "9123929", "BMUSOMRX", BigDecimal.TEN, "s note"));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/employees/ministries/bulk/{recordsCount}")
    public ResponseEntity<?> createMultiMinistriesEmployee(@PathVariable("recordsCount") int recordsCount) {
        for (int i = 0; i < recordsCount; i++) {
            ministriesEmployeeService.add(new MinistriesEmployee("Hamza Hamdan", "9123929", "BMUSOMRX", "8392847", BigDecimal.TEN, BigDecimal.ONE, "m note"));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/employees/wps/bulk/{recordsCount}")
    public ResponseEntity<?> createMultiEmployee(@PathVariable("recordsCount") int recordsCount) {
        for (int i = 0; i < recordsCount; i++) {
            wpsEmployeeService.add(new WpsEmployee("Hamza Hamdan", "9123929", "BMUSOMRX", "C", "12321321", 123, 1232, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        }
        return new ResponseEntity<>(new CustomResponse("200", "Records created successfully."),HttpStatus.OK);
    }

    */

}
