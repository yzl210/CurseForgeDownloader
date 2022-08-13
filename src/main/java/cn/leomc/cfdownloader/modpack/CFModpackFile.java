package cn.leomc.cfdownloader.modpack;

import cn.leomc.cfdownloader.CFMod;
import cn.leomc.cfdownloader.CurseForgeUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.stream.StreamSupport;

public record CFModpackFile(int projectID, int fileID, boolean required) {


    public CFMod getMod(){
        CFMod mod = CFMod.of(projectID);
        if(mod == null)
            mod = CFMod.of(CurseForgeUtils.getMod(projectID).orElseThrow());
        return mod;
    }

    public static CFModpackFile of(JsonObject object){
            return new CFModpackFile(object.get("projectID").getAsInt(), object.get("fileID").getAsInt(), object.get("required").getAsBoolean());
    }

    public static List<CFModpackFile> of(JsonArray array){
        return StreamSupport.stream(array.spliterator(), true)
                .filter(JsonElement::isJsonObject)
                .map(JsonElement::getAsJsonObject)
                .map(CFModpackFile::of)
                .toList();
    }

}
