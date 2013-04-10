/*
 * BestAsfParser
 * AssignmentsAndBlocks
 * Created on 05.04.2013
 */

package asf.parser.blocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * AssignmentsAndBlocks.
 * @author vladutb
 * @version 1.0
 * @since 05.04.2013
 * @see 
 */
public class AssignmentsAndBlocks {
    /** <code>assignments</code> */
    private List<String> assignments = new ArrayList<String>();
    /** <code>blocks</code> */
    private Set<String> blocks = new HashSet<String>();
    /**
     * 
     * @param assignment
     */
    public void addAssignment(String assignment) {
        this.assignments.add(assignment);
    }
    /**
     * 
     * @return
     */
    public String getLastAssignment() {
        if (this.assignments.size() == 0)
            return null;
        return this.assignments.get(this.assignments.size() - 1);
    }
    /**
     * 
     * @return
     */
    public Set<String> getAllBlocks() {
        return this.blocks;
    }
    /**
     * 
     * @param block
     */
    public void addBlock(String block) {
        //        boolean alreadyHere = false;
        //        for (String bl : blocks) {
        //            if (bl.equals(block)) {
        //                alreadyHere = true;
        //                break;
        //            }
        //        }
        //        if (!alreadyHere)
        this.blocks.add(block);
    }
    /**
     * Liefert alle Blocks zurück
     * @return
     */
    public String getMergedBlocks() {
        StringBuilder builder = new StringBuilder();
        for (String block : this.blocks) {
            builder.append(block);
            builder.append("\n").append(".el ");
        }
        if (blocks.size() > 0) {
            int index = builder.length();
            builder.delete(index - 4, index);
        }
        return builder.toString().trim();
    }
}
