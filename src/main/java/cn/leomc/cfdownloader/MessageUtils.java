package cn.leomc.cfdownloader;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.controlsfx.dialog.ExceptionDialog;

public class MessageUtils {


    public static void error(Throwable exception) {
        Platform.runLater(() -> {
            ExceptionDialog dialog = new ExceptionDialog(exception);
            dialog.setHeaderText(exception.getClass().getName());
            dialog.showAndWait();
        });
    }

    public static void info(String message) {
        Platform.runLater(() -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("info");
            dialog.setHeaderText(message);
            dialog.setContentText(message);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.showAndWait();
        });
    }

}
