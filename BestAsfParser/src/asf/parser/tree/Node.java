/*
 * BestAsfParser
 * Node
 * Created on 05.04.2013
 */

package asf.parser.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Node.
 * @author vladutb
 * @version 1.0
 * @since 05.04.2013
 * @see 
 */
public class Node {
    private String type;
    private String value;
    private List<Node> children = new ArrayList<Node>();
    /**
     * Node Konstruktor.
     */
    public Node(String type, String value) {
        this.type = type;
        this.value = value;
    }
    public void addChild(Node node) {
        children.add(node);
    }
    /**
     * 
     * @return
     */
    public String getValue() {
        return value;
    }
    public String getType() {
        return type;
    }
    public List<Node> getChildren() {
        return this.children;
    }
}
