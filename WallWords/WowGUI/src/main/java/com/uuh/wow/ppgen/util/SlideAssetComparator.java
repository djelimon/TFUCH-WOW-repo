package com.uuh.wow.ppgen.util;

import java.util.Comparator;

import com.uuh.wow.ppgen.model.SlideAsset;

public class SlideAssetComparator implements Comparator<SlideAsset> {

    @Override
    public int compare(SlideAsset o1, SlideAsset o2) {
        // TODO Auto-generated method stub
        return o1.getRank() - o2.getRank();
    }

}
