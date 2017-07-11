package com.uuh.wow.ppgen;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.uuh.wow.ppgen.model.AssetType;
import com.uuh.wow.ppgen.model.HymnalType;
import com.uuh.wow.ppgen.model.SlideAsset;
import com.uuh.wow.ppgen.view.EditAssetDialogController;
import com.uuh.wow.ppgen.view.MasterLayoutController;

import com.uuh.wow.ppgen.view.PreviewImageDialogController;

import com.uuh.wow.ppgen.view.SlideshowOverviewController;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SlideshowMasterApp extends Application {

    private static final int FILENAME_GROUP = 3;
    private static final int MAX_INDEX = 999;
    private static final int INDEX_GROUP = 1;
    private static final String INDEX_REGEX = "(\\d+)(\\s*-)(.+)";
    private Stage primaryStage;
    private BorderPane masterLayout;
    private File workingFolder;
    private Pattern indexPattern = Pattern.compile(INDEX_REGEX);
    private Integer globalMaxFont;



    public Integer getGlobalMaxFont() {
        return globalMaxFont;
    }


    public void setGlobalMaxFont(Integer globalMaxFont) {
        this.globalMaxFont = globalMaxFont;
    }

    /**
     * The data as an observable list of Persons.
     */
    private ObservableList<SlideAsset> slideshowData = FXCollections.observableArrayList();



	public SlideshowMasterApp(){
	    // Create working directory in home directory if not already there
	    File homeDir = new File(System.getProperty("user.home"));
	    if (! homeDir.exists()){
	        homeDir.mkdirs();
	    }
	    this.workingFolder= new File(homeDir,"UUSlidesWF");
	    this.workingFolder.mkdir();
	}


	public void loadWorkingAssets(){

	    this.slideshowData.clear();
	    File[] assets = this.workingFolder.listFiles((dir, name) -> !name.equals(".DS_Store"));
	    for (File asset : assets) {
	        if (asset.isDirectory()){
	            continue;
	        }
	        File rankedAsset = rankAsset(asset, null);
	        SlideAsset slideAsset = new SlideAsset();
	        slideAsset.setRank(getRank(rankedAsset));
	        String path = rankedAsset.getPath();
	        String absolutePath = rankedAsset.getAbsolutePath();
	        String name = rankedAsset.getName();
	        slideAsset.setAbsolutePath(absolutePath);
            slideAsset.setFileName(name);
            String extension = slideAsset.getFileName().toUpperCase();
            if (slideAsset.getAssetType() == null) {
                if (extension.endsWith(".TXT")) {
                    slideAsset.setAssetType(AssetType.UNISON);

//                    slideAsset.setAssetType(AssetType.HYMN);
//                    slideAsset.setHymnal(HymnalType.OTHER);
                    if (slideAsset.getMaxSize() == null){
                        slideAsset.setMaxSize(getGlobalMaxFont());
                        //asset.setMaxSize(100);
                    }

                } else if (extension.endsWith(".PPTX")) {
                    slideAsset.setAssetType(AssetType.SLIDESHOW);

                } else {
                    slideAsset.setAssetType(AssetType.GRAPHIC);


                }
            }

            this.slideshowData.add(slideAsset);
        }
	}

    public File rankAsset(File asset, Integer newRank) {
        String name = asset.getName();
        Matcher matcher = indexPattern.matcher(name);
        // file has been ranked
        if (matcher.matches()){
            String index = matcher.group(INDEX_GROUP);
            String rest = matcher.group(FILENAME_GROUP);
            if (newRank != null){
                String newRankString = String.format ("%03d", newRank);
                File rankedAsset = new File(asset.getParent(), newRankString + "-" + rest);
                asset.renameTo(rankedAsset);
                return rankedAsset;
            }
            return asset;
        }else{
            // new stuff goes to the bottom
            //rank the file
            File rankedAsset = new File(asset.getParent(), MAX_INDEX + "-" + asset.getName());
            asset.renameTo(rankedAsset);
            return rankedAsset;
        }

    }

    public Integer getRank(File asset){
        String name = asset.getName();
        Matcher matcher = indexPattern.matcher(name);
        // file has been ranked
        if (matcher.matches()){
            String index = matcher.group(INDEX_GROUP);
            return Integer.valueOf(index);
        }else{
            // new stuff goes to the bottom
            //rank the file
            return MAX_INDEX;

        }
    }


    public File getWorkingFolder() {
        return workingFolder;
    }



    public Stage getPrimaryStage() {
        return primaryStage;
    }




    public ObservableList<SlideAsset> getSlideshowData() {
        return slideshowData;
    }



    public void setSlideshowData(ObservableList<SlideAsset> slideshowData) {
        this.slideshowData = slideshowData;
    }



    @Override
	public void start(Stage primaryStage) {
	    this.primaryStage = primaryStage;
        this.primaryStage.setTitle("SlideComposerApp");

        initMasterLayout();

        showSlideShowOverview();

	}

	private void showSlideShowOverview() {
	    try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SlideshowMasterApp.class.getResource("/fxml/SlideshowOverview.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            masterLayout.setCenter(personOverview);

            // Give the controller access to the main app.
            SlideshowOverviewController controller = loader.getController();
            controller.setComposer(this);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initMasterLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SlideshowMasterApp.class
                    .getResource("/fxml/MasterLayout.fxml"));
            //.getResource("view/MasterLayout.fxml"));
            masterLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(masterLayout);
            primaryStage.setScene(scene);

            // Give the controller access to the main app.
            MasterLayoutController controller = loader.getController();
            controller.setComposer(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadWorkingAssets();

    }

    /**
     * Opens a dialog to rank an asset, i.e. give it a numeric prefix for sorting purposes. If the user
     * clicks OK, the changes are saved into the provided slide Asset object and true
     * is returned.
     *
     * @param person the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showEditAssetDialog(SlideAsset asset) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            //loader.setLocation(SlideshowMasterApp.class.getResource("view/EditAssetDialogue.fxml"));
            loader.setLocation(SlideshowMasterApp.class.getResource("/fxml/EditAssetDialogue.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Rank Asset");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            EditAssetDialogController controller = loader.getController();
            controller.setComposer(this);
            controller.setDialogStage(dialogStage);
            controller.setAsset(asset);


            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Opens a dialog to rank an asset, i.e. give it a numeric prefix for sorting purposes. If the user
     * clicks OK, the changes are saved into the provided slide Asset object and true
     * is returned.
     *
     * @param person the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showHymnSpecificEdit(SlideAsset asset) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SlideshowMasterApp.class.getResource("/fxml/HymnEditDialogue.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Rank Asset");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            EditAssetDialogController controller = loader.getController();
            controller.setComposer(this);
            controller.setDialogStage(dialogStage);
            controller.setAsset(asset);


            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Opens a dialog to rank an asset, i.e. give it a numeric prefix for sorting purposes. If the user
     * clicks OK, the changes are saved into the provided slide Asset object and true
     * is returned.
     *
     * @param person the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showEditTextDialog(SlideAsset asset) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SlideshowMasterApp.class.getResource("/fxml/EditTextDialogue.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Text Asset");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            EditAssetDialogController controller = loader.getController();
            controller.setComposer(this);
            controller.setDialogStage(dialogStage);
            controller.setAsset(asset);


            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Opens a dialog to rank an asset, i.e. give it a numeric prefix for sorting purposes. If the user
     * clicks OK, the changes are saved into the provided slide Asset object and true
     * is returned.
     *
     * @param person the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showPreviewImageDialog(SlideAsset asset) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SlideshowMasterApp.class.getResource("/fxml/PreviewImageDialogue.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Preview Image Asset");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            PreviewImageDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setAsset(asset);
            controller.setComposer(this);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
		launch(args);
	}
}
