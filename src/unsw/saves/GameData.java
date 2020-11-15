package unsw.saves;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import unsw.ui.UIPath;

public class GameData {
    public List<String> fileNames = new ArrayList<>();
    private File[] listoFiles;
    public void saveGame(String filename){
        this.fileNames.add(filename);
        File folder = new File(UIPath.SAVES.getPath());
        listoFiles = folder.listFiles();
    }
    public boolean checkFileName(String filename){
        if (fileNames.contains(filename)) {
            return true;
        }
        return false;
    }
    public void deleteFile(String name){
        int index = fileNames.indexOf(name);
        listoFiles[index].deleteOnExit();
    }
	public List<String> getFileNames() {
		return fileNames;
	}
	public GameData() {
		File folder = new File(UIPath.SAVES.getPath());
        listoFiles = folder.listFiles();
        for (int i = 0; i < listoFiles.length; i++) {
            fileNames.add(listoFiles[i].getName());
        }
        fileNames.remove("GameData.java");
    }
    
}
