package com.uuh.wow.ppgen.view;

import java.io.File;

import com.uuh.wow.ppgen.SlideshowMasterApp;
import com.uuh.wow.ppgen.model.AssetType;
import com.uuh.wow.ppgen.model.HymnalType;
import com.uuh.wow.ppgen.model.SlideAsset;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Dialog to edit a slide asset
 *
 * @author John Elliott
 */
public class EditAssetDialogController {

    @FXML
    private TextField rankField;
    @FXML
    private Label fileNameLabel;
    @FXML
    private Label pathLabel;
    @FXML
    private ComboBox<AssetType> assetType = new ComboBox<AssetType>();
    @FXML
    private ComboBox<HymnalType> hymnalType = new ComboBox<HymnalType>();
    @FXML
    private Boolean enableSpecificPRops = false;
    @FXML
    private Button editSpecificPropsButton;
    @FXML
    private Spinner<Integer> assetMaxFontSize;

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
        assetType.getItems().setAll(AssetType.values());
        hymnalType.getItems().setAll(HymnalType.values());
        assetMaxFontSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 100, 100));


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
        String extension = asset.getFileName().toUpperCase();
        if (asset.getAssetType() == null) {
            if (extension.endsWith(".TXT")) {
                assetType.setValue(AssetType.UNISON);
//                asset.setAssetType(AssetType.HYMN);
//                hymnalType.setValue(HymnalType.OTHER);
                if (asset.getMaxSize() == null){
                    asset.setMaxSize(composer.getGlobalMaxFont());
                    //asset.setMaxSize(100);
                }

            } else if (extension.endsWith(".PPTX")) {
                asset.setAssetType(AssetType.SLIDESHOW);

            } else {
                asset.setAssetType(AssetType.GRAPHIC);
                assetType.setValue(AssetType.GRAPHIC);

            }
        } else {
            assetType.setValue(asset.getAssetType());

        }
        syncGUIToAsset(asset);

    }

    private void syncGUIToAsset(SlideAsset asset) {
        // enable special properties if needed
        assetType.setValue(asset.getAssetType());

        switch (asset.getAssetType()) {
            case HYMN:
                hymnalType.setDisable(false);
                hymnalType.setValue(asset.getHymnal());
                break;

            default:
                hymnalType.setDisable(true);
                break;
        }
//        if (asset.getMaxSize() != null){
//            //assetMaxFontSize = new Spinner<Integer>(2,composer.getGlobalMaxFont(),asset.getMaxSize());
//            assetMaxFontSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 100, asset.getMaxSize()));
//            assetMaxFontSize.setDisable(true);
//        }else{
//            //assetMaxFontSize = new Spinner<Integer>(2,composer.getGlobalMaxFont(),composer.getGlobalMaxFont());
//            assetMaxFontSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 100, composer.getGlobalMaxFont()));
//            assetMaxFontSize.setDisable(true);
//        }
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
            asset.setAssetType(assetType.getValue());
            if (asset.getAssetType() == AssetType.HYMN){
                asset.setHymnal(hymnalType.getValue());
            }
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


    @FXML
    private void handleAssetTypeSelect(){
        asset.setAssetType(assetType.getValue());
        syncGUIToAsset(asset);
    }
}