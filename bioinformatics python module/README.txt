Description:
This Python module accepts user inputs and returns information related to the name of
the inputted gene, drug, or phenotype. The module uses the splite3 and pandas libraries
to convert .tsv files into pandas DataFrames that then become tables in an SQL relational
database. The database is then queried to find relevant information about the user’s input.


To Run the Program:
Run main.py and follow the prompt displayed in the console. Use CTRL+D to end the program.
Ensure that all the .tsv files and the pharmGKB.db file is located in the project folder.


Required Data Sets:
All data sets were retrieved from https://www.pharmgkb.org/downloads and are included as 
.tsv files. Each spreadsheet is converted into a table and stored within the included 
pharmGKB.db, which is an SQL database. Any gene, drug, or phenotype found in PharmGKB's 
Database is a valid input.


Function Descriptions:
create_connection(db_name) - creates a connection with the given database

create_table(dbname, file_name, table_name) - creates a table in the given database provided 
a file_name and a table_name

init() - initializes the tables in the pharmGKB.db database

filter_result(result, phenotypes) - helper function to filter the result of a query and only 
add new diseases to the phenotypes list


get_gene_information(user_input, gene_id, cursor) - a helper function that gets information
related to the gene input. Finds information in terms of associated diseases, FDA approved
drugs, dosing information, side effects, and the genetic profile of the gene.


get_drug_information(user_input, drug_id, cursor) - a helper function that gets information
related to the drug input. Relevant information includes diseases the drug is intended to
address, dosing information, side effects, and the genetic profile.


get_phenotype_information(user_input, phenotype_id, cursor) - a helper function that gets
information related to the disease/phenotype input. Retrieves genes associated with the
disease and drugs intended to address the disease.


get_information(user_input) - determines if the user inputs a gene or drug and prints 
related information 

main() - main script that displays the prompt for the user as well as information about the 
input
