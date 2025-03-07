package com.sauceLabs.common.utils.files;

import com.sauceLabs.common.utils.logs.MyLogger;
import org.apache.logging.log4j.core.Logger;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvUtils {
    private static final Logger log = new MyLogger().getLogger();
    private final FileUtil fileUtil = new FileUtil();

    /**
     * Reads a CSV file and returns the data as a list of maps (each map represents a row).
     *
     * @param directoryPath The path to the directory containing the CSV file.
     * @param fileName      The name of the CSV file.
     * @return A list of maps containing the CSV data.
     */
    public List<Map<String, String>> readCsvFile(String directoryPath, String fileName) {
        List<Map<String, String>> csvData = new ArrayList<>();
        String filePath = fileUtil.searchFileInDirectory(directoryPath, fileName);

        if (filePath == null) {
            log.error("File '{}' does not exist in directory '{}'.", fileName, directoryPath);
            return csvData;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String[] headers = null;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (headers == null) {
                    headers = values;
                } else {
                    Map<String, String> rowData = new HashMap<>();
                    for (int i = 0; i < headers.length; i++) {
                        rowData.put(headers[i], values[i]);
                    }
                    csvData.add(rowData);
                }
            }
        } catch (IOException e) {
            log.error("Failed to read CSV file '{}': {}", filePath, e.getMessage(), e);
        }
        return csvData;
    }

    /**
     * Writes data to a new CSV file.
     *
     * @param directoryPath The path to the directory to save the CSV file.
     * @param fileName      The name of the CSV file.
     * @param data          The data to write (list of maps, where each map represents a row).
     */
    public void writeCsvFile(String directoryPath, String fileName, List<Map<String, String>> data) {
        String filePath = Paths.get(directoryPath, fileName).toString();
        File file = new File(filePath);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            if (!data.isEmpty()) {
                // Write headers
                List<String> headers = new ArrayList<>(data.get(0).keySet());
                bw.write(String.join(",", headers));
                bw.newLine();

                // Write data
                for (Map<String, String> rowData : data) {
                    List<String> values = new ArrayList<>();
                    for (String header : headers) {
                        values.add(rowData.get(header));
                    }
                    bw.write(String.join(",", values));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            log.error("Failed to write CSV file: {}", e.getMessage(), e);
        }
    }

    /**
     * Updates an existing CSV file with new data.
     *
     * @param directoryPath The path to the directory containing the CSV file.
     * @param fileName      The name of the CSV file.
     * @param data          The data to write (list of maps, where each map represents a row).
     */
    public void updateCsvFile(String directoryPath, String fileName, List<Map<String, String>> data) {
        String filePath = fileUtil.searchFileInDirectory(directoryPath, fileName);
        if (filePath == null) {
            log.error("File '{}' does not exist in that directory '{}'.", fileName, directoryPath);
            return;
        }

        List<Map<String, String>> existingData = readCsvFile(directoryPath, fileName);
        existingData.addAll(data);

        writeCsvFile(directoryPath, fileName, existingData);
    }
}
