package hk.edu.polyu.comp.comp2021.cvfs.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a directory.
 */
public class Directory extends File {

    // Files directly stored in this directory.
    private final List<File> files;

    /**
     * Constructor.
     *
     * @param name The name of the file.
     * @throws IllegalArgumentException if the file name is invalid.
     */
    public Directory(String name) throws IllegalArgumentException {
        super(name);
        this.files = new LinkedList<>();
    }

    /**
     * @return The list of files directly contained in this directory.
     */
    public List<File> getFiles() {
        return files;
    }

    @Override
    public long getSize() {
        long total = 0;
        for (File file : this.files) {
            total = total + file.getSize();
        }
        return total + EMPTY_DIR_SIZE;
    }

    @Override
    public String toString() {
        return "[Dir] Name: " + getName() + ", Size: " + getSize() + " bytes";
    }
}
