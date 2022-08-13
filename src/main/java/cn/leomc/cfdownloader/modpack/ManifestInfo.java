package cn.leomc.cfdownloader.modpack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public record ManifestInfo(String name, List<ModLoader> modLoaders, String version, String author, String mcVersion, List<CFModpackFile> files,
                           String overrides) {


    public static ManifestInfo of(Path path) throws IOException {
        return of(JsonParser.parseString(Files.readString(path)).getAsJsonObject());
    }

    public static ManifestInfo of(JsonObject object) {
        return new ManifestInfo(
                object.get("name").getAsString(),
                ModLoader.of(object.getAsJsonObject("minecraft").getAsJsonArray("modLoaders")),
                object.get("version").getAsString(),
                object.get("author").getAsString(),
                object.getAsJsonObject("minecraft").get("version").getAsString(),
                CFModpackFile.of(object.getAsJsonArray("files")),
                object.get("overrides").getAsString()
        );
    }

}
