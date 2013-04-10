/*
 * BestAsfParser
 * StringPair
 * Created on 08.04.2013
 */

package asf.parser.blocks;

import org.apache.commons.lang3.tuple.Pair;
import asf.parser.enums.BlockTypesEnum;

/**
 * StringPair.
 * @author vladutb
 * @version 1.0
 * @since 08.04.2013
 * @see Pair
 */
public class BlockPair extends Pair<BlockTypesEnum, String> {
    /** <code>leftValue</code> */
    private BlockTypesEnum leftValue;
    /** <code>rightValue</code> */
    private String rightValue;
    /**
     * StringPair Konstruktor.
     * @param left
     * @param right
     */
    public BlockPair(BlockTypesEnum left, String right) {
        this.leftValue = left;
        this.rightValue = right;
    }
    /**
     * 
     * @param value
     * @return String
     * @see java.util.Map.Entry#setValue(java.lang.Object)
     */
    @Override
    public String setValue(String value) {
        return null;
    }
    /**
     * 
     * @return <code>leftValue</code>
     * @see org.apache.commons.lang3.tuple.Pair#getLeft()
     */
    @Override
    public BlockTypesEnum getLeft() {
        return leftValue;
    }
    /**
     * 
     * @return <code>rightValue</code>
     * @see org.apache.commons.lang3.tuple.Pair#getRight()
     */
    @Override
    public String getRight() {
        return rightValue;
    }
}
