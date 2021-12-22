package hk.edu.polyu.comp.comp2021.cvfs.model.criteria;

import hk.edu.polyu.comp.comp2021.cvfs.model.Document;
import hk.edu.polyu.comp.comp2021.cvfs.model.File;

/**
 * Criterion to check if a file is a documebnt
 */
public class IsDocumentCriterion extends SimpleCriterion{
    /**
     * Constructor.
     */
    public IsDocumentCriterion(){
        super("IsDocument", "", "", false);
    }

    @Override
    public boolean validateFile(File file) {
        return file instanceof Document;
    }

    @Override
    public String toString() {
        return "IsDocument";
    }
}
