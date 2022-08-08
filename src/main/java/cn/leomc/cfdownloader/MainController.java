package cn.leomc.cfdownloader;

import io.github.matyrobbrt.curseforgeapi.schemas.mod.Mod;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    public ListView<CFModWrapper> modListView;
    public TextField searchBar;
    public Button searchButton;
    public Button downloadButton;
    public Label resultsLabel;
    public MFXProgressSpinner progressSpinner;


    public ListView<CFModWrapper> modListListView;
    public Button removeSelectedButton;
    public Button downloadAllButton;
    public Button exportButton;
    public Button importButton;
    public Button removeAllButton;
    public Button addToListButton;
    private Stage stage;


    private CompletableFuture<?> initTask;
    private CompletableFuture<?> searchTask;

    private String lastSearch = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modListView.setCellFactory(param -> new ModListCell());
        modListListView.setCellFactory(param -> new ModListCell());
        initTask = CompletableFuture
                .supplyAsync(() -> CurseForgeUtils.searchMods(""))
                .exceptionally(t -> {
                    Log.LOGGER.log(Level.WARNING, t, () -> "Failed to load default mods");
                    return Collections.emptyList();
                })
                .thenAccept(mods -> Platform.runLater(() -> {
                    if(initTask == null)
                        return;
                    List<CFModWrapper> cfModWrappers = CFModWrapper.of(mods);
                    modListView.setItems(FXCollections.observableArrayList(cfModWrappers));

                    initTask = null;
                }));
    }

    public void onSearchButtonAction(ActionEvent actionEvent) {
        if (!searchBar.getText().isEmpty() && !lastSearch.equals(searchBar.getText()) && searchTask == null) {
            if(initTask != null)
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
                            List<CFModWrapper> cfModWrappers = CFModWrapper.of(mods);
                            if (!cfModWrappers.isEmpty()) {
                                resultsLabel.setText("Found " + cfModWrappers.size() + " mod(s)");
                                modListView.setItems(FXCollections.observableArrayList(cfModWrappers));
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
            CFModWrapper mod = getSelectedProject(modListView);
            if (mod == null)
                return;
            CurseForgeUtils.getLatestFile(mod.getMod()).ifPresentOrElse(file -> {
                    if(CurseForgeUtils.download(file, stage))
                        MessageUtils.info("Download finished!");
            }, () -> MessageUtils.info("This project has no file!"));
    }

    public void onAddToListButtonAction(ActionEvent actionEvent) {
        try {
            CFModWrapper project = getSelectedProject(modListView);
            if (project == null)
                return;
            if (!modListListView.getItems().contains(project))
                modListListView.getItems().add(project);
            else
                MessageUtils.info("This project is already in the list!");
        } catch (Exception e) {
            MessageUtils.error(e);
        }
    }

    public void onRemoveSelectedButtonAction(ActionEvent actionEvent) {
        CFModWrapper project = getSelectedProject(modListListView);
        if (project == null)
            return;
        modListListView.getItems().remove(project);
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
            for (CFModWrapper project : FileUtils.readProjects(file)) {
                if (!modListListView.getItems().contains(project))
                    modListListView.getItems().add(project);
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
                for (CFModWrapper project : modListListView.getItems()) {
                    builder
                            .append(project.getMod().id())
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
           if(fails != null)
               if (fails.isEmpty())
                   MessageUtils.info("Download Finished!");
               else {
                   String failed = fails.stream().map(Mod::name).collect(Collectors.joining(", "));
                   MessageUtils.warn("Not downloaded mods", "The following mods have failed to download: \n" + failed);
               }
        }
        else
            MessageUtils.info("Project List is empty!");
    }


    private CFModWrapper getSelectedProject(ListView<CFModWrapper> listView) {
        SelectionModel<CFModWrapper> selectionModel = listView.getSelectionModel();
        if (selectionModel != null && !selectionModel.isEmpty() && selectionModel.getSelectedItem().getMod() != null)
            return selectionModel.getSelectedItem();
        else
            MessageUtils.info("Please select a project first!");
        return null;
    }

}
