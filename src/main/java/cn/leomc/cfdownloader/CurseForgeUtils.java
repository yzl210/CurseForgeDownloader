package cn.leomc.cfdownloader;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.game.CurseCategorySection;
import com.therandomlabs.curseapi.game.CurseGame;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.project.CurseSearchQuery;

import java.util.List;
import java.util.Optional;

public class CurseForgeUtils {

    public static List<CurseProject> searchProjects(String name) {
        try {
            CurseGame game = getGame("Minecraft");
            return CurseAPI.searchProjects(new CurseSearchQuery().game(game).categorySection(getCategorySection(game, "mods")).searchFilter(name)).get();
        } catch (Throwable e) {
            MessageUtils.error(e);
            return null;
        }
    }

    public static CurseGame getGame(String name) {
        try {
            for (CurseGame game : CurseAPI.games().get())
                if (game.name().equalsIgnoreCase(name))
                    return game;
        } catch (Throwable e) {
            MessageUtils.error(e);
        }
        return null;
    }

    public static CurseCategorySection getCategorySection(CurseGame game, String name) {
        try {
            for (CurseCategorySection categorySection : game.categorySections())
                if (categorySection.name().equalsIgnoreCase(name))
                    return categorySection;
        } catch (Throwable e) {
            MessageUtils.error(e);
        }
        return null;

    }

    public static CurseFile getLatestFile(CurseProject project) {
        try {
            Optional<CurseFiles<CurseFile>> curseFiles = CurseAPI.files(project.id());
            if (curseFiles.isPresent() && !curseFiles.get().isEmpty()) {
                return curseFiles.get().withComparator(CurseFiles.SORT_BY_NEWEST).first();
            } else {
                MessageUtils.warn("Project " + project.name() + " has no file", "Will skip this project");
            }
        } catch (Throwable e) {
            MessageUtils.error(e);
        }
        return null;
    }

}
