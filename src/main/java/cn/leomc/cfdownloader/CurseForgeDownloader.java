package cn.leomc.cfdownloader;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CurseForgeDownloader extends Application {

    private final FXMLLoader loader = new FXMLLoader();

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("CF Downloader");

        Platform.runLater(CurseForgeUtils::new);

        loader.setLocation(ClassLoader.getSystemResource("Main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            event.consume();
            exit();
        });
        stage.show();
    }

    public static void exit(){
        exit(0);
    }

    public static void exit(int code){
        System.exit(code);
    }



}
