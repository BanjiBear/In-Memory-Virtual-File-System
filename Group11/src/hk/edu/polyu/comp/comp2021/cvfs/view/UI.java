package hk.edu.polyu.comp.comp2021.cvfs.view;

import hk.edu.polyu.comp.comp2021.cvfs.controller.CommandController;
import hk.edu.polyu.comp.comp2021.cvfs.model.CVFS;
import hk.edu.polyu.comp.comp2021.cvfs.model.File;
import hk.edu.polyu.comp.comp2021.cvfs.model.criteria.FileCriterion;

import java.util.*;

/**
 * The CVFS user interface.
 */
public class UI {
    private final CommandController commandController;


    /**
     * Constructor.
     *
     * @param commandController The command controller to which commands will be forwarded to
     *                          from the user.
     */
    public UI(CommandController commandController) {
        this.commandController = commandController;
    }

    /**
     * Display interactive UI to work with
     */
    public void displayUI(){
        // create scanner to read keyboard input.
        final Scanner scanner = new Scanner(System.in);

        // show the header info.
        System.out.println("Welcome to CVFS");
        System.out.println("Enter 'exit' to quit program.");
        System.out.println();


        String command  = "";
        while (true){
            // read the prompt from the user.
            command = prompt(commandController.getCurrentPath(), scanner);

            if(command.equalsIgnoreCase("exit")){
                System.out.println("Bye. :-)");
                break;
            }

            commandController.executeCommand(command);
        }
    }

    /**
     * Print a succcess message.
     * @param message message to be printed.
     */
    public static void printSuccess(String message){
        System.out.println("\nSuccess: " + message + "\n");
    }

    /**
     * Print files to the console.
     * @param fileList list of files returned.
     */
    public static void printFileList(List<File> fileList){
        // print the files in the current working directory, also compute the
        // total size during iteration.
        System.out.println("--- List Files -----");
        long totalSize = 0;
        for (File file : fileList) {
            System.out.println(file);
            totalSize = file.getSize() + totalSize;
        }

        System.out.println();
        System.out.println("Total Number of Files: " + fileList.size());
        System.out.println("Total File Sizes: " + totalSize + " bytes");
        System.out.println();
    }

    /**
     * Print file criteria to console.
     * @param fileCriteria The file criteria to be printed.
     */
    public static void printAllCriteria(List<FileCriterion> fileCriteria){
        System.out.println("--- File Criteria ----");
        fileCriteria.forEach(System.out::println);
    }

    /**
     * createSimpleCriterion
     * Print a file with a level.
     *
     * @param level The level.
     * @param file  The file to be printed.
     */
    private static void printFileWithLevel(int level, File file) {
        for (int i = 0; i <= level; i++) {
            System.out.print("  ");
        }
        System.out.println(file);
    }

    /**
     * @param fileBuckets Buckets of files.
     */
    public static void printFilesRecursively(List<CVFS.Bucket> fileBuckets) {
        // print files in the buckets provided.
        System.out.println("--- List Files Recursively -----");
        if (fileBuckets.isEmpty()) {
            return;
        }

        long totalSize = 0;
        long totalFileCount = 0;

        for (CVFS.Bucket fileBucket : fileBuckets) {
            for (File file : fileBucket.getFiles()) {
                printFileWithLevel(fileBucket.getLevel(), file);

                totalSize = totalSize + file.getSize();
                totalFileCount++;
            }
        }

        System.out.println();
        System.out.println("Total Number of Files: " + totalFileCount);
        System.out.println("Total Size: " + totalSize + " bytes");
        System.out.println();
    }

    /**
     * Print an error message
     * @param message The error message.
     */
    public static void printError(String message){
        System.out.println("\nError: " + message + "\n");
    }

    private String prompt(String message, Scanner scanner){
        System.out.print(message +  " # ");
        return scanner.nextLine().trim();
    }
}
