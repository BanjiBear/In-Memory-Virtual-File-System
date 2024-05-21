# [2020] In Memory Virtual File System
## Introduction
The In Memory Virtual File System project is an implementation and simulation of the actual file system with self-defined commands. A typical VFS is usually built on top of a host file system to enable uniform access to files located in different host file systems. We named the simulation CVFS. The requirements of the CVFS include:
- [x] Develop a Virtual File System that operates on one virtual disk at a time
- [x] Provide a command line interface (CLI) tool to facilitate the use of virtual disks with specified commands
- [x] Use symbol ```$``` to denote the working directory and symbol ```:``` to separate the file names in a path
- [x] Each virtual disk has a maximum size and may contain many documents and directories allowed by that size
- [x] Each document is maintained by its ```name```, ```type```, and ```content```
- [x] Each directory is maintained by its ```name```
- [x] Only digits and English letters are allowed in file names, and each file name may have at most 10 characters
- [x] Only documents of types txt, java, html, and css are allowed in the system
- [x] Provide Unit test for the program

![image](https://github.com/BanjiBear/In-Memory-Virtual-File-System/assets/70761188/7f7850a3-3c37-4d81-bedf-3cb5ca86f3ab)

## Required Commands for CVFS
### newDisk
```
newDisk diskSize
```
Creates a new virtual disk with the specified maximum size. The previous working disk, if any, is closed; The newly created disk is set to be the working disk of the system, and the working directory is set to be the root directory of the disk.

### newDoc
```
newDoc docName docType docContent
```
Creates a new document in the working directory with the specified name, type, and content.

### newDir
```
newDir dirName
```
Creates a new directory in the working directory with the specified name.

### delete
```
delete fileName
```
Delete an existing file with the specified name from the working directory.

### rename
```
rename oldFileName newFileName
```
Rename an existing file in the working directory from ```oldFileName``` to ```newFileName```.

### changeDir
```
changeDir dirName
```
Change working directory to the new working directory; If dirName is ```..```, change to the parent directory.

### list
```
list
```
List all the files directly contained in the working directory. Including the name, type, and size. Report the total number and size of files listed.

### rList
```
rList
```
recursively listing all files in the working directory

### newSimpleCri
```
newSimpleCri criName attrName op val
```
Construct a simple criterion that can be referenced by criNam

### IsDocument
```
IsDocument
```
Evaluates to true if and only if on a document

### newNegation, newBinaryCri
```
newNegation criName1 criName2
newBinaryCri criName1 criName3 logicOp criName4
```
Construct a composite criterion that can be referenced by criName1. The new criterion constructed using command ```newNegation``` is the negation of an existing criterion named criName2; The new criterion constructed using command ```newBinaryCri``` is criName3 op criName4, where criName3 and criName4 are two existing criteria, while logicOp is either && or ||.

### printAllCriteria
```
printAllCriteria
```
Print out all the criteria defined

### search
```
search criName
```
List all the files directly contained in the working directory that satisfy criterion criName

### rSearch
```
rSearch criName
```
recursively List all the files directly contained in the working directory that satisfy criterion criName

### store, load
```
store
load
```
Support for store and load commands that store/load a virtual disk to/from the local file system.

### undo, redo
```
undo
redo
```
Support for undo and redo commands


## Program Installation and Execution
Currently missing the environment and dependency details here, any future investigation and update will be greatly appreciated.
The Virtual File System is developed in Java using the IntelliJ IDEA IDE (requested), the code can be compile and executed with proper java installed and dependency downloaded. 

## System Design and Imeplementation

<img width="332" alt="Screenshot 2024-05-21 at 12 16 03 PM" src="https://github.com/BanjiBear/In-Memory-Virtual-File-System/assets/70761188/24dee5a2-23a2-4a14-82c5-58b216affc78"><img width="482" alt="image" src="https://github.com/BanjiBear/In-Memory-Virtual-File-System/assets/70761188/a5195fb6-da16-4050-8bd9-15f537210885">

The UML diagram is shown in the figure above. the ```Application``` class is used to launch the application. It instantiates the ```CVFS```, ```UI``` and ```CommandController``` classes and then asks the ```UI``` to display the starter user interface, so that the user can proceed from there.
```Java
public class newApplication {
    public static void main(String[] args){

        // create the core CVFS object.
        CVFS cvfs = new CVFS();

        // create the cmmand controller object.
        CommandController commandController = new CommandController(cvfs);

        // create the UI, and launch the program.
        UI ui = new UI(commandController);
        ui.displayUI();
    }
}
```

The ```UI``` class is responsible for user interactivity with the program. It displays output to the console in a human readable format and accepts input from the user. The output includes file listing, command results, error messages and more. The ```UI``` class sends user commands to the ```CommandController``` class. The ```CommandController``` interprets commands and decides which operation to carry out based on the type of command. The ```CommandController``` class delegates command execution to the ```CVFS``` class.
```Java
public class UI {
    private final CommandController commandController;

    public UI(CommandController commandController) {
        this.commandController = commandController;
    }

    /**
     * Display interactive UI to work with
     */
    public void displayUI(){...}

    private String prompt(String message, Scanner scanner){
        System.out.print(message +  " # ");
        return scanner.nextLine().trim();
    }
}
```

The ```CVFS``` class contains all the business logic for the application. The class exposes methods to create files, documents and criteria. Other functions include listing and searching files in the working directory or recursively. The ```VirtualDisk``` class represents a virtual disk. There can only be one virtual disk at a time from the ```CVFS``` perspective. The virtual disk handles some disk related tasks such as getting the current working directory path and switching directories. 
```Java
public class CVFS {
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
    public VirtualDisk getVirtualDisk() {...}

    /**
     * Create a new virtual disk. Code should handle side effects such as
     * closing a previous virtual disk.
     * @param maxSize The max size of the virtual disk.
     */
    public void newDisk(long maxSize) {...}

    /**
     * Check if it is possible to add a new file to the system or not.
     * @param testFile The file to be added.
     */
    private void checkDiskSize(File testFile){...}

    /**
     * Create a new document in the current working directory.
     * @param docName The document name.
     * @param docType The type of the document.
     * @param docContent The content of the document.
     * @throws IllegalArgumentException if the validation of the parameters fails.
     */
    public void newDocument(String docName, String docType, String docContent) throws IllegalArgumentException{...}

    /**
     * @param directory The directory to search the file in.
     * @param fileName  The name of the file to search.
     * @return The file if found. Return null if no file is found.
     */
    public File findFile(Directory directory, String fileName) {...}

    /**
     * @param directory The directory to check.
     * @param fileName  The name of the file to be searched.
     * @return Whether a file with the given name exists.
     */
    private boolean fileExists(Directory directory, String fileName) {...}

    /**
     * Create a new directory in the current working directory.
     * @param name The name of the directory to be created.
     */
    public void newDirectory(String name) {...}

    /**
     * Delete existing file in the current working directory.
     * @param name The name of the file to be deleted.
     * @throws IllegalArgumentException if the file could not be deleted.
     */
    public void deleteFile(String name) throws IllegalArgumentException{...}


    /**
     * Rename file.
     * @param oldName Old name.
     * @param newName New name.
     * @throws IllegalArgumentException if the given file could not be renamed.
     */
    public void renameFile(String oldName, String newName) throws IllegalArgumentException{...}

    /**
     * Change to a new directory, and set it as the current working directory.
     * @param directoryName The directory to switch to.
     * @throws IllegalArgumentException if could not change dir.
     */
    public void changeDir(String directoryName) throws IllegalArgumentException{...}


    /**
     * List all the files in the current working directory.
     * @return List of files in the current working dir.
     */
    public List<File> list() {...}


    /**
     * List all the files recursively in the current working directory.
     * @return The list of files to be returned recursively.
     */
    public List<Bucket> rList() {...}


    /**
     * Inner class to be used for recursively reading
     * files in a given directory.
     */
    public static class Bucket {
        private final int level;
        private final List<File> files;

        public Bucket(int level, List<File> files) {
            this.level = level;
            this.files = files;
        }
    }

    /**
     * @return The path to the current working dir.
     */
    public String getWorkingDirPath() {...}

    /**
     * Create a new simple criterion.
     * @param criName The criterion name.
     * @param attrName The file attribute.
     * @param op The operator.
     * @param value The criterion value.
     * @throws IllegalArgumentException if validation of the attributes fails.
     */
    public void createSimpleCriterion(String criName, String attrName, String op, String value) throws IllegalArgumentException{...}

    /**
     * Create the undo/redo operations when creating a criterion.
     * @param criName The name of the criterion to be created.
     */
    private void createUndoRedoOpsForCreatedCriteria(String criName) {...}

    /**
     * Create a negation criterion.
     *
     * @param criName1 The name of the new negation criterion.
     * @param criName2 The name of the existing criterion.
     */
    public void createNegationCriterion(String criName1, String criName2) {...}

    /**
     * Create a composite binary criterion.
     * @param criName The name of the new criterion to be created.
     * @param criName1 First criterion name.
     * @param criName2 Second criterion name.
     * @param operand Operand.
     */
    public void createBinaryCriterion(String criName, String criName1, String criName2, String operand) {...}

    /**
     * Search for files that satisfy a predicate.
     *
     * @param directory     Directory to search in.
     * @param filePredicate The predicate.
     * @return Files placed in a bucket. The bucket also has how deep (the level) the files were found.
     */
    private List<Bucket> searchFiles(Directory directory, Predicate<File> filePredicate) {...}

    private List<File> filterFilesByPredicate(List<File> files, Predicate<File> filePredicate) {...}

    /**
     * Print all the criteria.
     * @return A list of all file criterion.
     */
    public List<FileCriterion> getAllCriteria() {...}

    /**
     * Search files in the working directory by criterion.
     * @param criterionName The
     * @return The list of files that satifisy the given criterion.
     * @throws IllegalArgumentException if the criterion specified isn't found.
     */
    public List<File> searchByCriterion(String criterionName) throws IllegalArgumentException {...}

    /**
     * Store the current virtual disk to file system.
     *
     * @param name The name of the file to store the virtual disk.
     * @throws Exception when saving to disk.
     */
    public void store(String name) throws Exception{...}

    /**
     * Load virtual disk from file system.
     * @param fileName The name of the file with the virtual file system.
     * @throws Exception if there is an IO error when loading from disk.
     */
    public void load(String fileName) throws Exception {...}


    private Predicate<File> getFilePredicateFromCriterionName(String criterionName){...}

    /**
     * Search recursively by criterion name.
     * @param criterionName The criterion name.
     * @return The list of files stored in buckets.
     */
    public List<Bucket> searchRecursivelyByCriterion(String criterionName) {...}

    /**
     * Redo last comand that was undone.
     */
    public void redo() {...}

    /**
     * Undo last command that was done.
     */
    public void undo() {...}
}
```

The ```VirtualDisk``` class maintains a hierarchy of ```File``` abstract class objects. Both ```Document ``` and ```Directory ``` class inherits the ```File``` class.

Other classes include ```FileCriterion```, ```SimpleCriterion```, ```Action```, and ```Bucket```, just to name a few.
