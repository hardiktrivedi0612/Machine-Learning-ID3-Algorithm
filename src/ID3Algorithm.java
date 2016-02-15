
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

/**
 *
 * @author Hardik Trivedi 
 * NetId: hpt150030
 */
public class ID3Algorithm {

    public static final String currentDirectory = System.getProperty("user.dir");
    private static DataTable trainingData, validationData, testingData;
    public static final String POSITIVE_INPUT = "1";
    public static final String NEGATIVE_INPUT = "0";
    public static final String CLASS_ATTRIBUTE_NAME = "Class";

    public static void main(String[] args) {

        if (args.length < 6) {
            System.out.println("Some of the parameters seem to be missing. Please check the README.txt file for details and try again.");
            return;
        }

        int l = 0;
        try {
            l = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.out.println("It seems that the value of 'L' cannot be parsed to an integer. Please check the value and try again.");
            return;
        }

        int k = 0;
        try {
            k = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("It seems that the value of 'K' cannot be parsed to an integer. Please check the value and try again.");
            return;
        }

        String trainingSetFileName = args[2];
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(new File(currentDirectory + "/" + trainingSetFileName)));
            String line = csvReader.readLine();
            trainingData = new DataTable(line.split(","));
            line = csvReader.readLine();
            while (line != null) {
                if (trainingData.add(line.split(",")) == -1) {
                    throw new IOException();
                }
                line = csvReader.readLine();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("It seems that the program is unable to find the training data file. "
                    + "Make sure that the file is in the same folder as the ID3Algorithm.java file. "
                    + "Also, just enter the file name and not the entire file path. "
                    + "Please refer README.txt for more details and try again.");
            return;
        } catch (IOException ex) {
            System.out.println("It seems that the program is unable to write / read the training data file. "
                    + "Make sure that the file is in the same folder as the ID3Algorithm.java file. "
                    + "Also, just enter the file name and not the entire file path. "
                    + "Please refer README.txt for more details and try again.");
            return;
        }

        String validationSetFileName = args[3];
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(new File(currentDirectory + "/" + validationSetFileName)));
            String line = csvReader.readLine();
            validationData = new DataTable(line.split(","));
            line = csvReader.readLine();
            while (line != null) {
                if (validationData.add(line.split(",")) == -1) {
                    throw new IOException();
                }
                line = csvReader.readLine();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("It seems that the program is unable to find the validation data file. "
                    + "Make sure that the file is in the same folder as the ID3Algorithm.java file. "
                    + "Also, just enter the file name and not the entire file path. "
                    + "Please refer README.txt for more details and try again.");
            return;
        } catch (IOException ex) {
            System.out.println("It seems that the program is unable to write / read the validation data file. "
                    + "Make sure that the file is in the same folder as the ID3Algorithm.java file. "
                    + "Also, just enter the file name and not the entire file path. "
                    + "Please refer README.txt for more details and try again.");
            return;
        }

        String testDataFileName = args[4];
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(new File(currentDirectory + "/" + testDataFileName)));
            String line = csvReader.readLine();
            testingData = new DataTable(line.split(","));
            line = csvReader.readLine();
            while (line != null) {
                if (testingData.add(line.split(",")) == -1) {
                    throw new IOException();
                }
                line = csvReader.readLine();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("It seems that the program is unable to find the test data file. "
                    + "Make sure that the file is in the same folder as the ID3Algorithm.java file. "
                    + "Also, just enter the file name and not the entire file path. "
                    + "Please refer README.txt for more details and try again.");
            return;
        } catch (IOException ex) {
            System.out.println("It seems that the program is unable to write / read the test data file. "
                    + "Make sure that the file is in the same folder as the ID3Algorithm.java file. "
                    + "Also, just enter the file name and not the entire file path. "
                    + "Please refer README.txt for more details and try again.");
            return;
        }

        String toPrintString = args[5];
        boolean toPrint = true;
        if (toPrintString.equalsIgnoreCase("no") || toPrintString.equalsIgnoreCase("n")) {
            toPrint = false;
        }

        ArrayList<String> attributes = new ArrayList<>();
        for (String attribute : trainingData.getAttributeNames()) {
            if (!attribute.equals(CLASS_ATTRIBUTE_NAME)) {
                attributes.add(attribute);
            }
        }

        System.out.println("Creating ID3 using Gain heuristic");
        Node gainHeuristicRootNode = executeAlgorithmForGainHeuristic(trainingData, attributes);
        if (toPrint) {
            System.out.println("Decision Tree using Gain heuristic (Without Pruning)=============================>");
            displayTree(gainHeuristicRootNode, 0);
        }
        System.out.println("Executing pruning for decision tree created using Gain heuristic");
        Node gainHeuristicRootNodeBest = executePostPruning(l, k, gainHeuristicRootNode, validationData);
        if (toPrint) {
            System.out.println("Decision Tree using Gain heuristic (After pruning)=============================>");
            displayTree(gainHeuristicRootNodeBest, 0);
        }
        double gainHeuristicAccuracyBeforePruning = getTreeAccuracy(gainHeuristicRootNode, testingData);
        System.out.println("Accuracy of the decision tree created usin Gain heuristic before pruning for test data is====>" + gainHeuristicAccuracyBeforePruning);
        double gainHeuristicAccuracyAfterPruning = getTreeAccuracy(gainHeuristicRootNodeBest, testingData);
        System.out.println("Accuracy of the decision tree created using Gain heuristic after pruning for test data is====>" + gainHeuristicAccuracyAfterPruning);

        System.out.println("Creating ID3 using Variance Impurity Heuristic");
        Node varianceImpurityRootNode = executeAlgorithmForVarianceImpurityHeuristic(trainingData, attributes);
        if (toPrint) {
            System.out.println("Decision Tree using Variance Impurity heuristic (Without Pruning)=============================>");
            displayTree(varianceImpurityRootNode, 0);
        }
        System.out.println("Executing pruning for decision tree created using Variance Impurity heuristic");
        Node varianceImpurityRootNodeBest = executePostPruning(l, k, varianceImpurityRootNode, validationData);
        if (toPrint) {
            System.out.println("Decision Tree using Variance Impurity heuristic (After pruning)=============================>");
            displayTree(varianceImpurityRootNodeBest, 0);
        }
        double varianceImpurityAccuracyBeforePruning = getTreeAccuracy(varianceImpurityRootNode, testingData);
        System.out.println("Accuracy of the decision tree created using Variance Impurity heuristic before pruning for test data is====>" + varianceImpurityAccuracyBeforePruning);
        double varianceImpurityAccuracyAfterPruning = getTreeAccuracy(varianceImpurityRootNodeBest, testingData);
        System.out.println("Accuracy of the decision tree created using Variance Impurity heuristic after pruning for test data is====>" + varianceImpurityAccuracyAfterPruning);
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
                dBest = dDash;
            }
        }
        return dBest;
    }

    public static Node executeAlgorithmForGainHeuristic(DataTable data, ArrayList<String> attributes) {
        Node node = null;
        ArrayList<String> attributeList = new ArrayList<>();
        for (String attribute : attributes) {
            attributeList.add(attribute);
        }

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

        //Calculate information gain for each attribute
        double s = data.getRecords().size();
        HashMap<String, Double> gains = new HashMap<>();
        for (String attribute : attributeList) {
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
                gain = gain - ((sv / s) * svEntropy);
            }
            gains.put(attribute, gain);
        }

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
                node.getChildLinks().add(new Link(value, executeAlgorithmForGainHeuristic(svData, attributeList)));
            }

        }

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

        //Calculate information gain for each attribute
        double s = data.getRecords().size();
        HashMap<String, Double> gains = new HashMap<>();
        for (String attribute : attributeList) {
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
                gain = gain - ((sv / s) * svVarianceImpurity);
            }
            gains.put(attribute, gain);
        }

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
            if (level != 0) {
                System.out.println("");
            }
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
                String attributeName = node.getAttributeName();
                String recordValue = record.get(data.getAttributeNames().indexOf(attributeName));
                for (Link link : node.getChildLinks()) {
                    if (link.getAttributeValue().equals(recordValue)) {
                        node = link.getChildNode();
                    }
                }
            }
        }
        double accuracy = (correctlyClassifiedExamples / data.getRecords().size()) * 100;
        return accuracy;
    }
}
