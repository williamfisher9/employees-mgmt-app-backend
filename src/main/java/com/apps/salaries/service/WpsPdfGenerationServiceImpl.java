package com.apps.salaries.service;

import com.apps.salaries.entity.Employer;
import com.apps.salaries.entity.WpsEmployee;
import com.apps.salaries.enums.Banks;
import com.apps.salaries.enums.SalaryFrequency;
import com.apps.salaries.enums.WpsPaymentTypes;
import com.apps.salaries.exception.CustomResponse;
import com.apps.salaries.utils.AmountInWords;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.PDF417Writer;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
//import com.onbarcode.barcode.PDF417;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Service
public class WpsPdfGenerationServiceImpl implements WpsPdfGenerationService {

    @Value("${payment.files.directory}")
    private String paymentFilesDirectory;

    @Autowired
    private WpsEmployeeService wpsEmployeeService;

    @Autowired
    private EmployerService employerService;

    private DecimalFormat decimalFormat = new DecimalFormat("#,###.000");

    private String documentUuid = null;

    private long recordsCount;

    private BigDecimal totalAmount;

    private int numberOfPages;

    private final int NUMBER_OF_RECORDS_PER_PAGE = 6;

    @Value("${default.bank.account.number.length}")
    private String defaultBankAccountNumberLength;

    @Value("${default.bank.code}")
    private String defaultBankCode;

    @Value("${default.bank.name}")
    private String defaultBankName;

    @Value("${default.bank.shortName}")
    private String defaultBankShortName;

    private BaseColor boxesColor = new BaseColor(219, 219, 219);
    private BaseColor tableColor = new BaseColor(202, 214, 219);

    @Override
    public CustomResponse generatePdfFile() throws Exception {

        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);

        documentUuid = UUID.randomUUID().toString().replace("-","");

        Employer employer = employerService.getWpsEmployer();

        if(employer == null){
            return new CustomResponse("204", "Fill employer records first!");
        }

        if(employer.isDeliveryRequired()){
            if(employer.getDeliveryPersonId() == "" || employer.getDeliveryPersonId() == null ||
            employer.getDeliveryPersonName() == "" || employer.getDeliveryPersonName() == null){
                return new CustomResponse("204", "Fill delivery person details first!");
            }
        }

        List<WpsEmployee> employees = wpsEmployeeService.getAll();

        if(employees.isEmpty()){
            return new CustomResponse("204", "Create some employee records first!");
        }

        List<List<WpsEmployee>> partitionedEmployeesList = Lists.partition(employees, NUMBER_OF_RECORDS_PER_PAGE);

        numberOfPages = partitionedEmployeesList.size();

        totalAmount = wpsEmployeeService.getTotalAmount();
        recordsCount = wpsEmployeeService.getCount();

        Map<byte[], List<WpsEmployee>> barcodeAndEmployeesMap = new LinkedHashMap<>();

        int pageNumber = 1;
        for(List<WpsEmployee> employeesList: partitionedEmployeesList){
            StringBuilder stringBuilder = buildBarcodeDataString(employer, employeesList, pageNumber);
            //System.out.println(stringBuilder);
            byte[] imageBytes = generateBarcodeAsImageBytes(stringBuilder);
            barcodeAndEmployeesMap.put(imageBytes, employeesList);
            pageNumber++;
        }

        // generate image pdf
        String generatedFileName = generatePdfFile(barcodeAndEmployeesMap, employer);
        return new CustomResponse("200", generatedFileName);
    }

    private StringBuilder buildBarcodeDataString(Employer employer, List<WpsEmployee> employeesList, int pageNumber) {
        StringBuilder stringBuilder = new StringBuilder();

        if(pageNumber == 1){
            stringBuilder = getEmployerDetailsStringBuilder(employer);
        } else {
            stringBuilder.append("|");
        }

        stringBuilder.append(prepareEmployeesList(employeesList, pageNumber));

        if(employeesList.size() != 6){
            int numberOfRecordsUntilFullPage = 6 - employeesList.size();
            for(int i=0; i<numberOfRecordsUntilFullPage; i++){
                stringBuilder.append("|||||||||||||");
            }
        }

        stringBuilder.append(documentUuid).append("|").append(pageNumber).append("|").append(numberOfPages).append("|")
                .append(calculateHashValue(String.valueOf(stringBuilder))).append("|");

        return stringBuilder;
    }

    private StringBuilder getEmployerDetailsStringBuilder(Employer employer) {

        StringBuilder newStringBuilder = new StringBuilder();
        newStringBuilder.append("|" + defaultBankShortName +
                "|" + employer.getEmployerName() +
                "|" + employer.getDebtorAccountNumber() +
                "|" + employer.getEmployerCr() +
                "|" + employer.getPayerCr() +
                "|" + totalAmount +
                "|" + employer.getPaymentType() +
                "|" + employer.getSalaryFrequency() +
                "|" + employer.getValueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                "|" + employer.getPaymentYear() +
                "|" + Strings.padStart(String.valueOf(employer.getPaymentMonth()), 2, '0') +
                "|" + recordsCount + "|");

        return newStringBuilder;
    }

    private byte[] generateBarcodeAsImageBytes(StringBuilder stringBuilder) throws Exception{
        PDF417Writer barcodeWriter = new PDF417Writer();
        BitMatrix bitMatrix =
                barcodeWriter.encode(stringBuilder.toString(),
                        BarcodeFormat.PDF_417, 200, 200);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "png", baos);
        return baos.toByteArray();
        /*
        // Create Java PDF417 object
        PDF417 barcode = new PDF417();

        // PDF 417 Error Correction Level
        barcode.setEcl(PDF417.ECL_6);
        //barcode.setAutoResize(true);
        barcode.setDataMode(PDF417.M_AUTO);

        //StringBuilder stringBuilder = prepareEmployeesList(employeesList);

        barcode.setData(String.valueOf(stringBuilder));

        //BufferedImage bufferedImage = barcode.drawBarcode();

        byte[] imageInBytes = barcode.drawBarcodeToBytes();

        return imageInBytes;

         */
    }

    //--------------------------

    private String generatePdfFile(Map<byte[], List<WpsEmployee>> barcodeAndEmployeesMap, Employer employer) throws Exception{

        String shortFileName = fileNameGenerator().get(0);
        String fileName = fileNameGenerator().get(1);

        Document document = new Document(PageSize.A4, 36,36,36,36);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));

        int documentPageNumber = 1;
        for (Map.Entry<byte[],List<WpsEmployee>> entry : barcodeAndEmployeesMap.entrySet()){

            byte[] imageInByte = entry.getKey();
            List<WpsEmployee> employeeList = entry.getValue();

            //System.out.println("number of items in the list " + employeeList.size());

            /*ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write( bufferedImage, "jpg", byteArrayOutputStream );
            byteArrayOutputStream.flush();
            byte[] imageInByte = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();*/

            document.open();

            //draw border around the page
            //--------------start
            float width = document.getPageSize().getWidth();
            float height = document.getPageSize().getHeight();
            Rectangle pageBorderRectangle= new Rectangle(20, 20, width - 20, height - 20);
            pageBorderRectangle.setBorder(Rectangle.BOX);
            pageBorderRectangle.setBorderWidth(1);
            pageBorderRectangle.enableBorderSide(1);
            pageBorderRectangle.enableBorderSide(2);
            pageBorderRectangle.enableBorderSide(4);
            pageBorderRectangle.enableBorderSide(8);
            pageBorderRectangle.setBorderColor(BaseColor.BLACK);
            //document.add(pageBorderRectangle);
            //--------------end

            /*Rectangle logoRectangle = new Rectangle(237f, 770, 357f, 830);
            logoRectangle.setBackgroundColor(tableColor);
            logoRectangle.setBorderColor(BaseColor.BLACK);
            logoRectangle.setBorder(Rectangle.BOX);
            logoRectangle.setBorderWidth(0.5f);
            logoRectangle.enableBorderSide(1);
            logoRectangle.enableBorderSide(2);
            logoRectangle.enableBorderSide(4);
            logoRectangle.enableBorderSide(8);
            document.add(logoRectangle);*/

            try{
                String imgFileName = "src\\main\\resources\\logo.png";
                Image image = Image.getInstance(imgFileName);
                image.scaleToFit(90, 90);
                image.setAbsolutePosition(260f, 780f);
                document.add(image);
            }catch (Exception exc){
                Rectangle logoRectangle = new Rectangle(237f, 770, 357f, 830);
                logoRectangle.setBackgroundColor(tableColor);
                logoRectangle.setBorderColor(BaseColor.BLACK);
                logoRectangle.setBorder(Rectangle.BOX);
                logoRectangle.setBorderWidth(0.5f);
                logoRectangle.enableBorderSide(1);
                logoRectangle.enableBorderSide(2);
                logoRectangle.enableBorderSide(4);
                logoRectangle.enableBorderSide(8);
                document.add(logoRectangle);
            }

            document.add(new Paragraph(Chunk.NEWLINE));
            document.add(new Paragraph(Chunk.NEWLINE));

            Paragraph bankName = new Paragraph(defaultBankName + " - WPS", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
            bankName.setAlignment(Element.ALIGN_CENTER);
            document.add(bankName);

            document.add(new Paragraph(Chunk.NEWLINE));
            document.add(new Paragraph(Chunk.NEWLINE));

            Image img = Image.getInstance(imageInByte);
            //img.scaleToFit(523f, 300f);
            img.scaleToFit(document.getPageSize().getWidth()-72, 400f);
            //img.setAbsolutePosition(35f, 650f);
            //img.setAbsolutePosition(15f, 150f);
            document.add(img);
            //--------------start

            document.add(new Paragraph(Chunk.NEWLINE));
            document.add(new Paragraph(Chunk.NEWLINE));
            document.add(new Paragraph(Chunk.NEWLINE));
            document.add(new Paragraph(Chunk.NEWLINE));


            Font fontPageHeader = new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.NORMAL);

            PdfPTable debitOurAccountTable = addDebitOurAccountTable(fontPageHeader, employer, employeeList, totalAmount, recordsCount);
            debitOurAccountTable.setSpacingAfter(5f);
            document.add(debitOurAccountTable);

            PdfPTable employerDetailsTable = addEmployerDetailsTable(fontPageHeader, employer, totalAmount, recordsCount);

            //employerDetailsTable.setSpacingBefore(150f);
            document.add(employerDetailsTable);

            //document.add(new Paragraph(Chunk.NEWLINE));
            document.add(new Paragraph(Chunk.NEWLINE));

            //document.add(totalAmountInWordsChunk);

            PdfPTable employeesTable = addEmployeesTable(fontPageHeader, employeeList, documentPageNumber);
            document.add(employeesTable);

            /*BaseFont checkboxBase = BaseFont.createFont("C:\\Windows\\Fonts\\WINGDNG2.TTF", BaseFont.IDENTITY_H, false);
            Font checkBoxFont = new Font(checkboxBase, 12f, Font.BOLD);
            Phrase checkBoxPhrase = new Phrase("\u0052", checkBoxFont);
            Font chunkBoxFont = new Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.BOLD);
            Chunk termsAndConditionChunk = new Chunk(" Terms and Conditions were approved.", chunkBoxFont);
            checkBoxPhrase.add(termsAndConditionChunk);
            document.add(checkBoxPhrase);*/

            //........................................................................................................................................
            document.add(new Paragraph(Chunk.NEWLINE));
            /*DottedLineSeparator dottedLineSeparator = new DottedLineSeparator();
            dottedLineSeparator.setOffset(-2);
            dottedLineSeparator.setGap(2f);
            document.add(dottedLineSeparator);

            PdfPTable extraDetailsTable = addExtraDetailsTable(fontPageHeader, deliveryPerson);
            document.add(extraDetailsTable);*/

            //PdfPTable deliveryPersonTable = addDeliveryPersonTable(fontPageHeader, deliveryPerson);
            //document.add(deliveryPersonTable);

            //place text at an absolute position
            //-------------------------start

            PdfContentByte cb = writer.getDirectContent();
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            BaseFont checkboxBaseFont =
                    BaseFont.createFont(BaseFont.COURIER,
                            BaseFont.CP1252, false);
            cb.saveState();
            cb.beginText();
            cb.moveText(36, 175);
            cb.setFontAndSize(checkboxBaseFont, 12f);
            cb.showText("\u0052");
            cb.endText();
            cb.restoreState();

            cb.saveState();
            cb.beginText();
            cb.moveText(50, 175);
            //cb.moveText(50, 265);
            cb.setFontAndSize(bf, 12);
            cb.showText("Terms and conditions were approved." );
            cb.endText();
            cb.restoreState();

            /*PdfContentByte dashedLineCb = writer.getDirectContent();
            dashedLineCb.moveTo(34, 260);
            dashedLineCb.setLineDash(5, 7, 2.5);
            dashedLineCb.lineTo(560, 260);
            dashedLineCb.stroke();*/

            cb.saveState();
            cb.beginText();
            cb.moveText(36, 140);
            cb.setFontAndSize(bf, 10);
            cb.showText("Authorized Signatories and Stamp" );
            cb.endText();
            cb.restoreState();

            cb.saveState();
            cb.beginText();
            cb.moveText(36, 120);
            cb.setFontAndSize(checkboxBaseFont, 12);
            cb.showText("\u00A3");
            cb.endText();
            cb.restoreState();

            cb.saveState();
            cb.beginText();
            cb.moveText(50, 120);
            cb.setFontAndSize(bf, 10);
            cb.showText("Signature Verified");
            cb.endText();
            cb.restoreState();

            cb.saveState();
            cb.beginText();
            cb.moveText(36, 105);
            cb.setFontAndSize(bf, 10);
            cb.showText("(for bank use only)" );
            cb.endText();
            cb.restoreState();

            cb.moveTo(36, 36);
            cb.lineTo(560, 36);
            cb.stroke();

            float textWidth = bf.getWidthPoint(documentUuid, 8);
            float pageWidth = document.getPageSize().getWidth();
            float textXPosition = pageWidth - 36 - textWidth;
            cb.saveState();
            cb.beginText();
            cb.moveText(textXPosition, 27);
            cb.setFontAndSize(bf, 8);
            cb.showText(documentUuid);
            cb.endText();
            cb.restoreState();

            cb.saveState();
            cb.beginText();
            String pageNumber = "Page " + documentPageNumber + " of " + numberOfPages;
            float pageNumberWidth = bf.getWidthPoint(pageNumber, 8);
            float pageNumberXPosition = pageWidth/2 - pageNumberWidth/2;
            cb.moveText(pageNumberXPosition, 27);
            cb.setFontAndSize(bf, 8);
            cb.showText(pageNumber);
            cb.endText();
            cb.restoreState();

            cb.saveState();
            cb.beginText();
            cb.moveText(36, 27);
            cb.setFontAndSize(bf, 8);

            cb.showText("Designed and Built by William Fisher");
            cb.endText();
            cb.restoreState();

            //------------------------end

            Rectangle firstSignature= new Rectangle(560, 160, 385, 40);
            firstSignature.setBorder(Rectangle.BOX);
            //firstSignature.setBorderWidth(0.5f);
            firstSignature.setBackgroundColor(tableColor);
            firstSignature.enableBorderSide(1);
            firstSignature.enableBorderSide(2);
            firstSignature.enableBorderSide(4);
            firstSignature.enableBorderSide(8);
            //firstSignature.setBorderColor(BaseColor.BLACK);
            document.add(firstSignature);

            Rectangle secondSignature= new Rectangle(375, 160, 195, 40);
            secondSignature.setBorder(Rectangle.BOX);
            //secondSignature.setBorderWidth(0.5f);
            secondSignature.setBackgroundColor(tableColor);
            secondSignature.enableBorderSide(1);
            secondSignature.enableBorderSide(2);
            secondSignature.enableBorderSide(4);
            secondSignature.enableBorderSide(8);
            //secondSignature.setBorderColor(BaseColor.BLACK);
            document.add(secondSignature);

            document.newPage();

            documentPageNumber++;

        }

        document.close();
        return shortFileName;
    }

    /*private PdfPTable addExtraDetailsTable(Font fontPageHeader, DeliveryPerson deliveryPerson) {

        PdfPTable extraDetailsTable = new PdfPTable(6);
        extraDetailsTable.setWidthPercentage(100);
        extraDetailsTable.setSpacingBefore(0f);
        extraDetailsTable.setSpacingAfter(0f);

        PdfPCell deliveryPersonNameLabelCell = new PdfPCell(new Paragraph("Delivery Person Name:" ,fontPageHeader));
        deliveryPersonNameLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //deliveryPersonCell.setBackgroundColor(tableColor);
        deliveryPersonNameLabelCell.setBorder(Rectangle.NO_BORDER);
        deliveryPersonNameLabelCell.setColspan(1);
        deliveryPersonNameLabelCell.setFixedHeight(15f);

        PdfPCell deliveryPersonNameCell = new PdfPCell(new Paragraph(deliveryPerson.getDeliveryPersonName(),fontPageHeader));
        deliveryPersonNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //deliveryPersonCell.setBackgroundColor(tableColor);
        deliveryPersonNameCell.setBorder(Rectangle.NO_BORDER);
        deliveryPersonNameCell.setColspan(5);
        deliveryPersonNameCell.setFixedHeight(15f);

        extraDetailsTable.addCell(deliveryPersonNameLabelCell);
        extraDetailsTable.addCell(deliveryPersonNameCell);


        PdfPCell deliveryPersonIdLabelCell = new PdfPCell(new Paragraph("Delivery Person ID:" ,fontPageHeader));
        deliveryPersonIdLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //deliveryPersonCell.setBackgroundColor(tableColor);
        deliveryPersonIdLabelCell.setBorder(Rectangle.NO_BORDER);
        deliveryPersonIdLabelCell.setColspan(1);
        deliveryPersonIdLabelCell.setFixedHeight(15f);

        PdfPCell deliveryPersonIdCell = new PdfPCell(new Paragraph(deliveryPerson.getDeliveryPersonId(),fontPageHeader));
        deliveryPersonIdCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //deliveryPersonCell.setBackgroundColor(tableColor);
        deliveryPersonIdCell.setBorder(Rectangle.NO_BORDER);
        deliveryPersonIdCell.setColspan(5);
        deliveryPersonIdCell.setFixedHeight(15f);

        extraDetailsTable.addCell(deliveryPersonIdLabelCell);
        extraDetailsTable.addCell(deliveryPersonIdCell);

        return extraDetailsTable;

    }
    */


    private PdfPTable addEmployeesTable(Font fontPageHeader, List<WpsEmployee> employeeList, int documentPageNumber) throws DocumentException {
        PdfPTable employeesTable = new PdfPTable(5);
        employeesTable.setWidthPercentage(100);
        employeesTable.setSpacingBefore(0f);
        employeesTable.setSpacingAfter(0f);

        BaseColor tableColor = new BaseColor(202, 214, 219);

        int[] widths = new int[] { 15, 60, 150, 30, 60 };
        employeesTable.setWidths(widths);

        Font fontTableHeader = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);

        PdfPTable headerTable = new PdfPTable(5);
        headerTable.setWidths(widths);
        Phrase idPhraseHeader = new Phrase("No.", fontTableHeader);


        PdfPCell employeesTableCell = new PdfPCell(new Phrase("Employees Details"));
        employeesTableCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        employeesTableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        employeesTableCell.setColspan(5);
        employeesTableCell.setFixedHeight(20f);
        employeesTableCell.setBorder(2);
        headerTable.addCell(employeesTableCell);

        PdfPCell idHeaderCell = new PdfPCell(idPhraseHeader);
        idHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        idHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        idHeaderCell.setColspan(1);
        idHeaderCell.setFixedHeight(20f);
        idHeaderCell.setBorder(2);

        Phrase accountPhraseHeader = new Phrase("Account No.", fontTableHeader);
        PdfPCell accountHeaderCell = new PdfPCell(accountPhraseHeader);
        accountHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        accountHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        accountHeaderCell.setColspan(1);
        accountHeaderCell.setFixedHeight(20f);
        accountHeaderCell.setBorder(2);

        Phrase namePhraseHeader = new Phrase("Employee Name", fontTableHeader);
        PdfPCell nameHeaderCell = new PdfPCell(namePhraseHeader);
        nameHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        nameHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        nameHeaderCell.setColspan(1);
        nameHeaderCell.setFixedHeight(20f);
        nameHeaderCell.setBorder(2);

        Phrase salaryPhraseHeader = new Phrase("Net Salary", fontTableHeader);
        PdfPCell salaryHeaderCell = new PdfPCell(salaryPhraseHeader);
        salaryHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        salaryHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        salaryHeaderCell.setColspan(1);
        salaryHeaderCell.setFixedHeight(20f);
        salaryHeaderCell.setBorder(2);

        Phrase bankPhraseHeader = new Phrase("Bank Name", fontTableHeader);
        PdfPCell bankHeaderCell = new PdfPCell(bankPhraseHeader);
        bankHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        bankHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        bankHeaderCell.setColspan(1);
        bankHeaderCell.setFixedHeight(20f);
        bankHeaderCell.setBorder(2);

        headerTable.addCell(idHeaderCell);
        headerTable.addCell(accountHeaderCell);
        headerTable.addCell(nameHeaderCell);
        headerTable.addCell(salaryHeaderCell);
        headerTable.addCell(bankHeaderCell);
        PdfPCell headerCell = new PdfPCell(headerTable);
        headerCell.setColspan(5);
        headerCell.setBackgroundColor(tableColor);
        headerCell.setBorder(0);
        employeesTable.addCell(headerCell);

        int recordId = 1;
        for(WpsEmployee employee:employeeList){
            Phrase idPhrase = new Phrase(String.valueOf(recordId + ((documentPageNumber - 1)*NUMBER_OF_RECORDS_PER_PAGE)), fontPageHeader);
            PdfPCell idCell = new PdfPCell(idPhrase);
            idCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            idCell.setColspan(1);
            idCell.setFixedHeight(20f);
            idCell.setBorder(2|8);
            employeesTable.addCell(idCell);

            Phrase namePhrase = new Phrase(employee.getAccountNumber(), fontPageHeader);
            PdfPCell nameCell = new PdfPCell(namePhrase);
            nameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            nameCell.setColspan(1);
            nameCell.setFixedHeight(20f);
            nameCell.setBorder(2|8);
            employeesTable.addCell(nameCell);

            Phrase accountPhrase = new Phrase(employee.getEmployeeName(), fontPageHeader);
            PdfPCell accountCell = new PdfPCell(accountPhrase);
            accountCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            accountCell.setColspan(1);
            accountCell.setFixedHeight(20f);
            accountCell.setBorder(2|8);
            employeesTable.addCell(accountCell);

            BigDecimal employeeNetSalary = employee.getBasicSalary().add(employee.getExtraIncome())
                    .subtract(employee.getDeductions()).subtract(employee.getSocialSecurityDeductions());

            Phrase netSalaryPhrase = new Phrase(decimalFormat.format(employeeNetSalary), fontPageHeader);
            PdfPCell netSalaryCell = new PdfPCell(netSalaryPhrase);
            netSalaryCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            netSalaryCell.setColspan(1);
            netSalaryCell.setFixedHeight(20f);
            netSalaryCell.setBorder(2|8);
            employeesTable.addCell(netSalaryCell);

            Banks employeeBank = Banks.getBankByValue(employee.getEmployeeBank());

            Phrase bankPhrase = new Phrase(employeeBank.getDescription(), fontPageHeader);
            PdfPCell bankCell = new PdfPCell(bankPhrase);
            bankCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            bankCell.setColspan(1);
            bankCell.setFixedHeight(20f);
            bankCell.setBorder(2);
            employeesTable.addCell(bankCell);

            recordId++;
        }

        Phrase subtotalPhrase = new Phrase("Subtotal", new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL));
        PdfPCell subtotalCell = new PdfPCell(subtotalPhrase);
        subtotalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        subtotalCell.setColspan(3);
        subtotalCell.setFixedHeight(20f);
        subtotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        subtotalCell.setBackgroundColor(tableColor);
        subtotalCell.setBorder(2|8);
        employeesTable.addCell(subtotalCell);

        BigDecimal sum = BigDecimal.ZERO;
        if(employeeList != null){
            for(WpsEmployee employee: employeeList){
                //System.out.println(">>>" + employee.getBasicSalary());
                sum = sum.add(employee.getBasicSalary()).add(employee.getExtraIncome()).subtract(employee.getDeductions()).subtract(employee.getSocialSecurityDeductions());
            }
        }
        Phrase subtotalAmountPhrase = new Phrase(String.valueOf(decimalFormat.format(sum)), new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL));
        PdfPCell subtotalAmountCell = new PdfPCell(subtotalAmountPhrase);
        subtotalAmountCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        subtotalAmountCell.setColspan(2);
        subtotalAmountCell.setFixedHeight(20f);
        subtotalAmountCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        subtotalAmountCell.setBorder(2);
        employeesTable.addCell(subtotalAmountCell);

        //striping the table
        /*boolean b = true;
        for(PdfPRow r: employeesTable.getRows()) {
            System.out.println(employeesTable.getRows().size());
            for(PdfPCell c: r.getCells()) {
                c.setBackgroundColor(b ? tableColor : BaseColor.WHITE);
            }
            b = !b;
        }*/

        return employeesTable;
    }

    private List<String> fileNameGenerator() {

        File file = new File(paymentFilesDirectory);
        file.mkdirs();

        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");

        String shortFilename = "payments-file-" + date.format(formatter) + ".pdf";
        String fileName = file.getAbsoluteFile() + "/" + shortFilename;

        return Arrays.asList(shortFilename, fileName);
    }

    private StringBuilder prepareEmployeesList(List<WpsEmployee> employeesList, int pageNumber) {
        StringBuilder stringBuilder = new StringBuilder();
        int recordSequence = 1 + ((pageNumber-1) * NUMBER_OF_RECORDS_PER_PAGE);
        for(WpsEmployee employee: employeesList){
            stringBuilder.append((recordSequence++)
                    + "|" + employee.getAccountNumber()
                    + "|" + employee.getEmployeeName()
                    + "|" + employee.getEmployeeBank()
                    + "|" + employee.getIdType()
                    + "|" + employee.getIdNumber()
                    + "|" + employee.getNumberOfWorkingDays()
                    + "|" + employee.getNumberOfExtraHours()
                    + "|" + employee.getBasicSalary()
                    + "|" + employee.getExtraIncome()
                    + "|" + employee.getDeductions()
                    + "|" + employee.getSocialSecurityDeductions()
                    + "|" + employee.getBasicSalary().add(employee.getExtraIncome()).subtract(employee.getDeductions()).subtract(employee.getSocialSecurityDeductions())
                    + "|"
            );
        }
        return stringBuilder;
    }

    private PdfPTable addEmployerDetailsTable(Font fontPageHeader, Employer employer, BigDecimal totalAmount, long recordsCount) {

        WpsPaymentTypes wpsPaymentType = WpsPaymentTypes.getPaymentTypeByValue(employer.getPaymentType());
        //System.out.println(">>>" + employer.getSalaryFrequency());

        SalaryFrequency salaryFrequency = SalaryFrequency.getSalaryFrequencyByValue(employer.getSalaryFrequency());
        //System.out.println(">>>" + salaryFrequency.getDescription());
        //System.out.println(">>>" + salaryFrequency.getValue());

        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setSpacingBefore(0f);
        table.setSpacingAfter(0f);

        PdfPCell emptyCell = new PdfPCell(new Phrase(""));
        emptyCell.setColspan(1);
        emptyCell.setFixedHeight(20f);
        emptyCell.setBorder(Rectangle.BOTTOM);

        PdfPCell headerCell = new PdfPCell(new Phrase("Employer Details"));
        headerCell.setColspan(9);
        headerCell.setFixedHeight(20f);
        headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        headerCell.setBorder(2);
        headerCell.setBackgroundColor(tableColor);
        table.addCell(headerCell);

        Phrase employerNameLabelPhrase = new Phrase("Employer Name:",  fontPageHeader);
        Chunk employerNameChunk = new Chunk(employer.getEmployerName(),  fontPageHeader);
        //employerNameChunk.setUnderline(0.5f, -1.5f);
        Phrase employerNamePhrase = new Phrase(employerNameChunk);

        PdfPCell employerNameLabelCell = new PdfPCell(employerNameLabelPhrase);
        employerNameLabelCell.setBorder(Rectangle.BOTTOM);
        employerNameLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        employerNameLabelCell.setFixedHeight(20f);
        employerNameLabelCell.setColspan(1);

        PdfPCell employerNameCell = new PdfPCell(employerNamePhrase);
        employerNameCell.setColspan(2);
        //employerNameCell.setBackgroundColor(tableColor);
        employerNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        employerNameCell.setBorder(Rectangle.BOTTOM);
        employerNameCell.setFixedHeight(20f);
        employerNameCell.setBackgroundColor(tableColor);

        Phrase employerCrLabelPhrase = new Phrase("Employer CR/NO:",  fontPageHeader);
        PdfPCell employerCrLabelCell = new PdfPCell(employerCrLabelPhrase);
        employerCrLabelCell.setBorder(Rectangle.BOTTOM);
        employerCrLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        employerCrLabelCell.setColspan(1);
        employerCrLabelCell.setFixedHeight(20f);

        Chunk employerCrChunk = new Chunk(employer.getEmployerCr(),  fontPageHeader);
        //employerCrChunk.setUnderline(0.5f, -1.5f);
        Phrase employerCrPhrase = new Phrase(employerCrChunk);
        PdfPCell employerCrCell = new PdfPCell(employerCrPhrase);
        //employerCrCell.setBorder(Rectangle.NO_BORDER);
        employerCrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //employerCrCell.setBackgroundColor(tableColor);
        employerCrCell.setBorder(Rectangle.BOTTOM);
        employerCrCell.setColspan(2);
        employerCrCell.setFixedHeight(20f);
        employerCrCell.setBackgroundColor(tableColor);

        Phrase payerCrLabelPhrase = new Phrase("Payer CR/NO:",  fontPageHeader);
        PdfPCell payerCrLabelCell = new PdfPCell(payerCrLabelPhrase);
        payerCrLabelCell.setBorder(Rectangle.BOTTOM);
        payerCrLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        payerCrLabelCell.setColspan(1);
        payerCrLabelCell.setFixedHeight(20f);

        Chunk payerCrChunk = new Chunk(employer.getPayerCr(),  fontPageHeader);
        //payerCrChunk.setUnderline(0.5f, -1.5f);
        Phrase payerCrPhrase = new Phrase(payerCrChunk);
        PdfPCell payerCrCell = new PdfPCell(payerCrPhrase);
        payerCrCell.setBorder(Rectangle.BOTTOM);
        payerCrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //payerCrCell.setBackgroundColor(tableColor);
        payerCrCell.setColspan(2);
        payerCrCell.setFixedHeight(20f);
        payerCrCell.setBackgroundColor(tableColor);

        table.addCell(employerNameLabelCell);
        table.addCell(employerNameCell);

        table.addCell(employerCrLabelCell);
        table.addCell(employerCrCell);

        //empty cell
        //table.addCell(emptyCell);

        table.addCell(payerCrLabelCell);
        table.addCell(payerCrCell);


        //email address field
        Phrase emailAddressLabelPhrase = new Phrase("Email Address:",  fontPageHeader);
        PdfPCell emailAddressLabelCell = new PdfPCell(emailAddressLabelPhrase);
        emailAddressLabelCell.setBorder(Rectangle.BOTTOM);
        emailAddressLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        emailAddressLabelCell.setColspan(1);
        emailAddressLabelCell.setFixedHeight(20f);

        Chunk emailAddressChunk = new Chunk(employer.getEmailAddress(),  fontPageHeader);
        //emailAddressChunk.setUnderline(0.5f, -1.5f);
        Phrase emailAddressPhrase = new Phrase(emailAddressChunk);
        PdfPCell emailAddressCell = new PdfPCell(emailAddressPhrase);
        emailAddressCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //emailAddressCell.setBackgroundColor(tableColor);
        emailAddressCell.setBorder(Rectangle.BOTTOM);
        emailAddressCell.setColspan(2);
        emailAddressCell.setFixedHeight(20f);
        emailAddressCell.setBackgroundColor(tableColor);

        table.addCell(emailAddressLabelCell);
        table.addCell(emailAddressCell);

        //empty cell
        //table.addCell(emptyCell);

        //phone number field
        Phrase phoneNumberLabelPhrase = new Phrase("Phone Number:",  fontPageHeader);
        PdfPCell phoneNumberLabelCell = new PdfPCell(phoneNumberLabelPhrase);
        phoneNumberLabelCell.setBorder(Rectangle.BOTTOM);
        phoneNumberLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        phoneNumberLabelCell.setColspan(1);
        phoneNumberLabelCell.setFixedHeight(20f);

        Chunk phoneNumberChunk = new Chunk(employer.getPhoneNumber(),  fontPageHeader);
        //phoneNumberChunk.setUnderline(0.5f, -1.5f);
        Phrase phoneNumberPhrase = new Phrase(phoneNumberChunk);
        PdfPCell phoneNumberCell = new PdfPCell(phoneNumberPhrase);
        phoneNumberCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //phoneNumberCell.setBackgroundColor(tableColor);
        phoneNumberCell.setColspan(2);
        phoneNumberCell.setFixedHeight(20f);
        phoneNumberCell.setBorder(Rectangle.BOTTOM);
        phoneNumberCell.setBackgroundColor(tableColor);

        table.addCell(phoneNumberLabelCell);
        table.addCell(phoneNumberCell);

        //payment type field
        Phrase paymentTypeLabelPhrase = new Phrase("Payment Type:", fontPageHeader);
        PdfPCell paymentTypeLabelCell = new PdfPCell(paymentTypeLabelPhrase);
        paymentTypeLabelCell.setBorder(Rectangle.BOTTOM);
        paymentTypeLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        paymentTypeLabelCell.setColspan(1);
        paymentTypeLabelCell.setFixedHeight(20f);

        Chunk paymentTypeChunk = new Chunk(wpsPaymentType.getDescription(),  fontPageHeader);
        //paymentTypeChunk.setUnderline(0.5f, -1.5f);
        Phrase paymentTypePhrase = new Phrase(paymentTypeChunk);
        PdfPCell paymentTypeCell = new PdfPCell(paymentTypePhrase);
        paymentTypeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //paymentTypeCell.setBackgroundColor(tableColor);
        paymentTypeCell.setColspan(2);
        paymentTypeCell.setFixedHeight(20f);
        paymentTypeCell.setBorder(Rectangle.BOTTOM);
        paymentTypeCell.setBackgroundColor(tableColor);

        table.addCell(paymentTypeLabelCell);
        table.addCell(paymentTypeCell);

        //empty cell
        //table.addCell(emptyCell);

        //value date field
        Phrase valueDateLabelPhrase = new Phrase("Value Date:", fontPageHeader);
        PdfPCell valueDateLabelCell = new PdfPCell(valueDateLabelPhrase);
        valueDateLabelCell.setBorder(Rectangle.BOTTOM);
        valueDateLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        valueDateLabelCell.setColspan(1);
        valueDateLabelCell.setFixedHeight(20f);

        Chunk valueDateChunk = new Chunk(employer.getValueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontPageHeader);
        //valueDateChunk.setUnderline(0.5f, -1.5f);
        Phrase valueDatePhrase = new Phrase(valueDateChunk);
        PdfPCell valueDateCell = new PdfPCell(valueDatePhrase);
        valueDateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //valueDateCell.setBackgroundColor(tableColor);
        valueDateCell.setColspan(2);
        valueDateCell.setFixedHeight(20f);
        valueDateCell.setBorder(Rectangle.BOTTOM);
        valueDateCell.setBackgroundColor(tableColor);

        table.addCell(valueDateLabelCell);
        table.addCell(valueDateCell);

        //payment year field
        Phrase paymentYearLabelPhrase = new Phrase("Payment Year:", fontPageHeader);
        PdfPCell paymentYearLabelCell = new PdfPCell(paymentYearLabelPhrase);
        paymentYearLabelCell.setBorder(Rectangle.BOTTOM);
        paymentYearLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        paymentYearLabelCell.setColspan(1);
        paymentYearLabelCell.setFixedHeight(20f);

        Chunk paymentYearChunk = new Chunk(String.valueOf(employer.getPaymentYear()),  fontPageHeader);
        //paymentYearChunk.setUnderline(0.5f, -1.5f);
        Phrase paymentYearPhrase = new Phrase(paymentYearChunk);
        PdfPCell paymentYearCell = new PdfPCell(paymentYearPhrase);
        paymentYearCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //paymentYearCell.setBackgroundColor(tableColor);
        paymentYearCell.setColspan(2);
        paymentYearCell.setFixedHeight(20f);
        paymentYearCell.setBorder(Rectangle.BOTTOM);
        paymentYearCell.setBackgroundColor(tableColor);

        table.addCell(paymentYearLabelCell);
        table.addCell(paymentYearCell);

        //empty cell
        //table.addCell(emptyCell);

        //payment month field
        Phrase paymentMonthLabelPhrase = new Phrase("Payment Month:", fontPageHeader);
        PdfPCell paymentMonthLabelCell = new PdfPCell(paymentMonthLabelPhrase);
        paymentMonthLabelCell.setBorder(Rectangle.BOTTOM);
        paymentMonthLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        paymentMonthLabelCell.setColspan(1);
        paymentMonthLabelCell.setFixedHeight(20f);

        Chunk paymentMonthChunk = new Chunk(String.valueOf(employer.getPaymentMonth()),  fontPageHeader);
        //paymentMonthChunk.setUnderline(0.5f, -1.5f);
        Phrase paymentMonthPhrase = new Phrase(paymentMonthChunk);
        PdfPCell paymentMonthCell = new PdfPCell(paymentMonthPhrase);
        paymentMonthCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //paymentMonthCell.setBackgroundColor(tableColor);
        paymentMonthCell.setColspan(2);
        paymentMonthCell.setFixedHeight(20f);
        paymentMonthCell.setBorder(Rectangle.BOTTOM);
        paymentMonthCell.setBackgroundColor(tableColor);

        table.addCell(paymentMonthLabelCell);
        table.addCell(paymentMonthCell);

        //salary frequency field
        Phrase salaryFrequencyLabelPhrase = new Phrase("Salary Frequency:", fontPageHeader);
        PdfPCell paymentFrequencyLabelCell = new PdfPCell(salaryFrequencyLabelPhrase);
        paymentFrequencyLabelCell.setBorder(Rectangle.BOTTOM);
        paymentFrequencyLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        paymentFrequencyLabelCell.setColspan(1);
        paymentFrequencyLabelCell.setFixedHeight(20f);

        Chunk paymentFrequencyChunk = new Chunk(salaryFrequency.getDescription(),  fontPageHeader);
        //paymentFrequencyChunk.setUnderline(0.5f, -1.5f);
        Phrase paymentFrequencyPhrase = new Phrase(paymentFrequencyChunk);
        PdfPCell paymentFrequencyCell = new PdfPCell(paymentFrequencyPhrase);
        paymentFrequencyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //paymentFrequencyCell.setBackgroundColor(tableColor);
        paymentFrequencyCell.setColspan(2);
        paymentFrequencyCell.setFixedHeight(20f);
        paymentFrequencyCell.setBorder(Rectangle.BOTTOM);
        paymentFrequencyCell.setBackgroundColor(tableColor);

        table.addCell(paymentFrequencyLabelCell);
        table.addCell(paymentFrequencyCell);

        //empty cell
        //table.addCell(emptyCell);

        //records count field
        Phrase recordsCountLabelPhrase = new Phrase("Records Count:", fontPageHeader);
        PdfPCell recordsCountLabelCell = new PdfPCell(recordsCountLabelPhrase);
        recordsCountLabelCell.setBorder(Rectangle.BOTTOM);
        recordsCountLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        recordsCountLabelCell.setColspan(1);
        recordsCountLabelCell.setFixedHeight(20f);

        Chunk recordsCountChunk = new Chunk(String.valueOf(recordsCount),  fontPageHeader);
        //recordsCountChunk.setUnderline(0.5f, -1.5f);
        Phrase recordsCountPhrase = new Phrase(recordsCountChunk);
        PdfPCell recordsCountCell = new PdfPCell(recordsCountPhrase);
        recordsCountCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //recordsCountCell.setBackgroundColor(tableColor);
        recordsCountCell.setColspan(2);
        recordsCountCell.setFixedHeight(20f);
        recordsCountCell.setBorder(Rectangle.BOTTOM);
        recordsCountCell.setBackgroundColor(tableColor);

        table.addCell(recordsCountLabelCell);
        table.addCell(recordsCountCell);

        table.addCell(emptyCell);
        table.addCell(emptyCell);
        table.addCell(emptyCell);
        table.addCell(emptyCell);

        return table;
    }


    private PdfPTable addDebitOurAccountTable(Font fontPageHeader, Employer employer, List<WpsEmployee> employeeList, BigDecimal totalAmount, long recordsCount) {
        PdfPTable debitStatementTable = new PdfPTable(1);
        debitStatementTable.setWidthPercentage(100);
        debitStatementTable.setSpacingBefore(0f);
        debitStatementTable.setSpacingAfter(0f);

        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

        Paragraph debitOurAccountParagraph = new Paragraph("Debit our Account No. ", font);
        Chunk debtorAccountChunk = new Chunk(employer.getDebtorAccountNumber());
        debtorAccountChunk.setUnderline(0.5f, -1.5f);
        Chunk extraChunk1 = new Chunk(" with " + defaultBankName + " for ");
        Chunk totalAmountChunk = new Chunk(decimalFormat.format(totalAmount));
        totalAmountChunk.setUnderline(0.5f, -1.5f);
        Chunk extraChunk2 = new Chunk(" USD and credit all accounts below.");
        debitOurAccountParagraph.add(debtorAccountChunk);
        debitOurAccountParagraph.add(extraChunk1);
        debitOurAccountParagraph.add(totalAmountChunk);
        debitOurAccountParagraph.add(extraChunk2);

        PdfPCell debitAccountStatementCell = new PdfPCell(debitOurAccountParagraph);
        debitAccountStatementCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //deliveryPersonCell.setBackgroundColor(tableColor);
        debitAccountStatementCell.setBorder(Rectangle.NO_BORDER);
        debitAccountStatementCell.setColspan(1);
        debitAccountStatementCell.setFixedHeight(25f);

        debitStatementTable.addCell(debitAccountStatementCell);

        /*BigDecimal sum = BigDecimal.ZERO;
        if(employeeList != null){
            for(Employee employee: employeeList){
                //System.out.println(">>>" + employee.getBasicSalary());
                sum = sum.add(employee.getBasicSalary()).add(employee.getExtraIncome()).subtract(employee.getDeductions()).subtract(employee.getSocialSecurityDeductions());
            }
        }
        String totalAmountForAm = String.valueOf(sum);*/
        String[] amountArray = String.valueOf(totalAmount).split("\\.");
        //System.out.println(Arrays.toString(amountArray));
        Chunk totalAmountInWordsChunk = new Chunk("Total Amount in Words: "
                + AmountInWords.convert(Long.parseLong(amountArray[0]))
                + " USD and "
                + AmountInWords.convert(Long.parseLong(amountArray[1])) + " Cents.");
        totalAmountInWordsChunk.setFont(font);

        Paragraph amountInWordsParagraph = new Paragraph(totalAmountInWordsChunk);
        PdfPCell amountInWordsCell = new PdfPCell(amountInWordsParagraph);
        amountInWordsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //deliveryPersonCell.setBackgroundColor(tableColor);
        amountInWordsCell.setBorder(Rectangle.NO_BORDER);
        amountInWordsCell.setColspan(1);
        amountInWordsCell.setFixedHeight(25f);
        amountInWordsCell.setPaddingBottom(10f);
        //amountInWordsCell.setBackgroundColor(tableColor);

        debitStatementTable.addCell(amountInWordsCell);

        return debitStatementTable;
    }

    /*
    private PdfPTable addDeliveryPersonTable(Font fontPageHeader, DeliveryPerson deliveryPerson) {
        PdfPTable deliveryTable = new PdfPTable(6);
        deliveryTable.setWidthPercentage(100);
        deliveryTable.setSpacingBefore(0f);
        deliveryTable.setSpacingAfter(0f);

        //email address field
        Phrase deliveryPersonNameLabelPhrase = new Phrase("Delivery Person Name: ",  fontPageHeader);
        PdfPCell deliveryPersonNameLabelCell = new PdfPCell(deliveryPersonNameLabelPhrase);
        deliveryPersonNameLabelCell.setBorder(Rectangle.NO_BORDER);
        deliveryPersonNameLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        deliveryPersonNameLabelCell.setColspan(1);
        deliveryPersonNameLabelCell.setFixedHeight(20f);

        Chunk deliveryPersonNameChunk = new Chunk(deliveryPerson.getDeliveryPersonName(),  fontPageHeader);
        deliveryPersonNameChunk.setUnderline(0.5f, -1.5f);
        Phrase deliveryPersonPhrase = new Phrase(deliveryPersonNameChunk);
        PdfPCell deliveryPersonCell = new PdfPCell(deliveryPersonPhrase);
        deliveryPersonCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //deliveryPersonCell.setBackgroundColor(tableColor);
        deliveryPersonCell.setBorder(Rectangle.NO_BORDER);
        deliveryPersonCell.setColspan(2);
        deliveryPersonCell.setFixedHeight(20f);

        deliveryTable.addCell(deliveryPersonNameLabelCell);
        deliveryTable.addCell(deliveryPersonCell);

        //empty cell
        //PdfPCell emptyCell = new PdfPCell(new Phrase(""));
        //emptyCell.setColspan(1);
        //emptyCell.setFixedHeight(20f);
        //emptyCell.setBorder(Rectangle.NO_BORDER);
        //deliveryTable.addCell(emptyCell);

        //phone number field
        Phrase deliveryPersonIdLabelPhrase = new Phrase("Delivery Person ID: ",  fontPageHeader);
        PdfPCell deliveryPersonIdLabelCell = new PdfPCell(deliveryPersonIdLabelPhrase);
        deliveryPersonIdLabelCell.setBorder(Rectangle.NO_BORDER);
        deliveryPersonIdLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        deliveryPersonIdLabelCell.setColspan(1);
        deliveryPersonIdLabelCell.setFixedHeight(20f);

        Chunk deliveryPersonIdChunk = new Chunk(deliveryPerson.getDeliveryPersonId(),  fontPageHeader);
        deliveryPersonIdChunk.setUnderline(0.5f, -1.5f);
        Phrase deliveryPersonIdPhrase = new Phrase(deliveryPersonIdChunk);
        PdfPCell deliveryPersonIdCell = new PdfPCell(deliveryPersonIdPhrase);
        deliveryPersonIdCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //deliveryPersonIdCell.setBackgroundColor(tableColor);
        deliveryPersonIdCell.setColspan(2);
        deliveryPersonIdCell.setFixedHeight(20f);
        deliveryPersonIdCell.setBorder(Rectangle.NO_BORDER);

        deliveryTable.addCell(deliveryPersonIdLabelCell);
        deliveryTable.addCell(deliveryPersonIdCell);

        return deliveryTable;
    }
    */

    private String calculateHashValue(String input){

        HashCode hash = Hashing.md5().hashString(input, Charsets.UTF_8);
        String myChecksum = hash.toString().toLowerCase();
        return myChecksum;

    }

}
