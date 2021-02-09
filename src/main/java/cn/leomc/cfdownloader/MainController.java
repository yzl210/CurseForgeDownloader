package cn.leomc.cfdownloader;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public ListView<CurseForgeProjectWrapper> projectListView;
    public TextField searchBar;
    public Button searchButton;
    public Button downloadButton;
    public Label resultsLabel;

    public ListView<CurseForgeProjectWrapper> projectListListView;
    public Button removeSelectedButton;
    public Button downloadAllButton;
    public Button exportButton;
    public Button importButton;
    public Button removeAllButton;
    public Button addToListButton;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        projectListView.setCellFactory(param -> new PListCell());
        projectListListView.setCellFactory(param -> new PListCell());
    }

    public void onSearchButtonAction(ActionEvent actionEvent) {
        if (!searchBar.getText().isEmpty()) {
            List<CurseForgeProjectWrapper> curseForgeProjectWrappers = CurseForgeProjectWrapper.parseList(CurseForgeUtils.searchProjects(searchBar.getText()));
            if (!curseForgeProjectWrappers.isEmpty()) {
                resultsLabel.setText("Found " + curseForgeProjectWrappers.size() + " result(s)");
                projectListView.setItems(FXCollections.observableArrayList(curseForgeProjectWrappers));
            } else {
                projectListView.setItems(FXCollections.emptyObservableList());
                resultsLabel.setText("No result!");
            }
        } else
            MessageUtils.info("Please type the name!");
    }

    public void onDownloadButtonAction(ActionEvent actionEvent) {
        try {

            CurseForgeProjectWrapper project = getSelectedProject(projectListView);
            if (project == null)
                return;
            Optional<CurseFiles<CurseFile>> optionalFiles = CurseAPI.files(project.getProject().id());
            if (optionalFiles.isPresent()) {
                CurseFile file = optionalFiles.get().withComparator(CurseFiles.SORT_BY_NEWEST).first();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select Download Directory");
                String extension = file.displayName().substring(file.displayName().lastIndexOf(".") + 1);
                FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(extension.toUpperCase() + " File", "*." + extension);
                fileChooser.getExtensionFilters().add(extensionFilter);
                fileChooser.setInitialFileName(file.displayName());
                File directory = fileChooser.showSaveDialog(stage);
                if (directory != null) {
                    file.download(directory.toPath());
                    MessageUtils.info("Download finished!");
                }
            } else {
                MessageUtils.info("This project has no file!");
            }
        } catch (Exception e) {
            MessageUtils.error(e);
        }
    }

    public void onAddToListButtonAction(ActionEvent actionEvent) {
        try {
            CurseForgeProjectWrapper project = getSelectedProject(projectListView);
            if (project == null)
                return;
            if (!projectListListView.getItems().contains(project))
                projectListListView.getItems().add(project);
            else
                MessageUtils.info("This project is already in the list!");
        } catch (Exception e) {
            MessageUtils.error(e);
        }
    }

    public void onRemoveSelectedButtonAction(ActionEvent actionEvent) {
        CurseForgeProjectWrapper project = getSelectedProject(projectListListView);
        if (project == null)
            return;
        projectListListView.getItems().remove(project);
    }

    public void onRemoveAllButtonAction(ActionEvent actionEvent) {
        projectListListView.getItems().clear();
    }

    public void onImportButtonAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Import File");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Any File", "*.*");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            for (CurseForgeProjectWrapper project : FileUtils.readProjects(file)) {
                if (!projectListListView.getItems().contains(project))
                    projectListListView.getItems().add(project);
            }
            MessageUtils.info("Finished importing!");
        }
    }

    public void onExportButtonAction(ActionEvent actionEvent) {
        if (!projectListListView.getItems().isEmpty()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Export Directory");
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("TXT file", "*.txt");
            fileChooser.getExtensionFilters().add(extensionFilter);
            fileChooser.setInitialFileName("ExportedProjectList.txt");
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                StringBuilder builder = new StringBuilder();
                for (CurseForgeProjectWrapper project : projectListListView.getItems()) {
                    builder
                            .append(project.getProject().id())
                            .append("\n");
                }

                FileUtils.writeToFile(file, builder.toString());
                MessageUtils.info("Exported to " + file.getAbsolutePath());
            }
        } else
            MessageUtils.info("Project List is empty!");
    }

    public void onDownloadAllButtonAction(ActionEvent actionEvent) {
        if (!projectListListView.getItems().isEmpty()) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Download Directory");
            File directory = directoryChooser.showDialog(stage);
            if (directory != null) {
                for (CurseForgeProjectWrapper project : projectListListView.getItems()) {
                    try {
                        CurseForgeUtils.getLatestFile(project.getProject()).downloadToDirectory(directory.toPath());
                    } catch (CurseException e) {
                        MessageUtils.warn("Unable to download project: " + project.getProject().name(), e.toString());
                    }
                }
            }
        } else
            MessageUtils.info("Project List is empty!");
    }


    private CurseForgeProjectWrapper getSelectedProject(ListView<CurseForgeProjectWrapper> listView) {
        SelectionModel<CurseForgeProjectWrapper> selectionModel = listView.getSelectionModel();
        if (selectionModel != null && !selectionModel.isEmpty() && selectionModel.getSelectedItem().getProject() != null)
            return selectionModel.getSelectedItem();
        else
            MessageUtils.info("Please select one project first!");
        return null;
    }

}
