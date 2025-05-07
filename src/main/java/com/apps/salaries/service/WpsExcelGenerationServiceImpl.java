package com.apps.salaries.service;

import com.apps.salaries.entity.Employer;
import com.apps.salaries.entity.WpsEmployee;
import com.apps.salaries.enums.WpsPaymentTypes;
import com.apps.salaries.exception.CustomResponse;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class WpsExcelGenerationServiceImpl implements WpsExcelGenerationService {

    @Value("${payment.files.directory}")
    private String paymentFilesDirectory;

    @Value("${default.bank.shortName}")
    private String defaultBankShortName;

    @Autowired
    private WpsEmployeeService wpsEmployeeService;

    @Autowired
    private EmployerService employerService;

    private static String[] headerColumns = {
            "Employer CR-NO", "Payer CR-NO", "Payer Bank Short Name",
            "Payer Account Number", "Salary Year", "Salary Month",
            "Total Salaries", "Number Of Records", "Payment Type"
    };

    private static String[] bodyColumns = {
            "Ref No.", "Employee ID Type", "Employee ID No."
            ,"Employee Name" ,"Bank Name" ,"Account Number" ,"Salary Freq." ,"No. of Working Days"
            ,"Extra Hours" ,"Basic Salary" ,"Extra Income" ,"Deductions" ,"Social Security Deductions"
            ,"Net Salary" ,"Notes / Comments" ,"Status"
    };

    @Override
    public CustomResponse generateExcelFile() throws IOException {

        Employer employer = employerService.getWpsEmployer();

        if(employer == null){
            return new CustomResponse("204", "Fill employer records first!");
        }

        if(employer.isDeliveryRequired()){
            if(employer.getDeliveryPersonId() == "" || employer.getDeliveryPersonId() == null || employer.getDeliveryPersonName() == "" || employer.getDeliveryPersonName() == null){
                return new CustomResponse("204", "Fill delivery person details first!");
            }
        }

        List<WpsEmployee> employees = wpsEmployeeService.getAll();

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

    private String createExcelFIle(Employer employer, List<WpsEmployee> employees) throws IOException {

        //password protect the generated xls file
        Biff8EncryptionKey.setCurrentUserPassword("P@ssw0rd");

        // Create a Workbook
        Workbook workbook = new HSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        /* CreationHelper helps us create instances of various things like DataFormat,
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        CreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        Sheet sheet = workbook.createSheet("WPS");

        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create the header row
        Row headerRow = sheet.createRow(0);

        // Create header row cells
        for(int i = 0; i < headerColumns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headerColumns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create the body row
        Row bodyRow = sheet.createRow(2);

        // Create header row cells
        for(int i = 0; i < bodyColumns.length; i++) {
            Cell cell = bodyRow.createCell(i);
            cell.setCellValue(bodyColumns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create Cell Style for formatting Date
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        // Fill rows with data
        int rowNum = 1;
        Row employerRow = sheet.createRow(rowNum);

        int recordSequence = 1;
        int employeesRowNum = 3;
        BigDecimal sum = BigDecimal.ZERO;
        for(WpsEmployee employee: employees) {
            Row employeeRow = sheet.createRow(employeesRowNum);

            sum = sum.add(employee.getBasicSalary()).add(employee.getExtraIncome()).subtract(employee.getDeductions()).subtract(employee.getSocialSecurityDeductions());

            employeeRow.createCell(0).setCellValue(recordSequence);
            employeeRow.createCell(1).setCellValue(employee.getIdType());
            employeeRow.createCell(2).setCellValue(employee.getIdNumber());
            employeeRow.createCell(3).setCellValue(employee.getEmployeeName());
            employeeRow.createCell(4).setCellValue(employee.getEmployeeBank());
            employeeRow.createCell(5).setCellValue(employee.getAccountNumber());
            employeeRow.createCell(6).setCellValue(employer.getSalaryFrequency());
            employeeRow.createCell(7).setCellValue(employee.getNumberOfWorkingDays());
            employeeRow.createCell(8).setCellValue(employee.getNumberOfExtraHours());
            employeeRow.createCell(9).setCellValue(String.valueOf(employee.getBasicSalary()));
            employeeRow.createCell(10).setCellValue(String.valueOf(employee.getExtraIncome()));
            employeeRow.createCell(11).setCellValue(String.valueOf(employee.getDeductions()));
            employeeRow.createCell(12).setCellValue(String.valueOf(employee.getSocialSecurityDeductions()));
            employeeRow.createCell(13).setCellValue(String.valueOf(employee.getBasicSalary().add(employee.getExtraIncome()).subtract(employee.getDeductions()).subtract(employee.getSocialSecurityDeductions())));

            recordSequence++;
            employeesRowNum++;
        }

        employerRow.createCell(0).setCellValue(employer.getEmployerCr());
        employerRow.createCell(1).setCellValue(employer.getPayerCr());
        employerRow.createCell(2).setCellValue(defaultBankShortName);
        employerRow.createCell(3).setCellValue(employer.getDebtorAccountNumber());
        employerRow.createCell(4).setCellValue(employer.getPaymentYear());
        employerRow.createCell(5).setCellValue(employer.getPaymentMonth());
        employerRow.createCell(6).setCellValue(String.valueOf(sum));
        employerRow.createCell(7).setCellValue(employees.size());

        WpsPaymentTypes wpsPaymentType = WpsPaymentTypes.getPaymentTypeByValue(employer.getPaymentType());

        employerRow.createCell(8).setCellValue(wpsPaymentType.getDescription());

        // Resize all columns to fit the content size
        for(int i = 0; i < headerColumns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        for(int i = 0; i < bodyColumns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        String shortFileName = fileNameGenerator().get(0);
        String fileName = fileNameGenerator().get(1);
        FileOutputStream fileOut = new FileOutputStream(fileName);

        workbook.write(fileOut);

        fileOut.close();

        /*
        //use this to create the password protected xlsx file
        try (POIFSFileSystem fs = new POIFSFileSystem()) {
            EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
            // EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile, CipherAlgorithm.aes192, HashAlgorithm.sha384, -1, -1, null);
            Encryptor enc = info.getEncryptor();
            enc.confirmPassword("P@ssw0rd");
            // Read in an existing OOXML file and write to encrypted output stream
            // don't forget to close the output stream otherwise the padding bytes aren't added
            try (OPCPackage opc = OPCPackage.open(new File(fileName), PackageAccess.READ_WRITE);
                 OutputStream os = enc.getDataStream(fs)) {
                opc.save(os);
            }
            // Write out the encrypted version
            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                fs.writeFilesystem(fos);
            }
        } catch (IOException | InvalidFormatException | GeneralSecurityException e) {
            e.printStackTrace();
        }*/

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
        String fileName = file.getAbsoluteFile() + "\\" + shortFilename;

        return Arrays.asList(shortFilename, fileName);
    }

}
