import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SearchResultsWriterHTML {

    // Write results to .html file format
    public static void writeResultsToFile(List<List<org.apache.lucene.document.Document>> allResults, String field, String query, int numResults, String directoryPath) throws IOException {
        // Sanitize the query string to remove special characters
        String sanitizedQuery = sanitizeQueryString(query);

        // Construct file name
        String fileName = "Field_" + field + "_Query_" + sanitizedQuery + "_Number_" + numResults + ".html";
        String filePath = directoryPath + "\\" + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write the HTML header
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write("<title>Search Results</title>\n");
            writer.write("<style>\n");
            writer.write("body { font-family: Arial, sans-serif; }\n");
            writer.write("h2 { color: #333; }\n");
            writer.write(".result { margin-bottom: 20px; padding: 10px; border-bottom: 1px solid #ccc; }\n");
            writer.write("</style>\n");
            writer.write("</head>\n");
            writer.write("<body>\n");
            writer.write("<h1>Search Results</h1>\n");
            writer.write("<h2>Field: " + field + "</h2>\n");
            writer.write("<h2>Query: " + query + "</h2>\n");
            writer.write("<h2>Number of Pages: " + allResults.size() + "</h2>\n");
            writer.write("<h2>Number of Results: " + numResults + "</h2>\n");

            // Iterate over each page
            for (int i = 0; i < allResults.size(); i++) {
                List<org.apache.lucene.document.Document> results = allResults.get(i);

                // Write page number
                writer.write("<h2>Page " + (i + 1) + "</h2>\n");

                // Write results...
                for (org.apache.lucene.document.Document doc : results) {
                    writer.write("<div class='result'>\n");
                    writer.write("<p><strong>Source ID:</strong> " + doc.get("source_id") + "</p>\n");
                    writer.write("<p><strong>Year:</strong> " + doc.get("year") + "</p>\n");
                    writer.write("<p><strong>Title:</strong> " + doc.get("title") + "</p>\n");
                    writer.write("<p><strong>Full Text:</strong> " + doc.get("full_text") + "</p>\n");
                    writer.write("</div>\n");
                }
            }

            // Write the HTML footer
            writer.write("</body>\n");
            writer.write("</html>\n");
        }
        System.out.println("Search results have been saved to: " + filePath);
    }

    private static String sanitizeQueryString(String query) {
        // Replace special characters with space
        return query.replaceAll("[^a-zA-Z0-9]", "");
    }
}
