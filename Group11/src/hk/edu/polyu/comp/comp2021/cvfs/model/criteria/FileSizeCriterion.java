package hk.edu.polyu.comp.comp2021.cvfs.model.criteria;

import hk.edu.polyu.comp.comp2021.cvfs.model.File;

import java.util.Arrays;

/**
 * Criterian for file sizes.
 */
public class FileSizeCriterion extends SimpleCriterion {
    /**
     * Constructor.
     *
     * @param criName Name of criterion.
     * @param op      The operator.
     * @param value The criterion value.
     */
    public FileSizeCriterion(String criName, String op, String value) {
        super(criName, op, value);
        validateParameters();
    }

    /**
     * Validate the operator.
     */
    private void validateParameters() throws IllegalArgumentException{
        // ensure the operator provided works.
        if(!Arrays.asList(">", "<", ">=", "<=", "==", "!=").contains(this.op)){
            throw new IllegalArgumentException("The provided operator is invalid: " + this.op);
        }

        // check the value to ensure it is a numeric value.
        try {
            Long.parseLong(this.value);
        } catch (Exception e){
            throw new IllegalArgumentException("The provided value is a non-numeric value");
        }
    }

    @Override
    public boolean validateFile(File file) {
        long value = Long.parseLong(this.value);
        boolean returnValue = false;

        switch (this.op){
            case ">":
                returnValue = file.getSize() > value;
                break;
            case "<":
                returnValue = file.getSize() < value;
                break;
            case ">=":
                returnValue = file.getSize() >= value;
                break;
            case "<=":
                returnValue =  file.getSize() <= value;
                break;
            case "==":
                returnValue = file.getSize() == value;
                break;
            case "!=":
                returnValue = file.getSize()  != value;
                break;
        }
        return returnValue;
    }

    @Override
    public String toString() {
        return "attr: size, op: " + op + ", val: " + value;
    }
}
