package org.hvdw.jimagefuser;


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This is the big setter/getter class for the entire program
 */
public class MyVariables {

    private final static MyVariables staticInstance = new MyVariables();

    private MyVariables() {
    }

    private int SelectedRow;
    private int SelectedColumn;
    private String SelectedImagePath;
    private File[] loadedFiles;
    private int[] selectedFilenamesIndices;
    private String cantdisplaypng;
    private String tmpWorkFolder;
    private File CurrentWorkFile;
    private File CurrentFileInViewer;
    private File SaveToFile;
    private String ExiftoolVersion;
    private List<List> tableRowsCells;
    private List<Integer> selectedIndicesList;
    private String delayedOutput;
    private HashMap<String, String> imgBasicData;
    private String[] commandLineArgs;
    private boolean commandLineArgsgiven = false;
    private int ScreenWidth;
    private int ScreenHeight;
    private String[] mainScreenParams;
    private HashMap<String, String> Parameters;
    private String FusedImage;

    // The actual getters and setters
    public static int getSelectedRow() { return staticInstance.SelectedRow;}
    public static void setSelectedRow(int index) {staticInstance.SelectedRow = index; }

    public static int getSelectedColumn() {
        return staticInstance.SelectedColumn;
    }
    public static void setSelectedColumn(int num) {
        staticInstance.SelectedColumn = num;
    }

    public static String getSelectedImagePath() {
        return staticInstance.SelectedImagePath;
    }
    public static void setSelectedImagePath(String selImgPath) {
        staticInstance.SelectedImagePath = selImgPath;
    }

    public static String getcantdisplaypng() {
        return staticInstance.cantdisplaypng;
    }
    public static void setcantdisplaypng(String pngPath) {
        staticInstance.cantdisplaypng = pngPath;
    }

    public static void setLoadedFiles(File[] loadedFiles) {
        staticInstance.loadedFiles = Arrays.copyOf(loadedFiles, loadedFiles.length);
    }
    public static File[] getLoadedFiles() {
        return Arrays.copyOf(staticInstance.loadedFiles, staticInstance.loadedFiles.length);
    }

    public static String gettmpWorkFolder() {
        return staticInstance.tmpWorkFolder;
    }
    public static void settmpWorkFolder( String tmpworkfldr) {
        staticInstance.tmpWorkFolder = tmpworkfldr;
    }

    public static File getCurrentWorkFile() {
        return staticInstance.CurrentWorkFile;
    }
    public static void setCurrentWorkFile(File file) {
        staticInstance.CurrentWorkFile = file;
    }

    public static File getCurrentFileInViewer() { return staticInstance.CurrentFileInViewer; }
    public static void setCurrentFileInViewer(File file) { staticInstance.CurrentFileInViewer = file; }

    public static File getSaveToFile() { return staticInstance.SaveToFile; }
    public static void setSaveToFile(File file) {staticInstance.SaveToFile = file; }

    public static int[] getSelectedFilenamesIndices() {
        return Arrays.copyOf(staticInstance.selectedFilenamesIndices, staticInstance.selectedFilenamesIndices.length);
    }
    public static void setSelectedFilenamesIndices(int[] selectedTableIndices) {
        staticInstance.selectedFilenamesIndices = Arrays.copyOf(selectedTableIndices,selectedTableIndices.length);
    }

    public static List<Integer> getselectedIndicesList() { return staticInstance.selectedIndicesList; }
    public static void setselectedIndicesList(List<Integer> selIndList) { staticInstance.selectedIndicesList = selIndList; }

    public static String getExiftoolVersion() {
        return staticInstance.ExiftoolVersion;
    }
    public static void setExiftoolVersion(String exv) {
        staticInstance.ExiftoolVersion = exv;
    }

    public static List<List> gettableRowsCells() { return staticInstance.tableRowsCells; }
    public static void settableRowsCells (List<List> tblRwsClls) {staticInstance.tableRowsCells = tblRwsClls; }

    public static HashMap<String, String> getimgBasicData () { return staticInstance.imgBasicData; };
    public static void setimgBasicData( HashMap<String, String> imgBasData) {staticInstance.imgBasicData = imgBasData; }

    public static String[] getcommandLineArgs() { return Arrays.copyOf(staticInstance.commandLineArgs, staticInstance.commandLineArgs.length); }
    public static void setcommandLineArgs(String[] setcmdlnargs) { staticInstance.commandLineArgs = Arrays.copyOf(setcmdlnargs, setcmdlnargs.length); }

    public static boolean getcommandLineArgsgiven() { return staticInstance.commandLineArgsgiven;}
    public static void setcommandLineArgsgiven(boolean cmdlnrgsgvn) {staticInstance.commandLineArgsgiven = cmdlnrgsgvn; }

    public static int getScreenWidth() { return staticInstance.ScreenWidth;}
    public static void setScreenWidth(int width) {staticInstance.ScreenWidth = width; }

    public static int getScreenHeight() { return staticInstance.ScreenHeight;}
    public static void setScreenHeight(int height) {staticInstance.ScreenHeight = height; }

    public static HashMap<String, String> getParameters () { return staticInstance.Parameters; }
    public static void setParameters(HashMap<String, String> prmtrs) { staticInstance.Parameters = prmtrs; }

    public static String getFusedImage() { return staticInstance.FusedImage; }
    public static void setFusedImage(String fsdimg) { staticInstance.FusedImage = fsdimg; }
}