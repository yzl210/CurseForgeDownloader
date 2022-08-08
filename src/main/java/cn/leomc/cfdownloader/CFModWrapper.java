package cn.leomc.cfdownloader;

import io.github.matyrobbrt.curseforgeapi.schemas.mod.Mod;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFModWrapper {

    private Mod mod;

    public CFModWrapper(Mod mod) {
        this.mod = mod;
    }

    public static List<CFModWrapper> of(Collection<Mod> mods) {
        return mods.stream().map(CFModWrapper::new).toList();
    }

    public Mod getMod() {
        return mod;
    }

    public void setMod(Mod mod) {
        this.mod = mod;
    }

    @Override
    public String toString() {
        return mod.name();
    }

}
