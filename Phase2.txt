Spilios Spiliopoulos : 4495 Kostanitnos Chatzopoulos : 1796

Project Programming Language: Python | Java

General Project Description:

The project's goal is to create a search engine that will be able to find the best results based on user's inputs.

The system will be able to achieve the best search results based on key words, relevancy, search history and synonym extension.

Machine learning could be integrated into the system in order to achieve the feature of autocomplete sentences, search propositions and more.

Though, without using machine learning, we were able to create a search engine that also provides related queries based on the search history of all previous users.

Moreover, the system is able to find the best query based on the total number of results from related queries.

Furthermore, the system is able to sort the results based on the year of the publication.

Last but not least, the results are presented in batches of 10 per page. Then the user selects to see more if he wants to.

The results are also saved in the local directory "Documents" in a .txt and .html format (All the results are saved, not just the batches of 10)

The words that are present in the results will be highlighted in the relevant field.



This system will be based on the open-source library "Lucene":

"Lucene Core is a Java library providing powerful indexing and search features, as well as spellchecking, hit highlighting and advanced analysis/tokenization capabilities. The PyLucene sub project provides Python bindings for Lucene Core."

In particular the dataset that will be used for this project is in the following format: ['source_id']['year']['title']['full_text'] Where 'source_id' : The unique id of each Paper 'year' : The publication year of the Paper 'title' : The title of the paper 'full_text' : The contents of the paper.

In order to collect our data we used the "All NeurIPS (NIPS) Papers" from Kaggle at @ https://www.kaggle.com/datasets/rowhitswami/nips-papers-1987-2019-updated/data?select=papers.csv With the python "papers_selection.py" script we collect the exact number of the Papers for our dataset (500 random samples).

Introduction: The goal of the system is to provide an efficient mechanism to search for Paper's articles and related information such as the year of publication, the title of the paper and the context of the paper.

Text analysis and index construction: The text analysis and index construction will be implemented by modules that Luceneis offering.

Search: Search will be implemented using the available Lucene features, such as query string search and field search as mentioned above.

Results Presentation: Results will be presented in pages with a list of Paper's matching the search query. Each Paper will include the title, year, and a snippet of the contents(articles), with keywords highlighted at the search field. In addition, the results will be grouped in batches of 10 based on a criterion such as 'title' or other based on the rest fields. An option of sorting the results will be given to the user as well as the order of the sorting ( Ascending or Descending).

****************************************************************

Please Check the 'requirements.txt' for the project requirements

****************************************************************