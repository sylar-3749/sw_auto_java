package common;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Data-driven test data loader — mirrors the Python {@code DataProvider} class.
 *
 * <p>Loads test data from JSON or CSV files for use with Cucumber Scenario Outline
 * Examples or JUnit {@code @ParameterizedTest}.
 *
 * <p>Usage:
 * <pre>{@code
 * List<Map<String, String>> records = DataProvider.loadTestData("login_validations.json");
 * }</pre>
 */
public class DataProvider {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private DataProvider() { /* static utility */ }

    // ------------------------------------------------------------------
    // Public
    // ------------------------------------------------------------------

    /**
     * Load test data from a resource or file path.
     * <p>Supports {@code .json} and {@code .csv}.  CSV files must have a header row.
     *
     * @param path  Classpath resource name or absolute / relative filesystem path.
     * @return List of row maps (column-name → value).
     */
    public static List<Map<String, Object>> loadTestData(String path) throws IOException {
        String ext = path.substring(path.lastIndexOf('.')).toLowerCase();

        // Try classpath first, then filesystem
        try (InputStream in = DataProvider.class.getClassLoader().getResourceAsStream(path)) {
            if (in != null) {
                return switch (ext) {
                    case ".json" -> loadJson(in);
                    case ".csv"  -> loadCsv(new InputStreamReader(in, StandardCharsets.UTF_8));
                    default -> throw new IllegalArgumentException("Unsupported format: " + ext);
                };
            }
        }

        // Fallback to filesystem
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("Test data file not found: " + path);
        }
        return switch (ext) {
            case ".json" -> loadJson(new FileInputStream(file));
            case ".csv"  -> loadCsv(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            default -> throw new IllegalArgumentException("Unsupported format: " + ext);
        };
    }

    /**
     * Convenience overload — returns rows as {@code List<Map<String, String>>}.
     */
    public static List<Map<String, String>> loadTestDataAsStrings(String path) throws IOException {
        List<Map<String, Object>> raw = loadTestData(path);
        List<Map<String, String>> result = new ArrayList<>();
        for (Map<String, Object> row : raw) {
            Map<String, String> strRow = new LinkedHashMap<>();
            for (Map.Entry<String, Object> e : row.entrySet()) {
                strRow.put(e.getKey(), e.getValue() == null ? "" : e.getValue().toString());
            }
            result.add(strRow);
        }
        return result;
    }

    // ------------------------------------------------------------------
    // Loaders
    // ------------------------------------------------------------------

    private static List<Map<String, Object>> loadJson(InputStream in) throws IOException {
        List<Map<String, Object>> data = MAPPER.readValue(in,
            new TypeReference<List<Map<String, Object>>>() {});
        return data != null ? data : Collections.emptyList();
    }

    private static List<Map<String, Object>> loadCsv(Reader reader) throws IOException {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(reader)) {
            String headerLine = br.readLine();
            if (headerLine == null) return rows;

            String[] headers = headerLine.split(",");
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    String val = i < values.length ? values[i].trim() : "";
                    // Strip surrounding quotes if present
                    if (val.startsWith("\"") && val.endsWith("\"")) {
                        val = val.substring(1, val.length() - 1);
                    }
                    row.put(headers[i].trim(), val);
                }
                rows.add(row);
            }
        }
        return rows;
    }
}
