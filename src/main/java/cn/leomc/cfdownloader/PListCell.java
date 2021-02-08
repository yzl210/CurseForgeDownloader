package cn.leomc.cfdownloader;

import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PListCell extends ListCell<CurseForgeProjectWrapper> {

    @Override
    public void updateItem(CurseForgeProjectWrapper project, boolean empty) {
        super.updateItem(project, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(project.toString());
            Image image = ImageCache.getOrDownloadLogoAndUpdate(project, this);
            if (image != null)
                setGraphic(new ImageView(image));
            else
                setGraphic(null);

        }
    }

}
