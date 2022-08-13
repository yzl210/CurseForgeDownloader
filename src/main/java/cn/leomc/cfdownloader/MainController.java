package cn.leomc.cfdownloader;

import cn.leomc.cfdownloader.modpack.CFModpackFile;
import cn.leomc.cfdownloader.modpack.ModLoader;
import cn.leomc.cfdownloader.modpack.ModpackInfo;
import cn.leomc.cfdownloader.progress.ProgressBarControl;
import cn.leomc.cfdownloader.progress.ProgressPane;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.Mod;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.selection.base.IMultipleSelectionModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MainController {

    public TabPane tabPane;
    public Tab modpackTab;


    public MFXListView<ModNode> modListView;
    public MFXTextField searchBar;
    public MFXButton searchButton;
    public MFXButton downloadButton;
    public Label resultsLabel;
    public MFXProgressSpinner progressSpinner;


    protected Stage stage;


    private CompletableFuture<?> initTask;
    private CompletableFuture<?> searchTask;

    private String lastSearch = "";
    private boolean isModpack = false;

    public void initialize() {


        modListView.getSelectionModel().setAllowsMultipleSelection(false);
        modListView.setCellFactory(mod -> new ModListCell(modListView, mod));

        modListView.getSelectionModel().selectionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.values().iterator().next().getMod().getCategory() == CFMod.Category.MODPACK) {
                addToListButton.setText("Load Modpack");
                isModpack = true;
            } else {
                addToListButton.setText("Add To List");
                isModpack = false;
            }

        });

        initTask = CompletableFuture
                .supplyAsync(() -> CurseForgeUtils.searchMods(""))
                .exceptionally(t -> {
                    Log.LOGGER.log(Level.WARNING, t, () -> "Failed to load default mods");
                    return Collections.emptyList();
                })
                .thenAccept(mods -> Platform.runLater(() -> {
                    if (initTask == null)
                        return;
                    List<CFMod> cfMods = CFMod.of(mods);
                    modListView.setItems(FXCollections.observableArrayList(cfMods.stream().map(ModNode::new).toList()));

                    initTask = null;
                }));
        modListListView.setCellFactory(mod -> new ModListCell(modListListView, mod));
    }

    public void onSearchButtonAction(ActionEvent actionEvent) {
        if (!searchBar.getText().isEmpty() && !lastSearch.equals(searchBar.getText()) && searchTask == null) {
            if (initTask != null)
                initTask.cancel(true);
            progressSpinner.setVisible(true);
            String text = searchBar.getText();
            searchTask = CompletableFuture
                    .supplyAsync(() -> CurseForgeUtils.searchMods(text))
                    .exceptionally(t -> {
                        MessageUtils.error(t, "Failed to search \"" + text + "\"");
                        return Collections.emptyList();
                    })
                    .thenAccept(mods -> {
                        Platform.runLater(() -> {
                            progressSpinner.setVisible(false);
                            lastSearch = text;
                            List<CFMod> cfMods = CFMod.of(mods);
                            if (!cfMods.isEmpty()) {
                                resultsLabel.setText("Found " + cfMods.size() + " mod(s)");
                                modListView.setItems(FXCollections.observableArrayList(cfMods.stream().map(ModNode::new).toList()));
                            } else {
                                modListView.setItems(FXCollections.emptyObservableList());
                                resultsLabel.setText("No mod found!");
                            }
                            searchTask = null;

                        });
                    });
        }
    }

    public void onDownloadButtonAction(ActionEvent actionEvent) {
        ModNode mod = getSelectedProject(modListView);
        if (mod == null)
            return;
        CurseForgeUtils.getLatestFile(mod.getMod().getMod()).ifPresentOrElse(file -> {
            if (CurseForgeUtils.download(file, stage))
                MessageUtils.info("Download finished!");
        }, () -> MessageUtils.info("This project has no file!"));
    }


    public MFXListView<ModNode> modListListView;
    public Button removeSelectedButton;
    public Button downloadAllButton;
    public Button exportButton;
    public Button importButton;
    public Button removeAllButton;
    public Button addToListButton;

    public void onAddToListButtonAction(ActionEvent actionEvent) {
        if (isModpack) {
            tabPane.getSelectionModel().select(modpackTab);
            //loadModpack();
        } else {
            ModNode node = getSelectedProject(modListView);
            if (node == null)
                return;
            if (!modListListView.getItems().stream().map(ModNode::getMod).toList().contains(node.getMod()))
                modListListView.getItems().add(new ModNode(node.getMod()));
            else
                MessageUtils.info("This project is already in the list!");
        }
    }

    public void onRemoveSelectedButtonAction(ActionEvent actionEvent) {
        List<ModNode> node = getSelectedProjects(modListListView);
        if (node == null)
            return;
        modListListView.getItems().removeAll(node);
    }

    public void onRemoveAllButtonAction(ActionEvent actionEvent) {
        modListListView.getItems().clear();
    }

    public void onImportButtonAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Import File");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Any File", "*.*");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            for (CFMod project : FileUtils.readProjects(file)) {
                if (!modListListView.getItems().contains(project))
                    modListListView.getItems().add(new ModNode(project));
            }


            MessageUtils.info("Finished importing!");
        }
    }

    public void onExportButtonAction(ActionEvent actionEvent) {
        if (!modListListView.getItems().isEmpty()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export As...");
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("TXT file", "*.txt");
            fileChooser.getExtensionFilters().add(extensionFilter);
            fileChooser.setInitialFileName("ExportedModList.txt");
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                StringBuilder builder = new StringBuilder();
                for (ModNode project : modListListView.getItems()) {
                    builder
                            .append(project.getMod().getMod().id())
                            .append("\n");
                }

                FileUtils.writeToFile(file, builder.toString());
                MessageUtils.info("Exported to " + file.getAbsolutePath());
            }
        } else
            MessageUtils.info("Project List is empty!");
    }

    public void onDownloadAllButtonAction(ActionEvent actionEvent) {
        if (!modListListView.getItems().isEmpty()) {
            List<Mod> fails = CurseForgeUtils.downloadWrappers(modListListView.getItems(), stage);
            if (fails != null)
                if (fails.isEmpty())
                    MessageUtils.info("Download Finished!");
                else {
                    String failed = fails.stream().map(Mod::name).collect(Collectors.joining(", "));
                    MessageUtils.warn("Not downloaded mods", "The following mods have failed to download: \n" + failed);
                }
        } else
            MessageUtils.info("Project List is empty!");
    }


    public MFXListView<ModNode> modpackModListView;
    public Label modpackNameLabel;
    public Label modpackAuthor;
    public Label modpackVersion;
    public Label modpackMods;
    public Label modpackMCVersion;
    public Label modpackModLoaders;

    public MFXButton loadModpackButton;


    public void onLoadModpackButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Modpack ZIP");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Curseforge Modpack", "*.zip");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null)
            loadModpack(file);
    }

    public void loadModpack(File file) {
        Stage progressStage = new Stage();
        progressStage.initOwner(stage);
        progressStage.initStyle(StageStyle.UTILITY);
        progressStage.initModality(Modality.APPLICATION_MODAL);
        progressStage.setTitle("Loading Modpack...");
        progressStage.setHeight(100);
        progressStage.setWidth(400);

        ProgressPane progressPane = new ProgressPane();
        progressPane.setPadding(new Insets(5, 5, 5, 5));
        progressStage.setScene(new Scene(progressPane));

        progressStage.show();

        CompletableFuture
                .supplyAsync(() -> ModpackInfo.of(file, progressPane))
                .thenAccept(info -> {
                    Platform.runLater(() -> {
                        modpackNameLabel.setText(info.manifest().name());
                        modpackAuthor.setText("Author: " + info.manifest().author());
                        modpackMods.setText("Mods: " + info.manifest().files().size());
                        modpackVersion.setText("Version: " + info.manifest().version());
                        modpackMCVersion.setText("MC Version: " + info.manifest().mcVersion());
                        modpackModLoaders.setText("Mod Loader(s): " + info.manifest().modLoaders().stream().map(ModLoader::id).collect(Collectors.joining(", ")));

                        progressPane.main().setStatus("Loading Mods");
                        ProgressBarControl bar = progressPane.create();
                        progressPane.create().set("Fetching from curseforge", -1);

                        AtomicInteger added = new AtomicInteger();

                        Executor executor = Executors.newFixedThreadPool(8);

                        List<CompletableFuture<?>> futures = new ArrayList<>();
                        List<Integer> failed = new ArrayList<>();
                        for (CFModpackFile cfFile : info.manifest().files())
                            futures.add(CompletableFuture
                                    .supplyAsync(cfFile::getMod, executor)
                                    .exceptionally(throwable -> {
                                        Log.LOGGER.log(Level.WARNING, throwable, () -> "Failed to get mod " + cfFile.projectID() + ", trying again");
                                        try {
                                            return cfFile.getMod();
                                        } catch (Throwable t){
                                            Log.LOGGER.log(Level.WARNING, throwable, () -> "Failed to get mod " + cfFile.projectID() + ", giving up");
                                            return null;
                                        }
                                    })
                                    .thenAccept(mod -> Platform.runLater(() -> {
                                        added.incrementAndGet();
                                        double progress = (double) added.get() / info.manifest().files().size();
                                        progressPane.main().setProgress(0.75 + (progress / 4));
                                        bar.set(added.get() + "/" + info.manifest().files().size(), progress);
                                        if (mod != null)
                                            modpackModListView.getItems().add(new ModNode(mod));
                                        else
                                            failed.add(cfFile.projectID());
                                    })));

                        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                                .thenAccept(f -> Platform.runLater(() -> {
                                    modpackModListView.setItems(modpackModListView.getItems().sorted());
                                    modpackMods.setText("Mods: " + info.manifest().files().size());
                                    progressStage.close();
                                    if(!failed.isEmpty())
                                            MessageUtils.warn("The following mod ids have failed to load, they won't show up in the mod list, but CF Downloader will still try to download them when exporting.",
                                                    failed.stream().map(String::valueOf).collect(Collectors.joining(", ")));
                                }));

                    });
                });


    }


    private ModNode getSelectedProject(MFXListView<ModNode> listView) {
        IMultipleSelectionModel<ModNode> selectionModel = listView.getSelectionModel();
        if (selectionModel != null && !selectionModel.getSelectedValues().isEmpty())
            return selectionModel.getSelectedValues().get(0);
        else
            MessageUtils.info("Please select a project first!");
        return null;
    }

    private List<ModNode> getSelectedProjects(MFXListView<ModNode> listView) {
        IMultipleSelectionModel<ModNode> selectionModel = listView.getSelectionModel();
        if (selectionModel != null && !selectionModel.getSelectedValues().isEmpty())
            return selectionModel.getSelectedValues();
        else
            MessageUtils.info("Please select project(s) first!");
        return Collections.emptyList();
    }

}
