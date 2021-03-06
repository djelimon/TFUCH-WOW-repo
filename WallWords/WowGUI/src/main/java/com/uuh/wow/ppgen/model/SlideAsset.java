package com.uuh.wow.ppgen.model;

import java.io.File;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SlideAsset {

    //private File rawAsset;

    private HymnalType hymnal;

    private Integer maxSize = null;

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public HymnalType getHymnal() {
        return hymnal;
    }

    public void setHymnal(HymnalType hymnal) {
        this.hymnal = hymnal;
    }

    private StringProperty absolutePath = new SimpleStringProperty();

    private IntegerProperty rank = new SimpleIntegerProperty();

    private StringProperty fileName = new SimpleStringProperty();

    //private File rawAsset;
    private AssetType assetType;

    public void setAssetType(AssetType assetType){
        this.assetType = assetType;
    }

    public AssetType getAssetType(){
        return this.assetType;
    }

    public String getAbsolutePath(){
        return absolutePath.get();
    }

    public void setAbsolutePath(String absolutePath){
        this.absolutePath.set(absolutePath);
    }

    public StringProperty absolutePathProperty(){
        return absolutePath;
    }

    public Integer getRank(){
        return rank.get();
    }

    public void setRank(Integer rank){
        this.rank.set(rank);
    }

    public IntegerProperty rankProperty(){
        return rank;
    }

    public String getFileName(){
        return fileName.get();
    }

    public void setFileName(String fileName){
        this.fileName.set(fileName);
    }

    public StringProperty fileNameProperty(){
        return fileName;
    }
}
