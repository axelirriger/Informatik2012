/*
 * BestAsfParser
 * VariableNode
 * Created on 09.04.2013
 */

package asf.parser.tree;

/**
 * VariableNode.
 * @author vladutb
 * @version 1.0
 * @since 09.04.2013
 * @see Node
 */
public class VariableNode extends Node {
    /**
     * VariableNode Konstruktor.
     * @param value
     */
    public VariableNode(String value) {
        super("variable", value);
    }
}
