package hk.edu.polyu.comp.comp2021.cvfs.model.criteria;

import hk.edu.polyu.comp.comp2021.cvfs.model.File;

/**
 * The name criterion.
 */
public class FileNameCriterion extends SimpleCriterion {
    /**
     * Constructor.
     *
     * @param criName Name of criterion.
     * @param op      The operator.
     * @param value   Criterion value.
     * @throws IllegalArgumentException if the criteria name is invalid.
     */
    public FileNameCriterion(String criName, String op, String value) throws IllegalArgumentException {
        super(criName, op, value);
        validateParameters();
    }

    /**
     * Ensure the parameters are valid.
     * @throws IllegalArgumentException If the parameters are invalid.
     */
    private void validateParameters() throws IllegalArgumentException{
        if(!op.equals("contains")){
            throw new IllegalArgumentException("Operation should be: contains");
        }

        if(!value.startsWith("\"") || !value.endsWith("\"")){
            throw new IllegalArgumentException("Value should be between double quotes");
        }
    }

    @Override
    public boolean validateFile(File file) {
        String nameWthoutQuotes = this.value.substring(1, value.length() - 1);
        return file.getName().contains(nameWthoutQuotes);
    }

    @Override
    public String toString() {
        return "attr: name, op: " + op + ", val:" + value;
    }
}
