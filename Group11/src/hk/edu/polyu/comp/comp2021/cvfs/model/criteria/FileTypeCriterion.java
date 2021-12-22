package hk.edu.polyu.comp.comp2021.cvfs.model.criteria;

import hk.edu.polyu.comp.comp2021.cvfs.model.Document;
import hk.edu.polyu.comp.comp2021.cvfs.model.File;

/**
 * The Type Criterion.
 */
public class FileTypeCriterion extends SimpleCriterion {

    /**
     * Constructor.
     *
     * @param criName Name of criterion.
     * @param op      The operator.
     * @param value   Criterion value.
     * @throws IllegalArgumentException if the criteria name is invalid.
     */
    public FileTypeCriterion(String criName, String op, String value) throws IllegalArgumentException {
        super(criName, op, value);
        validateParameters();
    }

    /**
     * Check params.
     * @throws IllegalArgumentException if the params are invalid.
     */
    private void validateParameters() throws IllegalArgumentException {
        if(!op.equals("equals")){
            throw new IllegalArgumentException("Operator should have the value: equals");
        }

        if(!value.startsWith("\"") || !value.endsWith("\"")){
            throw new IllegalArgumentException("The value must start with \" and end with \"");
        }
    }

    @Override
    public boolean validateFile(File file) {
        //
        if(!(file instanceof Document)){
            return false;
        }

        // remove the dobule quotes from input type.
        String inputType = value.substring(1, value.length() - 1);

        // do the comparison.
        Document doc = (Document) file;
        return doc.getType().equals(inputType);
    }

    @Override
    public String toString() {
        return "attr: type, op: " + op + ", val:" + value;
    }
}
