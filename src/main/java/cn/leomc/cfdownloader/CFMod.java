package cn.leomc.cfdownloader;

import io.github.matyrobbrt.curseforgeapi.schemas.mod.Mod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CFMod {

    private static final Map<Integer, CFMod> CACHE = new ConcurrentHashMap<>();

    private final Mod mod;
    private final Category category;
    private final List<io.github.matyrobbrt.curseforgeapi.schemas.Category> subCategories;

    private CFMod(Mod mod) {
        this.mod = mod;
        this.category = Category.of(mod);
        List<io.github.matyrobbrt.curseforgeapi.schemas.Category> categories = new ArrayList<>();
        for (io.github.matyrobbrt.curseforgeapi.schemas.Category subCategory : mod.categories()) {
            if(subCategory.id() == category.id)
                continue;
            categories.add(subCategory);
        }
        this.subCategories = Collections.unmodifiableList(categories);
    }

    @Contract("null -> null; !null -> !null")
    public static CFMod of(Mod mod){
        if(mod == null)
            return null;
        CFMod cfMod = of(mod.id());
        if(cfMod != null)
            return cfMod;
        cfMod = new CFMod(mod);
        CACHE.put(mod.id(), cfMod);
        return cfMod;
    }

    @Nullable
    public static CFMod of(int id){
        return CACHE.getOrDefault(id, null);
    }

    public static List<CFMod> of(Collection<Mod> mods) {
        return mods.stream().map(CFMod::of).toList();
    }

    public Mod getMod() {
        return mod;
    }

    public Category getCategory() {
        return category;
    }

    public List<io.github.matyrobbrt.curseforgeapi.schemas.Category> getSubCategories() {
        return subCategories;
    }

    @Override
    public String toString() {
        return getCategory() + ": " + mod.name();
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CFMod cfMod)
            return mod.id() == cfMod.mod.id();
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return mod.id();
    }

    public enum Category{
        BUKKIT_PLUGIN(5, "Plugin"),
        MODPACK(4471, "Modpack"),
        CUSTOMIZATION(4546, "Customization"),
        ADDON(4559, "Addon"),
        MOD(6, "Mod"),
        RESOURCE_PACK(4980, "Resource Pack"),
        WORLD(17, "World"),
        UNKNOWN(-1, "Unknown");

        private final int id;
        private final String name;

        Category(int id, String name){
            this.id = id;
            this.name = name;
        }


        @Override
        public String toString() {
            return name;
        }

        public static Category of(Mod mod){
            for (io.github.matyrobbrt.curseforgeapi.schemas.Category category : mod.categories()) {
                for (Category value : values())
                    if(category.parentCategoryId() == value.id)
                        return value;

                for (io.github.matyrobbrt.curseforgeapi.schemas.Category subCategory : CurseForgeUtils.ALL_CATEGORIES) {
                    if(category.parentCategoryId() == subCategory.id())
                        for (Category value : values())
                            if(subCategory.parentCategoryId() != null && subCategory.parentCategoryId() == value.id)
                                return value;
                }
            }
            return UNKNOWN;
        }
    }


}
