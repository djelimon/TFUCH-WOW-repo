package com.uuh.wow.ppgen.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class TextAsset extends SlideAsset {


    private IntegerProperty slideMaxFontSize = new SimpleIntegerProperty();

    private BooleanProperty callAndResponse;

    public TextAsset(){
        super();
    }
}
