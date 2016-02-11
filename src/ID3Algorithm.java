
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Hardik
 */
public class ID3Algorithm {

    public static final String currentDirectory = System.getProperty("user.dir");
    public static final String dataSet1TrainingDataFileName = "dataset_1_training_set.csv";
    public static final String dataset1ValidationDataFileName = "dataset_1_validation_set.csv";
    public static final String dataset1TestDataFileName = "dataset_1_test_set.csv";
    public static final String dataSet2TrainingDataFileName = "dataset_2_training_set.csv";
    public static final String dataset2ValidationDataFileName = "dataset_2_validation_set.csv";
    public static final String dataset2TestDataFileName = "dataset_2_test_set.csv";
    public static final String dayDataFileName = "day_set.csv";
    private static DataTable data;
    public static final String POSITIVE_INPUT = "1";
    public static final String NEGATIVE_INPUT = "0";
    public static final String CLASS_ATTRIBUTE_NAME = "Class";

    public static void main(String[] args) {

        try {
            //Reading from the CSV File
            BufferedReader csvReader = new BufferedReader(new FileReader(new File(currentDirectory + "/" + dataSet1TrainingDataFileName)));
            //Reading the attribute Names
            String line = csvReader.readLine();
            data = new DataTable(line.split(","));
            //Reading the rest of the data
            line = csvReader.readLine();
            while (line != null) {
                if (data.add(line.split(",")) == -1) {
                    throw new IOException();
                }
                line = csvReader.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ID3Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ID3Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
//        data.displayTable();
//        System.out.println(data.getRecords().size());

        ArrayList<String> attributes = new ArrayList<>();
        for (String attribute : data.getAttributeNames()) {
            if (!attribute.equals(CLASS_ATTRIBUTE_NAME)) {
                attributes.add(attribute);
            }
        }
        System.out.println("Creating ID3 using Gain heuristic");
        Node gainHeuristicRootNode = executeAlgorithmForGainHeuristic(data, attributes);
        System.out.println("Creating ID3 using Variance Impurity Heuristic");
        Node varianceImpurityRootNode = executeAlgorithmForVarianceImpurityHeuristic(data, attributes);

        System.out.println("Decision Tree for Gain Heuristic=============================>");
        displayTree(gainHeuristicRootNode, 0);
//        System.out.println(countNonLeafNodes(gainHeuristicRootNode));

        System.out.println("Decision Tree for Variance Impurity Heuristic=============================>");
        displayTree(varianceImpurityRootNode, 0);
//        System.out.println(countNonLeafNodes(varianceImpurityRootNode));

//        displayTree(rootNode, 0);
        //Pruning the tree
        //TODO: Input for pruning the tree
        try {
            //Reading from the CSV File
            BufferedReader csvReader = new BufferedReader(new FileReader(new File(currentDirectory + "/" + dataset1ValidationDataFileName)));
            //Reading the attribute Names
            String line = csvReader.readLine();
            data = new DataTable(line.split(","));
            //Reading the rest of the data
            line = csvReader.readLine();
            while (line != null) {
                if (data.add(line.split(",")) == -1) {
                    throw new IOException();
                }
                line = csvReader.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ID3Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ID3Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        }

        int l = 100, k = 100;
        System.out.println("Executing pruning for gain tree");
        Node gainHeuristicRootNodeBest = executePostPruning(l, k, gainHeuristicRootNode, data);
        System.out.println("Executing pruning for variance impurity tree");
        Node varianceImpurityRootNodeBest = executePostPruning(l, k, varianceImpurityRootNode, data);

//        System.out.println("Tree after post pruning");
//        displayTree(rootNodeBest, 0);
        try {
            //Reading from the CSV File
            BufferedReader csvReader = new BufferedReader(new FileReader(new File(currentDirectory + "/" + dataset1TestDataFileName)));
            //Reading the attribute Names
            String line = csvReader.readLine();
            data = new DataTable(line.split(","));
            //Reading the rest of the data
            line = csvReader.readLine();
            while (line != null) {
                if (data.add(line.split(",")) == -1) {
                    throw new IOException();
                }
                line = csvReader.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ID3Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ID3Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
        double gainHeuristicAccuracyBeforePruning = getTreeAccuracy(gainHeuristicRootNode, data);
        System.out.println("Accuracy of the Gain Heuristic tree before pruning for test data is====>" + gainHeuristicAccuracyBeforePruning);
        double gainHeuristicAccuracyAfterPruning = getTreeAccuracy(gainHeuristicRootNodeBest, data);
        System.out.println("Accuracy of the Gain Heuristic pruned tree for test data is====>" + gainHeuristicAccuracyAfterPruning);

        double varianceImpurityAccuracyBeforePruning = getTreeAccuracy(varianceImpurityRootNode, data);
        System.out.println("Accuracy of the Variance Impurity Heuristic tree before pruning for test data is====>" + varianceImpurityAccuracyBeforePruning);
        double varianceImpurityAccuracyAfterPruning = getTreeAccuracy(varianceImpurityRootNodeBest, data);
        System.out.println("Accuracy of the Variance Impurity Heuristic pruned tree for test data is====>" + varianceImpurityAccuracyAfterPruning);

    }

    public static Node executePostPruning(int l, int k, Node rootNode, DataTable data) {
        Node dBest = rootNode;
        double dBestAccuracy = 0;

        Random random = new Random();
        for (int i = 1; i <= l; i++) {
            dBestAccuracy = getTreeAccuracy(dBest, data);
            Node dDash = copyTree(dBest);
            int m = random.nextInt(k) + 1;       //+1 as nextInt returns 0 inclusive and k exclusive
            for (int j = 1; j <= m; j++) {
                int n = countNonLeafNodes(dDash);
                assignNonLeafNodesNumbers(dDash, 1);
                int p = random.nextInt(n) + 1;
                dDash = replaceSubtree(dDash, p);
            }
            double dDashAccuracy = getTreeAccuracy(dDash, data);
            if (dDashAccuracy > dBestAccuracy) {
//                System.out.println("D' accuracy = "+dDashAccuracy);
//                System.out.println("DBest accuracy = "+dBestAccuracy);
                dBest = dDash;
//                System.out.println("D' has better accuracy. Accuracy on the validation set= " + dDashAccuracy);
            }
        }
        return dBest;
    }

    public static Node executeAlgorithmForGainHeuristic(DataTable data, ArrayList<String> attributes) {

//        System.out.println("Hello");
        Node node = null;

        ArrayList<String> attributeList = new ArrayList<>();
        for (String attribute : attributes) {
            attributeList.add(attribute);
        }

        //If data contains all positive then return new node with positive label or negative accordingly
        ArrayList<ArrayList<String>> records = data.getRecords();
        int positiveCount = 0, negativeCount = 0;
        for (ArrayList<String> record : records) {
            String val = record.get(record.size() - 1);
            if (val.equals(NEGATIVE_INPUT)) {
                negativeCount++;
            } else {
                positiveCount++;
            }
        }
        if (positiveCount == records.size()) {
            node = new Node(data);
            node.setLabel(POSITIVE_INPUT);
            return node;
        } else if (negativeCount == records.size()) {
            node = new Node(data);
            node.setLabel(NEGATIVE_INPUT);
            return node;
        }

        //check if attribute list is empty 
        if (attributeList.isEmpty()) {
            //Return the node with label as max occurence
            if (positiveCount > negativeCount) {
                node = new Node(data);
                node.setLabel(POSITIVE_INPUT);
                return node;
            } else {
                node = new Node(data);
                node.setLabel(NEGATIVE_INPUT);
                return node;
            }
        }

        //Calculating gain for all the attributes not in the arrtibute list
        double entropy = data.calculateEntropy();
//        System.out.println("Entropy = "+entropy);

//        System.out.println(entropy);
        //Calculate information gain for each attribute
        double s = data.getRecords().size();
        HashMap<String, Double> gains = new HashMap<>();
        for (String attribute : attributeList) {
//            System.out.println("Attribute========================>"+attribute);
            ArrayList<String> values = data.getAttributeValues(attribute);
            double gain = entropy;
            for (String value : values) {
                double sv = 0;
                DataTable svData = new DataTable();
                for (ArrayList<String> record : data.getRecords()) {
                    if (record.get(data.getAttributeNames().indexOf(attribute)).equals(value)) {
                        svData.getRecords().add(record);
                        sv++;
                    }
                }

                double svEntropy = svData.calculateEntropy();
//                System.out.println("SVEntropy====>"+svEntropy);

//                System.out.println("sv/s===>"+(sv/s));
//                System.out.println("*entropy==>"+((sv/s)*svEntropy));
                gain = gain - ((sv / s) * svEntropy);
//                svData.displayTable();
            }
            gains.put(attribute, gain);
        }

//        System.out.println(gains);
        //Check which attribute has the highest gain
        double maxGain = Double.MIN_VALUE;
        String maxAttribute = null;
        for (Map.Entry<String, Double> gainEntry : gains.entrySet()) {
            if (gainEntry.getValue() > maxGain) {
                maxGain = gainEntry.getValue();
                maxAttribute = gainEntry.getKey();
            }
        }
        
//        System.out.println("Max Gain===>"+maxGain);

        if (maxAttribute == null) {
            if (positiveCount > negativeCount) {
                node = new Node(data);
                node.setLabel(POSITIVE_INPUT);
                return node;
            } else {
                node = new Node(data);
                node.setLabel(NEGATIVE_INPUT);
                return node;
            }
        }

        attributeList.remove(maxAttribute);

        node = new Node(data);
        node.setAttributeName(maxAttribute);
        node.setNegativeCount(negativeCount);
        node.setPositiveCount(positiveCount);

        ArrayList<String> values = data.getAttributeValues(maxAttribute);
        for (String value : values) {
            DataTable svData = new DataTable();
            svData.setAttributeNames(data.getAttributeNames());
            for (ArrayList<String> record : data.getRecords()) {
                if (record.get(data.getAttributeNames().indexOf(maxAttribute)).equals(value)) {
                    svData.getRecords().add(record);
                }
            }

            if (svData.getRecords().isEmpty()) {
                if (positiveCount > negativeCount) {
                    Node childNode = new Node(svData);
                    childNode.setLabel(POSITIVE_INPUT);
                    node.getChildLinks().add(new Link(value, childNode));
                } else {
                    Node childNode = new Node(svData);
                    childNode.setLabel(NEGATIVE_INPUT);
                    node.getChildLinks().add(new Link(value, childNode));
                }
            } else {
                node.getChildLinks().add(new Link(value, executeAlgorithmForGainHeuristic(svData, attributeList)));
            }

        }

//        System.out.println(gains);
        return node;
    }

    public static Node executeAlgorithmForVarianceImpurityHeuristic(DataTable data, ArrayList<String> attributes) {

        Node node = null;

        ArrayList<String> attributeList = new ArrayList<>();
        for (String attribute : attributes) {
            attributeList.add(attribute);
        }

        //If data contains all positive then return new node with positive label or negative accordingly
        ArrayList<ArrayList<String>> records = data.getRecords();
        int positiveCount = 0, negativeCount = 0;
        for (ArrayList<String> record : records) {
            String val = record.get(record.size() - 1);
            if (val.equals(NEGATIVE_INPUT)) {
                negativeCount++;
            } else {
                positiveCount++;
            }
        }
        if (positiveCount == records.size()) {
            node = new Node(data);
            node.setLabel(POSITIVE_INPUT);
            return node;
        } else if (negativeCount == records.size()) {
            node = new Node(data);
            node.setLabel(NEGATIVE_INPUT);
            return node;
        }

        //check if attribute list is empty 
        if (attributeList.isEmpty()) {
            //Return the node with label as max occurence
            if (positiveCount > negativeCount) {
                node = new Node(data);
                node.setLabel(POSITIVE_INPUT);
                return node;
            } else {
                node = new Node(data);
                node.setLabel(NEGATIVE_INPUT);
                return node;
            }
        }

        //Calculating the variance impurity for the data
        double varianceImpurity = data.calculateVarianceImpurity();

//        //Calculating gain for all the attributes not in the arrtibute list
//        double entropy = data.calculateEntropy();
//        System.out.println(entropy);
        //Calculate information gain for each attribute
        double s = data.getRecords().size();
        HashMap<String, Double> gains = new HashMap<>();
        for (String attribute : attributeList) {
//            System.out.println("Attribute========================>"+attribute);
            ArrayList<String> values = data.getAttributeValues(attribute);
            double gain = varianceImpurity;
            for (String value : values) {
                double sv = 0;
                DataTable svData = new DataTable();
                for (ArrayList<String> record : data.getRecords()) {
                    if (record.get(data.getAttributeNames().indexOf(attribute)).equals(value)) {
                        svData.getRecords().add(record);
                        sv++;
                    }
                }

                double svVarianceImpurity = svData.calculateVarianceImpurity();
//                System.out.println("SVEntropy====>"+svEntropy);

//                System.out.println("sv/s===>"+(sv/s));
//                System.out.println("*entropy==>"+((sv/s)*svEntropy));
                gain = gain - ((sv / s) * svVarianceImpurity);
//                svData.displayTable();
            }
            gains.put(attribute, gain);
        }

//        System.out.println(gains);
        //Check which attribute has the highest gain
        double maxGain = Double.MIN_VALUE;
        String maxAttribute = null;
        for (Map.Entry<String, Double> gainEntry : gains.entrySet()) {
            if (gainEntry.getValue() > maxGain) {
                maxGain = gainEntry.getValue();
                maxAttribute = gainEntry.getKey();
            }
        }

        if (maxAttribute == null) {
            if (positiveCount > negativeCount) {
                node = new Node(data);
                node.setLabel(POSITIVE_INPUT);
                return node;
            } else {
                node = new Node(data);
                node.setLabel(NEGATIVE_INPUT);
                return node;
            }
        }

        attributeList.remove(maxAttribute);

        node = new Node(data);
        node.setAttributeName(maxAttribute);
        node.setNegativeCount(negativeCount);
        node.setPositiveCount(positiveCount);

        ArrayList<String> values = data.getAttributeValues(maxAttribute);
        for (String value : values) {
            DataTable svData = new DataTable();
            svData.setAttributeNames(data.getAttributeNames());
            for (ArrayList<String> record : data.getRecords()) {
                if (record.get(data.getAttributeNames().indexOf(maxAttribute)).equals(value)) {
                    svData.getRecords().add(record);
                }
            }

            if (svData.getRecords().isEmpty()) {
                if (positiveCount > negativeCount) {
                    Node childNode = new Node(svData);
                    childNode.setLabel(POSITIVE_INPUT);
                    node.getChildLinks().add(new Link(value, childNode));
                } else {
                    Node childNode = new Node(svData);
                    childNode.setLabel(NEGATIVE_INPUT);
                    node.getChildLinks().add(new Link(value, childNode));
                }
            } else {
                node.getChildLinks().add(new Link(value, executeAlgorithmForVarianceImpurityHeuristic(svData, attributeList)));
            }

        }

//        System.out.println(gains);
        return node;
    }

    public static void displayTree(Node node, int level) {
        if (node.getChildLinks().isEmpty()) {
            System.out.println(" " + node.getLabel());
            return;
        } else {
            System.out.println("");
        }

        for (Link link : node.getChildLinks()) {
            for (int i = 0; i < level; i++) {
                System.out.print("| ");
            }
            System.out.print(node.getAttributeName() + " = " + link.getAttributeValue() + ":");
            displayTree(link.getChildNode(), level + 1);
        }
    }

    public static Node copyTree(Node rootNode) {
        Node node = null;
        if (rootNode.getChildLinks().isEmpty()) {
            //is a leaf node
            node = new Node(rootNode.getTable());
            node.setLabel(rootNode.getLabel());
            return node;
        }

        node = new Node(rootNode.getTable());
        node.setAttributeName(rootNode.getAttributeName());
        node.setNegativeCount(rootNode.getNegativeCount());
        node.setPositiveCount(rootNode.getPositiveCount());

        for (Link link : rootNode.getChildLinks()) {
            node.getChildLinks().add(new Link(link.getAttributeValue(), copyTree(link.getChildNode())));
        }

        return node;
    }

    public static int countNonLeafNodes(Node rootNode) {
        int count = 0;
        if (rootNode.getChildLinks().isEmpty()) {
            return count;
        }
        count = 1;
        for (Link link : rootNode.getChildLinks()) {
            count = count + countNonLeafNodes(link.getChildNode());
        }
        return count;
    }

    public static Node replaceSubtree(Node rootNode, int position) {
        Node nodeToBeReplaced = findNodeByPosition(rootNode, position);
        if (nodeToBeReplaced == rootNode) {
            return rootNode;
        }
        Node nodeToBeReplacedParent = findNodeParent(rootNode, nodeToBeReplaced);

//        System.out.println("Node to be replaced ==>  " + nodeToBeReplaced.getAttributeName() + " number ==>" + nodeToBeReplaced.getNumber());
//        System.out.println("Parent of node to be replaced ==>  " + nodeToBeReplacedParent.getAttributeName() + " number ==>" + nodeToBeReplacedParent.getNumber());
        for (Link link : nodeToBeReplacedParent.getChildLinks()) {
            if (link.getChildNode() == nodeToBeReplaced) {
                //Replace with leaf node of most repeating
                DataTable data = nodeToBeReplacedParent.getTable();
                ArrayList<ArrayList<String>> records = data.getRecords();
                int positiveCount = 0, negativeCount = 0;
                for (ArrayList<String> record : records) {
                    String val = record.get(record.size() - 1);
                    if (val.equals(NEGATIVE_INPUT)) {
                        negativeCount++;
                    } else {
                        positiveCount++;
                    }
                }
                if (negativeCount > positiveCount) {
                    Node node = new Node(data);
                    node.setLabel(NEGATIVE_INPUT);
                    link.setChildNode(node);
                } else {
                    Node node = new Node(data);
                    node.setLabel(POSITIVE_INPUT);
                    link.setChildNode(node);
                }
            }
        }
        return rootNode;
    }

    public static Node findNodeByPosition(Node rootNode, int position) {
        if (rootNode.getNumber() == position) {
            return rootNode;
        }

        for (Link link : rootNode.getChildLinks()) {
            Node node = findNodeByPosition(link.getChildNode(), position);
            if (node != null) {
                return node;
            }
        }

        return null;
    }

    public static int assignNonLeafNodesNumbers(Node rootNode, int number) {
        if (rootNode.getChildLinks().isEmpty()) {
            return number - 1;
        }

        rootNode.setNumber(number);

        for (Link link : rootNode.getChildLinks()) {
            number = assignNonLeafNodesNumbers(link.getChildNode(), number + 1);
        }

        return number;
    }

    public static Node findNodeParent(Node rootNode, Node targetNode) {
        if (rootNode.getChildLinks().isEmpty()) {
            return null;
        }
        for (Link link : rootNode.getChildLinks()) {
            if (targetNode == link.getChildNode()) {
                return rootNode;
            }
        }
        for (Link link : rootNode.getChildLinks()) {
            Node node = findNodeParent(link.getChildNode(), targetNode);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    public static double getTreeAccuracy(Node rootNode, DataTable data) {
        double correctlyClassifiedExamples = 0;
        for (ArrayList<String> record : data.getRecords()) {
//            System.out.println("Checking for record = "+record);
            Node node = rootNode;
            while (node != null) {
                if (node.getChildLinks().isEmpty()) {
                    //it is a leaf node
                    String recordClassValue = record.get(data.getAttributeNames().indexOf(CLASS_ATTRIBUTE_NAME));
                    if (node.getLabel().equals(recordClassValue)) {
                        correctlyClassifiedExamples++;
                    }
                    break;
                }
//                System.out.println("Checking for node = "+node.getAttributeName());
                String attributeName = node.getAttributeName();
                String recordValue = record.get(data.getAttributeNames().indexOf(attributeName));
//                System.out.println("Record Value = "+recordValue);
                for (Link link : node.getChildLinks()) {
                    if (link.getAttributeValue().equals(recordValue)) {
//                        System.out.println("Found a match");
                        node = link.getChildNode();
                    }
                }
            }
        }
        double accuracy = (correctlyClassifiedExamples / data.getRecords().size()) * 100;
        return accuracy;
    }
}
