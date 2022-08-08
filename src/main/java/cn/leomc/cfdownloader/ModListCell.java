package cn.leomc.cfdownloader;

import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ModListCell extends ListCell<CFModWrapper> {

    @Override
    public void updateItem(CFModWrapper mod, boolean empty) {
        super.updateItem(mod, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(mod.toString());
            ImageCache.updateIcon(mod, this);
        }
    }
}
