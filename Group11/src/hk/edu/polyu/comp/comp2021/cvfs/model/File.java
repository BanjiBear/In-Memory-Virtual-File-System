package hk.edu.polyu.comp.comp2021.cvfs.model;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract class to represent a file. A file
 * could be a directory or a document.
 */
public abstract class File implements Serializable {
    private static final long serialVersionUID = 1;

    /**
     * empty dir size.
     */
    public static final int EMPTY_DIR_SIZE = 40;

    // The name of the file.
    private String name;

    /**
     * Constructor.
     *
     * @param name The name of the file.
     * @throws IllegalArgumentException if the file name is invalid.
     */
    public File(String name) throws IllegalArgumentException{
        validateFileName(name);
        this.name = name;
    }

    /**
     * @return The file size.
     */
    public abstract long getSize();

    /**
     * Validate the supplied file name.
     * @param fileName The filename to be validated.
     */
    private void validateFileName(String fileName) throws IllegalArgumentException{
        if(fileName.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");

        if(fileName.length() > 10)
            throw new IllegalArgumentException("Name cannot have more than 10 characters: " + fileName.length());

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
        Matcher matcher = pattern.matcher(fileName);

        if(!matcher.matches()){
            throw new IllegalArgumentException("Only English letters and numbers are allowed");
        }
    }

    /* --- Getters and Setters -- */
    /**
     * @return The name of the file.
     */
    public String getName(){
        return this.name;
    }

    /**
     * @param name The new file name.
     * @throws IllegalArgumentException if the new filename fails validation.
     */
    public void setName(String name) throws IllegalArgumentException{
        validateFileName(name);
        this.name = name;
    }
}

