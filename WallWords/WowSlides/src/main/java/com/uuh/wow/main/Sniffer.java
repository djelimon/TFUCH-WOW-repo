package com.uuh.wow.main;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

public class Sniffer {

    public static void main(String[] args){
        File slideshow = new File("/Users/johnelliott/UUSlidesWF/targ/dump.pptx");
        try {
            XMLSlideShow srcPPT = new XMLSlideShow(new FileInputStream(slideshow));

            List<XSLFSlideMaster> masters = srcPPT.getSlideMasters();
            int i = 0;
            for (XSLFSlideMaster master : masters) {
                System.out.println("Master " + i);
                System.out.println("slide height " + srcPPT.getPageSize().height);

                XSLFSlideLayout tnc = master.getLayout(SlideLayout.TITLE_AND_CONTENT);
                XSLFTextShape[] placeHolders = tnc.getPlaceholders();
                int j = 0;
                for (XSLFTextShape placeholder : placeHolders) {
                    System.out.println("    Shape " + j);
                    Rectangle2D anchor = placeholder.getAnchor();
                    double height = anchor.getHeight();
                    System.out.println("        Anchor height: " + height );
                    Rectangle2D frame = anchor.getFrame();
                    double frameHeight = frame.getHeight();
                    System.out.println("        Frame height: " + height );
                    j++;
                }
                i++;
            }
                    } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
