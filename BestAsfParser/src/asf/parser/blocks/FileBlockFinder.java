/*
 * BestAsfParser
 * FileBlockFinder
 * Created on 08.04.2013
 */

package asf.parser.blocks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import asf.parser.enums.BlockTypesEnum;

/**
 * FileBlockFinder.
 * @author vladutb
 * @version 1.0
 * @since 08.04.2013
 */
public class FileBlockFinder {
    /** <code>LOG</code> */
    private static final Log LOG = LogFactory.getLog(FileBlockFinder.class);
    /** <code>bufferedReader</code> */
    private BufferedReader bufferedReader;
    /** <code>line</code> */
    private String line;
    /** <code>notReturned</code> */
    private boolean notReturned = false;
    /**
     * FileBlockFinder Konstruktor.
     * @param filename 
     */
    public FileBlockFinder(String filename) {
        try {
            init(filename);
        }
        catch (FileNotFoundException exept) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("File '" + filename + "' cannot be accessed!");
            }
        }
    }
    /**
     * 
     * @param filename
     * @throws FileNotFoundException 
     */
    private void init(String filename) throws FileNotFoundException {
        FileReader reader = new FileReader(filename);
        bufferedReader = new BufferedReader(reader);
    }
    /**
     * 
     * @return BlockPair
     * @throws IOException
     */
    public BlockPair nextBlock() throws IOException {
        if (!notReturned) {
            line = deleteRowNumber(bufferedReader.readLine());
        }
        else {
            notReturned = false;
        }
        if (line == null)
            return null;
        StringBuilder builder = new StringBuilder();
        if (line != null && isAssignment(line)) {
            return new BlockPair(BlockTypesEnum.ZUWEISUNG, line);
        }
        if (isFieldAssignment(line)) {
            return new BlockPair(BlockTypesEnum.FELD_ZUWEISUNG, line);
        }
        if (line != null && !isStartingClause(line)) {
            return nextBlock();
        }
        builder.append(line);
        line = deleteRowNumber(bufferedReader.readLine());
        while (line != null && intermediateClause(line)) {
            builder.append("\n").append(line);
            line = deleteRowNumber(bufferedReader.readLine());
        }
        //TODO-villeicht andere Loesung finden ->  line ist nicht geprueft
        notReturned = true;
        return new BlockPair(BlockTypesEnum.IF_BLOCK, builder.toString());
    }
    /**
     * 
     * @param lineArg
     * @return String
     */
    private String deleteRowNumber(String lineArg) {
        if (lineArg == null)
            return null;
        String[] splitStrs = lineArg.split(": ");
        StringBuilder builder = new StringBuilder();
        if (splitStrs.length > 0) {
            for (int i = 1; i < splitStrs.length; i++) {
                builder.append(splitStrs[i]);
            }
        }
        return builder.toString();
    }
    /**
     * 
     * @param lineArg
     * @return boolean
     */
    private boolean isAssignment(String lineArg) {
        return !intermediateClause(lineArg) && !isStartingClause(lineArg)
            && StringUtils.contains(lineArg, ".se");
    }
    /**
     * 
     * @param lineArg
     * @return boolean
     */
    private boolean intermediateClause(String lineArg) {
        return lineArg.contains(".th") || lineArg.contains(".el") || lineArg.contains(".or")
            || lineArg.contains(".an") || (lineArg.contains(".th") && lineArg.contains(".se"));
    }
    /**
     * 
     * @param lineArg
     * @return boolean
     */
    private boolean isStartingClause(String lineArg) {
        return StringUtils.contains(lineArg, ".if ");
    }
    /**
     * 
     * @param lineArg
     * @return boolean
     */
    private boolean isFieldAssignment(String lineArg) {
        return lineArg.startsWith("[") && lineArg.endsWith("]") && lineArg.contains(",");
    }
}
