package hk.edu.polyu.comp.comp2021.cvfs.model.criteria;

import hk.edu.polyu.comp.comp2021.cvfs.model.File;

/**
 * Represents a simple criterion.
 */
public abstract class SimpleCriterion extends FileCriterion{
    /**
     * The criterion operator.
     */
    protected final String op;

    /**
     * The criterion value.
     */
    protected final String value;

    /**
     * Constructor.
     *
     * @param criName Name of criterion.
     * @param op The operator.
     * @param value Criterion value.
     * @throws IllegalArgumentException if the criteria name is invalid.
     */
    protected SimpleCriterion(String criName, String op, String value) throws IllegalArgumentException{
        this(criName, op, value, true);
    }

    /**
     * Constructor.
     *
     * @param criName criterion name.
     * @param op operator.
     * @param value criterion value.
     * @param shouldValidateName whether we should validae the name or not.
     * @throws IllegalArgumentException if the name validation fails.
     */
    protected  SimpleCriterion(String criName, String op, String value, boolean shouldValidateName) throws IllegalArgumentException{
        super(criName, shouldValidateName);
        this.op = op;
        this.value = value;
    }

}
