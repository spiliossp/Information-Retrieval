import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SearchResultWriter {

    // Write results to .txt file format
    public static void writeResultsToFile(List<List<org.apache.lucene.document.Document>> allResults, String field, String query, int numResults, String directoryPath) throws IOException {
        // Sanitize the query string to remove special characters
        String sanitizedQuery = sanitizeQueryString(query);

        // Construct file name
        String fileName = "Field_" + field + "_Query_" + sanitizedQuery + "_Number_" + numResults + ".txt";
        String filePath = directoryPath + "\\" + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            writer.write("Search Results: \n");
            writer.write("Field: " + field + "\n");
            writer.write("Query: " + query + "\n");
            writer.write("Number of Pages: " + allResults.size() + "\n");
            writer.write("Number of Results: " + numResults + "\n");

            // Iterate over each page
            for (int i = 0; i < allResults.size(); i++) {
                List<org.apache.lucene.document.Document> results = allResults.get(i);

                // Write page number
                writer.write("Page " + (i + 1) + ":\n");

                // Write results...
                for (org.apache.lucene.document.Document doc : results) {
                    writer.write("Source ID: " + doc.get("source_id") + "\n");
                    writer.write("Year: " + doc.get("year") + "\n");
                    writer.write("Title: " + doc.get("title") + "\n");
                    writer.write("Full Text: " + doc.get("full_text") + "\n");
                    writer.write("-----------------------------------\n");
                }
            }
        }
        System.out.println("Search results have been saved to: " + filePath);
    }

    private static String sanitizeQueryString(String query) {
        // Replace special characters with underscores
        return query.replaceAll("[^a-zA-Z0-9]", "");
    }
}
