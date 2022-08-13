package cn.leomc.cfdownloader;

import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;

public class ModListCell extends MFXListCell<ModNode> {

    public ModListCell(MFXListView<ModNode> listView, ModNode data) {
        super(listView, data);
        //setStyle("-fx-padding: 2px; -fx-background-color: transparent, -fx-background; -fx-border-color: darkgray; -fx-border-style: ;-fx-background-insets: 0px, 2px");
    }


}
