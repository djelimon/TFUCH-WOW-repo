package com.uuh.wow.ppgen.view;

import java.io.File;

import com.uuh.wow.ppgen.SlideshowMasterApp;
import com.uuh.wow.ppgen.model.SlideAsset;
import com.uuh.wow.ppgen.util.DateUtil;
import com.uuh.wow.ppgen.util.SlideAssetComparator;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 * Dialog to edit details of a person.
 *
 * @author Marco Jakob
 */
public class RankAssetDialogController {

    @FXML
    private TextField rankField;
    @FXML
    private Label fileNameLabel;
    @FXML
    private Label pathLabel;

    SlideshowMasterApp composer;



    public void setComposer(SlideshowMasterApp composer) {
        this.composer = composer;
    }

    private Stage dialogStage;
    private SlideAsset asset;
    private boolean okClicked = false;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the asset to be edited in the dialog.
     *
     * @param person
     */
    public void setAsset(SlideAsset asset) {
        this.asset = asset;

        rankField.setText(asset.getRank().toString());
        fileNameLabel.setText(asset.getFileName());
        pathLabel.setText(asset.getAbsolutePath());

    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            Integer newRank = Integer.valueOf(rankField.getText());
            File assetFile = new File(asset.getAbsolutePath());
            File rankedFile = composer.rankAsset(assetFile, newRank);
            asset.setRank(newRank);
            asset.setAbsolutePath(rankedFile.getAbsolutePath());
            asset.setFileName(rankedFile.getName());
            okClicked = true;
            dialogStage.close();

        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (rankField.getText() == null || rankField.getText().length() == 0) {
            errorMessage += "Rank must be populated!\n";
        }


        try {
            Integer newRank = Integer.valueOf(rankField.getText());
            if (newRank < 0 || newRank > 999) {
                errorMessage += "Rank must be >= 0 or <= 999!\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "Rank is invalid integer format!\n";
        }



        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}