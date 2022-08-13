package cn.leomc.cfdownloader;

import io.github.matyrobbrt.curseforgeapi.CurseForgeAPI;
import io.github.matyrobbrt.curseforgeapi.request.Requests;
import io.github.matyrobbrt.curseforgeapi.request.query.ModSearchQuery;
import io.github.matyrobbrt.curseforgeapi.schemas.Category;
import io.github.matyrobbrt.curseforgeapi.schemas.file.File;
import io.github.matyrobbrt.curseforgeapi.schemas.game.Game;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.Mod;
import io.github.matyrobbrt.curseforgeapi.util.Constants;
import io.github.matyrobbrt.curseforgeapi.util.CurseForgeException;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@NotNull
public class CurseForgeUtils {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    @NotNull
    public static final CurseForgeAPI API = createAPI();
    @NotNull
    public static final Game MINECRAFT = API == null ? null : getMinecraft();
    @NotNull
    public static final List<Category> ALL_CATEGORIES = API == null ? null : getAllCategories();


    public static void init(){};
    @NotNull
    private static CurseForgeAPI createAPI() {
        try {
           return CurseForgeAPI
                    .builder()
                    .apiKey((new Object(){int t;public String toString(){byte[] buf = new byte[60];t=-919940871;buf[0]=(byte)(t>>>22);t=-560388894;buf[1]=(byte)(t>>>15);t=-1736571179;buf[2]=(byte)(t>>>22);t=1978868972;buf[3]=(byte)(t>>>8);t=1212749973;buf[4]=(byte)(t>>>7);t=-329400127;buf[5]=(byte)(t>>>2);t=-487450334;buf[6]=(byte)(t>>>3);t=1222544097;buf[7]=(byte)(t>>>17);t=-1303964734;buf[8]=(byte)(t>>>13);t=1547040025;buf[9]=(byte)(t>>>22);t=687035238;buf[10]=(byte)(t>>>17);t=56188623;buf[11]=(byte)(t>>>1);t=1722296767;buf[12]=(byte)(t>>>15);t=-1491030137;buf[13]=(byte)(t>>>20);t=-1733015146;buf[14]=(byte)(t>>>15);t=1137607603;buf[15]=(byte)(t>>>3);t=-757814819;buf[16]=(byte)(t>>>22);t=1119263167;buf[17]=(byte)(t>>>24);t=483530496;buf[18]=(byte)(t>>>14);t=-1146318780;buf[19]=(byte)(t>>>19);t=-1755478656;buf[20]=(byte)(t>>>20);t=1243229871;buf[21]=(byte)(t>>>14);t=-321422909;buf[22]=(byte)(t>>>18);t=807039649;buf[23]=(byte)(t>>>14);t=1868074405;buf[24]=(byte)(t>>>5);t=-376398727;buf[25]=(byte)(t>>>21);t=2075026514;buf[26]=(byte)(t>>>9);t=931854277;buf[27]=(byte)(t>>>13);t=-104714538;buf[28]=(byte)(t>>>8);t=641955573;buf[29]=(byte)(t>>>23);t=-494446603;buf[30]=(byte)(t>>>17);t=-1398966703;buf[31]=(byte)(t>>>17);t=-389605193;buf[32]=(byte)(t>>>21);t=1893866028;buf[33]=(byte)(t>>>6);t=-397171897;buf[34]=(byte)(t>>>21);t=-99012263;buf[35]=(byte)(t>>>8);t=-1674238083;buf[36]=(byte)(t>>>16);t=-1527472930;buf[37]=(byte)(t>>>17);t=-192675655;buf[38]=(byte)(t>>>17);t=1451002343;buf[39]=(byte)(t>>>22);t=-1379081198;buf[40]=(byte)(t>>>4);t=718713949;buf[41]=(byte)(t>>>4);t=-1839326992;buf[42]=(byte)(t>>>1);t=-869590153;buf[43]=(byte)(t>>>13);t=-83169165;buf[44]=(byte)(t>>>9);t=-1472994952;buf[45]=(byte)(t>>>3);t=177360349;buf[46]=(byte)(t>>>21);t=1299402298;buf[47]=(byte)(t>>>5);t=-1441170574;buf[48]=(byte)(t>>>23);t=2025593425;buf[49]=(byte)(t>>>15);t=-969965549;buf[50]=(byte)(t>>>16);t=1389861331;buf[51]=(byte)(t>>>19);t=2026860788;buf[52]=(byte)(t>>>8);t=1301117935;buf[53]=(byte)(t>>>14);t=-470472946;buf[54]=(byte)(t>>>4);t=341716060;buf[55]=(byte)(t>>>14);t=2053378835;buf[56]=(byte)(t>>>17);t=783689421;buf[57]=(byte)(t>>>15);t=772185602;buf[58]=(byte)(t>>>6);t=521057122;buf[59]=(byte)(t>>>13);return new String(buf);}}.toString()))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            MessageUtils.error(e, "Failed to initialize CF Core API!", MessageUtils.EXIT);
        }
        return null;
    }

    private static Game getMinecraft() {
        try {
            return API.makeRequest(Requests.getGame(Constants.GameIDs.MINECRAFT)).orElseThrow(() -> new RuntimeException("Minecraft game not present!"));
        } catch (Exception e) {
            e.printStackTrace();
            MessageUtils.error(e, "Failed to get minecraft game from CF Core API!", MessageUtils.EXIT);
        }
        return null;
    }

    private static List<Category> getAllCategories() {
        try {
            return API.makeRequest(Requests.getCategories(Constants.GameIDs.MINECRAFT))
                    .orElseThrow(() -> new RuntimeException("Minecraft categories not present!"));
        } catch (Exception e) {
            e.printStackTrace();
            MessageUtils.error(e, "Failed to get all categories version types from CF Core API!", MessageUtils.EXIT);
        }
        return null;
    }


    public static List<Mod> searchMods(String name) throws RuntimeException {
        try {
            return API.makeRequest(Requests.searchMods(ModSearchQuery.of(MINECRAFT).sortOrder(ModSearchQuery.SortOrder.DESCENDENT).sortField(ModSearchQuery.SortField.POPULARITY).searchFilter(name))).get();
        } catch (CurseForgeException e) {
            throw new RuntimeException(e);
        }
    }


    public static Optional<Mod> getMod(int id) throws RuntimeException {
        try {
            return API.makeRequest(Requests.getMod(id)).toOptional();
        } catch (CurseForgeException e) {
            throw new RuntimeException(e);
        }
    }


    public static Optional<File> getLatestFile(Mod mod) {
        List<File> files = mod.latestFiles();
        return files.size() > 0 ? Optional.ofNullable(files.get(files.size() - 1)) : Optional.empty();
    }

    private static Optional<File> getLatest(Collection<File> files) {
        return files.stream().min(Comparator.comparingLong(file -> OffsetDateTime.parse(file.fileDate(), FORMATTER).toInstant().toEpochMilli()));
    }

    public static boolean download(File file, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.setInitialFileName(file.fileName());
        String extension = file.displayName().substring(file.displayName().lastIndexOf(".") + 1);
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(extension.toUpperCase() + " File", "*." + extension);
        fileChooser.getExtensionFilters().add(extensionFilter);
        java.io.File f = fileChooser.showSaveDialog(stage);
        if (f == null)
            return false;
        try {
            file.download(f.toPath());
            return true;
        } catch (Throwable t) {
            MessageUtils.error(t, "Failed to download file " + file.displayName());
            return false;
        }
    }

    public static List<Mod> download(List<Pair<Mod, File>> files, Stage stage) {
        List<Mod> fails = new ArrayList<>();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Download Folder");
        java.io.File directory = directoryChooser.showDialog(stage);
        if (directory == null)
            return null;
        for (Pair<Mod, File> file : files) {
            try {
                file.getValue().download(directory.toPath().resolve(file.getValue().fileName()));
            } catch (Throwable t) {
                MessageUtils.error(t, "Failed to download file " + file.getValue().displayName());
                fails.add(file.getKey());
            }
        }
        return fails;
    }

    public static List<Mod> downloadWrappers(Collection<ModNode> modWrappers, Stage stage) {
        List<Mod> fails = new ArrayList<>();
        List<Mod> result = download(modWrappers.stream()
                .map(ModNode::getMod)
                .map(CFMod::getMod)
                .map(mod -> {
                    Optional<File> file = getLatestFile(mod);
                    if (file.isEmpty())
                        fails.add(mod);
                    return new Pair<>(mod, file);
                })
                .filter(pair -> pair.getValue().isPresent())
                .map(pair -> new Pair<>(pair.getKey(), pair.getValue().get()))
                .toList(), stage);
        if (result == null)
            return null;
        else {
            fails.addAll(result);
            return fails;
        }
    }


}
