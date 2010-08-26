
/**
 * An object that represents a query
 * 
 * @author jenglert
 *
 */
public class QueryClause {

    /**
     * The parameter that we are searching in. 
     */
    private String parameter;
    
    /**
     * The operator we are comparing with.
     */
    private String operator;
    
    /**
     * The right hand side of the operator.
     */
    private String operand;
  
    /**
     * Determines whether the query clause is valid or not.
     * 
     * @return boolean true iff the query clause is valid.
     */
    public boolean isValid() {
        return parameter != null && operator != null && operand != null;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperand() {
        return operand;
    }

    public void setOperand(String operand) {
        this.operand = operand;
    }
}
