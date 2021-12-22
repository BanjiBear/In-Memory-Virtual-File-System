package hk.edu.polyu.comp.comp2021.cvfs.model;

import java.util.Arrays;

/**
 * Represents a document in the CVFS.
 */
public class Document extends File{
    // The type of the document, such as 'txt'
    private final String type;

    // The content of the document.
    private final String content;

    /**
     * Constructor.
     *
     * @param name The name of the file.
     * @param content The content of the document.
     * @param type The type of the document.
     * @throws IllegalArgumentException if the file name or type is providied.
     */
    public Document(String name, String type, String content) throws IllegalArgumentException {
        super(name);
        validateDocumentType(type);
        this.type = type;
        this.content = content;
    }

    /**
     * Validate the document type.
     * @param type Document type.
     * @throws IllegalArgumentException if the type fails validation.
     */
    private void validateDocumentType(String type) throws IllegalArgumentException{
        if(!Arrays.asList("txt", "html", "java", "css").contains(type)){
            throw new IllegalArgumentException("Invalid document type: " + type
                    + ". Only 'txt', 'html', 'java' and 'css' are allowed");
        }
    }

    @Override
    public long getSize() {
        return EMPTY_DIR_SIZE + (content.length() * 2);
    }

    /**
     * @return The type of the document.
     */
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "[Doc] Name: " + getName() + ", Type: " + type + ", Size: " + getSize() + " bytes";
    }
}
