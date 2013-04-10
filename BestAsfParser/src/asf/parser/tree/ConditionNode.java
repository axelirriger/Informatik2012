/*
 * BestAsfParser
 * ConditionNode
 * Created on 08.04.2013
 */

package asf.parser.tree;

/**
 * ConditionNode.
 * @author vladutb
 * @version 1.0
 * @since 08.04.2013
 * @see Node
 */
public class ConditionNode extends Node {
    /**
     * ConditionNode Konstruktor.
     * @param value
     */
    public ConditionNode(String value) {
        super("condition", value);
        //chlidren[0] - trueNode, children[1] - falseNode
        getChildren().add(null);
        getChildren().add(null);
    }
    /**
     * 
     * @param node
     */
    public void setFalseNode(Node node) {
        getChildren().remove(1);
        getChildren().add(1, node);
    }
    /**
     * 
     * @param node
     */
    public void setTrueNode(Node node) {
        getChildren().remove(0);
        getChildren().add(0, node);
    }
    /**
     * 
     * @return <code>trueNode</code>
     */
    public Node getTrueNode() {
        return getChildren().get(0);
    }
    /**
     * 
     * @return <code>falseNode</code>
     */
    public Node getFalseNode() {
        return getChildren().get(1);
    }
}
