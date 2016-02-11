
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hardik
 */
public class DataTable {
    
    private ArrayList<String> attributeNames;
    private ArrayList<ArrayList<String>> records;
    
    public DataTable (String[] attributeNames) {
        this.attributeNames = new ArrayList<>();
        for(int i=0;i<attributeNames.length;i++) {
            this.attributeNames.add(attributeNames[i].trim());
        }
    }
    
    public DataTable() {
        this.attributeNames = new ArrayList<>();
        this.records = new ArrayList<>();
    }
    
    public int add(String[] rowData) {
        if(records==null) {
            records = new ArrayList<>();
        }
        if(rowData.length!=attributeNames.size()) {
            return -1;
        }
        ArrayList<String> row = new ArrayList<>();
        for (int i = 0; i < rowData.length; i++) {
            row.add(rowData[i]);
        }
        records.add(row);
        return 0;
    }
    
    public void displayTable() {
        for(String attributeName: attributeNames) {
            System.out.print(attributeName+" ");
        }
        System.out.println("");
        for(ArrayList<String> list : records) {
            for(String data: list) {
                System.out.print(data+" ");
            }
            System.out.println("");
        }
    }
    
    public ArrayList<String> getAttributeValues(String attributeName) {
        if(!attributeNames.contains(attributeName)) {
            return null;
        }
        ArrayList<String> values = new ArrayList<>();
        for(ArrayList<String> record : records) {
            if(!values.contains(record.get(attributeNames.indexOf(attributeName)))) {
                values.add(record.get(attributeNames.indexOf(attributeName)));
            }
        }
        return values;   
    }
    
    public double calculateEntropy() {
        double entropy = 0;
        double positiveCount = 0, negativeCount = 0;

        ArrayList<ArrayList<String>> records = getRecords();
        for (ArrayList<String> record : records) {
            if (record.get(record.size() - 1).equals(ID3Algorithm.NEGATIVE_INPUT)) {
                negativeCount++;
            } else {
                positiveCount++;
            }
        }

        double total = records.size();

        if (positiveCount == total || negativeCount == total) {
            return 0;
        }

        entropy = (-1 * (positiveCount / total) * (Math.log10((positiveCount / total)) / Math.log10(2))) + (-1 * (negativeCount / total) * (Math.log10((negativeCount / total)) / Math.log10(2)));
        return entropy;
    }
    
    public double calculateVarianceImpurity() {
        double varianceImpurity = 0;
        double positiveCount = 0, negativeCount = 0;

        ArrayList<ArrayList<String>> records = getRecords();
        for (ArrayList<String> record : records) {
            if (record.get(record.size() - 1).equals(ID3Algorithm.NEGATIVE_INPUT)) {
                negativeCount++;
            } else {
                positiveCount++;
            }
        }

        double total = records.size();
        
        varianceImpurity = (positiveCount / total) * (negativeCount / total);
        
        return varianceImpurity;
    }
    
    public ArrayList<String> getAttributeNames() {
        return attributeNames;
    }

    public void setAttributeNames(ArrayList<String> attributeNames) {
        this.attributeNames = attributeNames;
    }

    public ArrayList<ArrayList<String>> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<ArrayList<String>> records) {
        this.records = records;
    }
}
