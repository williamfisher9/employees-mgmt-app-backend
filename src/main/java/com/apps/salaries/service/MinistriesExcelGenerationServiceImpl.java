package com.apps.salaries.service;

import com.apps.salaries.entity.Employer;
import com.apps.salaries.entity.MinistriesEmployee;
import com.apps.salaries.enums.Banks;
import com.apps.salaries.exception.CustomResponse;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class MinistriesExcelGenerationServiceImpl implements MinistriesExcelGenerationService {

    @Value("${payment.files.directory}")
    private String paymentFilesDirectory;

    @Autowired
    private EmployerService employerService;

    @Autowired
    private MinistriesEmployeeService ministriesEmployeeService;

    private static String[] bodyColumns = {
            "Account Number" ,"Employee Name" ,"Bank Name" ,"Basic Salary","Deductions","Net Salary", "Employee ID/Staff Number", "Notes"
    };

    @Override
    public CustomResponse generateExcelFile() throws IOException {

        Employer employer = employerService.getMinistriesEmployer();

        if(employer == null){
            return new CustomResponse("204", "Fill employer records first!");
        }

        if(employer.isDeliveryRequired()){
            if(employer.getDeliveryPersonId() == "" || employer.getDeliveryPersonId() == null ||
                    employer.getDeliveryPersonName() == "" || employer.getDeliveryPersonName() == null){
                return new CustomResponse("204", "Fill delivery person details first!");
            }
        }

        List<MinistriesEmployee> employees = ministriesEmployeeService.getAll();

        if(employees.isEmpty()){
            return new CustomResponse("204", "Create some employee records first!");
        }

        String fileName = null;

        try{
            fileName = createExcelFIle(employer, employees);
        }catch(Exception e){
            return new CustomResponse("204", "Error while creating the excel file!");
        }

        return new CustomResponse("200", fileName);
    }

    private String createExcelFIle(Employer employer, List<MinistriesEmployee> employees)  throws IOException{

        //password protect the generated xls file
        Biff8EncryptionKey.setCurrentUserPassword("P@ssw0rd");

        // Create a Workbook
        Workbook workbook = new HSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        /* CreationHelper helps us create instances of various things like DataFormat,
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        CreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        Sheet sheet = workbook.createSheet("non-wps");

        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create the body row
        Row bodyRow = sheet.createRow(0);

        // Create header row cells
        for(int i = 0; i < bodyColumns.length; i++) {
            Cell cell = bodyRow.createCell(i);
            cell.setCellValue(bodyColumns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int employeesRowNum = 1;
        BigDecimal sum = BigDecimal.ZERO;
        for(MinistriesEmployee employee: employees) {
            Row employeeRow = sheet.createRow(employeesRowNum);

            sum = sum.add(employee.getAmount()).subtract(employee.getDeductions());

            employeeRow.createCell(0).setCellValue(employee.getAccountNumber());
            employeeRow.createCell(1).setCellValue(employee.getEmployeeName());
            employeeRow.createCell(2).setCellValue(Banks.getBankByValue(employee.getEmployeeBank()).getDescription());
            employeeRow.createCell(3).setCellValue(String.valueOf(employee.getAmount()));
            employeeRow.createCell(4).setCellValue(String.valueOf(employee.getDeductions()));
            employeeRow.createCell(5).setCellValue(String.valueOf(employee.getAmount().subtract(employee.getDeductions())));
            employeeRow.createCell(6).setCellValue(employee.getIdNumber());
            employeeRow.createCell(7).setCellValue(employee.getNote());

            employeesRowNum++;
        }

        // Resize all columns to fit the content size
        for(int i = 0; i < bodyColumns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        String shortFileName = fileNameGenerator().get(0);
        String fileName = fileNameGenerator().get(1);
        FileOutputStream fileOut = new FileOutputStream(fileName);
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();

        //password protect the generated xls file
        Biff8EncryptionKey.setCurrentUserPassword(null);

        return shortFileName;

    }

    private List<String> fileNameGenerator() {

        File file = new File(paymentFilesDirectory);
        file.mkdirs();

        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");

        String shortFilename = "payments-file-" + date.format(formatter) + ".xls";
        String fileName = file.getAbsoluteFile() + "/" + shortFilename;

        return Arrays.asList(shortFilename, fileName);
    }

}
