# ID3-algorithm
This is the implementation of the ID3 Algorithm that will give a decision tree as it's output. The program will first create a decision tree based on a heuristic that will decide which attributes of the data to select first.
In this implementation I have implemented 2 different heuristics for creating a decision tree:
a. Gain heuristic
b. Variance Impurity heuristic

Based on both the heuristics, the program will create two trees using the Training data provided by the user. After that the program will prune the tree based on the Validation data provided by the user.

The output of the program will be four different trees and their accuracies based on the Test data provided by the user.

HOW TO RUN:

This folder already contains the data files named: 
	"dataset_1_training_data.csv", "dataset_1_validation_data.csv", "dataset_1_test_data.csv"
	"dataset_2_training_data.csv", "dataset_2_validation_data.csv", "dataset_2_test_data.csv"

	(If the program needs to be tested on some other input, PLEASE COPY THE FILES TO THE "ID3 Implementation" FOLDER. CSV files need to be in the same folder.)
	
Open the Command Prompt (Windows) / Terminal (Linux / Unix)
 
Navigate to the folder "ID3 Implementation"
 
Execute command:

	javac ID3Algorithm.java

The format for the next command is:

	java ID3Algorithm <L> <K> <TRAINING_DATA_FILENAME> <VALIDATION_DATA_FILENAME> <TEST_DATA_FILENAME> <TO_PRINT?>

	Where:
	<L> : Value of L
	<K> : Value of K
	<TRAINING_DATA_FILENAME> : Name of the file to be passed as training data
	<VALIDATION_DATA_FILENAME> : Name of the file to be passed as validation data
	<TEST_DATA_FILENAME> : Name of the file to be passed as test data
	<TO_PRINT?> : Will have values "Yes" / "No" --> If you want to print the decision trees or not
	

SAMPLE INPUTS:

1. If you want to execute the algorithm on DataSet 1

	java ID3Algorithm 200 20 dataset_1_training_data.csv dataset_1_validation_data.csv dataset_1_test_data.csv yes
	java ID3Algorithm 200 20 dataset_1_training_data.csv dataset_1_validation_data.csv dataset_1_test_data.csv no

2. If you want to execute the algorithm on DataSet 2

	java ID3Algorithm 200 20 dataset_2_training_data.csv dataset_2_validation_data.csv dataset_2_test_data.csv yes
	java ID3Algorithm 200 20 dataset_2_training_data.csv dataset_2_validation_data.csv dataset_2_test_data.csv no
	
	
NOTE: FOR CUSTOM INPUT FILES, PLEASE MAKE SURE THAT THE INPUT FILES ARE IN THE SAME FOLDER AS THE JAVA FILES.
NOTE: JUST THE FILE NAMES TO BE GIVEN IN THE INPUT AND NOT THE ENTIRE PATH
