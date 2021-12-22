package hk.edu.polyu.comp.comp2021.cvfs.model;

import hk.edu.polyu.comp.comp2021.cvfs.model.criteria.FileCriterion;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Test suite for the methods in the CVFS class.
 */
public class CVFSTest {
    private CVFS cvfs;

    /**
     * Create the CVFS object.
     */
    @Before
    public void setUp() {
        this.cvfs = new CVFS();
    }

    /**
     * Test creation of new disk.
     */
    @Test
    public void testNewDisk() {
        cvfs.newDisk(200);

        assertNotNull(cvfs.getVirtualDisk());
        assertEquals(200, cvfs.getVirtualDisk().getMaxSize());
        assertEquals("root", cvfs.getVirtualDisk().getCurrentWorkingDirectory().getName());
    }

    /**
     * Test the addition of a new document.
     */
    @Test
    public void testNewDocument() {
        cvfs.newDocument("testDoc", "java", "nice-content");
        cvfs.newDocument("testDoc1", "html", "nice-content");
        cvfs.newDocument("testDoc2", "css", "nice-content");
        cvfs.newDocument("testDoc3", "css", "nice-content");
        List<File> files = cvfs.getVirtualDisk().getCurrentWorkingDirectory().getFiles();
        assertEquals(4, files.size());
    }

    /**
     * Should not be able to add documents with the same name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDuplicateDocument() {
        cvfs.newDocument("testDoc", "java", "nice-content");
        cvfs.newDocument("testDoc", "java", "lanlana");
    }

    /**
     * Test add new directories.
     */
    @Test
    public void testNewDir() {
        cvfs.newDirectory("testDir1");
        cvfs.newDirectory("testDir2");
        cvfs.newDirectory("testDoc3");
        List<File> files = cvfs.getVirtualDisk().getCurrentWorkingDirectory().getFiles();
        assertEquals(3, files.size());
    }

    /**
     * Shouldn't be able to add duplicate dirs.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNewDirDuplicate() {
        cvfs.newDirectory("testDir1");
        cvfs.newDirectory("testDir1");
    }

    /**
     * Test delete file.
     */
    @Test
    public void testDeleteFile() {
        cvfs.newDirectory("test1Dir");
        File file = cvfs.findFile(cvfs.getVirtualDisk().getCurrentWorkingDirectory(), "test1Dir");
        assertNotNull(file);
        cvfs.deleteFile("test1Dir");
        file = cvfs.findFile(cvfs.getVirtualDisk().getCurrentWorkingDirectory(), "test1Dir");
        assertNull(file);
    }

    /**
     * Should not be able to delete non existent file
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteNonExistentFile() {
        cvfs.deleteFile("nofile");
    }

    /**
     * Should rename file successfully.
     */
    @Test
    public void testRenameFile() {
        cvfs.newDirectory("oldname");
        File file = cvfs.findFile(cvfs.getVirtualDisk().getCurrentWorkingDirectory(), "oldname");
        cvfs.renameFile("oldname", "newname");
        assertEquals("newname", file.getName());
    }

    /**
     * Give an existing name when trying to rename file.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRenameFileExists(){
        cvfs.newDirectory("dir1");
        cvfs.newDirectory("dir2");
        cvfs.renameFile("dir1", "dir2");
    }

    /**
     * Should not rename a non existent file.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRenameNonExistentFile() {
        cvfs.renameFile("nosuch", "newname");
    }

    /**
     * Should change dir successfully.
     */
    @Test
    public void testChangeDir() {
        File rootDir = cvfs.getVirtualDisk().getCurrentWorkingDirectory();
        cvfs.newDirectory("dir1");
        File newDir = cvfs.findFile(cvfs.getVirtualDisk().getCurrentWorkingDirectory(), "dir1");
        cvfs.changeDir("dir1");
        File changedDir = cvfs.getVirtualDisk().getCurrentWorkingDirectory();
        assertEquals(newDir, changedDir);
        cvfs.changeDir("..");
        assertEquals(rootDir, cvfs.getVirtualDisk().getCurrentWorkingDirectory());
    }

    /**
     * Change to non-existent dir should fail.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testChangeDirNonExistent() {
        cvfs.changeDir("notexist");
    }

    /**
     * Change dir using a doc as an argument.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testChangeDirWithDoc() {
        cvfs.newDocument("test", "java", "code");
        cvfs.changeDir("test");
    }

    /**
     * Test get list of files in the working director.
     */
    @Test
    public void testListFiles() {
        cvfs.newDirectory("conf");
        cvfs.changeDir("conf");

        cvfs.newDirectory("test1");
        cvfs.newDirectory("test2");
        cvfs.newDocument("doc1", "java", "code");
        cvfs.newDocument("doc2", "css", "code");
        cvfs.newDocument("doc3", "html", "code");
        cvfs.newDocument("doc4", "java", "code");

        assertEquals(6, cvfs.list().size());
    }

    /**
     * Test if all the files can be printed recursively from the current working directory
     */
    @Test()
    public void testListFilesRecursively() {
        cvfs.newDirectory("test1");
        cvfs.newDirectory("test2");
        cvfs.newDirectory("conf");

        cvfs.changeDir("conf");
        cvfs.newDocument("doc1", "java", "code");
        cvfs.newDocument("doc2", "css", "code");

        cvfs.changeDir("..");

        cvfs.newDocument("doc3", "html", "code");
        cvfs.newDocument("doc4", "java", "code");
        cvfs.newDirectory("tap");

        cvfs.changeDir("tap");
        cvfs.newDocument("doc2", "css", "code");

        cvfs.changeDir("..");
        cvfs.changeDir("..");

        List<File> allFiles = new ArrayList<>();
        for (CVFS.Bucket fileBucket : cvfs.rList()) {
            allFiles.addAll(fileBucket.getFiles());
        }
        assertEquals(9, allFiles.size());
    }

    /**
     * Test working dir path.
     */
    @Test
    public void testGetWorkingDirPath() {
        cvfs.newDirectory("web");
        cvfs.changeDir("web");
        cvfs.newDirectory("tools");
        cvfs.changeDir("tools");

        assertEquals("root:web:tools", cvfs.getWorkingDirPath());
        cvfs.changeDir("..");
        assertEquals("root:web", cvfs.getWorkingDirPath());
    }


    /**
     * should successfully create simple criteria.
     */
    @Test
    public void testCreateCriteria(){
        Map<String, FileCriterion> criteriaMap = cvfs.getVirtualDisk().getCriteriaMap();

        cvfs.createSimpleCriterion("c1", "name", "contains", "\"sample\"");
        cvfs.createSimpleCriterion("c2", "type", "equals", "\"java\"");
        cvfs.createSimpleCriterion("c3", "size", ">", "34");
        cvfs.createNegationCriterion("c4", "c2");
        cvfs.createBinaryCriterion("c5", "c1", "c2", "&&");

        assertTrue(criteriaMap.containsKey("c1"));
        assertTrue(criteriaMap.containsKey("c2"));
        assertTrue(criteriaMap.containsKey("c3"));
        assertTrue(criteriaMap.containsKey("c4"));
        assertTrue(criteriaMap.containsKey("c5"));
    }

    /**
     * Test failure to create simple criterion with invalid values.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateSimpleCriterionWithInvalidParams(){
        cvfs.createSimpleCriterion("c1", "name", "aana", "alann");
    }

    /**
     * Negation of criterion should fail on non-existing criterion.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFailureNegationCriterion(){
        cvfs.createNegationCriterion("c1", "c10");
    }

    /**
     * Fails to create binary criterion since first param is invalid.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFirstCriterionForBinaryCriterion(){
        cvfs.createBinaryCriterion("cr1", "cr2", "cr3", "&&");
    }

    /**
     * Second arg for binary criterion is invalid.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSecondArgBinaryCriterion(){
        cvfs.createBinaryCriterion("cr1", "IsDocument", "cr3", "&&");
    }

    /**
     * Test if the search works for files in the current working directory.
     */
    @Test
    public void testSearch(){
        cvfs.newDirectory("dir1");
        cvfs.newDirectory("dir2");
        cvfs.newDirectory("dir3");
        cvfs.newDocument("doc1", "java", "code");
        cvfs.newDocument("doc2", "java", "code");
        cvfs.newDocument("doc3", "java", "code");
        cvfs.newDirectory("web");
        cvfs.changeDir("web");
        cvfs.newDocument("doc1", "html", "code");
        cvfs.newDocument("doc2", "java", "code");
        cvfs.newDocument("doc3", "java", "code");
        cvfs.changeDir("..");

        List<File> files = cvfs.searchByCriterion("IsDocument");
        assertEquals(3, files.size());

        List<CVFS.Bucket> fileBuckets = cvfs.searchRecursivelyByCriterion("IsDocument");

        List<File> rFiles = new ArrayList<>();
        for (CVFS.Bucket fileBucket : fileBuckets) {
            rFiles.addAll(fileBucket.getFiles());
        }
        assertEquals(6, rFiles.size());
    }

    /**
     * Test search files invalid criterion.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchInvalidCriterion(){
        cvfs.searchByCriterion("no-such-thing");
    }

    /**
     * Should not add file due to limited disk size.
     */
    @Test(expected = IllegalStateException.class)
    public void testExceedDiskSize(){
        cvfs.newDisk(10);
        cvfs.newDocument("doc", "java", "some sample content");
    }

    /**
     * Test the undo/redo feature.
     */
    @Test
    public void testUndoRedo(){
        // undo/redo file.
        cvfs.newDisk(200);
        cvfs.undo();
        assertEquals(CVFS.DEFAULT_DISK_SIZE, cvfs.getVirtualDisk().getMaxSize());
        cvfs.redo();
        assertEquals(200, cvfs.getVirtualDisk().getMaxSize());

        Function<String, File> getFile = name -> {
            return cvfs.findFile(cvfs.getVirtualDisk().getCurrentWorkingDirectory(), name);
        };

        // undo redo/dir
        cvfs.newDirectory("sample");
        cvfs.undo();
        assertNull(getFile.apply("sample"));
        cvfs.redo();
        assertNotNull(getFile.apply("sample"));

        // undo/redo document
        cvfs.newDocument("doc", "java", "kontent");
        cvfs.undo();
        assertNull(getFile.apply("doc"));
        cvfs.redo();
        assertNotNull(getFile.apply("doc"));

        // undo/redo rename file
        cvfs.renameFile("doc", "doc1");
        cvfs.undo();
        assertNull(getFile.apply("doc1"));
        cvfs.redo();
        assertNotNull(getFile.apply("doc1"));

        // undo/redo file delete
        cvfs.deleteFile("doc1");
        cvfs.undo();
        assertNotNull(getFile.apply("doc1"));
        cvfs.redo();
        assertNull(getFile.apply("doc1"));

        // undo/redo change dir
        cvfs.changeDir("sample");
        cvfs.undo();
        assertEquals("root", cvfs.getWorkingDirPath());
        cvfs.redo();
        assertEquals("root:sample", cvfs.getWorkingDirPath());
        cvfs.changeDir("..");
        cvfs.undo();
        assertEquals("root:sample", cvfs.getWorkingDirPath());
        cvfs.redo();
        assertEquals("root", cvfs.getWorkingDirPath());

        // undo/redo create criterion
        System.out.println(cvfs.getVirtualDisk().getCriteriaMap());
        cvfs.createNegationCriterion("ca", "IsDocument");
        cvfs.undo();
        assertFalse(cvfs.getVirtualDisk().getCriteriaMap().containsKey("ca"));
        cvfs.redo();
        assertTrue(cvfs.getVirtualDisk().getCriteriaMap().containsKey("ca"));
    }

    /**
     * Test a case where 'undo' is not possible.
     */
    @Test(expected = IllegalStateException.class)
    public void testInvalidUndoState(){
        cvfs.undo();
    }

    /**
     * Test where 'redo' is not possible.
     */
    @Test(expected = IllegalStateException.class)
    public void testInvalidRedoState(){
        cvfs.redo();
    }

    /**
     * Test load/store current work dir
     * @throws Exception if could not load file.
     */
    @Test
    public void testStoreAndLoadVirtualDisk() throws Exception {
        cvfs.newDisk(500);
        cvfs.store("test-save");
        java.io.File file = new java.io.File("test-save");
        assertTrue(file.isFile());
        cvfs.undo();
        cvfs.load("test-save");
        assertEquals(500, cvfs.getVirtualDisk().getMaxSize());
        cvfs.undo();
        assertEquals(CVFS.DEFAULT_DISK_SIZE, cvfs.getVirtualDisk().getMaxSize());
        cvfs.redo();
        assertEquals(500, cvfs.getVirtualDisk().getMaxSize());
    }

    /**
     * Test fina all defined criteria.
     */
    @Test
    public void testFindAllCriteria(){
        cvfs.createSimpleCriterion("c1", "size", ">", "45");
        cvfs.createSimpleCriterion("c2", "size", "<", "45");
        cvfs.createSimpleCriterion("c3", "size", "==", "45");
        cvfs.createSimpleCriterion("c4", "size", ">=", "45");

        assertEquals(5, cvfs.getAllCriteria().size());
    }
}
