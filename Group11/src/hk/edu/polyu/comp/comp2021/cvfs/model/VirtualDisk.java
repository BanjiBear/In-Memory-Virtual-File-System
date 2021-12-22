package hk.edu.polyu.comp.comp2021.cvfs.model;

import hk.edu.polyu.comp.comp2021.cvfs.model.criteria.FileCriterion;
import hk.edu.polyu.comp.comp2021.cvfs.model.criteria.IsDocumentCriterion;
import hk.edu.polyu.comp.comp2021.cvfs.model.criteria.SimpleCriterion;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Represents a virtual disk.
 */
public class VirtualDisk implements Serializable {
    private static final long serialVersionUID = 1;

    // a map of criteria
    private Map<String, FileCriterion> criteriaMap;

    // the maximum disk size.
    private final long maxSize;

    // the root directory.
    private final Directory rootDir;

    // a stack of directories the user has navigated to.
    private Stack<Directory> directoryStack;

    /**
     * Constructor.
     *
     * @param maxSize maximum size of the virtual disk.
     */
    public VirtualDisk(long maxSize) {
        this.maxSize = maxSize;
        rootDir = new Directory("root");
        directoryStack = new Stack<>();
        // create the the criteria map.
        criteriaMap = new HashMap<>();
        // add the IsDocument criterion
        SimpleCriterion isDocumentCri = new IsDocumentCriterion();
        criteriaMap.put(isDocumentCri.getName(), isDocumentCri);
    }

    /**
     * @return The current working directory.
     */
    public Directory getCurrentWorkingDirectory() {
        if (directoryStack.isEmpty()) {
            return rootDir;
        }
        return directoryStack.peek();
    }

    /**
     * Change the working directory to the parent
     */
    public void changeWorkingDirectoryToParent() {
        // the current directory stack is empty
        if (directoryStack.isEmpty()) {
            return;
        }

        // remove the current directory from the top of the stack
        directoryStack.pop();

        // if the stack is empty after the pop, then the parent is the root directory of the disk.
        if (directoryStack.isEmpty()) {
            changeWorkingDirectory(rootDir);
            return;
        }

        // switch to the directory on top of the stack
        changeWorkingDirectory(directoryStack.peek());
    }

    /**
     * @return The path to the current working directory.
     */
    public String getWorkkingDirPath() {
        // the stack is empty, so just return the name of the root directory.
        if (directoryStack.isEmpty()) {
            return rootDir.getName();
        }

        // the stack has some directories the user has navigated to. Concatenate
        // the directory names to get the path.
        StringBuilder builder = new StringBuilder();
        for (Directory directory : directoryStack) {
            builder.append(directory.getName()).append(":");
        }

        // remove the trailing ":" and concatenate with the 'root'
        return "root:" + builder.substring(0, builder.length() - 1);
    }


    /**
     * Change directory.
     *
     * @param directory The directory to switch to.
     */
    public void changeWorkingDirectory(Directory directory) {
        // the directory is the same as the current working directory. Do nothing.
        if (directory == getCurrentWorkingDirectory()) {
            return;
        }

        // push the new directory to the top of the stack.
        this.directoryStack.push(directory);
    }

    /**
     * @return The max size of this disk.
     */
    public long getMaxSize() {
        return maxSize;
    }

    /**
     * @return The criteria map.
     */
    public Map<String, FileCriterion> getCriteriaMap() {
        return criteriaMap;
    }

    /**
     * Navigate to the prevous directory.
     *
     * @param directory The previous directory.
     */
    public void changeWorkingDirectoryToPrevious() {
        directoryStack.pop();
    }
}
