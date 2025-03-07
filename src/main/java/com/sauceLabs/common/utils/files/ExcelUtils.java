package com.sauceLabs.common.utils.files;

import com.sauceLabs.common.utils.logs.MyLogger;
import org.apache.logging.log4j.core.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class ExcelUtils {
    private static final Logger log = new MyLogger().getLogger();
    private final FileUtil fileUtil = new FileUtil();

    /**
     * Reads an Excel file and returns the data as a list of maps (each map represents a row).
     *
     * @param directoryPath The path to the directory containing the Excel file.
     * @param fileName      The name of the Excel file.
     * @param sheetName     The name of the sheet to read.
     * @return A list of maps containing the Excel data.
     */
    public List<Map<String, String>> readExcelFile(String directoryPath, String fileName, String sheetName) {
        List<Map<String, String>> excelData = new ArrayList<>();
        String filePath = fileUtil.searchFileInDirectory(directoryPath, fileName);

        if (filePath == null) {
            log.error("File '{}' does not exist in directory '{}'.", fileName, directoryPath);
            return excelData;
        }

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                log.warn("Sheet '{}' not found in Excel file: {}", sheetName, filePath);
                return excelData;
            }

            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                List<String> headers = new ArrayList<>();
                for (Cell cell : headerRow) {
                    headers.add(cell.getStringCellValue());
                }

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Map<String, String> rowData = new HashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        Cell cell = row.getCell(i);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case STRING:
                                    rowData.put(headers.get(i), cell.getStringCellValue());
                                    break;
                                case NUMERIC:
                                    rowData.put(headers.get(i), String.valueOf(cell.getNumericCellValue()));
                                    break;
                                case BOOLEAN:
                                    rowData.put(headers.get(i), String.valueOf(cell.getBooleanCellValue()));
                                    break;
                                case FORMULA:
                                    rowData.put(headers.get(i), cell.getCellFormula());
                                    break;
                                default:
                                    rowData.put(headers.get(i), "");
                            }
                        } else {
                            rowData.put(headers.get(i), "");
                        }
                    }
                    excelData.add(rowData);
                }
            }
        } catch (IOException e) {
            log.error("Failed to read Excel file '{}': {}", filePath, e.getMessage(), e);
        }
        return excelData;
    }

    /**
     * Writes data to a new Excel file.
     *
     * @param directoryPath The path to the directory to save the Excel file.
     * @param fileName      The name of the Excel file.
     * @param sheetName     The name of the sheet to write.
     * @param data          The data to write (list of maps, where each map represents a row).
     */
    public void writeExcelFile(String directoryPath, String fileName, String sheetName, List<Map<String, String>> data) {
        String filePath = Paths.get(directoryPath, fileName).toString();
        File file = new File(filePath);
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            Sheet sheet = workbook.createSheet(sheetName);

            if (!data.isEmpty()) {
                // Write headers
                Row headerRow = sheet.createRow(0);
                List<String> headers = new ArrayList<>(data.get(0).keySet());
                for (int i = 0; i < headers.size(); i++) {
                    headerRow.createCell(i).setCellValue(headers.get(i));
                }

                // Write data
                for (int i = 0; i < data.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    Map<String, String> rowData = data.get(i);
                    for (int j = 0; j < headers.size(); j++) {
                        row.createCell(j).setCellValue(rowData.get(headers.get(j)));
                    }
                }
            }

            workbook.write(fileOutputStream);
        } catch (IOException e) {
            log.error("Failed to write Excel file: {}", e.getMessage(), e);
        }
    }


    /**
     * Updates an existing Excel file with new data.
     *
     * @param directoryPath The path to the directory containing the Excel file.
     * @param fileName      The name of the Excel file.
     * @param sheetName     The name of the sheet to update.
     * @param data          The data to write (list of maps, where each map represents a row).
     */
    public void updateExcelFile(String directoryPath, String fileName, String sheetName, List<Map<String, String>> data) {
        String filePath = fileUtil.searchFileInDirectory(directoryPath, fileName);
        if (filePath == null) {
            log.error("File '{}' does not exist in directory '{}'.", fileName, directoryPath);
            return;
        }

        Workbook workbook;
        Sheet sheet;

        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            workbook = new XSSFWorkbook(fileInputStream);
            sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            }

            int rowCount = sheet.getLastRowNum();

            if (!data.isEmpty()) {
                // Write headers if the sheet is newly created
                if (rowCount == 0) {
                    Row headerRow = sheet.createRow(0);
                    List<String> headers = new ArrayList<>(data.get(0).keySet());
                    for (int i = 0; i < headers.size(); i++) {
                        headerRow.createCell(i).setCellValue(headers.get(i));
                    }
                    rowCount++;
                }

                // Write data
                for (Map<String, String> rowData : data) {
                    Row row = sheet.createRow(rowCount++);
                    List<String> headers = new ArrayList<>(data.get(0).keySet());
                    for (int j = 0; j < headers.size(); j++) {
                        row.createCell(j).setCellValue(rowData.get(headers.get(j)));
                    }
                }
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                workbook.write(fileOutputStream);
            }
        } catch (IOException e) {
            log.error("Failed to update Excel file: {}", e.getMessage(), e);
        }
    }

//    public static void main(String[] args) {
//        ExcelUtils excelUtils = new ExcelUtils();
//
//        // Test reading an Excel file
//        String fileName = "example.xlsx";
//        String sheetName = "Sheet1";
//        List<Map<String, String>> data = excelUtils.readExcelFile("src/test/resources/testData/project/",fileName, sheetName);
//
//        // Print the data read from the Excel file
//        System.out.println("Data read from Excel file:");
//        for (Map<String, String> row : data) {
//            System.out.println(row);
//        }
//
//        // Test writing to an Excel file
//        List<Map<String, String>> newData = new ArrayList<>();
//        Map<String, String> row1 = new HashMap<>();
//        row1.put("Name", "John Doe");
//        row1.put("Age", "30");
//        row1.put("City", "New York");
//
//        Map<String, String> row2 = new HashMap<>();
//        row2.put("Name", "Jane Smith");
//        row2.put("Age", "25");
//        row2.put("City", "Los Angeles");
//
//        newData.add(row1);
//        newData.add(row2);
//
//        String newFileName = "output.xlsx";
//        String newSheetName = "Sheet1";
//        excelUtils.writeExcelFile("src/test/resources/testData/project/", newFileName, newSheetName, newData);
//
//        System.out.println("Data written to Excel file: " + newFileName);
//    }
}