package hk.edu.polyu.comp.comp2021.cvfs.controller;

import hk.edu.polyu.comp.comp2021.cvfs.model.CVFS;
import hk.edu.polyu.comp.comp2021.cvfs.model.File;
import hk.edu.polyu.comp.comp2021.cvfs.model.criteria.FileCriterion;
import hk.edu.polyu.comp.comp2021.cvfs.view.UI;

import java.util.List;

/**
 * The command controller interpets each
 * command and invokes the appropriate
 * service from the CVFS.
 */
public class CommandController {
    private final CVFS cvfs;

    /**
     * Constructor.
     *
     * @param cvfs The CVFS instance on which to forward command requests.
     */
    public CommandController(CVFS cvfs) {
        this.cvfs = cvfs;
    }

    /**
     * @return Return the path of the current working directory.
     */
    public String getCurrentPath() {
        return cvfs.getWorkingDirPath();
    }

    private Long parseLongOrNull(String longStr) {
        try {
            return Long.parseLong(longStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param command The command to be executed.
     */
    public void executeCommand(String command) {
        String[] commandSplit = command.split(" ");
        switch (commandSplit[0]) {
            case "newDisk":
                // check the parts of the 'newDisk' command.
                if (commandSplit.length != 2) {

                    UI.printError("Could not create disk due " +
                            "to bad command format. Try again with:" +
                            " newDisk <diskSize> where '<diskSize>' is the size of the disk");
                    return;
                }

                // check the value for the disksize.
                Long diskSize = parseLongOrNull(commandSplit[1]);
                if (diskSize == null) {
                    UI.printError("Bad value for the the disksize: " + commandSplit[1]);
                    return;
                }

                // now execute the command.
                cvfs.newDisk(diskSize);

                UI.printSuccess("Created new disk successfully!");
                break;
            case "newDoc":
                // check if the parts of the 'newDoc' command
                if (commandSplit.length < 4) {
                    System.out.println("\nCould not create new document due to a bad command format. Try again" +
                            " with: newDoc docName docType docContent\n");
                    return;
                }


                // group the values from the third index all the way the last array index
                StringBuilder builder = new StringBuilder();
                for(int i = 3; i < commandSplit.length; i++){
                    builder.append(commandSplit[i]);
                }

                // execute the command
                try {
                    cvfs.newDocument(commandSplit[1], commandSplit[2], builder.toString());
                    UI.printSuccess("The document was created successfully!");
                } catch (Exception e){
                    UI.printError(e.getMessage());
                }
                break;
            case "newDir":
                // check if the parts of the 'newDir' are okay
                if (commandSplit.length != 2) {

                    UI.printError("Could not create directory due to a bad command format. " +
                            "Try again with: " + "newDir dirName");
                    return;
                }

                // execute the command
                try {
                    cvfs.newDirectory(commandSplit[1]);
                    UI.printSuccess("Created directory successfully!");
                } catch (Exception e){
                    UI.printError(e.getMessage());
                }
                break;
            case "delete":
                // check if the parts of the 'delete' are okay
                if (commandSplit.length != 2) {
                    UI.printError("Could not delete file due a bad command format. " +
                            "Try again with: delete fileName");
                    return;
                }

                try {
                    // execute the command
                    cvfs.deleteFile(commandSplit[1]);
                    UI.printSuccess("Deleted file successfully!");
                } catch (Exception e){
                    UI.printError(e.getMessage());
                }
                break;
            case "rename":
                // check if the parts of the 'rename' command are okay.
                if (commandSplit.length != 3) {
                    UI.printError("Bad command format. Try again with: rename oldName newName");
                    return;
                }

                try {
                    // execute the rename command
                    cvfs.renameFile(commandSplit[1], commandSplit[2]);
                    UI.printSuccess("Renamed file successfully");
                } catch (Exception e){
                    UI.printError(e.getMessage());
                }
                break;
            case "changeDir":
                // check command format
                if (commandSplit.length != 2) {
                    UI.printError("Bad comand format. Try again with: changeDir dirName");
                    return;
                }

                try {
                    // execute command
                    cvfs.changeDir(commandSplit[1]);
                    UI.printSuccess("Changed to new directory");
                } catch (Exception e){
                    UI.printError(e.getMessage());
                }

                break;
            case "list":
                // execute the list command
                List<File> fileList = cvfs.list();
                UI.printFileList(fileList);
                break;
            case "rList":
                // execute the rList command
                List<CVFS.Bucket> fileBuckets = cvfs.rList();
                UI.printFilesRecursively(fileBuckets);
                break;
            case "newSimpleCri":
                // check the command format
                if (commandSplit.length != 5) {

                    UI.printError("Bad command format. " +
                            "Try again with: newSimpleCri criName attrName op val");
                    return;
                }
                try {
                    cvfs.createSimpleCriterion(commandSplit[1], commandSplit[2], commandSplit[3], commandSplit[4]);
                    UI.printSuccess("Created criterion: " + commandSplit[1] + " successfully!");
                } catch (Exception e){
                    UI.printError(e.getMessage());
                }
                break;
            case "newNegation":
                if (commandSplit.length != 3) {
                    UI.printError("Bad command formt. Try again with: newNegation criName1 criName2");
                    return;
                }

                try {
                    cvfs.createNegationCriterion(commandSplit[1], commandSplit[2]);
                    UI.printSuccess("Created criterion: " + commandSplit[1] + " successfully!");
                } catch (Exception e){
                    UI.printError(e.getMessage());
                }
                break;

            case "newBinaryCri":
                if (commandSplit.length != 5) {
                    UI.printError("Bad comand format. " +
                            "Try again with: newBinaryCri criName criName1 logicOp criName2");
                    return;
                }
                try {
                    cvfs.createBinaryCriterion(commandSplit[1], commandSplit[2], commandSplit[4], commandSplit[3]);
                    UI.printSuccess("Created criterion: " + commandSplit[1] + " successfully");
                } catch (Exception e){
                    UI.printError(e.getMessage());
                }
                break;

            case "printAllCriteria":
                List<FileCriterion> criteria = cvfs.getAllCriteria();
                UI.printAllCriteria(criteria);
                break;

            case "search":
                if(commandSplit.length != 2){
                    UI.printError("Bad command format. Try again with: search criName");
                    return;
                }

                try {
                    List<File> files = cvfs.searchByCriterion(commandSplit[1]);
                    UI.printFileList(files);
                } catch (Exception e){
                    UI.printError(e.getMessage());
                }

                break;
            case "rSearch":
                if(commandSplit.length != 2){
                    UI.printError("Bad command format. Try again with: rSearch criName");
                    return;
                }

                try {
                    List<CVFS.Bucket> files = cvfs.searchRecursivelyByCriterion(commandSplit[1]);
                    UI.printFilesRecursively(files);
                } catch (Exception e){
                    UI.printError(e.getMessage());
                }
                break;
            case "undo":

                if(commandSplit.length != 1) {
                    UI.printError("Bad command format. Try again with: undo");
                    return;
                }

                try {
                    cvfs.undo();
                } catch (Exception e){
                    UI.printError(e.getMessage());
                }

                break;
            case "redo":
                if(commandSplit.length != 1){
                    UI.printError("Bad command format. Try again with: redo");
                    return;
                }
                cvfs.redo();
                break;
            case "store":
                if(commandSplit.length != 2){
                    UI.printError("Bad command format. Try again with: store fileName");
                    return;
                }
                try {
                    cvfs.store(commandSplit[1]);
                } catch (Exception e) {
                    UI.printError(e.getMessage());
                }
                break;
            case "load":
                if(commandSplit.length != 2){
                    UI.printError("Bad command format. Try again with: load fileName");
                    return;
                }

                try {
                    cvfs.load(commandSplit[1]);
                } catch (Exception e){
                    UI.printError(e.getMessage());
                }
                break;
            default:
                UI.printError("Bad command. Check and try again.");
        }

    }
}
