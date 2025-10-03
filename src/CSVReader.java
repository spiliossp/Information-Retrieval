import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    // Read each line of the CSV file
    public static List<String[]> readCSV(String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(parseCSVLine(line));
            }
        }
        return data;
    }

    // For each line of the CSV file, parse it accordingly
    private static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean withinQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                withinQuotes = !withinQuotes; // Toggle flag when encountering double quotes
            } else if (c == ',' && !withinQuotes) {
                fields.add(field.toString());
                field.setLength(0); // Reset StringBuilder for next field
            } else {
                field.append(c); // Append character to current field
            }
        }

        fields.add(field.toString()); // Add the last field

        return fields.toArray(new String[0]);
    }
}
