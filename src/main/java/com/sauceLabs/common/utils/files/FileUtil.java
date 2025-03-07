package com.sauceLabs.common.utils.files;

import com.sauceLabs.common.utils.logs.MyLogger;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.core.Logger;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * this class used for interacting with files
 *
 * @author MahmoudOsama
 */
public class FileUtil {
    private static final Logger log = new MyLogger().getLogger();
    private static File projectDirectory = null;


    public FileUtil() {
        ClassLoader classloader = FileUtil.class.getClassLoader();
        URL resource = classloader.getResource("");
        if (resource != null) {
            setProjectDirectory(resource.getPath().replace("test-classes/", "classes/"));
        } else {
            String fallbackPath = System.getProperty("user.dir"); // Set to working directory
            log.info("Resource path not found; setting fallback path to {}", fallbackPath);
            setProjectDirectory(fallbackPath);
        }
    }

    /**
     * Sets the project directory.
     *
     * @param folderPath The path of the directory.
     * @return The directory file object.
     */
    private File setProjectDirectory(String folderPath) {
        projectDirectory = new File(folderPath);
        return projectDirectory;
    }

    /**
     * Gets the project directory file object.
     *
     * @return The project directory.
     */
    public File getProjectDirectory() {
        return projectDirectory;
    }


    /**
     * Creates a directory with the specified path.
     *
     * @param directoryPath The path of the directory to create.
     * @return The created directory file object.
     */
    public synchronized File createDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists()) {
            log.info("Found directory: {}", directory.getPath());
        } else {
            if (directory.mkdirs()) {
                log.info("Directory created: {}", directory.getPath());
            } else {
                log.info("Directory could not be created!");
            }
        }
        return directory;
    }

    /**
     * Creates a file inside the specified directory.
     *
     * @param directoryPath The path of the directory.
     * @param fileName      The name of the file to create.
     * @return The created file object.
     */
    public File createFile(String directoryPath, String fileName) {
        Path filePath = Paths.get(directoryPath, fileName);
        File file = filePath.toFile();
        synchronized (fileName.intern()) {
            if (Files.exists(filePath)) {
                log.info("{} file exists.", fileName);
            } else {
                try {
                    Files.createFile(filePath);
                    log.info("{} file created.", fileName);
                } catch (IOException e) {
                    log.error("Failed to create file: {}", fileName, e);
                }
            }
        }
        return file;
    }


    /**
     * Searches for a file in a given directory path.
     *
     * @param directoryPath The path of the directory to search in.
     * @param fileName      The name of the file to search for.
     * @return The file path if found, otherwise null.
     */
    public String searchFileInDirectory(String directoryPath, String fileName) {
        Path directory = Paths.get(directoryPath);
        System.out.println("Searching in directory: " + directory);
        if (Files.isDirectory(directory)) {
            try (Stream<Path> filePathStream = Files.walk(directory)) {
                Optional<Path> foundFilePath = filePathStream
                        .filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().equals(fileName))
                        .findFirst();
                return foundFilePath.map(Path::toAbsolutePath).map(Path::toString).orElse(null);
            } catch (IOException e) {
                throw new RuntimeException("Error while searching for the file", e);
            }
        } else {
            throw new IllegalArgumentException("The provided path is not a directory: " + directoryPath);
        }
    }

    /**
     * Copies a file from the source to the destination.
     *
     * @param source The source file.
     * @param dest   The destination file.
     */
    public void copyFile(File source, File dest) {
        try (InputStream input = new FileInputStream(source);
             OutputStream output = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) > 0) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            log.catching(e);
        }
    }

    /**
     * Gets the resource path for a given file name.
     *
     * @param fileName The name of the file.
     * @return The resource path if found, otherwise null.
     */
    public String getResourcePath(String fileName) {
        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource != null) {
            try {
                return Paths.get(resource.toURI()).toString();
            } catch (Exception e) {
                log.error("Error converting resource URL to URI", e);
            }
        }
        return null;
    }

    /**
     * Gets a file object with the specified path, name, and extension.
     *
     * @param filePath     The path of the file.
     * @param fileName     The name of the file.
     * @param extension    The extension of the file.
     * @return The file object.
     */
    public File getFile(String filePath, String fileName, String extension) {
        return new File(filePath + fileName + extension);
    }

    /**
     * Reads the content of a file as a string.
     *
     * @param directory The directory path.
     * @param fileName  The name of the file.
     * @return The content of the file as a string.
     */
    public String readFile(String directory, String fileName) {
        ClassLoader classloader = FileUtil.class.getClassLoader();
        String content = "";
        try (InputStream in = classloader.getResourceAsStream(Paths.get(directory, fileName).toString().replace("test-classes", "classes"));
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content += line + "\n";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content;
    }

    /**
     * Reads the contents of a file as a string.
     *
     * @param directoryPath The path to the directory containing the file.
     * @param fileName      The name of the file to read.
     * @return The contents of the file as a string, or null if the file is not found.
     */
    public String readFileAsString(String directoryPath, String fileName) {
        String filePath = searchFileInDirectory(directoryPath, fileName);

        if (filePath.isEmpty()) {
            filePath = getResourcePath(fileName);
            log.info("Getting file with name: {} at path: {}", fileName, filePath);
            if (filePath.isEmpty()) {
                return null;
            }
        }

        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            return new String(fileBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Exception Message for reading file as string: {} \n Stack Trace: {}", e.getMessage(), ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Reads data from a file and stores it in a string.
     *
     * @param fileName The name of the file.
     * @return The content of the file.
     */
    public String readFromFile(String fileName) {
        log.info("Reading from file");
        String content = null;
        try {
            String filePath = System.getProperty("user.dir") + "/" + fileName;
            content = new String(Files.readAllBytes(Paths.get(filePath)));
            log.info("File content: {}", content);
        } catch (IOException e) {
            log.info("IO Exception caught in readFromFile: {}", e.getMessage());
        } catch (Exception e) {
            log.info("Exception caught in readFromFile: {}", e.getMessage());
        }
        return content;
    }

    /**
     * Reads a file using a subprocess (e.g., for Unix-based systems).
     *
     * @param filePath The file path.
     * @return The file contents as a string.
     */
    public String subProcessToReadFile(String filePath) {
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", String.format("cat '%s'", filePath));
            Process process = processBuilder.start();
            process.waitFor();
            try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = stdInput.readLine()) != null) {
                    output.append(line).append("\n");
                }
                while ((line = stdError.readLine()) != null) {
                    log.info(line);
                }
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error reading file with subprocess: {}", e.getMessage(), e);
        }
        return output.toString();
    }

    /**
     * Writes content to a file with the specified path, name, and extension.
     *
     * @param filePath    The path of the file.
     * @param fileName    The name of the file.
     * @param extension   The extension of the file.
     * @param fileContent The content to write to the file.
     */
    public void writeFile(String filePath, String fileName, String extension, String fileContent) {
        try (FileWriter fileWriter = new FileWriter(filePath + fileName + extension)) {
            fileWriter.write(fileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes content to a file with synchronization.
     *
     * @param file The file to write to.
     * @param text The content to write.
     */
    public void writeFileWithSync(File file, String text) {
        synchronized (file) {
            try (FileWriter out = new FileWriter(file)) {
                out.write(text);
            } catch (IOException e) {
                log.catching(e);
            }
        }
    }
}
