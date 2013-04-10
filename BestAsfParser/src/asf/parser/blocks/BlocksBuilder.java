/*
 * BestAsfParser
 * BlocksBuilder
 * Created on 08.04.2013
 */

package asf.parser.blocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import asf.parser.enums.BlockTypesEnum;

/**
 * BlocksBuilder.
 * @author vladutb
 * @version 1.0
 * @since 08.04.2013
 */
public class BlocksBuilder {
    /** <code>LOG</code> */
    private static final Log LOG = LogFactory.getLog(BlocksBuilder.class);
    /** <code>feldVarMap</code> */
    private Map<String, String> feldVarMap = new HashMap<String, String>();
    /**
     * 
     * @param fileName
     * @return Map<String, AssignmentsAndBlocks>
     * @throws IOException
     */
    public Map<String, AssignmentsAndBlocks> load(String fileName) throws IOException {
        Map<String, AssignmentsAndBlocks> map = new HashMap<String, AssignmentsAndBlocks>();
        FileBlockFinder blockFinder = new FileBlockFinder(fileName);
        BlockPair blockPair = null;
        while ((blockPair = blockFinder.nextBlock()) != null) {
            String blockLine = blockPair.getRight();
            BlockTypesEnum type = blockPair.getLeft();
            switch (type) {
                case ZUWEISUNG :
                    String[] allSet = blockLine.split(";");
                    for (String str : allSet) {
                        extractVariableAndAddToMap(str, map);
                    }
                    break;
                case FELD_ZUWEISUNG :
                    addFieldAssignmentToMap(blockLine);
                    break;
                case IF_BLOCK :
                    extractAllVariablesFromBlockAndAddDependencies(blockLine, map);
                    break;
                default :
                    break;
            }
        }
        return map;
    }
    /**
     * 
     * @param blockLine
     */
    private void addFieldAssignmentToMap(String blockLine) {
        String[] result = blockLine.substring(1, blockLine.length() - 1).split(",");
        if (result.length == 2) {
            if (feldVarMap.containsKey(result[0].trim()))
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Problem: Die Feldname '" + result[0] + "' mehrmalig gesetzt!!");
                }
            feldVarMap.put(result[0].trim(), result[1].trim());
        }
    }
    /**
     * 
     * @param line
     * @param varBlocksMap
     */
    private void extractVariableAndAddToMap(String line,
        Map<String, AssignmentsAndBlocks> varBlocksMap) {
        String variableName = getVariableNameFromLine(line);
        AssignmentsAndBlocks asBl = null;
        if (!varBlocksMap.containsKey(variableName)) {
            asBl = new AssignmentsAndBlocks();
        }
        else {
            asBl = varBlocksMap.get(variableName);
        }
        asBl.addAssignment(line);
        varBlocksMap.put(variableName, asBl);
    }
    /**
     * 
     * @param line
     * @return
     */
    private String getVariableNameFromLine(String line) {
        String auxStr = line.split(" ")[1];
        return auxStr;
    }
    /**
     * 
     * @param block
     * @param varBlocksMap
     */
    private void extractAllVariablesFromBlockAndAddDependencies(String block,
        Map<String, AssignmentsAndBlocks> varBlocksMap) {
        List<String> allVars = extractVariables(block);
        for (String ss : allVars) {
            AssignmentsAndBlocks assBlocks = null;
            if (varBlocksMap.containsKey(ss)) {
                assBlocks = varBlocksMap.get(ss);
            }
            else {
                assBlocks = new AssignmentsAndBlocks();
                varBlocksMap.put(ss, assBlocks);
            }
            assBlocks.addBlock(removeOtherVariablesFromBlock(block, ss, varBlocksMap));
        }
    }
    /**
     * 
     * @param block
     * @return
     */
    private List<String> extractVariables(String block) {
        List<String> allVars = new ArrayList<String>();
        String fluentBlock = block.replaceAll("\n", " ").replaceAll("\r", " ");
        String[] parts = fluentBlock.split(" ");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals(".se") && i + 1 < parts.length) {
                allVars.add(parts[i + 1]);
            }
        }
        return allVars;
    }
    /**
     * 
     * @param block
     * @param variable 
     * @param varBlocksMap 
     * @return
     */
    private String removeOtherVariablesFromBlock(String block,
        String variable,
        Map<String, AssignmentsAndBlocks> varBlocksMap) {
        String[] lines = block.split("\n");
        StringBuilder builder = new StringBuilder();
        boolean varSetOnElse = false;
        for (String line : lines) {
            List<String> variables = extractVariables(line);
            boolean hasCurrentVars = false;
            for (String var : variables) {
                if (var.equals(variable)) {
                    hasCurrentVars = true;
                    if (line.startsWith(".el"))
                        varSetOnElse = true;
                    break;
                }
            }
            if (variables.size() == 0 || hasCurrentVars) {
                builder.append(line).append("\n");
            }
        }
        if (!varSetOnElse && varBlocksMap.containsKey(variable)) {
            AssignmentsAndBlocks assignBlock = varBlocksMap.get(variable);
            String assign = assignBlock.getLastAssignment();
            if (assign != null) {
                builder.append(".el ").append(assign);
            }
        }
        return builder.toString();
    }
}
