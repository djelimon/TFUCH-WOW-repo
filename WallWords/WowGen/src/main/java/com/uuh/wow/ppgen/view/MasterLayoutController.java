package com.uuh.wow.ppgen.view;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.uuh.wow.ppgen.SlideshowMasterApp;
import com.uuh.wow.ppgen.util.FileUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

/**
 * The controller for the root layout. The root layout provides the basic
 * application layout containing a menu bar and space where other JavaFX
 * elements can be placed.
 *
 * @author Marco Jakob
 */
public class MasterLayoutController {

    private static final String DOCX_EXTENSION = ".DOCX";
    // Reference to the main application
    private SlideshowMasterApp composer;

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param composer
     */
    public void setComposer(SlideshowMasterApp composer) {
        this.composer = composer;
    }

    /**
     * Creates an empty address book.
     *
     * @throws Exception
     */
    // @FXML
    // private void handleNew() {
    // mainApp.getPersonData().clear();
    // mainApp.setPersonFilePath(null);
    // }

    @FXML
    /**
     * Add assets from folder to working folder using file chooser
     *
     * @throws IOException
     */
    private void handleAddAssets() throws Exception {
        File workFolder = composer.getWorkingFolder();
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
            "Text, docx, image, and pptx files", "*.jpg", "*.jpeg", "*.png", "*.pptx", "*.docx",
            "*.JPG", "*.JPEG", "*.PNG", "*.PPTX", "*.txt", "*.TXT", "*.DOCX");

        fileChooser.getExtensionFilters().add(extFilter);

        List<File> assets = fileChooser.showOpenMultipleDialog(composer.getPrimaryStage());
        if (assets != null) {
            for (File asset : assets) {
                // convert word to text
                if (asset.getName().toUpperCase().trim().endsWith(DOCX_EXTENSION)) {
                    String targetName = asset.getName().substring(0,
                        asset.getName().length() - DOCX_EXTENSION.length());
                    targetName += ".txt";
                    File targetFile = new File(workFolder, targetName);
                    FileUtils.docxToText(asset, targetFile);
                } else {
                    FileUtils.copyFile(asset, new File(workFolder, asset.getName()));
                }
            }
            composer.loadWorkingAssets();
        }

    }

    /**
     * Opens a FileChooser to let the user select an address book to load.
     */
    // @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)",
            "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showOpenDialog(composer.getPrimaryStage());

        if (file != null) {
            // composer.loadPersonDataFromFile(file);
        }
    }

    /**
     * Saves the file to the person file that is currently open. If there is no
     * open file, the "save as" dialog is shown.
     */
    // @FXML
    // private void handleSave() {
    // File personFile = mainApp.getPersonFilePath();
    // if (personFile != null) {
    // mainApp.savePersonDataToFile(personFile);
    // } else {
    // handleSaveAs();
    // }
    // }

    /**
     * Opens a FileChooser to let the user select a file to save to.
     */
    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)",
            "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(composer.getPrimaryStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            // composer.savePersonDataToFile(file);
        }
    }

    /**
     * Opens an about dialog.
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("AddressApp");
        alert.setHeaderText("About");
        alert.setContentText("Author: Marco Jakob\nWebsite: http://code.makery.ch");

        alert.showAndWait();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
}
