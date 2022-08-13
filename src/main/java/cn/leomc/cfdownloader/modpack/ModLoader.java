package cn.leomc.cfdownloader.modpack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.stream.StreamSupport;

public record ModLoader(String id, boolean primary) {


    public static List<ModLoader> of(JsonArray array){
        return StreamSupport.stream(array.spliterator(), true)
                .filter(JsonElement::isJsonObject)
                .map(JsonElement::getAsJsonObject)
                .map(ModLoader::of)
                .toList();
    }

    public static ModLoader of(JsonObject object){
        return new ModLoader(object.get("id").getAsString(), object.get("primary").getAsBoolean());
    }

}
