/*
 * BestAsfParser
 * TreeBuilder
 * Created on 08.04.2013
 */

package asf.parser.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import asf.parser.blocks.AssignmentsAndBlocks;

/**
 * TreeBuilder.
 * @author vladutb
 * @version 1.0
 * @since 08.04.2013
 * @see 
 */
public class TreeBuilder {
    /** <code>varBlocksMap</code> */
    private Map<String, AssignmentsAndBlocks> varBlocksMap;
    /**
     * TreeBuilder Konstruktor.
     * @param varBlocksMap 
     */
    public TreeBuilder(Map<String, AssignmentsAndBlocks> varBlocksMap) {
        this.varBlocksMap = varBlocksMap;
    }
    /**
     * 
     * @param block
     * @param variable
     * @return
     */
    public Node buildTreeFromIfBlock(String block, String variable) {
        Node root = new VariableNode(variable);
        String clause = null;
        int i = 0;
        while (!(clause = block.substring(i, i + 3)).equals(".se")
            && !(clause = block.substring(i, i + 3)).equals(".th") && i < block.length()) {
            i++;
        }
        String condition = null;
        if (clause.equals(".se")) {
            condition = block.substring(4, i);
            block = block.substring(i, block.length()).trim();
        }
        else if (clause.equals(".th")) {
            condition = block.substring(4, i);
            block = block.substring(i + 3, block.length()).trim();
        }
        else {
            return null;
        }
        ConditionNode conditionNode = new ConditionNode(condition);
        Node ifNode = new IfNode();
        ifNode.addChild(conditionNode);
        root.addChild(ifNode);
        if (block.startsWith(".if")) {
            //TODO
        }
        else if (block.startsWith(".se")) {
            String setStr = null;
            String elseClause = null;
            if (block.contains(".el")) {
                setStr = block.substring(4, block.indexOf(".el") - 1).trim();
                elseClause = block.substring(block.indexOf(".el") + 3).trim();
            }
            else {
                setStr = block.substring(4);
            }
            Node seNode = new Node(".se", setStr);
            conditionNode.setTrueNode(seNode);
            List<String> variables = extractVariablesFromAssignmentValue(setStr);
            for (String var : variables) {
                if (varBlocksMap.containsKey(var)
                    && varBlocksMap.get(var).getAllBlocks().size() > 0) {
                    String einBlock = varBlocksMap.get(var).getMergedBlocks();
                    Node varXNode = buildTreeFromIfBlock(einBlock, var);
                    seNode.addChild(varXNode);
                }
            }
            if (elseClause != null) {
                String elseSetStr = null;
                if (elseClause.startsWith(".if")) {
                    //TODO
                }
                else if (elseClause.startsWith(".se")) {
                    elseSetStr = elseClause.substring(4);
                }
                Node elseNode = new Node(".se", elseSetStr);
                conditionNode.setFalseNode(elseNode);
            }
        }
        return root;
    }
    /**
     * 
     * @param string
     * @return
     */
    private List<String> extractVariablesFromAssignmentValue(String string) {
        List<String> variables = new ArrayList<String>();
        boolean start = false;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            if (!start && ch == '&') {
                start = true;
                continue;
            }
            if (start && ch != '&' && ch != '.') {
                builder.append(ch);
            }
            else if (start && (ch == '&' || ch == '.')) {
                variables.add(builder.toString());
                builder = new StringBuilder();
                if (ch == '.')
                    start = false;
            }
        }
        return variables;
    }
}
