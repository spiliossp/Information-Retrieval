import pandas as pd

df = pd.read_csv('papers.csv')

print(df.head())
print(df.columns)
# df.drop('Unnamed: 0',axis=1, inplace=True)

print(df.head())

## Remove Empty Lines
c = 0 
for i in df['full_text']:
    # print(type(i))
    if type(i) != float:
        i = i.replace('\n', ' ')
    df.loc[c,'full_text'] = i
    c = c+ 1

# We can set the source_id as index as well 
# df.set_index('source_id',inplace=True)
# print(df.head())

# Drop Nan Values
# df.dropna(inplace=True)

## No need to convert it everytime
# df.to_csv('papers_cleaned.csv')



# print(df.head())
print(df.columns)

# print(df.__len__)
print('Done')