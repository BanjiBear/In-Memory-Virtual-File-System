package hk.edu.polyu.comp.comp2021.cvfs.model;

import hk.edu.polyu.comp.comp2021.cvfs.model.criteria.*;
import hk.edu.polyu.comp.comp2021.cvfs.view.UI;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static hk.edu.polyu.comp.comp2021.cvfs.model.File.EMPTY_DIR_SIZE;

/**
 * An implemntation of the CVFS interface.
 */
public class CVFS {
    /**
     * Default disk size when creating a new disk.
     */
    public static final int DEFAULT_DISK_SIZE = 600;

    // the current virtual disk the CVFS is working with.
    private VirtualDisk virtualDisk;

    // undo actions.
    private final Stack<BonusFeatures> undoActions;

    // redo actions
    private final Stack<BonusFeatures> redoActions;

    /**
     * Constructor.
     */
    public CVFS() {
        // create a default virtual disk
        virtualDisk = new VirtualDisk(DEFAULT_DISK_SIZE);

        // init the undo, redo stacks
        undoActions = new Stack<>();
        redoActions = new Stack<>();
    }

    /**
     * @return Virtual Disk.
     */
    public VirtualDisk getVirtualDisk() {
        return virtualDisk;
    }

    /**
     * Create a new virtual disk. Code should handle side effects such as
     * closing a previous virtual disk.
     * @param maxSize The max size of the virtual disk.
     */
    public void newDisk(long maxSize) {
        // create new virtual disk, and set it to be the current one.
        final VirtualDisk previousDisk = this.virtualDisk;
        final VirtualDisk newDisk = new VirtualDisk(maxSize);
        this.virtualDisk = newDisk;

        // specify the 'undo' operation
        Runnable undo = () -> {
            this.virtualDisk = previousDisk;
            UI.printSuccess("Undid Creation of Disk.");
        };

        // specify the 'redo' operation
        Runnable redo = () -> {
            this.virtualDisk = newDisk;
            UI.printSuccess("Redid Creation of Disk");
        };

        // create and save the opration to the 'undo' stack
        BonusFeatures action = new BonusFeatures(undo, redo);
        undoActions.push(action);
    }

    /**
     * Check if it is possible to add a new file to the system or not.
     * @param testFile The file to be added.
     */
    private void checkDiskSize(File testFile){
        long currTotalSize = 0;
        for (Bucket bucket : rList()) {
            for (File file : bucket.getFiles()) {
                currTotalSize = file.getSize() + currTotalSize;
            }
        }

        currTotalSize = currTotalSize + EMPTY_DIR_SIZE;
        long newTotalSize = currTotalSize + testFile.getSize();

        if(newTotalSize > getVirtualDisk().getMaxSize()){
            throw new IllegalStateException("Not enough space found on disk.");
        }
    }

    /**
     * Create a new document in the current working directory.
     * @param docName The document name.
     * @param docType The type of the document.
     * @param docContent The content of the document.
     * @throws IllegalArgumentException if the validation of the parameters fails.
     */

    public void newDocument(String docName, String docType, String docContent) throws IllegalArgumentException{

        // get the current working directory.
        Directory currentWorkingDirectory = virtualDisk.getCurrentWorkingDirectory();

        // check if the file exists
        if (fileExists(currentWorkingDirectory, docName)) {
            throw new IllegalArgumentException("A file with the same name exists. Check and try again.");
        }

        // create a new document now
        final Document document = new Document(docName, docType, docContent);

        // check that we should not exceed max disk size
        checkDiskSize(document);

        // save the document in the current working directory
        currentWorkingDirectory.getFiles().add(document);

        // specify the 'undo' operation
        Runnable undo = () -> {
            currentWorkingDirectory.getFiles().remove(document);
            UI.printSuccess("Undid Creation of Document");
        };

        // 'redo' operation
        Runnable redo = () -> {
            currentWorkingDirectory.getFiles().add(document);
            System.out.println("Redid Creation of Document");
        };

        // save action
        undoActions.add(new BonusFeatures(undo, redo));
    }

    /**
     * @param directory The directory to search the file in.
     * @param fileName  The name of the file to search.
     * @return The file if found. Return null if no file is found.
     */
    public File findFile(Directory directory, String fileName) {
        for (File file : directory.getFiles()) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    /**
     * @param directory The directory to check.
     * @param fileName  The name of the file to be searched.
     * @return Whether a file with the given name exists.
     */
    private boolean fileExists(Directory directory, String fileName) {
        File file = findFile(directory, fileName);
        return file != null;
    }

    /**
     * Create a new directory in the current working directory.
     * @param name The name of the directory to be created.
     */

    public void newDirectory(String name) {
        // get the current working directory.
        Directory currentWorkingDirectory = virtualDisk.getCurrentWorkingDirectory();

        // check if the file exists
        if (fileExists(currentWorkingDirectory, name)) {
            throw new IllegalArgumentException("A file with the same name exists. Check and try again.");
        }

        // create the directory.
        final Directory directory = new Directory(name);

        checkDiskSize(directory);

        // save the directory to the current working directory
        currentWorkingDirectory.getFiles().add(directory);

        // specify the undo, redo ops
        Runnable undo = () -> {
            currentWorkingDirectory.getFiles().remove(directory);
            System.out.println("\nUndid Creation of Directory \n");
        };

        Runnable redo = () -> {
            currentWorkingDirectory.getFiles().add(directory);
            System.out.println("\nRedid Creation of Directory \n");
        };

        undoActions.add(new BonusFeatures(undo, redo));
    }

    /**
     * Delete existing file in the current working directory.
     * @param name The name of the file to be deleted.
     * @throws IllegalArgumentException if the file could not be deleted.
     */

    public void deleteFile(String name) throws IllegalArgumentException{
        // get the current working dir
        Directory currentWorkingDir = virtualDisk.getCurrentWorkingDirectory();

        // pull out the target file to be deleted
        File targetFile = findFile(currentWorkingDir, name);

        // ensure the file exists.
        if (targetFile == null) {
            throw new IllegalArgumentException("The file was not found. Check and try again.");
        }

        // delete the file
        currentWorkingDir.getFiles().remove(targetFile);

        // specify the undo, redo ops
        Runnable undo = () -> {
            currentWorkingDir.getFiles().add(targetFile);
            UI.printSuccess("Undid delete file");
        };

        Runnable redo = () -> {
            currentWorkingDir.getFiles().remove(targetFile);
            UI.printSuccess("Redid delete file");
        };

        undoActions.add(new BonusFeatures(undo, redo));
    }


    /**
     * Rename file.
     * @param oldName Old name.
     * @param newName New name.
     * @throws IllegalArgumentException if the given file could not be renamed.
     */

    public void renameFile(String oldName, String newName) throws IllegalArgumentException{
        // get the current working directory
        Directory currentWorkingDir = virtualDisk.getCurrentWorkingDirectory();

        // find the target
        File targetFile = findFile(currentWorkingDir, oldName);

        // ensure the target file exists.
        if (targetFile == null) {
            throw new IllegalArgumentException("No file found with name: " + oldName);
        }

        // ensure the new name does not point to another existing ile
        if (fileExists(currentWorkingDir, newName)) {
            throw new IllegalArgumentException("A file already exists with the name: " + newName
                    + ". Check and try again");
        }

        String oldFileName = targetFile.getName();

        // rename the file
        targetFile.setName(newName);

        // specify the undo/redo ops
        Runnable undo = () -> {
            targetFile.setName(oldFileName);
            UI.printSuccess("Undid Rename File");
        };

        Runnable redo = () -> {
            targetFile.setName(newName);
            UI.printSuccess("Redid Rename File");
        };

        undoActions.add(new BonusFeatures(undo, redo));
    }

    /**
     * Change to a new directory, and set it as the current working directory.
     * @param directoryName The directory to switch to.
     * @throws IllegalArgumentException if could not change dir.
     */

    public void changeDir(String directoryName) throws IllegalArgumentException{
        // get the current working directory
        Directory currentWorkingDir = virtualDisk.getCurrentWorkingDirectory();

        // check if the directory to switch to is not the parent of the current working directory.
        if (!directoryName.trim().equals("..")) {
            // get the target directory
            File targetDirectory = findFile(currentWorkingDir, directoryName);
            if (targetDirectory == null) {
                throw new IllegalArgumentException("No such directory was found");
            }

            // ensure the file found is a directory
            if (!(targetDirectory instanceof Directory)) {
                throw new IllegalArgumentException("File given is not a directory");
            }

            // cast to get the target directory
            Directory targetDir = (Directory) targetDirectory;

            // switch over to the target directory
            virtualDisk.changeWorkingDirectory(targetDir);

            // create the undo/redo ops
            Runnable undo = () -> {
                virtualDisk.changeWorkingDirectoryToPrevious();
                UI.printSuccess("Undid change directory");
            };

            Runnable redo = () -> {
                virtualDisk.changeWorkingDirectory(targetDir);
                UI.printSuccess("Redid change directory");
            };

            undoActions.add(new BonusFeatures(undo, redo));
        } else {
            Directory prevWorkdir = virtualDisk.getCurrentWorkingDirectory();

            // the directory to switch to is the parent of the current directory.
            virtualDisk.changeWorkingDirectoryToParent();

            Runnable undo = () -> {
                virtualDisk.changeWorkingDirectory(prevWorkdir);
                UI.printSuccess("Undid change directory");
            };

            Runnable redo = () -> {
                virtualDisk.changeWorkingDirectoryToParent();
                UI.printSuccess("Redid change directory");
            };

            undoActions.add(new BonusFeatures(undo, redo));
        }
    }


    /**
     * List all the files in the current working directory.
     * @return List of files in the current working dir.
     */

    public List<File> list() {
        // get the working dir
        Directory workingDirectory = virtualDisk.getCurrentWorkingDirectory();
        return workingDirectory.getFiles();
    }


    /**
     * List all the files recursively in the current working directory.
     * @return The list of files to be returned recursively.
     */
    public List<Bucket> rList() {
        // get the working dir
        Directory workingDirectory = virtualDisk.getCurrentWorkingDirectory();

        List<Bucket> files = new ArrayList<>();

        Deque<Bucket> bucketQueue = new ArrayDeque<>();
        Bucket firstBucket = new Bucket(0, workingDirectory.getFiles());
        bucketQueue.add(firstBucket);

        while (!bucketQueue.isEmpty()) {
            Bucket targetBucket = bucketQueue.removeFirst();
            files.add(targetBucket);
            for (File file : targetBucket.getFiles()) {
                if (file instanceof Directory) {
                    Directory dir = (Directory) file;
                    Bucket bucket = new Bucket(targetBucket.getLevel() + 1, dir.getFiles());
                    bucketQueue.add(bucket);
                }
            }
        }
        return files;
    }


    /**
     * Inner class to be used for recursively reading
     * files in a given directory.
     */
    public static class Bucket {
        private final int level;
        private final List<File> files;

        /**
         * Constructor.
         *
         * @param level The level the files are away from the working directory.
         * @param files The files in the given level
         */
        public Bucket(int level, List<File> files) {
            this.level = level;
            this.files = files;
        }

        /**
         * @return The files in the bucket.
         */
        public List<File> getFiles() {
            return files;
        }

        /**
         * @return The level the bucket represents.
         */
        public int getLevel() {
            return level;
        }
    }

    /**
     * @return The path to the current working dir.
     */

    public String getWorkingDirPath() {
        return virtualDisk.getWorkkingDirPath();
    }

    /**
     * Create a new simple criterion.
     * @param criName The criterion name.
     * @param attrName The file attribute.
     * @param op The operator.
     * @param value The criterion value.
     * @throws IllegalArgumentException if validation of the attributes fails.
     */

    public void createSimpleCriterion(String criName, String attrName, String op, String value) throws IllegalArgumentException{
        Map<String, FileCriterion> criteriaMap = virtualDisk.getCriteriaMap();
        switch (attrName) {
            case "name":
                criteriaMap.put(criName, new FileNameCriterion(criName, op, value));
                break;
            case "size":
                criteriaMap.put(criName, new FileSizeCriterion(criName, op, value));
                break;
            case "type":
                criteriaMap.put(criName, new FileTypeCriterion(criName, op, value));
                break;
        }

        createUndoRedoOpsForCreatedCriteria(criName);
    }

    /**
     * Create the undo/redo operations when creating a criterion.
     * @param criName The name of the criterion to be created.
     */
    private void createUndoRedoOpsForCreatedCriteria(String criName) {
        Map<String, FileCriterion> criteriaMap = virtualDisk.getCriteriaMap();

        FileCriterion createdCriterion = criteriaMap.get(criName);

        // specify the undo/redo ops
        Runnable undo = () -> {
            criteriaMap.remove(criName);
            System.out.println("\nUndid creation of the criterion\n");
        };

        Runnable redo = () -> {
            criteriaMap.put(criName, createdCriterion);
            System.out.println("\nRedid creation of the criterion\n");
        };

        undoActions.add(new BonusFeatures(undo, redo));
    }

    /**
     * Create a negation criterion.
     *
     * @param criName1 The name of the new negation criterion.
     * @param criName2 The name of the existing criterion.
     */

    public void createNegationCriterion(String criName1, String criName2) {
        Map<String, FileCriterion> criteriaMap = virtualDisk.getCriteriaMap();
        if (!criteriaMap.containsKey(criName2)) {
            throw new IllegalArgumentException("No criterion was found with name: " + criName2);
        }

        FileCriterion targetFileCriterion = criteriaMap.get(criName2);
        criteriaMap.put(criName1, new NegationCompositeCriterion(criName1, targetFileCriterion));

        // define the undo/redo ops
        createUndoRedoOpsForCreatedCriteria(criName1);
    }

    /**
     * Create a composite binary criterion.
     * @param criName The name of the new criterion to be created.
     * @param criName1 First criterion name.
     * @param criName2 Second criterion name.
     * @param operand Operand.
     */

    public void createBinaryCriterion(String criName, String criName1, String criName2, String operand) {
        Map<String, FileCriterion> criteriaMap = virtualDisk.getCriteriaMap();
        if (!criteriaMap.containsKey(criName1)) {
            throw new IllegalArgumentException("Criterion not found: " + criName1);
        }

        if (!criteriaMap.containsKey(criName2)) {
            throw new IllegalArgumentException("Criterion not found: " + criName2);
        }

        FileCriterion criterion1 = criteriaMap.get(criName1);
        FileCriterion criterion2 = criteriaMap.get(criName2);

        criteriaMap.put(criName, new BinaryCompositeCriterion(criName, operand, criterion1, criterion2));

        // define the undo/redo ops
        createUndoRedoOpsForCreatedCriteria(criName);
    }

    /**
     * Search for files that satisfy a predicate.
     *
     * @param directory     Directory to search in.
     * @param filePredicate The predicate.
     * @return Files placed in a bucket. The bucket also has how deep (the level) the files were found.
     */
    private List<Bucket> searchFiles(Directory directory, Predicate<File> filePredicate) {
        List<File> files = directory.getFiles();
        List<File> filteredFiles = filterFilesByPredicate(files, filePredicate);
        return Collections.singletonList(new Bucket(0, filteredFiles));
    }

    private List<File> filterFilesByPredicate(List<File> files, Predicate<File> filePredicate) {
        return files.stream()
                .filter(filePredicate)
                .collect(Collectors.toList());
    }

    /**
     * Print all the criteria.
     * @return A list of all file criterion.
     */

    public List<FileCriterion> getAllCriteria() {
        return new ArrayList<>(virtualDisk.getCriteriaMap().values());
    }

    /**
     * Search files in the working directory by criterion.
     * @param criterionName The
     * @return The list of files that satifisy the given criterion.
     * @throws IllegalArgumentException if the criterion specified isn't found.
     */

    public List<File> searchByCriterion(String criterionName) throws IllegalArgumentException {
        // create the predicate.
        Predicate<File> fileCriterionPredicate = getFilePredicateFromCriterionName(criterionName);


        // search the files in the current working dir that satisfy the predicate.
        List<File> files = searchFiles(getVirtualDisk().getCurrentWorkingDirectory(), fileCriterionPredicate)
                .get(0).getFiles();
        return files;
    }

    /**
     * Store the current virtual disk to file system.
     *
     * @param name The name of the file to store the virtual disk.
     * @throws Exception when saving to disk.
     */

    public void store(String name) throws Exception{
        try (
                // create output stream to store the virtual disk.
                OutputStream outputStream = new FileOutputStream(name);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            // save the virtual disk to file system.
            objectOutputStream.writeObject(virtualDisk);
            objectOutputStream.flush();
            UI.printSuccess("Stored the current virtual disk successfully");
        }
    }

    /**
     * Load virtual disk from file system.
     * @param fileName The name of the file with the virtual file system.
     * @throws Exception if there is an IO error when loading from disk.
     */

    public void load(String fileName) throws Exception {
        try (
                InputStream inputStream = new FileInputStream(fileName);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
        ) {

            VirtualDisk prevDisk = this.virtualDisk;

            VirtualDisk newVirtualDisk = (VirtualDisk) objectInputStream.readObject();
            this.virtualDisk = newVirtualDisk;

            UI.printSuccess("Read virtual disk successfully!");

            // create the undo/redo ops
            Runnable undo = () -> {
                this.virtualDisk = prevDisk;
                UI.printSuccess("Undid loading of virtual disk");
            };

            Runnable redo = () -> {
                this.virtualDisk = newVirtualDisk;
                UI.printSuccess("Redid loading of virtual disk");
            };

            undoActions.push(new BonusFeatures(undo, redo));
        }
    }


    private Predicate<File> getFilePredicateFromCriterionName(String criterionName){
        Map<String, FileCriterion> criteriaMap = virtualDisk.getCriteriaMap();

        if (!criteriaMap.containsKey(criterionName)) {
            throw new IllegalArgumentException("No criterion found with name: " + criterionName);
        }

        // find the criterion
        final FileCriterion fileCriterion = criteriaMap.get(criterionName);

        // create the predicate.
        return fileCriterion::validateFile;
    }

    /**
     * Search recursively by criterion name.
     * @param criterionName The criterion name.
     * @return The list of files stored in buckets.
     */
    public List<Bucket> searchRecursivelyByCriterion(String criterionName) {
        // create the predicate.
        Predicate<File> fileCriterionPredicate = getFilePredicateFromCriterionName(criterionName);

        List<Bucket> allFiles = rList();
        List<Bucket> allFilesFiltered = new ArrayList<>();
        for (Bucket fileBucket : allFiles) {
            List<File> files = fileBucket
                    .getFiles()
                    .stream()
                    .filter(fileCriterionPredicate)
                    .collect(Collectors.toList());

            if(!files.isEmpty()){
                allFilesFiltered.add(new Bucket(fileBucket.getLevel(), files));
            }
        }
        return allFilesFiltered;
    }

    /**
     * Redo last comand that was undone.
     */

    public void redo() {
        if (redoActions.isEmpty()) {
            throw new IllegalStateException("No action found to be redone");
        }

        BonusFeatures action = redoActions.pop();
        action.redo();
        undoActions.push(action);
    }

    /**
     * Undo last command that was done.
     */

    public void undo() {
        if (undoActions.isEmpty()) {
            throw new IllegalStateException("No action found to be undone");
        }

        BonusFeatures action = undoActions.pop();
        action.undo();
        this.redoActions.push(action);
    }
}
