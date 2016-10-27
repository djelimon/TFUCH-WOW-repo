package com.uuh.wow.ppgen.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Helper class to wrap a list of slideAssets. This is used for saving the
 * list of persons to XML.
 *
 * @author Marco Jakob
 */
@XmlRootElement(name = "slides")
public class SlideAssetWrapper {

    private List<SlideAsset> slideAssets;

    @XmlElement(name = "slideAssets")
    public List<SlideAsset> getSlideAssets() {
        return slideAssets;
    }

    public void setSlideAssets(List<SlideAsset> slideAssets) {
        this.slideAssets = slideAssets;
    }
}
