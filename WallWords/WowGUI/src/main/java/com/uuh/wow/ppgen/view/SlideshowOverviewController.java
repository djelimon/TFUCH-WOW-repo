package com.uuh.wow.ppgen.view;


import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.File;
import java.util.List;

import org.apache.poi.sl.usermodel.PictureData.PictureType;

import com.uuh.wow.main.SlideGenerator;
import com.uuh.wow.ppgen.SlideshowMasterApp;
import com.uuh.wow.ppgen.model.SlideAsset;
import com.uuh.wow.ppgen.util.DateUtil;
import com.uuh.wow.ppgen.util.SlideAssetComparator;

public class SlideshowOverviewController {
    @FXML
    private TableView<SlideAsset> assetTable;
    @FXML
    private TableColumn<SlideAsset, Number> rankColumn;
    @FXML
    private TableColumn<SlideAsset, String> filenameColumn;
    @FXML
    private TableColumn<SlideAsset, String> pathColumn;

    @FXML
    private Spinner<Integer> globalMaxFontSize;

    // Reference to the main application.
    private SlideshowMasterApp composer;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public SlideshowOverviewController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize the person table with the two columns.
        rankColumn.setCellValueFactory(cellData -> cellData.getValue().rankProperty());
        filenameColumn.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());
        pathColumn.setCellValueFactory(cellData -> cellData.getValue().absolutePathProperty());
        globalMaxFontSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 100, 100));

     // Clear person details.
      //  showPersonDetails(null);

//        // Listen for selection changes and show the person details when changed.
//        assetTable.getSelectionModel().selectedItemProperty().addListener(
//                (observable, oldValue, newValue) -> showPersonDetails(newValue));

    }


    /**
     * Called when the user clicks on the delete button.
     */
    @FXML
    private void handleRemovAsset() {
        int selectedIndex = assetTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            SlideAsset selectedAsset = assetTable.getItems().get(selectedIndex);
            File assetFile = new File(selectedAsset.getAbsolutePath());
            assetFile.delete();
            assetTable.getItems().remove(selectedIndex);
        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(composer.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Asset Selected");
            alert.setContentText("Please select an asset to remove.");

            alert.showAndWait();
        }
    }

//    /**
//     * Called when the user clicks the new button. Opens a dialog to edit
//     * details for a new person.
//     */
//    @FXML
//    private void handleNewPerson() {
//        Person tempPerson = new Person();
//        boolean okClicked = mainApp.showPersonEditDialog(tempPerson);
//        if (okClicked) {
//            mainApp.getPersonData().add(tempPerson);
//        }
//    }
//
//    /**
//     * Called when the user clicks the edit button. Opens a dialog to edit
//     * details for the selected person.
//     */
    @FXML
    private void handleEditAsset() {
        SlideAsset selectedAsset = assetTable.getSelectionModel().getSelectedItem();
        if (selectedAsset != null) {
            boolean okClicked = composer.showEditAssetDialog(selectedAsset);
            if (okClicked) {
                composer.getSlideshowData().sort(new SlideAssetComparator());
            }

        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(composer.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Asset Selected");
            alert.setContentText("Please select an asset in the table.");

            alert.showAndWait();
        }
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setComposer(SlideshowMasterApp composer) {
        this.composer = composer;

        // Add observable list data to the table
        assetTable.setItems(composer.getSlideshowData());
        composer.setGlobalMaxFont(globalMaxFontSize.getValue());
    }

    /**
     * Preview images, edit text
     */

    @FXML
    private void handleViewEdit(){
        SlideAsset selectedAsset = assetTable.getSelectionModel().getSelectedItem();
        if (selectedAsset != null) {
           String upperCaseFileName = selectedAsset.getFileName().toUpperCase();
        if (upperCaseFileName.endsWith(".TXT")){
               renderTextEdit(selectedAsset);
           }else if(upperCaseFileName.endsWith(".PPTX")){
               Alert alert = new Alert(AlertType.WARNING);
               alert.initOwner(composer.getPrimaryStage());
               alert.setTitle("Not  Supported");
               alert.setHeaderText("Sorry, but you will need Microsoft Powerpoint to edit this file");
               alert.setContentText("Please select another asset in the table.");

               alert.showAndWait();

           }else{
               renderImagePreview(selectedAsset);
           }

        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(composer.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Asset Selected");
            alert.setContentText("Please select an asset in the table.");

            alert.showAndWait();
        }
    }

    private void renderImagePreview(SlideAsset selectedAsset) {
        boolean okClicked = composer.showPreviewImageDialog(selectedAsset);
    }

    private void renderTextEdit(SlideAsset asset) {
        // TODO Auto-generated method stub

    }

    @FXML
    private void generateSlideShow(){
        SlideGenerator generator = new SlideGenerator();
        try {
            generator.setSourceFolder(composer.getWorkingFolder());
            generator.setGlobalMaxSize(globalMaxFontSize.getValue());
            generator.processContentInFolder(composer.getWorkingFolder());


            FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "PowerPoint file (*.pptx)", "*.pptx", "*.PPTX");

            fileChooser.getExtensionFilters().add(extFilter);

            File target = fileChooser.showSaveDialog(composer.getPrimaryStage());
            if (target != null){
                generator.dumpSlideShow(target);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @FXML
    private void genSlideShow(){
        SlideGenerator generator = new SlideGenerator();
        boolean headerFile = true;
        try {
            ObservableList<SlideAsset> data = composer.getSlideshowData();
            for (SlideAsset slideAsset : data) {
                File assetFile = new File(slideAsset.getAbsolutePath());
                String assetFileName = assetFile.getName().toUpperCase();
                PictureType picType;
                Integer maxSize = (slideAsset.getMaxSize() == null)?globalMaxFontSize.getValue():slideAsset.getMaxSize();
                switch (slideAsset.getAssetType()) {

                    case GRAPHIC:
                        if(assetFileName.endsWith(".JPG") ||
                           assetFileName.endsWith(".JPEG")){
                           picType = PictureType.JPEG;
                        }else{
                            picType = PictureType.PNG;
                        }

                        generator.appendImage(assetFile, picType);
                        break;
                    case UNISON:
                        generator.appendSpacedText(assetFile, false, maxSize, null);
                        break;
                    case HYMN:
                        generator.appendSpacedText(assetFile, false, maxSize, slideAsset.getHymnal().toString());
                        break;
                    case RESPONSIVE:
                        generator.appendSpacedText(assetFile, true, maxSize, null);
                        break;
                    case SLIDESHOW:
                        generator.appendSlides(assetFile, headerFile);
                        headerFile = false;
                        break;
                    default:
                        break;
                }
                generator.appendBlankSlide();
            }



            FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "PowerPoint file (*.pptx)", "*.pptx", "*.PPTX");

            fileChooser.getExtensionFilters().add(extFilter);

            File target = fileChooser.showSaveDialog(composer.getPrimaryStage());
            if (target != null){
                generator.dumpSlideShow(target);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

//    /**
//     * Resort the list
//     */
//    @FXML
//    private void handleRefresh(){
//        ObservableList<SlideAsset> data = composer.getSlideshowData();
//        data.sort(new SlideAssetComparator());
//    }


}