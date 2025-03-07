package com.sauceLabs.common.utils.files;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sauceLabs.common.utils.logs.MyLogger;
import com.sauceLabs.common.utils.properties.PropertiesManager;
import org.apache.logging.log4j.core.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * this class extract given json file name to script dir then call extracted
 * file to collect data,
 *
 * @author MahmoudOsama
 */
public class JsonUtils {
    private static final Logger log = new MyLogger().getLogger();
    private List<JSONObject> foundJsonObjects = new ArrayList<>();
    private static final FileUtil fileUtil = new FileUtil();
    private static final PropertiesManager propertiesManager = new PropertiesManager();


    public static String ReadJson(String key) {
        String value = null;
        String path = System.getProperty("user.dir") + propertiesManager.getProp("test.data.path");
        log.info("Json Path is set to: {}", path);

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            JSONObject jsonObject = new JSONObject(jsonContent.toString());
            value = jsonObject.getString(key);
            log.info("Successfully retrieved the value for key '{}': {}", key, value);
        } catch (IOException e) {
            log.error("IOException occurred: {}", e.getMessage());
        } catch (org.json.JSONException e) {
            log.error("JSONException occurred: {}", e.getMessage());
        }
        return value;
    }

//    /**
//     * Parses the given file name to JSON and returns the JSON object.
//     *
//     * @param fileName The JSON file name.
//     * @return The JSON object of the file.
//     */
//    public static JSONObject getJsonFileObject(String directoryPath,String fileName) {
//        JSONObject jsonObject = new JSONObject();
//        String filePath = fileUtil.searchFileInDirectory(directoryPath, fileName);
//
//        if (filePath.isEmpty()) {
//            filePath = fileUtil.getResourcePath(fileName);
//            log.info("Getting JSON file with name: {} and path: {}", fileName, filePath);
//            if (filePath.isEmpty()) {
//                return null;
//            }
//        }
//        try {
//            jsonObject = new JSONObject(fileUtil.readFileAsString("",filePath));
//        } catch (JSONException e) {
//            log.warn("Failed to create JSONObject from main read file method. Trying secondary method...");
//            try {
//                jsonObject = new JSONObject(fileUtil.subProcessToReadFile(filePath));
//            } catch (JSONException ex) {
//                log.fatal("Failed to create JSONObject from secondary read file method. Exception: {}", ex.getMessage(), ex);
//            }
//        }
//        return jsonObject;
//    }
//
//    /**
//     * Converts the contents of a file to a JSON object.
//     *
//     * @param content The file content as a string.
//     * @return The JSON object.
//     */
//    public JSONObject getJsonFromString(String content) {
//        return new JSONObject(content);
//    }
//
//    /**
//     * Reads a JSON file and converts it to a map.
//     *
//     * @param fileName The JSON file name.
//     * @return The map containing the JSON file contents.
//     */
//    public Map<String, String> readJsonFileToMap(String directoryPath, String fileName) {
//        Map<String, String> map = new HashMap<>();
//        JSONObject jsonObject = getJsonFileObject(directoryPath, fileName);
//        if (jsonObject == null) {
//            throw new RuntimeException("Failed to read JSON file: " + fileName);
//        }
//        Iterator<String> keys = jsonObject.keys();
//        while (keys.hasNext()) {
//            String key = keys.next();
//            String value = jsonObject.getString(key);
//            map.put(key, value);
//        }
//        return map;
//    }
//
//    /**
//     * Searches for a JSON object by key and value within a parent JSON object.
//     *
//     * @param jsonObject The parent JSON object.
//     * @param key        The key name.
//     * @param value      The key value.
//     * @return The JSON object if found.
//     */
//    public JSONObject getJsonObjectByKeyAndValue(JSONObject jsonObject, String key, String value) {
//        foundJsonObjects.clear();
//        return searchJsonObjectByKeyAndValue(jsonObject, key, value);
//    }
//
//    /**
//     * Helper method to search for a JSON object by key and value.
//     *
//     * @param jsonObject The parent JSON object.
//     * @param key        The key name.
//     * @param value      The key value.
//     * @return The JSON object if found.
//     */
//    private JSONObject searchJsonObjectByKeyAndValue(JSONObject jsonObject, String key, String value) {
//        if (!foundJsonObjects.isEmpty()) {
//            return foundJsonObjects.get(0);
//        }
//        if (jsonObject.has(key) && jsonObject.optString(key, "$null").equals(value)) {
//            foundJsonObjects.add(jsonObject);
//        }
//
//        for (String e : jsonObject.keySet()) {
//            if (!foundJsonObjects.isEmpty()) {
//                break;
//            }
//            JSONObject jsonObjX = jsonObject.optJSONObject(e);
//            if (jsonObjX != null) {
//                JSONObject finalJsonObj = jsonObjX;
//                for (String i : finalJsonObj.keySet()) {
//                    if (finalJsonObj.optString(key, "$null").equals(value)) {
//                        foundJsonObjects.add(finalJsonObj);
//                        break;
//                    } else {
//                        if (getJsonObjectByKeyAndValue(jsonObjX, key, value) != null) {
//                            break;
//                        }
//                    }
//                }
//            } else {
//                JSONArray jsonArray = jsonObject.optJSONArray(e);
//                if (jsonArray != null) {
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        if (getJsonObjectByKeyAndValue(jsonArray.getJSONObject(i), key, value) != null) {
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        return foundJsonObjects.isEmpty() ? null : foundJsonObjects.get(0);
//    }
//
//    /**
//     * Returns a JSON object for a file.
//     *
//     * @param fileName The name of the file.
//     * @return The JSON object.
//     */
//    public JSONObject getJSONObjectAlt(String directoryPath, String fileName) {
//        String fileContent = fileUtil.readFileAsString(directoryPath, fileName);
//        return new JSONObject(fileContent);
//    }
//
//    /**
//     * From a JSON array, selects an object by index and then gets the value for a specific key.
//     *
//     * @param jsonArray The JSON array.
//     * @param index     The index of the object in the array.
//     * @param key       The key to retrieve the value for.
//     * @return The value for the specified key.
//     */
//    public static String getValueFromJSONArray(JSONArray jsonArray, int index, String key) {
//        JSONObject jsonObject = jsonArray.getJSONObject(index);
//        return jsonObject.getString(key);
//    }
//
//    /**
//     * Gets an object tree JSON object from the given JSON object.
//     *
//     * @param jsonObject The parent JSON object.
//     * @param objTree    An array representing the tree structure of JSON objects.
//     * @return The JSON object if it exists.
//     */
//    public Object getJsonObject(JSONObject jsonObject, Object[] objTree) {
//        JSONArray jsonArray = new JSONArray();
//        for (Object element : objTree) {
//            String cleanedElement = element.toString().trim();
//            if (!jsonArray.isEmpty()) {
//                int index = parseInteger(cleanedElement);
//                if (index != -1) {
//                    jsonObject = jsonArray.optJSONObject(index);
//                    if (jsonArray.optJSONArray(index) != null) {
//                        jsonArray = jsonArray.getJSONArray(index);
//                    } else if (jsonObject == null) {
//                        return jsonArray.get(index);
//                    } else {
//                        jsonArray = new JSONArray();
//                    }
//                }
//            } else if (jsonObject.optJSONObject(cleanedElement) != null) {
//                jsonObject = jsonObject.getJSONObject(cleanedElement);
//            } else if (jsonObject.optJSONArray(cleanedElement) != null) {
//                log.info("Getting Array {}", jsonObject.optJSONArray(cleanedElement));
//                jsonArray = jsonObject.getJSONArray(cleanedElement);
//            } else if (Arrays.asList(objTree).indexOf(element) != (objTree.length - 1)) {
//                log.info("JSON object with name '{}' does not exist!!!", cleanedElement);
//                jsonObject = null;
//            }
//        }
//        log.info("Found JSON data >> {}", jsonArray.isEmpty() ? jsonObject.toString() : jsonArray.toString());
//        return jsonArray.isEmpty() ? jsonObject : jsonArray;
//    }
//
//    /**
//     * Converts a JsonNode to a Map.
//     *
//     * @param jsonNode The JsonNode.
//     * @return The map containing the JsonNode contents.
//     */
//    public Map<String, String> jsonNodeToMap(JsonNode jsonNode) {
//        Map<String, String> map = new HashMap<>();
//        Iterator<String> fieldNames = jsonNode.fieldNames();
//
//        while (fieldNames.hasNext()) {
//            String fieldName = fieldNames.next();
//            String fieldValue = jsonNode.get(fieldName).asText();
//            map.put(fieldName, fieldValue);
//        }
//        return map;
//    }
//
//    /**
//     * Parses a string as an integer.
//     *
//     * @param str The string to parse.
//     * @return The integer value, or -1 if parsing fails.
//     */
//    private int parseInteger(String str) {
//        try {
//            return Integer.parseInt(str);
//        } catch (NumberFormatException ignored) {
//            return -1;
//        }
//    }
//
}
