/*
 * BestAsfParser
 * Main
 * Created on 04.04.2013
 */

package asf.parser.main;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import asf.parser.blocks.AssignmentsAndBlocks;
import asf.parser.blocks.BlocksBuilder;
import asf.parser.conditions.Condition;
import asf.parser.conditions.ConditionsBuilder;
import asf.parser.tree.Node;
import asf.parser.tree.TreeBuilder;

/**
 * Main.
 * @author vladutb
 * @version 1.0
 * @since 04.04.2013
 */
public class Main {
    //
    /**
     * 
     * @param args
     */
    //    public static void main(String[] args) {
    //        File file = new File("asfFiles/asfExample.txt");
    //        List<String> allBlocks = new ArrayList<String>();
    //        List<String> allAssignment = new ArrayList<String>();
    //        Map<String, AssignmentsAndBlocks> varBlocksMap = new HashMap<String, AssignmentsAndBlocks>();
    //        Map<String, String> feldVarMap = new HashMap<String, String>();
    //        try {
    //            FileReader reader = new FileReader(file);
    //            BufferedReader bR = new BufferedReader(reader);
    //            String line = null;
    //            while ((line = bR.readLine()) != null) {
    //                StringBuilder builder = new StringBuilder();
    //                String lineNoLn = deleteRowNumber(line);
    //                if (line != null && isAssignment(line)) {
    //                    String[] allSet = lineNoLn.split(";");
    //                    for (String str : allSet) {
    //                        allAssignment.add(str);
    //                        extractVariableAndAddToMap(str, varBlocksMap);
    //                    }
    //                    continue;
    //                }
    //                if (isFieldAssignment(lineNoLn)) {
    //                    String[] result = lineNoLn.substring(1, lineNoLn.length() - 1).split(",");
    //                    if (result.length == 2) {
    //                        if (feldVarMap.containsKey(result[0].trim()))
    //                            System.out.println("Problem - merhmalig param gesetzt: "
    //                                + result[0].trim());
    //                        feldVarMap.put(result[0].trim(), result[1].trim());
    //                    }
    //                    continue;
    //                }
    //                if (line != null && !isStartingClause(line)) {
    //                    continue;
    //                }
    //                builder.append(deleteRowNumber(line));
    //                line = bR.readLine();
    //                while (line != null && intermediateClause(line)) {
    //                    builder.append("\n").append(deleteRowNumber(line));
    //                    line = bR.readLine();
    //                }
    //                //TODO - noch mal checken für line(anders es ist ignoriert)
    //                allBlocks.add(builder.toString());
    //                extractAllVariablesFromBlockAndAddDependencies(builder.toString(), varBlocksMap);
    //            }
    //            bR.close();
    //            System.out.println();
    //            System.out.println();
    //                for (String var : varBlocksMap.keySet()) {
    //                    System.out.println("Variable: " + var);
    //                    System.out.println("All Blocks:");
    //                    AssignmentsAndBlocks assignBlock = varBlocksMap.get(var);
    //                    for (String block : assignBlock.getAllBlocks()) {
    //                        System.out.println(block);
    //                        System.out.println("------------------------------------");
    //                    }
    //                }
    //                String variable = "Erblasser";
    //                String block = varBlocksMap.get(variable).getAllBlocks().get(0);
    //                TreeBuilder builderTree = new TreeBuilder(varBlocksMap);
    //                Node node = builderTree.buildTree(block, variable);
    //                List<Condition> conditions = buildConditions(node);
    //            System.out.println("Node: " + node.getValue());
    //        }
    //        catch (FileNotFoundException exept) {
    //            // TODO Auto-generated catch block
    //            //log.error(exept, exept);
    //        }
    //        catch (IOException exept) {
    //            // TODO Auto-generated catch block
    //            //log.error(exept, exept);
    //        }
    //    }
    public static void main(String[] args) {
        BlocksBuilder blocksBuilder = new BlocksBuilder();
        try {
            Map<String, AssignmentsAndBlocks> loadMap = blocksBuilder
                .load("asfFiles/asfExample.txt");
            printAllBlocks(loadMap);
            String variable = "Erblasser";
            String block = loadMap.get(variable).getMergedBlocks();
            TreeBuilder builderTree = new TreeBuilder(loadMap);
            Node node = builderTree.buildTreeFromIfBlock(block, variable);
            ConditionsBuilder conditionsBuilder = new ConditionsBuilder();
            List<Condition> conditions = conditionsBuilder.buildConditionsFromTree(node);
            printAllConditions(conditions);
        }
        catch (IOException exept) {
            // TODO Auto-generated catch block
            // log.error(exept, exept);
        }
    }
    /**
     * 
     * @param conditions
     */
    private static void printAllConditions(List<Condition> conditions) {
        for (Condition cond : conditions) {
            System.out.println("Variable Name:" + cond.getVariableName() + " Value: "
                + cond.getVariableValue());
            System.out.println("Included Conditions:");
            for (Condition.ConditionAndValue cav : cond.getAllConditions()) {
                System.out.println(cav.getCondition() + " - " + cav.getConditionValue());
            }
            System.out.println("---------------------------------");
        }
    }
    /**
     * 
     * @param loadMap
     */
    private static void printAllBlocks(Map<String, AssignmentsAndBlocks> loadMap) {
        for (String var : loadMap.keySet()) {
            System.out.println("Variable: " + var);
            System.out.println("All Blocks:");
            AssignmentsAndBlocks assignBlock = loadMap.get(var);
            for (String block : assignBlock.getAllBlocks()) {
                System.out.println(block);
                System.out.println("------------------------------------");
            }
        }
    }
    //    /**
    //     * 
    //     * @param node
    //     * @return
    //     */
    //    private static List<Condition> buildConditions(Node node) {
    //        String variableName = null;
    //        List<Condition> conditions = new ArrayList<Condition>();
    //        Node helpNode = null;
    //        if (node.getType().equals("variable"))
    //            variableName = node.getValue();
    //        Condition condition = new Condition();
    //        condition.setVariableName(variableName);
    //        conditions.add(condition);
    //        helpNode = node.getChildren().get(0);
    //        if (helpNode.getType().equals(".if"))
    //            helpNode = helpNode.getChildren().get(0);
    //        Node trueNode = ((ConditionNode) helpNode).getTrueNode();
    //        Node falseNode = ((ConditionNode) helpNode).getFalseNode();
    //        Condition conditionFalse = condition.clone();
    //        condition.addCondition(((ConditionNode) helpNode).getValue(), true);
    //        buildConditionsForChild(conditions, condition, trueNode);
    //        conditionFalse.addCondition(((ConditionNode) helpNode).getValue(), false);
    //        conditions.add(conditionFalse);
    //        buildConditionsForChild(conditions, conditionFalse, falseNode);
    //        return conditions;
    //    }
    //    /**
    //     * 
    //     * @param conditions
    //     * @param condition
    //     * @param trueNode
    //     */
    //    private static void buildConditionsForChild(List<Condition> conditions,
    //        Condition condition,
    //        Node trueNode) {
    //        if (trueNode == null)
    //            return;
    //        if (trueNode.getType().equals(".if")) {
    //            //TODO 
    //        }
    //        else if (trueNode.getType().equals(".se")) {
    //            condition.setVariableValue(trueNode.getValue());
    //            if (trueNode.getChildren().size() > 0)
    //                for (Node nn : trueNode.getChildren()) {
    //                    List<Condition> otherConditions = buildConditions(nn);
    //                    List<Condition> toDeleteConds = new ArrayList<Condition>();
    //                    List<Condition> toAddConds = new ArrayList<Condition>();
    //                    for (Condition cc : conditions) {
    //                        toDeleteConds.add(cc);
    //                        for (Condition cond : otherConditions) {
    //                            Condition xCond = cc.clone();
    //                            List<Condition.ConditionAndValue> condAndValue = cond
    //                                .getAllConditions();
    //                            for (Condition.ConditionAndValue bb : condAndValue) {
    //                                xCond.addCondition(bb);
    //                                xCond.replaceVariableValue(cond.getVariableName(), cond
    //                                    .getVariableValue());
    //                            }
    //                            toAddConds.add(xCond);
    //                        }
    //                    }
    //                    for (Condition delC : toDeleteConds) {
    //                        conditions.remove(delC);
    //                    }
    //                    for (Condition addC : toAddConds) {
    //                        conditions.add(addC);
    //                    }
    //                }
    //        }
    //    }
    //    /**
    //     * 
    //     * @param line
    //     * @return
    //     */
    //    private static boolean isFieldAssignment(String line) {
    //        return line.startsWith("[") && line.endsWith("]") && line.contains(",");
    //    }
    //    /**
    //     * 
    //     * @param block
    //     * @param varBlocksMap
    //     */
    //    private static void extractAllVariablesFromBlockAndAddDependencies(String block,
    //        Map<String, AssignmentsAndBlocks> varBlocksMap) {
    //        List<String> allVars = extractVariables(block);
    //        System.out.println("Variables in Block:");
    //        for (String ss : allVars) {
    //            System.out.println(ss);
    //            AssignmentsAndBlocks assBlocks = null;
    //            if (varBlocksMap.containsKey(ss)) {
    //                assBlocks = varBlocksMap.get(ss);
    //            }
    //            else {
    //                assBlocks = new AssignmentsAndBlocks();
    //                varBlocksMap.put(ss, assBlocks);
    //            }
    //            //prepare block -> remove not relevant th
    //            //add resulted block
    //            assBlocks.addBlock(removeOtherVariablesFromBlock(block, ss, varBlocksMap));
    //        }
    //    }
    //    /**
    //     * 
    //     * @param block
    //     * @return
    //     */
    //    private static List<String> extractVariables(String block) {
    //        List<String> allVars = new ArrayList<String>();
    //        String fluentBlock = block.replaceAll("\n", " ").replaceAll("\r", " ");
    //        String[] parts = fluentBlock.split(" ");
    //        for (int i = 0; i < parts.length; i++) {
    //            if (parts[i].equals(".se") && i + 1 < parts.length) {
    //                allVars.add(parts[i + 1]);
    //            }
    //        }
    //        return allVars;
    //    }
    //    /**
    //     * 
    //     * @param block
    //     * @param variable 
    //     * @param varBlocksMap 
    //     * @return
    //     */
    //    private static String removeOtherVariablesFromBlock(String block,
    //        String variable,
    //        Map<String, AssignmentsAndBlocks> varBlocksMap) {
    //        String[] lines = block.split("\n");
    //        StringBuilder builder = new StringBuilder();
    //        boolean varSetOnElse = false;
    //        for (String line : lines) {
    //            List<String> variables = extractVariables(line);
    //            boolean hasCurrentVars = false;
    //            for (String var : variables) {
    //                if (var.equals(variable)) {
    //                    hasCurrentVars = true;
    //                    if (line.startsWith(".el"))
    //                        varSetOnElse = true;
    //                    break;
    //                }
    //            }
    //            if (variables.size() == 0 || hasCurrentVars) {
    //                builder.append(line).append("\n");
    //            }
    //        }
    //        if (!varSetOnElse && varBlocksMap.containsKey(variable)) {
    //            AssignmentsAndBlocks assignBlock = varBlocksMap.get(variable);
    //            String assign = assignBlock.getLastAssignment();
    //            if (assign != null) {
    //                builder.append(".el ").append(assign);
    //            }
    //        }
    //        return builder.toString();
    //    }
    //    /**
    //     * 
    //     * @param line
    //     * @param varBlocksMap
    //     */
    //    private static void extractVariableAndAddToMap(String line,
    //        Map<String, AssignmentsAndBlocks> varBlocksMap) {
    //        String variableName = getVariableNameFromLine(line);
    //        AssignmentsAndBlocks asBl = null;
    //        if (!varBlocksMap.containsKey(variableName)) {
    //            asBl = new AssignmentsAndBlocks();
    //        }
    //        else {
    //            asBl = varBlocksMap.get(variableName);
    //        }
    //        asBl.addAssignment(line);
    //        varBlocksMap.put(variableName, asBl);
    //    }
    //    /**
    //     * 
    //     * @param line
    //     * @return
    //     */
    //    private static String getVariableNameFromLine(String line) {
    //        String auxStr = line.split(" ")[1];
    //        return auxStr;
    //    }
    //    private static void testNPL(File file) throws FileNotFoundException, IOException {
    //        NextPrevLineFileReader fileRR = new NextPrevLineFileReader(file, "r");
    //        fileRR.readNextLine();
    //        fileRR.readNextLine();
    //        fileRR.readNextLine();
    //        fileRR.readNextLine();
    //        fileRR.readPreviousLine();
    //        fileRR.readPreviousLine();
    //        fileRR.close();
    //    }
    //    /**
    //     * 
    //     * @param line
    //     * @return boolean
    //     */
    //    private static boolean isAssignment(String line) {
    //        return !intermediateClause(line) && !isStartingClause(line) && line.contains(".se");
    //    }
    //    /**
    //     * 
    //     * @param line
    //     * @return
    //     */
    //    private static boolean intermediateClause(String line) {
    //        return line.contains(".th") || line.contains(".el") || line.contains(".or")
    //            || line.contains(".an") || (line.contains(".th") && line.contains(".se"));
    //    }
    //    /**
    //     * 
    //     * @param line
    //     * @return
    //     */
    //    private static boolean isStartingClause(String line) {
    //        return line.contains(".if ");
    //    }
    //    /**
    //     * 
    //     * @param line
    //     * @return
    //     */
    //    private static String deleteRowNumber(String line) {
    //        String[] splitStrs = line.split(": ");
    //        StringBuilder builder = new StringBuilder();
    //        if (splitStrs.length > 0) {
    //            for (int i = 1; i < splitStrs.length; i++) {
    //                builder.append(splitStrs[i]);
    //            }
    //        }
    //        return builder.toString();
    //    }
}
