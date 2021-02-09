package cn.leomc.cfdownloader;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.project.CurseProject;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileUtils {


    public static List<String> readFileByLine(File file) {
        try {
            return Files.readAllLines(file.toPath());
        } catch (Throwable e) {
            MessageUtils.error(e);
            return null;
        }
    }

    public static List<CurseForgeProjectWrapper> readProjects(File file) {
        List<String> content = readFileByLine(file);

        List<CurseForgeProjectWrapper> projects = new ArrayList<>();

        for (String line : content) {
            try {
                int projectID = Integer.parseInt(line);
                try {
                    Optional<CurseProject> project = CurseAPI.project(projectID);
                    if (project.isPresent()) {
                        projects.add(new CurseForgeProjectWrapper(project.get()));
                    } else
                        MessageUtils.warn("Unable to parse file " + file.getName(), "Project " + projectID + " not found, will continue to parse remaining.");
                } catch (CurseException e) {
                    MessageUtils.error(e);
                }
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
