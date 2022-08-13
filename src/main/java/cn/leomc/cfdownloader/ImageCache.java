package cn.leomc.cfdownloader;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;
import java.util.logging.Level;

public class ImageCache {

    public static final Map<Integer, Image> MOD_LOGOS = new ConcurrentHashMap<>();
    private static final BlockingQueue<IconFetchAttempt> QUEUE = new LinkedBlockingQueue<>();


    private static final Supplier<Thread> IMAGE_THREAD = () -> new Thread(ImageCache::run);

    public static void init(){
        for(int i = 0;i < 4;i++){
            IMAGE_THREAD.get().start();
        }
    }

    public static void updateIcon(CFMod mod, ModNode node) {
        if (MOD_LOGOS.containsKey(mod.getMod().id()))
            node.setImage(new ImageView(MOD_LOGOS.get(mod.getMod().id())));
        else
            QUEUE.offer(IconFetchAttempt.of(mod, node));
    }

    private static void run() {
        while (CurseForgeDownloader.isRunning()) {
            IconFetchAttempt attempt = null;
            try {
                attempt = QUEUE.take();
            } catch (InterruptedException ignored) {
            }
            if (attempt == null)
                continue;
            try {
                Image image = SwingFXUtils.toFXImage(ImageUtils.resizeImage(ImageIO.read(new URL(attempt.mod().getMod().logo().thumbnailUrl())), 32, 32), null);
                MOD_LOGOS.put(attempt.mod().getMod().id(), image);
                IconFetchAttempt finalInfo = attempt;
                Platform.runLater(() -> {
                    finalInfo.node().setImage(new ImageView(image));
                });
            } catch (Throwable t) {
                IconFetchAttempt finalInfo = attempt;
                Log.LOGGER.log(Level.WARNING, t, () -> "Failed to get icon for \"" + finalInfo.mod().toString() + "\", tries " + (finalInfo.trials() + 1) + "/3");
                if (attempt.trials() < 2)
                    QUEUE.add(attempt.plus());
            }
        }
    }

    public record IconFetchAttempt(CFMod mod, ModNode node, int trials){

        public static IconFetchAttempt of(CFMod mod, ModNode node){
            return new IconFetchAttempt(mod, node, 0);
        }

        public IconFetchAttempt plus(){
            return new IconFetchAttempt(mod(), node(), trials() + 1);
        }

    }


}
