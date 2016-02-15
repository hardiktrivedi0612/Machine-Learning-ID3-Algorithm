
import java.util.ArrayList;

/**
 *
 * @author Hardik Trivedi
 * NetId: hpt150030
 */
public class Node {

    private String attributeName;
    private ArrayList<Link> childLinks;
    private int positiveCount, negativeCount;
    private String label;
    private DataTable table;
    private int number;
    
    public Node(DataTable table) {
        childLinks = new ArrayList<>();
        label = "";
        attributeName = "";
        this.table = table;
        this.number = 0;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public ArrayList<Link> getChildLinks() {
        return childLinks;
    }

    public void setChildLinks(ArrayList<Link> childLinks) {
        this.childLinks = childLinks;
    }

    public int getPositiveCount() {
        return positiveCount;
    }

    public void setPositiveCount(int positiveCount) {
        this.positiveCount = positiveCount;
    }

    public int getNegativeCount() {
        return negativeCount;
    }

    public void setNegativeCount(int negativeCount) {
        this.negativeCount = negativeCount;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public DataTable getTable() {
        return table;
    }

    public void setTable(DataTable table) {
        this.table = table;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    
    
}
