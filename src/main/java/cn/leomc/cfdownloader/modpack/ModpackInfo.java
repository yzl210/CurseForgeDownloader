package cn.leomc.cfdownloader.modpack;

import cn.leomc.cfdownloader.progress.ProgressBarControl;
import cn.leomc.cfdownloader.progress.ProgressTracker;
import javafx.application.Platform;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public record ModpackInfo(Path path, ManifestInfo manifest) {

    public static ModpackInfo of(File file, @Nullable ProgressTracker progressTracker) {
        try {

            Optional<ProgressTracker> tracker = Optional.ofNullable(progressTracker);
            Platform.runLater(() -> tracker.ifPresent(t -> t.main().set("Unzipping modpack file", 0)));

            Path temp = Files.createTempDirectory("cfdownloader");
            ZipFile zip = new ZipFile(file);
            List<? extends ZipEntry> entries = zip.stream().filter(entry -> !entry.isDirectory()).toList();
            byte[] buffer = new byte[1024];
            double size = entries.size();
            double unzipped = 0;
            for(ZipEntry entry : entries){
                String fileName = entry.getName();
                AtomicReference<Optional<ProgressBarControl>> bar = new AtomicReference<>();
                Platform.runLater(() -> bar.set(tracker.map(ProgressTracker::create)));
                Platform.runLater(() -> bar.get().ifPresent(b -> b.set("Unzipping " + fileName, -1)));
                Path newFile = temp.resolve(fileName);
                Files.createDirectories(newFile.getParent());
                InputStream is = zip.getInputStream(entry);
                OutputStream os = Files.newOutputStream(newFile);
                int len;
                while ((len = is.read(buffer)) > 0)
                    os.write(buffer, 0, len);
                os.close();
                is.close();
                double finalUnzipped = unzipped;
                Platform.runLater(() -> tracker.ifPresent(t -> t.main().setProgress(finalUnzipped / size / 2)));
                Platform.runLater(() -> bar.get().ifPresent(ProgressBarControl::remove));

                unzipped++;
            }

            Path manifestPath = temp.resolve("manifest.json");

            AtomicReference<Optional<ProgressBarControl>> bar = new AtomicReference<>();
            Platform.runLater(() -> bar.set(tracker.map(ProgressTracker::create)));
            Platform.runLater(() -> bar.get().ifPresent(b -> b.set("Parsing manifest", -1)));
            ManifestInfo info;
            try {
                info = ManifestInfo.of(manifestPath);
            } catch (Exception e) {
                throw ModpackParseException.createForManifest(manifestPath, e);
            } finally {
                Platform.runLater(() -> bar.get().ifPresent(ProgressBarControl::remove));
                Platform.runLater(() -> tracker.ifPresent(t -> t.main().setProgress(0.75)));
            }

            return new ModpackInfo(temp, info);

        } catch (Exception e) {
            throw new ModpackParseException("Could not parse modpack", e);
        }
    }


    /*
    while (entry != null) {

     */

}
