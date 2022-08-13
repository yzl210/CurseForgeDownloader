package cn.leomc.cfdownloader.progress;

import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class ProgressPane extends BorderPane implements ProgressTracker {

    private final ProgressBar main;
    private final VBox vBox;

    public ProgressPane(){
        setPrefWidth(400);
        this.vBox = new VBox();
        this.main = new ProgressBar();
        vBox.setSpacing(1.5);
        vBox.getChildren().add(main);
        setCenter(vBox);
    }


    @Override
    public ProgressBarControl main() {
        return main;
    }

    @Override
    public ProgressBarControl create() {
        ProgressBar bar = new ProgressBar();
        vBox.getChildren().add(bar);
        return bar;
    }

    @Override
    public void remove(ProgressBarControl bar) {
        vBox.getChildren().remove(bar);
    }

    public class ProgressBar extends BorderPane implements ProgressBarControl{

        private final Label label;
        private final MFXProgressBar bar;


        public ProgressBar(){
            label = new Label();
            bar = new MFXProgressBar();
            bar.getStylesheets().add("Main.css");
            bar.getStyleClass().add("mfx-progress-bar");
            bar.setPrefWidth(vBox.getWidth());
            vBox.widthProperty().addListener(((observable, oldValue, newValue) -> bar.setPrefWidth((Double) newValue)));
            setTop(label);
            setLeft(bar);
        }

        @Override
        public void setStatus(String status) {
            label.setText(status);
        }

        @Override
        public void setProgress(double progress) {
            bar.setProgress(progress);
        }

        @Override
        public void remove() {
            ProgressPane.this.remove(this);
        }
    }

}
