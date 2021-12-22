package hk.edu.polyu.comp.comp2021.cvfs.model.criteria;
import hk.edu.polyu.comp.comp2021.cvfs.model.File;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Represents any criterion used to filter files.
 */
public abstract class FileCriterion implements Serializable {
    private static final long serialVersionUID = 1;
    /**
     * Name of the criterion.
     */
    protected final String name;

    /**
     * Constructor.
     *
     * @param name criterion name.
     */
    protected FileCriterion(String name) {
        this(name, true);
    }

    /**
     * Constructor.
     *
     * @param name name of criterion.
     * @param shouldValidateName Whether we should validaate the name or not.
     * @throws IllegalArgumentException if the name fails validation.
     */
    protected FileCriterion(String name, boolean shouldValidateName) throws IllegalArgumentException{
        if(shouldValidateName && name.length() != 2){
            throw new IllegalArgumentException("Length of name is not 2: " + name.length());
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
        Matcher matcher = pattern.matcher(name);

        if(!matcher.matches()){
            throw new IllegalArgumentException("Only English letters and numbers are allowed");
        }

        this.name = name;
    }

    /**
     * @return The file name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param file The file to be validated against the criterion.
     * @return Whether the file matches the criterion or not.
     */
    public abstract boolean validateFile(File file);
}