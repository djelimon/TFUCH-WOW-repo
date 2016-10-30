package com.uuh.wow.ppgen.view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.uuh.wow.ppgen.SlideshowMasterApp;
import com.uuh.wow.ppgen.model.SlideAsset;
import com.uuh.wow.ppgen.util.DateUtil;
import com.uuh.wow.ppgen.util.SlideAssetComparator;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


/**
 * Dialog to edit details of a person.
 *
 * @author Marco Jakob
 */
public class PreviewImageDialogController  implements Initializable  {

    @FXML
    private Image img;
    @FXML
    private ImageView imageView;
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
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the asset to be viewed in the dialog.
     *
     * @param person
     */
    public void setAsset(SlideAsset asset) {
        this.asset = asset;
        //populateImageView(asset);
        //img = new Image("file://" + asset.getAbsolutePath());

        //imageView.setCache(true);


    }

    @FXML
    private void populateImageView() {
        BufferedImage bufferedImage;
        //try {
//            bufferedImage = ImageIO.read(new File(asset.getAbsolutePath()));
//            img = SwingFXUtils.toFXImage(bufferedImage, null);
           Image img=new Image("file:" + asset.getAbsolutePath());
           imageView=new ImageView(img);
           //img = new Image(imageFile.toURI().toString());
         //  imageView = new ImageView(img);

        //} catch (IOException e) {
        //    Alert alert = new Alert(AlertType.ERROR);
        //    alert.initOwner(dialogStage);
        //    alert.setTitle("Trouble Viewing Image");
        //    alert.setHeaderText("An error was thrown trying to view the image:");
        //    alert.setContentText(e.getMessage());

          //  alert.showAndWait();
       // }



        imageView = new ImageView();
        imageView.setImage(img);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        dialogStage.show();
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

            okClicked = true;
            dialogStage.close();
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }


}