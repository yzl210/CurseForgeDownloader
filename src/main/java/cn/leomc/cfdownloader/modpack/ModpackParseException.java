package cn.leomc.cfdownloader.modpack;

import java.nio.file.Path;

public class ModpackParseException extends RuntimeException {

    public ModpackParseException(String message, Throwable cause){
        super(message, cause);
    }



    public static ModpackParseException createForManifest(Path path, Throwable cause){
        return new ModpackParseException("Manifest file (" + path.toString() + ") of the modpack is in incorrect format or corrupted", cause);
    }


}
