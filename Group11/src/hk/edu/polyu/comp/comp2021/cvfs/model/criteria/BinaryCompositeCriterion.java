package hk.edu.polyu.comp.comp2021.cvfs.model.criteria;

import hk.edu.polyu.comp.comp2021.cvfs.model.File;

import java.util.Arrays;

/**
 * Represents a composite criterion that evaluates two simple critera
 * based on an operand.
 */
public class BinaryCompositeCriterion extends FileCriterion {
    private final String operand;
    private final FileCriterion fileCriterion1;
    private final FileCriterion fileCriterion2;


    /**
     * Constructor.
     *
     * @param name The name of the criterion.
     * @param operand The operand to be used.
     * @param fileCriterion1 The first criterion.
     * @param fileCriterion2 The second criterion.
     * @throws IllegalArgumentException if validation of the params fails.
     */
    public BinaryCompositeCriterion(String name,
                                    String operand,
                                    FileCriterion fileCriterion1,
                                    FileCriterion fileCriterion2) throws IllegalArgumentException {
        super(name);
        this.operand = operand;
        this.fileCriterion1 = fileCriterion1;
        this.fileCriterion2 = fileCriterion2;

        checkParameters();
    }

    /**
     * Check params to ensure they are valid.
     */
    private void checkParameters(){
        if(!Arrays.asList("&&", "||").contains(this.operand)){
            throw new IllegalArgumentException("Invalid operand specified: " + this.operand);
        }
    }

    @Override
    public boolean validateFile(File file) {
        if (this.operand.equals("||")) {
            return fileCriterion1.validateFile(file) || fileCriterion2.validateFile(file);
        } else {
            return fileCriterion1.validateFile(file) && fileCriterion2.validateFile(file);
        }
    }

    @Override
    public String toString() {
        return fileCriterion1.toString() + " " + operand + " " + fileCriterion2.toString();
    }
}
