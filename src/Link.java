/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hardik
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
