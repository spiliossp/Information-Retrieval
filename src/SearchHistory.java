import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.util.Arrays;


public class SearchHistory {
    private List<SearchEntry> searchEntries;

    public SearchHistory() {
        this.searchEntries = new ArrayList<>();
    }

    // Add the original query to the search history
    public void addSearchEntry(String field, String query, int numResults) {
//        System.out.println("Query inside the searchEntry method: "+query);
        searchEntries.add(new SearchEntry(field, query, numResults));
    }

    // Display the search History
    public void displaySearchHistory() {
        if (searchEntries.isEmpty()) {
            System.out.println("No search history available.");
        } else {
            for (int i = 0; i < searchEntries.size(); i++) {
                SearchEntry entry = searchEntries.get(i);
                System.out.println((i + 1) + ". Field: " + entry.getField() + ", Query: " + entry.getQuery() +
                        ", NumResults: " + entry.getNumResults());
            }
        }
    }

    // Save the search history to a file
    public void saveToFile(String filePath) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            outputStream.writeObject(searchEntries);
        }
    }

    // Load the search history from a file
    public void loadFromFile(String filePath) {
        File file = new File(filePath);
        if (file.length() == 0) {
            System.out.println("File is empty. No search history to load.");
            return;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath))) {
            searchEntries = (List<SearchEntry>) inputStream.readObject();
            System.out.println("Search history has been loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error occurred while loading search history.");
        }
    }


    // Suggest related queries based on the current query
    public List<String> suggestQueries(String currentQuery) {
        List<String> suggestions = new ArrayList<>();
        List<String> currentKeywords = extractKeywords(currentQuery);

        for (SearchEntry entry : searchEntries) {
            List<String> entryKeywords = extractKeywords(entry.getQuery());
            for (String keyword : currentKeywords) {
                for ( String entryWord : entryKeywords){
                    if (entryWord.contains(keyword) && !suggestions.contains(entry.getQuery())) {
                        suggestions.add(entry.getQuery());
                        break;  // Add each query only once
                    }
                }
            }
        }
        return suggestions;
    }


    // Extract keywords from a query
    private List<String> extractKeywords(String query) {
        return Arrays.asList(query.toLowerCase().split("\\s+"));
    }

    // We need to implement the Serializable interface in order to:
    // 1) Load from file (as a byte stream)
    // 2) Write to file  (as a byte stream)
    private static class SearchEntry implements Serializable {
        private String field;
        private String query;
        private int numResults;

        // Constructor
        public SearchEntry(String field, String query, int numResults) {
            this.field = field;
            this.query = query;
            this.numResults = numResults;
        }

        public String getField() {
            return field;
        }

        public String getQuery() {
            return query;
        }

        public int getNumResults() {
            return numResults;
        }
    }
}
