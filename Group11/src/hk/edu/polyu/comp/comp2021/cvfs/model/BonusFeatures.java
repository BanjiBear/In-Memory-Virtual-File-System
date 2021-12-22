package hk.edu.polyu.comp.comp2021.cvfs.model;

/**
 * Specifies an action that can be undone or redone.
 */
public class BonusFeatures {
    private final Runnable undoRunnable;
    private final Runnable redoRunnable;

    /**
     * Constructor.
     *
     * @param undoRunnable Runnable to be executed upon an 'undo'.
     * @param redoRunnable Runnable to be executed upon a 'redo'.
     */
    public BonusFeatures(Runnable undoRunnable, Runnable redoRunnable){
        this.undoRunnable = undoRunnable;
        this.redoRunnable = redoRunnable;
    }

    /**
     * Undo action.
     */
    void undo(){
        this.undoRunnable.run();
    }

    /**
     * Redo action.
     */
    void redo(){
        this.redoRunnable.run();
    }
}
