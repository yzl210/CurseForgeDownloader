package cn.leomc.cfdownloader;

import cn.leomc.cfdownloader.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class ImageCache {

    private static final Executor EXECUTOR = Executors.newCachedThreadPool();
    public static final Map<Integer, Image> MOD_LOGOS = new ConcurrentHashMap<>();

    private static final Map<Integer, CompletableFuture<?>> TASKS = new ConcurrentHashMap<>();

    public static void updateIcon(CFModWrapper mod, ModListCell cell) {
        if (MOD_LOGOS.containsKey(mod.getMod().id()))
            cell.setGraphic(new ImageView(MOD_LOGOS.get(mod.getMod().id())));
        else if(!TASKS.containsKey(mod.getMod().id())){
            AtomicBoolean b = new AtomicBoolean(true);
            TASKS.put(mod.getMod().id(), createTask(mod, cell, b));
            b.set(false);
        }
    }

    private static CompletableFuture<?> createTask(CFModWrapper mod, ModListCell cell, AtomicBoolean b){
        return CompletableFuture
                .supplyAsync(() -> {
                    while (b.get());
                    try {
                        return SwingFXUtils.toFXImage(ImageUtils.resizeImage(ImageIO.read(new URL(mod.getMod().logo().url())), 32, 32), null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, EXECUTOR)
                .whenComplete((image, throwable) -> {
                    if(throwable != null)
                        Log.LOGGER.log(Level.WARNING, throwable, () -> "Failed to get icon for mod \"" + mod.toString() + "\"");
                    if(image != null){
                        MOD_LOGOS.put(mod.getMod().id(), image);
                        Platform.runLater(() -> cell.setGraphic(new ImageView(image)));
                        TASKS.remove(mod.getMod().id());
                    }
                });
    }

}
