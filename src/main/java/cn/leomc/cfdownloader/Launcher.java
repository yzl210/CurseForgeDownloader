package cn.leomc.cfdownloader;


import javax.swing.*;

public class Launcher {

    public static void main(String[] args) {
        try {
            Class.forName("javafx.application.Application");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "JavaFX not found!\nPlease install OpenJFX.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        CurseForgeDownloader.launch(CurseForgeDownloader.class, args);
    }

}
