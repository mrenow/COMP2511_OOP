package unsw.ui;

public enum UIPath {
    MENU("src/unsw/ui/MainMenu/MainMenu.fxml"),
    NEWGAME("src/unsw/ui/GameSetting/GameSetting.fxml"),
    GAME("src/unsw/gloriaromanus/main.fxml"),
    LOADSAVE("src/unsw/ui/LoadSave/LoadSave.fxml"),
    SAVES("src/unsw/saves/"),
    TOPBAR("src/unsw/ui/topbar/TopBar.fxml"),
    VIC("src/unsw/ui/VicUI/VicUI.fxml");

    private String path;
    UIPath (String path){
        this.path = path;
    }
    public String getPath(){
        return path;
    }
}
