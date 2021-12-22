package hk.edu.polyu.comp.comp2021.cvfs.model.criteria;

import hk.edu.polyu.comp.comp2021.cvfs.model.File;

/**
 * Composite criterion built off from other criteria.
 */
public class NegationCompositeCriterion extends FileCriterion {
    private final FileCriterion targetCriterion;

    /**
     * Constructor.
     *
     * @param name the name of the composite criterion.
     * @param targetCriterion the simple criterion to negate.
     */
    public NegationCompositeCriterion(String name, FileCriterion targetCriterion) {
        super(name);
        this.targetCriterion = targetCriterion;
    }

    @Override
    public boolean validateFile(File file) {
        return !this.targetCriterion.validateFile(file);
    }

    @Override
    public String toString() {
        return "Negation of: " + targetCriterion;
    }
}
