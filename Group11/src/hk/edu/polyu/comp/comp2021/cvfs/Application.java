package hk.edu.polyu.comp.comp2021.cvfs;

import hk.edu.polyu.comp.comp2021.cvfs.controller.CommandController;
import hk.edu.polyu.comp.comp2021.cvfs.model.CVFS;
import hk.edu.polyu.comp.comp2021.cvfs.view.UI;

/**
 * Driver class to launch the CVFS application.
 */
public class Application {

    /**
     * Main function.
     * @param args commandline args.
     */
    public static void main(String[] args){
        //
        // Initialize and utilize the system
        //


        // create the core CVFS object.
        CVFS cvfs = new CVFS();

        // create the cmmand controller object.
        CommandController commandController = new CommandController(cvfs);

        // create the UI, and launch the program.
        UI ui = new UI(commandController);
        ui.displayUI();
    }
}
