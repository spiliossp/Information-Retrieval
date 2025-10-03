import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.highlight.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class LuceneSearch{

    private final Directory indexDirectory;
    private int resultsSize;

    public LuceneSearch(String indexPath) throws IOException {
        this.indexDirectory = FSDirectory.open(Paths.get(indexPath));
    }


    public void indexCSVData(List<String[]> csvData) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        // Use CREATE_OR_APPEND to add new documents to an existing index
        config.setOpenMode(OpenMode.CREATE_OR_APPEND);

        try (IndexWriter indexWriter = new IndexWriter(indexDirectory, config)) {
            for (String[] row : csvData) {

                // Inside the indexCSVData method
//                System.out.println(row[0]);
//                System.out.println(row[1]);
//                System.out.println(row[2]);
//                System.out.println(row[3]);

                Document doc = new Document();
                doc.add(new TextField("source_id", row[0], Field.Store.YES));
                doc.add(new TextField("year", row[1], Field.Store.YES));
                doc.add(new TextField("title", row[2], Field.Store.YES));
                doc.add(new TextField("full_text", row[3], Field.Store.YES));
                indexWriter.addDocument(doc);
            }
        }
    }

    private boolean isValidSearchField(String searchField) {
        return searchField.equals("source_id") || searchField.equals("year") ||
                searchField.equals("title") || searchField.equals("full_text");
    }

    public List<List<Document>> search(String queryText, String searchField) throws IOException,ParseException {


        int resultsPerPage = 10;
        StandardAnalyzer analyzer = new StandardAnalyzer();
        if (!isValidSearchField(searchField)) {
            throw new IllegalArgumentException("Invalid search field. Please enter one of the following: source_id, year, title, full_text");
        }

        QueryParser queryParser = new QueryParser(searchField, analyzer);
        Query query = queryParser.parse(queryText);


        Set<Integer> uniqueDocIds = new HashSet<>(); // To ensure uniqueness of results

        // List of lists of documents where:
        // 1) The List of Lists represents the total pages
        // 2) the list of documents represents a page
        // 3) and the documents are the results for this page
        List<List<Document>> paginatedResults = new ArrayList<>();
        List<Document> allResults = new ArrayList<>();


        try (DirectoryReader reader = DirectoryReader.open(indexDirectory)) {
            IndexSearcher searcher = new IndexSearcher(reader);

            // Execute the search query to retrieve all documents
            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                int docId = Integer.parseInt(doc.get("source_id")); // Assuming source_id is the unique integer identifier
                if (!uniqueDocIds.contains(docId)) {
                    try {
                        String fieldValue = doc.get(searchField);

                        // Highlight the whole text instead of just the best fragment
                        QueryScorer queryScorer = new QueryScorer(query);
                        Highlighter highlighter = new Highlighter(queryScorer);
                        highlighter.setTextFragmenter(new SimpleFragmenter(Integer.MAX_VALUE)); // Highlight the whole text
                        String highlightedField = highlighter.getBestFragment(analyzer, searchField, fieldValue);

                        // Create a new document to store the highlighted field
                        Document highlightedDoc = new Document();
                        // Check if highlightedField is null, indicating no match or invalid token offsets
                        // Add the source_id field to the highlighted document
                        highlightedDoc.add(new TextField("source_id", doc.get("source_id"), Field.Store.YES));
                        // Add the year field to the highlighted document
                        highlightedDoc.add(new TextField("year", doc.get("year"), Field.Store.YES));
                        // Add the title field to the highlighted document
                        highlightedDoc.add(new TextField("title", doc.get("title"), Field.Store.YES));
                        // Add the full_text field to the highlighted document
                        highlightedDoc.add(new TextField("full_text", doc.get("full_text"), Field.Store.YES));
                        // Add the highlighted search field to the highlighted document
                        if (highlightedField != null) {
                            // Replace the content of the search field with the highlighted result
                            highlightedDoc.removeFields(searchField);
                            highlightedDoc.add(new TextField(searchField, highlightedField, Field.Store.YES));
                        }
                        allResults.add(highlightedDoc);
                        uniqueDocIds.add(docId);
                    } catch (InvalidTokenOffsetsException e) {
                        // Log the exception or handle it as appropriate for your application
                        System.err.println("Invalid token offsets encountered while highlighting document: " + e.getMessage());
                        // Optionally, you can skip this document and continue processing
                    }
                }
            }
        }

        // Set the size in order to retrieve it in main ( for file name )
        setResultsSize(allResults.size());

        // In order to avoid this question when we compare the suggested improved queries results length
        // We have a global flag that has the value of "false" only when we compare
        // Ask user if he wants to see the results sorted based on Year
        if (Main.askSortByYear == true){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Do you want to view the results sorted by year? (yes/no)");
            String sortedAns = scanner.nextLine();

            if (sortedAns.equalsIgnoreCase("yes")) {

                System.out.println("Enter 'A' (or 'a') for Ascending order or 'D' (or 'd') for Descending order (Not case sensitive)");
                String sortedOrder = scanner.nextLine();
                // In order to not have case-sensitive
                sortedOrder = sortedOrder.toLowerCase();

                if (sortedOrder.equals("a")){
                    System.out.println("Sorting with option: Ascending order");
                    allResults = sortResultsByYear(allResults,true);
                } else if (sortedOrder.equals("d")) {
                    System.out.println("Sorting with option: Descending order");
                    allResults = sortResultsByYear(allResults,false);
                }
                else{
                    System.out.println("Expected either 'a' or 'd'");
                    System.out.println("Sorting with the default option: Descending order");
                    allResults = sortResultsByYear(allResults,false);
                }

                System.out.println("Sorted results successfully");
            }
        }

        // Paginate the results
        int totalResults = allResults.size();
        int totalPages = (int) Math.ceil((double) totalResults / resultsPerPage);
        for (int page = 1; page <= totalPages; page++) {
            int startIndex = (page - 1) * resultsPerPage;
            int endIndex = Math.min(startIndex + resultsPerPage, totalResults);
            List<Document> currentPage = allResults.subList(startIndex, endIndex);
            paginatedResults.add(currentPage);
        }
        return paginatedResults;
    }

    // Get The number of the total results
    public int getResultsSize() {
        return resultsSize;
    }

    // Set The number of the total results
    public void setResultsSize(int size) {
        this.resultsSize = size;
    }

    // Sort results by year if needed
    public List<Document> sortResultsByYear(List<Document> results, boolean ascending) {
        Comparator<Document> comparator = Comparator.comparing(doc -> doc.get("year"));

        if (!ascending) {
            comparator = comparator.reversed();
        }

        results.sort(comparator);
        return results;
    }

}
