/*
 * BestAsfParser
 * ConditionsBuilder
 * Created on 08.04.2013
 */

package asf.parser.conditions;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import asf.parser.tree.ConditionNode;
import asf.parser.tree.Node;

/**
 * ConditionsBuilder.
 * @author vladutb
 * @version 1.0
 * @since 08.04.2013
 */
public class ConditionsBuilder {
    /**
     * 
     * @param node
     * @return List<Condition>
     */
    public List<Condition> buildConditionsFromTree(Node node) {
        String variableName = null;
        List<Condition> conditions = new ArrayList<Condition>();
        Node helpNode = null;
        if (node.getType().equals("variable"))
            variableName = node.getValue();
        Condition condition = new Condition();
        condition.setVariableName(variableName);
        conditions.add(condition);
        helpNode = node.getChildren().get(0);
        if (helpNode.getType().equals(".if"))
            helpNode = helpNode.getChildren().get(0);
        Node trueNode = ((ConditionNode) helpNode).getTrueNode();
        Node falseNode = ((ConditionNode) helpNode).getFalseNode();
        Condition conditionFalse = condition.clone();
        condition.addCondition(((ConditionNode) helpNode).getValue().trim(), true);
        buildConditionsForChild(conditions, condition, trueNode);
        conditionFalse.addCondition(((ConditionNode) helpNode).getValue().trim(), false);
        conditions.add(conditionFalse);
        buildConditionsForChild(conditions, conditionFalse, falseNode);
        return conditions;
    }
    /**
     * 
     * @param conditions
     * @param condition
     * @param trueNode
     */
    private void buildConditionsForChild(List<Condition> conditions,
        Condition condition,
        Node trueNode) {
        if (trueNode == null)
            return;
        if (trueNode.getType().equals(".if")) {
            //TODO 
        }
        else if (trueNode.getType().equals(".se")) {
            condition.setVariableValue(getValueFromAssignment(trueNode.getValue()));
            if (trueNode.getChildren().size() > 0)
                for (Node nn : trueNode.getChildren()) {
                    List<Condition> otherConditions = buildConditionsFromTree(nn);
                    List<Condition> toDeleteConds = new ArrayList<Condition>();
                    List<Condition> toAddConds = new ArrayList<Condition>();
                    for (Condition cc : conditions) {
                        toDeleteConds.add(cc);
                        for (Condition cond : otherConditions) {
                            Condition xCond = cc.clone();
                            List<Condition.ConditionAndValue> condAndValue = cond
                                .getAllConditions();
                            for (Condition.ConditionAndValue bb : condAndValue) {
                                xCond.addCondition(bb);
                                xCond.replaceVariableValue(cond.getVariableName(), cond
                                    .getVariableValue());
                            }
                            toAddConds.add(xCond);
                        }
                    }
                    for (Condition delC : toDeleteConds) {
                        conditions.remove(delC);
                    }
                    for (Condition addC : toAddConds) {
                        conditions.add(addC);
                    }
                }
        }
    }
    /**
     * 
     * @param string
     * @return String
     */
    private String getValueFromAssignment(String string) {
        String stringToReturn = null;
        if (StringUtils.contains(string, "=")) {
            stringToReturn = StringUtils.substringAfter(string, "=");
        }
        else {
            stringToReturn = StringUtils.substringAfter(string.trim(), " ");
        }
        return stringToReturn.trim().replaceAll("'", "");
    }
}
