/*
 * BestAsfParser
 * Condition
 * Created on 08.04.2013
 */

package asf.parser.conditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Condition.
 * @author vladutb
 * @version 1.0
 * @since 08.04.2013
 * @see 
 */
public class Condition {
    /**
     * ConditionAndValue.
     * @author vladutb
     * @version 1.0
     * @since 08.04.2013
     * @see 
     */
    public class ConditionAndValue {
        /** <code>condition</code> */
        private String condition;
        /** <code>value</code> */
        private boolean value;
        /**
         * ConditionAndValue Konstruktor.
         * @param condition
         * @param value
         */
        ConditionAndValue(String condition, boolean value) {
            this.condition = condition;
            this.value = value;
        }
        /**
         * 
         * @return
         */
        public String getCondition() {
            return condition;
        }
        /**
         * 
         * @return
         */
        public boolean getConditionValue() {
            return value;
        }
    }
    private List<ConditionAndValue> allConditions = new ArrayList<Condition.ConditionAndValue>();
    private String variableName;
    private String variableValue;
    /**
     * Accessor.
     * @return liefert <code>variableValue</code> zurück.
     */
    public String getVariableValue() {
        return variableValue;
    }
    /**
     * Accessor.
     * @param variableValue Wert für <code>variableValue</code>.
     */
    public void setVariableValue(String variableValue) {
        this.variableValue = variableValue;
    }
    /**
     * Accessor.
     * @return liefert <code>variableName</code> zurück.
     */
    public String getVariableName() {
        return variableName;
    }
    /**
     * Accessor.
     * @param variableName Wert für <code>variableName</code>.
     */
    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }
    /**
     * 
     * @param condition
     * @param value
     */
    public void addCondition(String condition, boolean value) {
        allConditions.add(new ConditionAndValue(condition, value));
    }
    public void addCondition(ConditionAndValue cav) {
        allConditions.add(cav);
    }
    public void replaceVariableValue(String localVar, String value) {
        if (value == null) {
            return;
        }
        variableValue = variableValue.replaceAll("&" + localVar + ".", value);
        variableValue = variableValue.replaceAll("&" + localVar, value);
    }
    public List<ConditionAndValue> getAllConditions() {
        return allConditions;
    }
    /**
     * 
     * @return
     * @throws CloneNotSupportedException
     * @see java.lang.Object#clone()
     */
    @Override
    public Condition clone() {
        Condition cloneCond = new Condition();
        cloneCond.setVariableName(variableName);
        cloneCond.setVariableValue(variableValue);
        for (ConditionAndValue anv : getAllConditions()) {
            cloneCond.addCondition(anv.condition, anv.value);
        }
        return cloneCond;
    }
}
