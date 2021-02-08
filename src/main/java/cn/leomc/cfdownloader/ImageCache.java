package cn.leomc.cfdownloader;

import com.therandomlabs.curseapi.project.CurseProject;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ListView;
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
}
