import java.io.IOException;
import java.util.*;
import java.io.File;

import org.apache.lucene.document.Document;


public class Main {
    // Synonyms map
    private static final Map<String, String[]> synonymsMap = new HashMap<>();

    // For knowing when to ask to sort by year (used for the calculation of the improved queries)
    // In order to not ask the user about sorting the results when we calculate which query has the most results
    public static boolean askSortByYear = true;

    // *EXTRA* Additional Feature -> Improve Query with synonyms map
    static {
        // Initialize the synonyms map
        synonymsMap.put("research", new String[]{"study", "investigation", "inquiry", "exploration"});
        synonymsMap.put("paper", new String[]{"article", "publication", "document"});
        synonymsMap.put("author", new String[]{"writer", "contributor", "researcher"});
        synonymsMap.put("abstract", new String[]{"summary", "synopsis", "overview"});
        synonymsMap.put("conclusion", new String[]{"findings", "results", "summary"});
        synonymsMap.put("algorithm", new String[]{"procedure", "method", "process", "routine"});
        synonymsMap.put("database", new String[]{"DBMS", "data store", "repository"});
        synonymsMap.put("programming", new String[]{"coding", "software development", "scripting"});
        synonymsMap.put("network", new String[]{"communication", "connectivity", "link"});
        synonymsMap.put("server", new String[]{"host", "node", "mainframe"});
        synonymsMap.put("security", new String[]{"protection", "safety", "defense"});
        synonymsMap.put("software", new String[]{"application", "program", "tool"});
        synonymsMap.put("hardware", new String[]{"device", "equipment", "machinery"});
        synonymsMap.put("machine learning", new String[]{"ML", "artificial intelligence", "AI", "data mining"});
        synonymsMap.put("deep learning", new String[]{"neural networks", "DL", "deep neural networks"});
        synonymsMap.put("natural language processing", new String[]{"NLP", "text mining", "linguistic analysis"});
        synonymsMap.put("computer vision", new String[]{"CV", "image processing", "visual recognition"});
        synonymsMap.put("reinforcement learning", new String[]{"RL", "adaptive learning", "reward-based learning"});
        synonymsMap.put("data science", new String[]{"DS", "data analytics", "big data"});
        synonymsMap.put("predictive modeling", new String[]{"predictive analytics", "forecasting", "regression analysis"});
    }


    public static void main(String[] args) {

        // Get the user's desktop directory
        String userHome = System.getProperty("user.home");
        String desktopPath = userHome + File.separator + "Documents";
        String baseDirectory = desktopPath + File.separator + "information-retrieval";
        String resultsDirectory = baseDirectory + File.separator + "Results";
        String metaDataDirectory = baseDirectory + File.separator + "MetaData";
        String searchHistoryDirectory = baseDirectory + File.separator + "SearchHistory";
        String searchHistoryFilePath = searchHistoryDirectory + File.separator + "search_history.dat";


        // Set this to the location of the papers CSV File
//        String filePath = "C:\\Users\\ontov\\OneDrive\\Desktop\\Courses\\CSE\\Ανάκτηση Πληροφορίας\\Phase 2\\Data\\papers_cleaned.csv";
        String filePath = "C:\\Users\\mysmu\\Desktop\\Desktop Folders\\information-retrieval-source-code-master\\papers_cleaned.csv";
//        String indexPath = "C:\\Users\\ontov\\OneDrive\\Desktop\\Courses\\CSE\\Ανάκτηση Πληροφορίας\\Phase 2\\MetaData";
//        String searchHistoryFilePath = "C:\\Users\\ontov\\OneDrive\\Desktop\\Courses\\CSE\\Ανάκτηση Πληροφορίας\\Phase 2\\Search_History\\search_history.dat";


        // Create directories if they do not exist
        createDirectory(baseDirectory);
        createDirectory(resultsDirectory);
        createDirectory(metaDataDirectory);
        createDirectory(searchHistoryDirectory);

        // Initialize SearchHistory
        SearchHistory searchHistory = new SearchHistory();

        // Load search history from file
        searchHistory.loadFromFile(searchHistoryFilePath);

        // Display search history
        System.out.println("Search History:");
        searchHistory.displaySearchHistory();

        try {
        // Read CSV data
        List<String[]> csvData = CSVReader.readCSV(filePath);

        // Index CSV data using Lucene
        LuceneSearch luceneSearch = new LuceneSearch(metaDataDirectory);
        luceneSearch.indexCSVData(csvData);

        Scanner scanner = new Scanner(System.in);

        boolean exit = false;
        while (!exit) {
            // Ask user for field to search
            System.out.println("Enter the field you want to search (e.g., source_id, year, title, full_text):");
            String field = scanner.nextLine();

            // Ask user for query
            System.out.println("Enter the query:");
            String query = scanner.nextLine();

            // Suggest related queries based on past search history
//            System.out.println("Suggest queries v2");
            List<String> suggestions = searchHistory.suggestQueries(query);
            if (!suggestions.isEmpty()) {
                System.out.println("Related queries:");
                for (int i = 0; i < suggestions.size(); i++) {
                    System.out.println((i + 1) + ". " + suggestions.get(i));
                }
            } else {
                System.out.println("No related queries found.");
            }

            // *EXTRA* Additional Feature -> Related Queries
            // Ask user if they want to change the query to an existing one
            if (!suggestions.isEmpty()){
                System.out.println("Do you want to use a related query? (yes/no): ");
                String changeQuery = scanner.nextLine();
                if (changeQuery.equals("yes")){
                    System.out.println("Enter the number of the related query that you would like to search: ");
                    Integer numberQuery = Integer.valueOf(scanner.nextLine());
                    numberQuery = numberQuery - 1;
                    if (numberQuery >= 0 && numberQuery <= suggestions.size()-1 ){
                        query = suggestions.get(numberQuery);
                        System.out.println("New query: "+ query);
                    } else {
                        System.out.println("The number you provided is not valid");
                        System.out.println("Searching the initial query: "+ query);
                    }
                }
            }



            // *EXTRA* Additional Search Feature -> WildCard search
            // Ask user if they want to perform a wildcard search
            System.out.println("Do you want to perform a wildcard search? (yes/no): ");
            String wildcardOption = scanner.nextLine();
            boolean wildcardSearch = wildcardOption.equalsIgnoreCase("yes");

            // If wildcard search is chosen, provide syntax and modify the query
            if (wildcardSearch) {
                System.out.println("For wildcard search, use '*' to match any characters and '?' to match any single character.");
                System.out.println("For example, 'comp*' will match 'computer', 'company', etc.");
                System.out.println("Do you want to search with multi-characters(*) or single-characters(?) (multi/single): ");
                String wildcardMultiSingle = scanner.nextLine();
                if (wildcardMultiSingle.equals("multi")){
                    // Modify the query to include '*' or '?' as needed
                    query += "*"; // Add '*' for wildcard search
                }
                else if (wildcardMultiSingle.equals("single")){
                    query += "?"; // Add '*' for wildcard search
                }
                else{
                    System.out.println("Did not recognise the input");
                    System.out.println("Searching with 'multi-characters' as the default option");
                    query += "*"; // Add '*' for wildcard search
                }
                System.out.println("Reformed the query to wildcard: "+ query);
            }



            // Ask user for number of results, not needed
//                System.out.println("Enter the max number of results that you wish to retrieve (integer): ");
//                int numRes = Integer.parseInt(scanner.nextLine());

            // *EXTRA* Additional Feature -> Improve Query based on results length
            // Ask the user if they want to see an improved query suggestion
            System.out.println("Do you want to see an improved query suggestion? (yes/no): ");
            String suggestImprovedQuery = scanner.nextLine();
            if (suggestImprovedQuery.equalsIgnoreCase("yes")) {
                // Get the improved query
//                String improvedQuery = suggestImprovedQuery(query);
                String improvedQuery = suggestImprovedQuery(query, field, luceneSearch);
                System.out.println("Improved query suggestion: " + improvedQuery);
                // Ask if the user wants to use the improved query
                System.out.println("Do you want to use the improved query? (yes/no): ");
                String useImprovedQuery = scanner.nextLine();
                // If the user chooses to use the improved query, pass both the improved query to the SearchHistory
                if (useImprovedQuery.equalsIgnoreCase("yes")) {
                    query = improvedQuery; // Use the improved query

                }
            }

            // Print the final Query to search
            System.out.println("Query: "+query);

            // Perform search using Lucene
            List<List<Document>> results = luceneSearch.search(query, field);

            System.out.println("----------------------------------");
            System.out.println("Total Pages length: " + results.size());
            System.out.println("----------------------------------");

            System.out.println("----------------------------------");
            System.out.println("Total Results length: " +luceneSearch.getResultsSize());
            System.out.println("----------------------------------");

            // Check if there are results before processing them
            if (!results.isEmpty()) {

                // Number of Pages
                int pages = results.size();

                // Number of Results
                int resultsSize = luceneSearch.getResultsSize();

                // If results has pages, we will always show the first page
                int currentPage = 0;



                // Display initial set of results
                displayResults(results,currentPage);


                // Process search results
                // Add search entry to search history
                searchHistory.addSearchEntry(field, query, resultsSize);


                // Save search history to file
                try {
                    searchHistory.saveToFile(searchHistoryFilePath);
                    System.out.println("Search history has been saved successfully.");
                    System.out.println("Stored at: "+ searchHistoryFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Error occurred while saving search history.");
                }


                // Path where you want to save the file
                // We will use resultsDirectory for now
//                String saveDirectory = "C:\\Users\\ontov\\OneDrive\\Desktop\\Courses\\CSE\\Ανάκτηση Πληροφορίας\\Phase 2\\Results";

                try {
                    SearchResultWriter.writeResultsToFile(results, field, query, resultsSize, resultsDirectory);
                    SearchResultsWriterHTML.writeResultsToFile(results, field, query, resultsSize, resultsDirectory);
                    System.out.println("Results (.txt and .html) files has been saved successfully.");
                    System.out.println("Stored at: "+ resultsDirectory);
                } catch (IOException e) {
                    System.err.println("Error occurred while writing search results to file: " + e.getMessage());
                }



                boolean viewMoreResults = true;
                while (viewMoreResults) {
                    // Check if user wants to view more results
                    System.out.println("Do you want to view more results? (yes/no): ");
                    String viewMore = scanner.nextLine();
                    if (viewMore.equalsIgnoreCase("yes")) {
                        // pages-1 because currentPage variable is an index, pages is length
                        if (currentPage<pages-1){
                            // If we got more pages and the user wishes to check the next one
                            // Increase the number of our current page and display the results
//                                System.out.println("Pages Length: "+pages);
//                                System.out.println("Increasing current page number");
//                                System.out.println("Current Page before increment: "+currentPage);
                            currentPage++;
                            displayResults(results,currentPage);
                        }
                        else{
                            // If we reach the last page, exit this loop
                            System.out.println("You have reached the last page");
                            break;
                        }
                    }
                    else{
                        // If the user does not want to see more results, break the loop
                        break;
                    }
                }
            } else {
                System.out.println("No search results found.");
            }


            // Prompt user to continue or exit
            System.out.println("Do you want to perform another search? (yes/no): ");
            String continueSearch = scanner.nextLine();
            if (!continueSearch.equalsIgnoreCase("yes")) {
                exit = true;
            }

        }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String suggestImprovedQuery(String query, String field, LuceneSearch luceneSearch) {
        // Modifying the global variable
        Main.askSortByYear = false;
        String bestImprovedQuery = query;
        int maxResults = 0;
        // Not case-sensitive
        query = query.toLowerCase();

        try {
            // Search the initial query and get its result count
            luceneSearch.search(query, field);
            int initialResultCount = luceneSearch.getResultsSize();
            System.out.println("Total results for initial query '" + query + "': " + initialResultCount);

            // Set the initial query as the best query if it has the maximum results so far
            maxResults = initialResultCount;

        } catch (IOException | org.apache.lucene.queryparser.classic.ParseException e) {
            // Handle IOException or ParseException here
            e.printStackTrace();
        }

        // Generate improved queries by replacing multi-word synonyms
        for (Map.Entry<String, String[]> entry : synonymsMap.entrySet()) {
            String synonym = entry.getKey();
            String[] words = entry.getValue();

            // Check if the synonym is present in the query
            if (query.contains(synonym)) {
                // Replace the entire phrase with each alternative synonym
                for (String word : words) {
                    String improvedQuery = query.replace(synonym, word);
                    try {
                        luceneSearch.search(improvedQuery, field);
                        int resultCount = luceneSearch.getResultsSize();

                        System.out.println("Total results for query '" + improvedQuery + "': " + resultCount);
                        if (resultCount > maxResults) {
                            maxResults = resultCount;
                            bestImprovedQuery = improvedQuery;
                        }
                    } catch (IOException | org.apache.lucene.queryparser.classic.ParseException ex) {
                        // Handle IOException or ParseException here
                        ex.printStackTrace();
                    }
                }
            }
        }

        // Reset flag to true
        Main.askSortByYear = true;

        return bestImprovedQuery;
    }



    private static void displayAllResults(List<List<Document>> allResults) {
        // Iterate over each page
        for (int i = 0; i < allResults.size(); i++) {
            List<Document> results = allResults.get(i);

            // Display page number
            System.out.println("Page " + (i + 1) + ":");

            // Display results...
            for (Document doc : results) {
                System.out.println("Source ID: " + doc.get("source_id"));
                System.out.println("Year: " + doc.get("year"));
                System.out.println("Title: " + doc.get("title"));
                System.out.println("Full Text: " + doc.get("full_text"));
                System.out.println("-----------------------------------");
            }
        }
    }

    private static void displayResults(List<List<Document>> allResults, int currentPage) {
    // Iterate over each page

        List<Document> results = allResults.get(currentPage);

        // Display page number
        System.out.println("Page " + (currentPage + 1) + ":");

        // Display results...
        for (Document doc : results) {
            System.out.println("Source ID: " + doc.get("source_id"));
            System.out.println("Year: " + doc.get("year"));
            System.out.println("Title: " + doc.get("title"));
            System.out.println("Full Text: " + doc.get("full_text"));
            System.out.println("-----------------------------------");
        }
    }

    private static void createDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directory created: " + path);
            } else {
                System.err.println("Failed to create directory: " + path);
            }
        }
    }

}
