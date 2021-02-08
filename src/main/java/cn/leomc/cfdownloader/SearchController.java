package cn.leomc.cfdownloader;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.project.CurseProject;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SearchController implements Initializable {

    public ListView<CurseForgeProjectWrapper> projectListView;
    public TextField searchBar;
    public Button searchButton;
    public Button downloadButton;
    public Label resultsLabel;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
            SelectionModel<CurseForgeProjectWrapper> selectionModel = projectListView.getSelectionModel();
            if (selectionModel != null && !selectionModel.isEmpty() && selectionModel.getSelectedItem().getProject() != null) {
                CurseProject project = selectionModel.getSelectedItem().getProject();
                Optional<CurseFiles<CurseFile>> optionalFiles = CurseAPI.files(project.id());
                if (optionalFiles.isPresent()) {
                    CurseFile file = optionalFiles.get().withComparator(CurseFiles.SORT_BY_NEWEST).first();
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Select Download Directory");
                    fileChooser.setInitialFileName(file.displayName());
                    File directory = fileChooser.showSaveDialog(stage);
                    if (directory != null)
                        file.download(directory.toPath());
                } else {
                    MessageUtils.info("This project has no file!");
                }
            } else {
                MessageUtils.info("Please select one mod first!");
            }
        } catch (Exception e) {
            MessageUtils.error(e);
        }
    }
}
