package cn.leomc.cfdownloader;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {


    public static List<String> readFileByLine(File file) {
        try {
            return Files.readAllLines(file.toPath());
        } catch (Throwable e) {
            MessageUtils.error(e);
            return Collections.emptyList();
        }
    }

    public static List<CFMod> readProjects(File file) {
        List<String> content = readFileByLine(file);
        List<CFMod> projects = new ArrayList<>();
        for (String line : content) {
            try {
                int id = Integer.parseInt(line);
                CurseForgeUtils.getMod(id).ifPresentOrElse(mod -> projects.add(CFMod.of(mod)),
                        () -> MessageUtils.warn("Unable to parse file " + file.getName(), "Mod " + id + " not found, will continue to parse remaining."));
            } catch (NumberFormatException e) {
                MessageUtils.warn("Unable to parse file " + file.getName(), "Expected integer, but got: " + line + "\nWill continue to parse remaining.");
            }
        }
        return projects;
    }

    public static void writeToFile(File file, String content) {
        try {
            if (file.exists())
                file.delete();
            //file.mkdirs();
            file.createNewFile();
            Files.write(file.toPath(), content.getBytes());
        } catch (Throwable e) {
            MessageUtils.error(e);
        }
    }

}
