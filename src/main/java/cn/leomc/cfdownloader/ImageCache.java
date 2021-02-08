package cn.leomc.cfdownloader;

import com.therandomlabs.curseapi.project.CurseProject;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.util.HashMap;

public class ImageCache {

    public static final HashMap<CurseProject, Image> PROJECT_LOGOS = new HashMap<>();


    public static Image getOrDownloadLogo(CurseProject project) {
        if (PROJECT_LOGOS.containsKey(project))
            return PROJECT_LOGOS.get(project);
        else {
            AsyncTaskExecutor.run(() -> {
                try {
                    PROJECT_LOGOS.put(project, SwingFXUtils.toFXImage(ImageUtils.resizeImage(project.logo().get(), 32, 32), null));
                } catch (Exception e) {
                    MessageUtils.error(e);
                }
            });
            return null;
        }
    }

    public static Image getOrDownloadLogoAndUpdate(CurseForgeProjectWrapper project, PListCell listCell) {
        if (PROJECT_LOGOS.containsKey(project.getProject()))
            return PROJECT_LOGOS.get(project.getProject());
        else {
            AsyncTaskExecutor.run(() -> {
                try {
                    PROJECT_LOGOS.put(project.getProject(), SwingFXUtils.toFXImage(ImageUtils.resizeImage(project.getProject().logo().get(), 32, 32), null));
                    Platform.runLater(() -> listCell.updateItem(project, false));
                } catch (Exception e) {
                    MessageUtils.error(e);
                }
            });
            return null;
        }
    }

}
