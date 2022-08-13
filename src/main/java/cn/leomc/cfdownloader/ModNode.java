package cn.leomc.cfdownloader;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class ModNode extends BorderPane {

    private final CFMod mod;

    private ImageView image;
    private final Label label;

    public ModNode(CFMod mod){
        this.mod = mod;
        this.label = new Label(mod.toString());
        setCenter(label);
        ImageCache.updateIcon(mod, this);
    }

    public void setImage(ImageView image){
        this.image = image;
        setLeft(image);
        setMargin(image, new Insets(2.5, 2.5, 2.5, 0));
    }

    public CFMod getMod() {
        return mod;
    }

    public ImageView getImage() {
        return image;
    }



}
