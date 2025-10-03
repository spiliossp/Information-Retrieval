<<<<<<< HEAD
import pandas as pd
import numpy as np

np.random.seed(17)

df = pd.read_csv('Data/papers.csv')

# print(df.head())
# print(len(df))


list = []

while len(list) < 500:
    random_number = np.random.randint(0,len(df))
    if not list.__contains__(random_number):
        list.append(random_number)

unique_elements = []
[unique_elements.append(x) for x in list  if x not in unique_elements]
print('Length of unique elements: '+str(len(unique_elements)))



df_papers = pd.DataFrame()

while len(df_papers) < 500:
    random_number = list.pop(0)
    # print('Row:'+str(df.iloc[random_number]))
    random_paper = df.iloc[random_number]
    df_papers = pd.concat([df_papers, random_paper.to_frame().transpose()])  # Append the song data to df_songs

# print(df_papers.head())
# print(len(df_papers))
# print(df_papers.columns)


df_papers = df_papers.drop('abstract', axis=1)

# Sort the DataFrame based on the 'source_id' column
df_papers = df_papers.sort_values(by='source_id')

df_papers = df_papers.reset_index(drop=True)

# print(df_papers.head())
# print(df_papers.columns)
print(df_papers.iloc[0]['full_text'])

# Save DataFrame to a specific directory
# df_papers.to_excel('MetaData/papers.xlsx', index=False)

# Save DataFrame to CSV with specific delimiter and encoding
df_papers.to_csv('MetaData/papers.csv', index=False, encoding='utf-8')
=======
import pandas as pd
import numpy as np

np.random.seed(17)

df = pd.read_csv('Data/papers.csv')

# print(df.head())
# print(len(df))


list = []

while len(list) < 500:
    random_number = np.random.randint(0,len(df))
    if not list.__contains__(random_number):
        list.append(random_number)

unique_elements = []
[unique_elements.append(x) for x in list  if x not in unique_elements]
print('Length of unique elements: '+str(len(unique_elements)))



df_papers = pd.DataFrame()

while len(df_papers) < 500:
    random_number = list.pop(0)
    # print('Row:'+str(df.iloc[random_number]))
    random_paper = df.iloc[random_number]
    df_papers = pd.concat([df_papers, random_paper.to_frame().transpose()])  # Append the song data to df_songs

# print(df_papers.head())
# print(len(df_papers))
# print(df_papers.columns)


df_papers = df_papers.drop('abstract', axis=1)

# Sort the DataFrame based on the 'source_id' column
df_papers = df_papers.sort_values(by='source_id')

df_papers = df_papers.reset_index(drop=True)

# print(df_papers.head())
# print(df_papers.columns)
print(df_papers.iloc[0]['full_text'])

# Save DataFrame to a specific directory
# df_papers.to_excel('MetaData/papers.xlsx', index=False)

# Save DataFrame to CSV with specific delimiter and encoding
df_papers.to_csv('MetaData/papers.csv', index=False, encoding='utf-8')
>>>>>>> 87cb683f7679e0f5ebaf9384087d4bfbd8232060
# Enjoy