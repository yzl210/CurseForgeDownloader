package cn.leomc.cfdownloader;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CurseForgeDownloader extends Application {

    private static boolean RUNNING = true;

    private final FXMLLoader loader = new FXMLLoader();

    public static void main(String[] args) {
        launch(args);
    }



    @Override
    public void start(Stage stage) throws Exception {

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            //if(Platform.isFxApplicationThread())
                //MessageUtils.error(e);
        });

        stage.setTitle("CF Downloader");

        CurseForgeUtils.init();
        ImageCache.init();

        loader.setLocation(ClassLoader.getSystemResource("Main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        MainController controller = loader.getController();
        controller.stage = stage;
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
        RUNNING = false;
        System.exit(code);
    }

    public static boolean isRunning() {
        return RUNNING;
    }
}
