package cn.leomc.cfdownloader;


import javafx.application.Application;
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
        loader.setLocation(ClassLoader.getSystemResource("Main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        MainController mainController = loader.getController();
        stage.setOnCloseRequest(event -> {
            event.consume();
            AsyncTaskExecutor.pool.shutdown();
            System.exit(0);
        });

        stage.show();
    }


}
