/**
 *
 * @author Hardik Trivedi
 * NetId: hpt150030
 */
public class Link {
    private String attributeValue;
    private Node childNode;
    
    public Link(String attributeValue, Node childNode) {
        this.attributeValue = attributeValue;
        this.childNode = childNode;
    }
    
    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public Node getChildNode() {
        return childNode;
    }

    public void setChildNode(Node childNode) {
        this.childNode = childNode;
    }
}
