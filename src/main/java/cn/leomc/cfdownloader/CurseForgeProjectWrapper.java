package cn.leomc.cfdownloader;

import com.therandomlabs.curseapi.project.CurseProject;

import java.util.ArrayList;
import java.util.List;

public class CurseForgeProjectWrapper {

    private CurseProject project;

    public CurseForgeProjectWrapper(CurseProject project) {
        this.project = project;
    }

    public static List<CurseForgeProjectWrapper> parseList(List<CurseProject> projects) {
        List<CurseForgeProjectWrapper> list = new ArrayList<>();
        for (CurseProject project : projects)
            list.add(new CurseForgeProjectWrapper(project));
        return list;
    }

    public CurseProject getProject() {
        return project;
    }

    public void setProject(CurseProject project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return project.name();
    }

}
