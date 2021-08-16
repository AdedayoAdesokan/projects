import sqlite3
from sqlite3 import Error
import pandas as pd


# creates a connection with the given database
def create_connection(db_name):
    conn = None
    try:
        conn = sqlite3.connect(db_name)     # try to connect to the database
    except Error as e:
        print(e)                            # if an error occurs, print the error
    finally:
        if conn:
            conn.close()                    # if the connection is successful, close it


# creates a table in the given database provided a file_name and a table_name
def create_table(dbname, file_name, table_name):
    data = ""                                       # create an empty string to hold the data
    fn = file_name                                  # the name of the file containing the information
    with open(fn, "r", encoding="utf-8") as f:      # open the file
        data = f.read()                             # read every line in the file and save it in the data string

    long_string = [x for x in data.split("\n")]     # split the string by newlines and store it in a list
    separated_data = []                             # create an empty list to hold the data
    for i in long_string:                           # iterate through the list
        row = i.split("\t")                         # split each element by tabs
        separated_data.append(row)                  # append the row to the list

    column_titles = []                  # create an empty list to hold the column titles
    for i in separated_data[0]:         # iterate through the 1st row of data
        title = i.replace(" ", "_")     # replace any spaces with underscores
        column_titles.append(title)     # add each column title to the list

    df = pd.DataFrame(
        separated_data[1:], columns=column_titles)  # convert the list to a pandas dataFrame

    create_connection(dbname)                       # create a connection with the database
    con = sqlite3.connect(dbname)                   # connect to the database
    df.to_sql(
        table_name, con, if_exists="replace")       # convert the dataFrame to a sql database
    con.close()  # close the connection


# initializes the tables in the database
def init():
    # create all of the necessary tables in the pharmGKB database
    create_table("pharmGKB.db", "genes.tsv", "genes")
    create_table("pharmGKB.db", "drugsWithDosing.tsv", "drugs")
    create_table("pharmGKB.db", "phenotypes.tsv", "phenotypes")
    create_table("pharmGKB.db", "relationships.tsv", "relationships")
    create_table("pharmGKB.db", "clinical_annotations.tsv", "clinicalAnnotations")
    create_table("pharmGKB.db", "clinical_ann_alleles.tsv", "clinicalAnnotationAlleles")
    create_table("pharmGKB.db", "chemicals.tsv", "chemicals")
    create_table("pharmGKB.db", "clinicalVariants.tsv", "clinicalVariants")


# helper function to filter the result of a query and only add new diseases to the phenotypes list
def filter_result(result, phenotypes):
    for i in result.fetchall():                 # iterate through the result
        if i[0] is None:                        # break if the result is None or empty
            break
        if i[0].__len__() < 1:
            break

        elif i[0].__contains__(","):            # if the result contains a comma
            results = i[0].split(",")           # split the result at the commas
            for x in results:                   # iterate through the results
                if x.__contains__(";"):         # if the result contains a semi-colon
                    results += x.split(";")     # split the result and add it to the list

            if phenotypes.__len__() > 0:                    # if the list of phenotypes is not empty
                for j in range(results.__len__()):          # iterate through the list
                    result_disease = results[j].lower()     # get the disease in the list in lowercase
                    for k in range(phenotypes.__len__()):   # iterate through the list of diseases

                        # if the last disease in the list doesn't equal the result, add the result to the list
                        if phenotypes[k].lower() != result_disease and k == phenotypes.__len__() - 1:
                            phenotypes.append(results[j])
                        elif phenotypes[k].lower() == result_disease:  # break if the result matches the disease
                            break
            else:                           # if the list is empty
                for j in results:           # add each result to the list of phenotypes
                    phenotypes.append(j)

        elif i[0].__contains__(";"):                        # if the result contains a semi-colon
            results = i[0].split(";")                       # split the result at the semi-colons
            for x in results:                               # iterate through the results
                if x.__contains__(","):                     # if the result contains a comma
                    results += x.split(",")                 # split the result and add it to the list
            if phenotypes.__len__() > 0:                    # if the list of phenotypes is not empty
                for j in range(results.__len__()):          # iterate through the list
                    result_disease = results[j].lower()     # get the disease in the list in lowercase
                    for k in range(phenotypes.__len__()):   # iterate through the list of diseases

                        # if the last disease in the list doesn't equal the result, add the result to the list
                        if phenotypes[k].lower() != result_disease and k == phenotypes.__len__() - 1:
                            phenotypes.append(results[j])
                        elif phenotypes[k].lower() == result_disease:  # break if the result matches the disease
                            break
            else:                           # if the list of phenotypes is empty
                for j in results:           # add each result to the list
                    phenotypes.append(j)

        else:  # if the result doesn't contain commas or semi-colons
            if phenotypes.__len__() > 0:                # if the list of phenotypes isn't empty
                result_disease = i[0].lower()           # get the result in lowercase
                for j in range(phenotypes.__len__()):   # iterate through the phenotypes

                    # if the last phenotype in the list doesn't match
                    if phenotypes[j].lower() != result_disease and j == phenotypes.__len__() - 1:
                        phenotypes.append(i[0])                         # add the result to the list
                    elif phenotypes[j].lower() == result_disease:       # break if the phenotype is already in the list
                        break
            else:  # if the list of phenotypes is empty
                phenotypes.append(i[0])  # add the result to the list


# get information related to the gene
def get_gene_information(user_input, gene_id, cursor):
    accession_id = gene_id[0][0]  # get the accession id of the gene

    # get the proper name of the gene for the user input
    gene_name = ""
    cmd = "SELECT Name FROM genes WHERE PharmGKB_Accession_Id = '{0}'".format(accession_id)
    result = cursor.execute(cmd)    # execute the query
    for i in result.fetchall():     # iterate through the result
        gene_name += str(i[0])      # save the gene name

    # print the name of the gene and the accession number
    print("  Gene Found: " + gene_name)
    print("  PharmGKB Accession Id: " + accession_id)

    # create lists to hold the drugs and diseases associated with the gene
    diseases = []
    chemicals = []

    # search the relationships database for any drugs or diseases related to the gene
    cmd = "SELECT Entity2_id, Entity2_type FROM relationships WHERE Entity1_id = '{0}'".format(accession_id)
    result = cursor.execute(cmd)    # execute the query
    for i in result.fetchall():     # iterate through the results
        if i[1] == 'Chemical':      # if the element is a chemical
            chemicals.append(i[0])  # add it to the list of chemicals
        elif i[1] == 'Disease':     # if the element is a disease
            diseases.append(i[0])   # add it to the list of diseases

    phenotypes = []                         # create a list to hold the phenotypes associated with the gene
    for i in range(diseases.__len__()):     # iterate through the diseases
        disease = diseases[i]               # get the disease at i

        # search the phenotype table for the diseases name
        cmd = "SELECT Name FROM Phenotypes WHERE PharmGKB_Accession_Id = '{0}'".format(disease)
        result = cursor.execute(cmd)    # execute the query
        for j in result.fetchall():     # iterate through the result
            phenotypes.append(j[0])

    # get the symbol of the gene
    gene_symbol = ""
    cmd = "SELECT Symbol FROM genes WHERE PharmGKB_Accession_Id = '{0}'".format(accession_id)
    result = cursor.execute(cmd)
    for i in result.fetchall():
        gene_symbol += str(i[0])

    # search clinicalAnnotations table for phenotypes associated to the gene
    cmd = "SELECT Phenotypes FROM clinicalAnnotations WHERE Gene = '{0}'".format(gene_symbol)
    result = cursor.execute(cmd)        # execute the command
    filter_result(result, phenotypes)   # filter the result to add new diseases to the list of phenotypes

    # search clinicalVariants table for phenotypes associated to the gene
    cmd = "SELECT phenotypes FROM clinicalVariants WHERE gene = '{0}'".format(gene_symbol)
    result = cursor.execute(cmd)        # execute the command
    filter_result(result, phenotypes)   # filter the result to add new diseases to the list of phenotypes

    associated_diseases = ""                            # create a string to hold the diseases
    if len(phenotypes) > 0:                             # if the list is not empty
        for i in range(phenotypes.__len__()):           # iterate through the phenotypes
            if i == phenotypes.__len__() - 1:           # if i is at the last index
                associated_diseases += phenotypes[i]    # don't add a comma after the last disease
            else:
                associated_diseases += phenotypes[i] + ", "  # add a comma after the disease
    else:
        associated_diseases += "N/A"                    # print N/A if the list is empty

    # print the associated diseases
    print("  Associated Diseases: " + associated_diseases)

    drugs = []              # a list to hold the drug names
    associated_drugs = ""   # a string to hold the drug names
    if len(chemicals) < 1:          # if the list is empty
        associated_drugs += "N/A"   # add N/A to the output
    else:                                       # if the list isn't empty
        for i in range(chemicals.__len__()):    # iterate through the list of chemicals
            drug = chemicals[i]                 # get the chemical at i

            # search the drugs table for drugs that match the accession id
            cmd = "SELECT Name FROM Drugs WHERE PharmGKB_Accession_Id = '{0}'".format(drug)
            result = cursor.execute(cmd)            # execute the query
            for j in result.fetchall():             # iterate through the result
                if i == chemicals.__len__() - 1:    # if this is the last drug
                    associated_drugs += str(j[0])   # add the drug to the string without a comma
                    drugs.append(j[0])              # add the drug to the list
                else:                                       # if this isn't the last drug in the result
                    associated_drugs += str(j[0]) + ", "    # add the drug to the string with a comma
                    drugs.append(j[0])                      # add the drug to the list

    print("  FDA Approved Drugs: " + associated_drugs)  # print the string of approved drugs
    for i in drugs:                                     # iterate through the list of drugs
        print("   - " + str(i))                         # print the name of the drug indented

        # search the drugs table for side effect information
        cmd = "SELECT Side_Effects FROM drugs WHERE Name = '{0}'".format(i)
        result = cursor.execute(cmd)    # execute the query
        for j in result:                # iterate through the result
            side_effect = j[0]          # get the side effect
            if side_effect == "":       # if it is empty
                print("      - Side Effects: N/A")              # print an N/A
            else:
                print("      - Side Effects: " + side_effect)   # else, print the side effect

        # search the drug table for side effect information
        cmd = "SELECT Dosing_Information FROM drugs WHERE Name = '{0}'".format(i)
        result = cursor.execute(cmd)    # execute the query
        for j in result:                # iterate through the result
            dosing = j[0]               # get the dosing info
            if dosing == "":            # if it is empty
                print("      - Dosing Information: N/A")        # print N/A
            else:
                print("      - Dosing Information: " + dosing)  # else, print the dosing information

    gene_clinical_id = []  # create a list to hold the clinical annotation associated to the gene
    # search the clinicalAnnotations table for the clinical annotations about the gene
    cmd = "SELECT Clinical_Annotation_ID FROM clinicalAnnotations WHERE Gene = '{0}'".format(gene_symbol)
    result = cursor.execute(cmd)            # execute the query
    for i in result.fetchall():             # iterate through the result
        if i[0] not in gene_clinical_id:    # if the id of the annotation is not in the list yet
            gene_clinical_id.append(i[0])   # add the id to the list

    if len(gene_clinical_id) < 1:           # if there are no clinical annotations for the gene
        print("  Genetic Profile: N/A")     # print N/A
    else:                                   # else
        print("  Genetic Profile:")         # print genetic profile
        # search the clinicalAnnotationAlleles for the gene's genetic profile
        count = 0                           # keep count of the annotations printed
        for i in gene_clinical_id:
            cmd = "SELECT Annotation_Text FROM clinicalAnnotationAlleles WHERE Clinical_Annotation_ID = '{0}'".format(i)
            result = cursor.execute(cmd)            # execute the command
            for j in result:                        # iterate through the result
                if count < 10:                      # only print the 1st 10 annotations
                    print("    - " + str(j[0]))     # print the annotation
                    count += 1                      # increment count
                else:                               # if 10 annotations have been printed
                    break                           # break


# get information related to the drug
def get_drug_information(user_input, drug_id, cursor):
    accession_id = drug_id[0][0]  # get the accession id of the drug
    drug_name = ""

    # get the proper name of the drug from the user's input
    cmd = "Select Name FROM drugs where PharmGKB_Accession_Id = '{0}'".format(accession_id)
    result = cursor.execute(cmd)    # execute the command
    for i in result.fetchall():     # iterate through the result
        drug_name += str(i[0])      # save the name of the drug

    # print the name of the drug and accession id
    print("  Drug Found: " + drug_name)
    print("  PharmGKB Accession Id: " + accession_id)
    diseases = []  # a list to hold the associated disease accession ids

    # search the relationships table for diseases delated to the drug
    cmd = "SELECT Entity2_id, Entity2_type FROM relationships WHERE Entity1_id = '{0}'".format(accession_id)
    result = cursor.execute(cmd)    # execute the query
    for i in result.fetchall():     # iterate through the results
        if i[1] == 'Phenotype':     # if the element is a disease
            diseases.append(i[0])   # add it to the list of chemicals

    phenotypes = []  # create an empty list to hold the names of the diseases
    for i in range(diseases.__len__()):     # iterate through the diseases
        disease = diseases[i]               # get the name of the disease

        # search the phenotypes table for phenotypes with names that match the disease
        cmd = "SELECT Name FROM phenotypes WHERE Accession_ID = '{0}'".format(disease)
        result = cursor.execute(cmd)        # execute the query
        for j in result.fetchall():         # iterate through the result
            if j[0] not in phenotypes:      # if the disease is not in the list yet
                phenotypes.append(j[0])     # add the disease to the list

    # search the clnicalAnnotations table for phenotypes with names that match the disease
    cmd = "SELECT Phenotypes FROM clinicalAnnotations WHERE Drugs = '{0}'".format(drug_name)
    result = cursor.execute(cmd)        # execute the query
    filter_result(result, phenotypes)   # filter the result to add new diseases to the list of phenotypes

    # search the clinicalVariants table for phenotypes with names that match the disease
    cmd = "SELECT phenotypes FROM clinicalVariants WHERE chemicals = '{0}'".format(drug_name)
    result = cursor.execute(cmd)        # execute the query
    filter_result(result, phenotypes)   # filter the result to add new diseases to the list of phenotypes

    # search the drugs table for phenotypes with names that match the disease
    cmd = "SELECT Disease_Drug_Addresses FROM drugs WHERE Name = '{0}'".format(drug_name)
    result = cursor.execute(cmd)        # execute the query
    filter_result(result, phenotypes)   # filter the result to add new diseases to the list of phenotypes

    associated_phenotypes = ""          # create an empty string to hold the phenotypes

    # check if the list is empty, if so print N/A
    if len(phenotypes) < 1:
        associated_phenotypes += "N/A"
    else:                                               # if the list of diseases isn't empty
        for i in range(phenotypes.__len__()):           # iterate through the phenotypes
            if i == phenotypes.__len__() - 1:           # if i is at the last index
                associated_phenotypes += phenotypes[i]  # don't add a comma after the last disease
            else:                                               # if the this isn't the last element in the list
                associated_phenotypes += phenotypes[i] + ", "  # add a comma after the disease

    # print the associated diseases
    print("    - Associated Diseases/Uses of Drug: " + associated_phenotypes)

    # search the drugs table for side effect information
    cmd = "SELECT Side_Effects FROM drugs WHERE Name = '{0}'".format(drug_name)
    result = cursor.execute(cmd)                        # execute the query
    for i in result.fetchall():                         # iterate through the result
        side_effect = i[0]                              # get the side effect
        if side_effect == "":                           # if there's no side effect information
            print("    - Side Effects: N/A")            # print an N/A
        else:
            print("    - Side Effects: " + side_effect)     # else, print the side effect

    # get the dosing information associated with the drug
    dosing_info = ""
    cmd = "SELECT Dosing_Information FROM drugs WHERE Name = '{0}'".format(drug_name)
    result = cursor.execute(cmd)    # execute the command
    for i in result.fetchall():     # iterate through the result
        if i[0] == "":              # if the string is empty
            dosing_info += "N/A"    # print N/A
        else:                       # if the string isn't empty
            dosing_info += i[0]     # add the string to the dosing information

    # print the dosing information
    print("    - Dosing Information: " + dosing_info)

    drug_clinical_id = []   # create a list to hold the clinical annotation ids
    # search the clinicalAnnotations table for the clinical annotations about the drug
    cmd = "SELECT Clinical_Annotation_ID FROM clinicalAnnotations WHERE Drugs = '{0}'".format(drug_name)
    result = cursor.execute(cmd)            # execute the command
    for i in result.fetchall():             # iterate through the result
        if i[0] not in drug_clinical_id:    # if the clinical id is not in the list yet
            drug_clinical_id.append(i[0])   # add the id to the list

    if len(drug_clinical_id) < 1:               # if there are no annotations for the drug
        print("    - Genetic Profile: N/A")     # print N/A
    else:                                       # if there are annotations for the drug
        print("    - Genetic Profile:")         # print genetic profile
        # search the clinicalAnnotationAlleles for the drug's genetic profile
        count = 0                   # keep track of the amount of printed annotations
        for i in drug_clinical_id:  # iterate through the clinical ids
            # search the clinicalAnnotationAlleles table for annotations about the drug
            cmd = "SELECT Annotation_Text FROM clinicalAnnotationAlleles WHERE Clinical_Annotation_ID = '{0}'".format(i)
            result = cursor.execute(cmd)            # execute the command
            for j in result:                        # iterate through the result
                if count < 10:                      # only print the 1st 10 annotations
                    print("      - " + str(j[0]))   # print the annotation
                    count += 1                      # increment count
                else:                               # if 10 annotations have been printed
                    break                           # break the loop


# gets information about the disease / phenotype the user inputted
def get_phenotype_information(user_input, phenotype_id, cursor):
    for i in phenotype_id:      # iterate through each result

        # get the name of the phenotype from the accession number
        cmd = "SELECT Name FROM phenotypes WHERE PharmGKB_Accession_Id = '{0}'".format(i[0])
        result = cursor.execute(cmd)    # execute the command
        for j in result.fetchall():     # iterate through the result

            # print the found disease
            print("  - Disease Found: " + str(j[0]))
            print("  - PharmGKB Accession Id: " + str(i[0]))
            disease_name = j[0]     # get the disease's name
            accession_id = i[0]     # get the disease's id

            # create lists to hold associated drugs and genes
            drugs = []
            genes = []

            # search the relationships table for drugs and genes associated to the disease
            cmd = "SELECT Entity2_name, Entity2_type FROM relationships WHERE Entity1_id = '{0}'".format(accession_id)
            result = cursor.execute(cmd)    # execute the command
            for k in result.fetchall():     # iterate through the result
                if k[1] == 'Chemical':      # if the type of entity is a chemical
                    drugs.append(k[0])      # add the name to the list of drugs
                elif k[1] == 'Gene':        # if the type of entity is a gene
                    genes.append(k[0])      # add the name to the list of genes

            # search the clinicalVariants table for drugs associated to the disease
            cmd = "SELECT chemicals FROM clinicalVariants WHERE phenotypes = '{0}'".format(disease_name)
            result = cursor.execute(cmd)    # execute the command
            filter_result(result, drugs)    # filter the result to only add new drugs to the list

            # search the clinicalVariants table for genes associated to the disease
            cmd = "SELECT gene FROM clinicalVariants WHERE phenotypes = '{0}'".format(disease_name)
            result = cursor.execute(cmd)    # execute the command
            filter_result(result, genes)    # filter the result to only add new genes to the list

            # search the clinicalAnnotations table for drugs associated to the disease
            cmd = "SELECT Drugs FROM clinicalAnnotations WHERE Phenotypes = '{0}'".format(disease_name)
            result = cursor.execute(cmd)    # execute the command
            filter_result(result, drugs)    # filter the result to only add new drugs to the list

            # search the clinicalAnnotations table for genes associated to the disease
            cmd = "SELECT Gene FROM clinicalAnnotations WHERE Phenotypes = '{0}'".format(disease_name)
            result = cursor.execute(cmd)    # execute the command
            filter_result(result, genes)    # filter the result to only add new genes to the list

            # add all the associated genes to a string and print it
            associated_genes = ""                       # a string to hold the name of associated genes
            if len(genes) == 0:                         # if no associated genes were found
                associated_genes += "N/A"               # print N/A
            else:                                       # if associated genes were found
                for k in range(genes.__len__()):        # iterate through the list of genes
                    if k == genes.__len__() - 1:        # if this is the last gene in the list
                        associated_genes += genes[k]    # add it to the string without a comma
                    else:                                       # if this isn't the last string in the list
                        associated_genes += genes[k] + ", "     # add the gene to the string with a comma

            # print the list of associated genes
            print("    - Genes Associated to " + disease_name + ": " + associated_genes)

            # add all the associated drugs to a string and print it
            associated_drugs = ""               # create a string to hold the name of associated drugs
            if len(drugs) == 0:                 # if no drugs were found
                associated_drugs += "N/A"       # print N/A
            else:                                       # if associated drugs were found
                for k in range(drugs.__len__()):        # iterate through the list of drugs
                    if k == drugs.__len__() - 1:        # if this is the last drug in the list
                        associated_drugs += drugs[k]    # add the drug to the string without a comma
                    else:                                       # if this isn't the last drug in the gene
                        associated_drugs += drugs[k] + ", "     # add the drug to the string with a comma

            # print the string of associated drugs
            print("    - Drugs Associated to " + disease_name + ": " + associated_drugs)

            # iterate through the list of drugs
            for drug_name in drugs:
                print("      - Drug Found for " + disease_name + ": " + drug_name)    # print the found drug

                # search for dosing information about the drug and print the result
                cmd = "SELECT Dosing_Information FROM drugs WHERE Name = '{0}'".format(drug_name)
                result = cursor.execute(cmd)    # execute the command
                for m in result.fetchall():     # iterate through the list
                    if m[0] == "":              # if the string is empty
                        print("        - Dosing Information for " + drug_name + ": N/A")        # print N/A
                    else:                                                                       # if there's dosing info
                        print("        - Dosing Information for " + drug_name + ": " + m[0])    # print the dosing info

                clinical_id = []    # create a list to hold clinical annotation ids
                # search for clinical annotations about the drug
                cmd = "SELECT Clinical_Annotation_ID FROM clinicalAnnotations WHERE Drugs = '{0}'".format(drug_name)
                result = cursor.execute(cmd)        # execute the command
                for m in result.fetchall():         # iterate through the result
                    if m[0] not in clinical_id:     # if the annotation's id isn't in the list yet
                        clinical_id.append(m[0])    # add the id to the list

                if len(clinical_id) < 1:            # if no clinical annotations were found
                    print("        - Genetic Profiles Related to " + drug_name + ": N/A")   # print N/A
                else:                                                                       # if annotations were found
                    print("        - Genetic Profiles Related to " + drug_name + ":")       # print the drug's name
                    # search for the drug's genetic profile and print the result
                    count = 0               # keep track of the amount of annotations printed
                    for m in clinical_id:   # iterate through the annotations
                        # search the clinicalAnnotationAlleles table for annotations about the phenotype
                        cmd = "SELECT Annotation_Text FROM clinicalAnnotationAlleles " \
                              "WHERE Clinical_Annotation_ID = '{0}'".format(m)
                        result = cursor.execute(cmd)                # execute the command
                        for n in result.fetchall():                 # iterate through the result
                            if count < 10:                          # only print 10 of the annotations
                                print("          - " + str(n[0]))   # print the genetic profile
                                count += 1                          # increment the count
                            else:                                   # if 10 annotations have been printed
                                break                               # break the loop


# determines if the user inputs a gene or drug and prints related information
def get_information(user_input):
    conn = sqlite3.connect("pharmGKB.db")   # connect to the database
    cursor = conn.cursor()                  # create a cursor

    user_input = user_input.strip()         # strip the input of leading/trailing whitespaces
    # search for a gene that matches the given input
    gene_cmd = "SELECT PharmGKB_Accession_Id FROM genes WHERE NAME LIKE '{0}' OR " \
               "Symbol LIKE '{0}' OR Alternate_Names LIKE '{0}' OR Alternate_Symbols LIKE '{0}'".format(user_input)
    result = cursor.execute(gene_cmd)   # execute the query
    gene_id = result.fetchall()         # get the result of the query

    # search for a drug that matches the given input
    drug_cmd = "SELECT PharmGKB_Accession_Id FROM drugs " \
               "WHERE Name LIKE '{0}' OR Generic_Names LIKE '{0}' OR Trade_Names LIKE '{0}'".format(user_input)
    result = cursor.execute(drug_cmd)   # execute the query
    drug_id = result.fetchall()         # get the result of the query

    # search for a chemical that matches the given input
    chemical_cmd = "SELECT PharmGKB_Accession_Id FROM chemicals " \
                   "WHERE Name LIKE '{0}' OR Generic_Names LIKE '{0}' OR Trade_Names LIKE '{0}'".format(user_input)
    result = cursor.execute(chemical_cmd)   # execute the query
    chemical_id = result.fetchall()         # get the result of the query

    phenotype_cmd = "SELECT PharmGKB_Accession_Id FROM phenotypes " \
                    "WHERE Name LIKE '%{0}%' OR Alternate_Names LIKE '%{0}%'".format(user_input)
    result = cursor.execute(phenotype_cmd)  # execute the query
    phenotype_id = result.fetchall()        # get the result of the query

    # if a gene was found
    if len(gene_id) > 0:
        get_gene_information(user_input, gene_id, cursor)   # get the related information about the gene

    # if a drug was found
    elif len(drug_id) > 0:
        get_drug_information(user_input, drug_id, cursor)   # get the related information about the drug

    # if a chemical was found
    elif len(chemical_id) > 0:
        # indicate that the module doesn't return info about chemicals
        print("Chemical Found, Please Input a Gene or Drug")

    # if a phenotype is found
    elif len(phenotype_id) > 0:
        get_phenotype_information(user_input, phenotype_id, cursor)     # get related information about the phenotype

    # if nothing was found
    else:
        # print a message for the user
        print("No Gene, Drug, or Phenotype found for input: " + user_input)

    conn.close()    # close the connection


# main script that displays the prompt for the user as well as information about the input
def main():
    # init()                                                        # initialize the database
    prompt = "Enter the name of a gene, drug, or phenotype: "       # the prompt displayed for the user
    done = False                                                    # set done to false

    try:
        user_input = input(prompt)                  # try to read the users input
    except EOFError:                                # when CTRL+D is entered
        print()                                     # skip a line
        print("Thank you for using our system!")    # print a thank you message
        done = True                                 # set done to true

    while not done:                                 # while done is false
        get_information(user_input)                 # get information about the user input
        print()                                     # skip a line
        try:
            user_input = input(prompt)              # try to read the users input
        except EOFError:                            # when CTRL+D is entered
            print()                                     # skip a line
            print("Thank you for using our system!")    # print a thank you message
            done = True                                 # set done to true


# run the main program
if __name__ == '__main__':
    main()


