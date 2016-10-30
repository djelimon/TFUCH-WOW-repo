package com.uuh.wow.main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.PictureData.PictureType;
import org.apache.poi.sl.usermodel.TextShape.TextAutofit;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

public class SlideGenerator {

    private static final String POWERPOINT_PRESENTATION_XML_EXTENSION = "PPSX";
    private static final String POWERPOINT_XML_EXTENSION = "PPTX";
    private static final String TEXT_EXTENSION = "TXT";
    private static final String BODY_FONT = "Gill Sans (Body)";
    private static final String TITLE_FONT = "Gill Sans (Headings)";
    private static final String ITALICS_TAG = "<i>";
    private static final String LINES_PER_PAGE_SUFFIX = "LPP";
    private static final String DESCRIPTOR_TAG = "<descriptor>";
    private static final double DECREMENT = 0.01;
    private static final String USAGE = "CORRECT USAGE: java PPTSlideGen /path/to/sourceFolder";
    private static final int NUM_PARMS = 1;
    private File sourceFolder;
    private File targetFolder;
    private XMLSlideShow ppt = new XMLSlideShow();
    private double globalMaxSize = 100;

    public double getGlobalMaxSize() {
        return globalMaxSize;
    }

    public void setGlobalMaxSize(double globalMaxSize) {
        this.globalMaxSize = globalMaxSize;
    }

    public void setGlobalMaxSize(Integer globalMaxSize) {
        this.globalMaxSize = globalMaxSize;
    }

    private XSLFSlideMaster defaultSlideMaster;
    private int slideFrameWidth;
    private int currentImageHeight;
    private int currentImageWidth;
    private int slideFrameHeight;
    private double titlePercentage = .20;
    private double bodyPercentage = 1 - titlePercentage;
    private Scanner scanner;
    private XMLSlideShow srcPPT;

    public void setTitlePercentage(double titlePercentage) throws Exception {
        if (titlePercentage > 0.5) {
            throw new Exception("title too big");
        }
        this.titlePercentage = titlePercentage;
        this.bodyPercentage = 1 - titlePercentage;
    }

    public File getSourceFolder() {
        return sourceFolder;
    }

    public void setSourceFolder(File sourceFolder) throws Exception {
        if (sourceFolder != null && sourceFolder.exists() && sourceFolder.isDirectory()) {
            this.sourceFolder = sourceFolder;
            this.targetFolder = new File(getSourceFolder(), "targ");
            if (!this.targetFolder.exists()) {
                targetFolder.mkdir();
            }
        } else {
            throw new Exception("source folder " + sourceFolder
                + "not specified, doesn't exist, or isn't a folder");
        }

    }

    public File getTargetFolder() {

        return targetFolder;
    }

    public XMLSlideShow getPpt() {
        return ppt;
    }

    public void setPpt(XMLSlideShow ppt) {
        this.ppt = ppt;
        this.defaultSlideMaster = ppt.getSlideMasters().get(0);
        defaultSlideMaster.getBackground().setFillColor(Color.BLACK);

        java.awt.Dimension pgsize = ppt.getPageSize();
        slideFrameWidth = pgsize.width - 5;
        slideFrameHeight = pgsize.height - 5;
    }

    public void appendImage(File imageFile, PictureData.PictureType pictureType)
        throws Exception, Exception {
        XSLFSlide slide = ppt.createSlide(defaultSlideMaster.getLayout(SlideLayout.PIC_TX));

        byte[] pictureData = IOUtils.toByteArray(new FileInputStream(imageFile));
        sniffImageDimensions(pictureData);
        Double adjustmentFactor = 1.0;
        Double adjustedWidth = 0.0;
        Double adjustedHeight = 0.0;
        if (currentImageHeight > currentImageWidth) {
            adjustmentFactor = new Double(slideFrameHeight) / new Double(currentImageHeight);
            adjustedWidth = currentImageWidth * adjustmentFactor;
            if (adjustedWidth > slideFrameWidth) {
                adjustmentFactor = adjustmentFactor * (slideFrameWidth / adjustedWidth);
            }
        } else {
            adjustmentFactor = new Double(slideFrameWidth) / new Double(currentImageWidth);
            adjustedHeight = currentImageHeight * adjustmentFactor;
            if (adjustedHeight > slideFrameHeight) {
                adjustmentFactor = adjustmentFactor * (slideFrameHeight / adjustedHeight);
            }
        }
        adjustedWidth = (currentImageWidth * adjustmentFactor);
        adjustedHeight = (currentImageHeight * adjustmentFactor);
        pictureData = resizeImage(pictureData, adjustedWidth.intValue(), adjustedHeight.intValue());

        XSLFPictureData pd = ppt.addPicture(pictureData, pictureType);
        XSLFPictureShape pic = slide.createPicture(pd);

        XYcoordinates coordinates = calculateCenterCoordinates(adjustedWidth.intValue(),
            adjustedHeight.intValue(), slideFrameWidth, slideFrameHeight);
        Rectangle2D anchor = new java.awt.Rectangle(coordinates.getxCoordinate(),
            coordinates.getyCoordinate(), adjustedWidth.intValue(), adjustedHeight.intValue());
        pic.setAnchor(anchor);

    }

    private XYcoordinates calculateCenterCoordinates(int imageWidth, int imageHeight,
        int frameWidth, int frameHeight) {
        int widthOffset = 0;
        int heightOffset = 0;
        if (imageWidth < frameWidth) {
            widthOffset = (frameWidth - imageWidth) / 2;
        }
        if (imageHeight < frameHeight) {
            heightOffset = (frameHeight - imageHeight) / 2;
        }
        XYcoordinates returnCoords = new XYcoordinates();
        returnCoords.setxCoordinate(10 + widthOffset);
        returnCoords.setyCoordinate(10 + heightOffset);
        return returnCoords;
    }

    public void processContentInFolder(File contentFolder) throws Exception {

        boolean firstFile = true;
        File[] contents = contentFolder.listFiles();
        for (File content : contents) {
            if (content.isDirectory()) {
                continue;
            }
            System.out.println("File " + content.getAbsolutePath() + " being processed");
            String ext = FilenameUtils.getExtension(content.getAbsolutePath());
            if (ext.equalsIgnoreCase("JPG")) {
                appendImage(content, PictureType.JPEG);
            } else if (ext.equalsIgnoreCase("PNG")) {
                appendImage(content, PictureType.PNG);
            } else if (ext.equalsIgnoreCase(TEXT_EXTENSION)) {
                appendText(content);
            } else if (ext.equalsIgnoreCase(POWERPOINT_XML_EXTENSION)
                || ext.equalsIgnoreCase(POWERPOINT_PRESENTATION_XML_EXTENSION)) {
                appendSlides(content, firstFile);
                if (firstFile) {
                    firstFile = false;
                }
            } else {
                System.err.println("Warning: Unsupported format: for file "
                    + content.getAbsolutePath() + " : " + ext);
                continue;
            }
            try {
                appendBlankSlide();
            } catch (Throwable e) {

            }

        }
    }

    public void appendBlankSlide() {
        ppt.createSlide();
    }

    public void appendSlides(File powerpointFile, boolean headerFile) throws Exception {

        srcPPT = new XMLSlideShow(new FileInputStream(powerpointFile));

        if (headerFile) {
            setPpt(srcPPT);
            return;
        }

        for (XSLFSlide srcSlide : srcPPT.getSlides()) {

            ppt.createSlide().importContent(srcSlide);

        }

    }

    public void appendText(File textFile) throws Exception {
        ArrayList<String> lines = new ArrayList<String>();
        loadTextData(textFile, lines);
        String descriptor = null;
        String titleString = null;
        int titleIndex = 0;
        for (String line : lines) {
            titleIndex++;
            if (line.trim().length() > 0) {
                if (line.startsWith(DESCRIPTOR_TAG)) {
                    descriptor = line;
                    continue;
                }
                titleString = line;
                break;
            }
        }
        if (titleString == null) {
            // ignore empty files
            return;
        }
        // clean up used data
        for (int i = 0; i < titleIndex; i++) {
            lines.remove(0);
        }
        // divvy the rest up into pages
        boolean newSlide = true;
        int slideDataIndex = -1;
        int lineIndex = 0;
        List<List<String>> slideData = new ArrayList<List<String>>();
        Integer slideSize = 0;
        if (descriptor != null) {
            //
            if (descriptor.endsWith(LINES_PER_PAGE_SUFFIX)) {
                String newSizeString = descriptor.substring(DESCRIPTOR_TAG.length(),
                    descriptor.length() - LINES_PER_PAGE_SUFFIX.length());
                slideSize = Integer.parseInt(newSizeString.trim());
            }
        }
        if (slideSize > 0) {
            for (String line : lines) {
                if (newSlide) {
                    // new slide - store lines in separate
                    slideData.add(new ArrayList<String>());
                    slideDataIndex++;
                    lineIndex = 0;
                    if (slideSize > 0) {
                        newSlide = false;
                    }
                }
                if (line.trim().length() > 0) {
                    slideData.get(slideDataIndex).add(line.trim());
                    lineIndex++;
                }
                if (lineIndex > slideSize - 1) {
                    // end of current slide
                    newSlide = true;

                }
            }
        } else {
            boolean previousBlank = true;
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    previousBlank = true;
                    continue;
                } else {
                    if (previousBlank) {
                        // new slide
                        slideData.add(new ArrayList<String>());
                        slideDataIndex++;
                        previousBlank = false;
                    }
                    slideData.get((slideDataIndex)).add(line.trim());
                }
            }
        }
        slideDataIndex = 1;
        for (List<String> slideDatum : slideData) {
            // more than one page per slide
            String effectiveTitle;
            if (slideData.size() > 1) {
                effectiveTitle = titleString.trim() + " (" + slideDataIndex + "/" + slideData.size()
                    + ")";
            } else {
                effectiveTitle = titleString.trim();
            }
            loadTextIntoSlide(effectiveTitle, slideDatum);
            slideDataIndex++;
        }

    }

    private void loadTextIntoSlide(String titleString, List<String> verses) {
        XSLFSlide slide;
        slide = ppt.createSlide(defaultSlideMaster.getLayout(SlideLayout.TITLE_AND_CONTENT));

        // selection of title place holder
        XSLFTextShape title = slide.getPlaceholder(0);
        title.setTextAutofit(TextAutofit.NORMAL);
        title.clearText();
        // System.out.println("Title before anything:");
        // dumpShapeHeight(title);
        XSLFTextParagraph titlePara = title.addNewTextParagraph();
        XSLFTextRun titleRun = titlePara.addNewTextRun();
        titleRun.setText(titleString.trim());
        titleRun.setFontSize(84.0);
        titleRun.setFontColor(Color.WHITE);
        titleRun.setFontFamily(TITLE_FONT);
        // System.out.println("Resizing title");
        // resizeTextToBlock(title, this.slideFrameHeight *
        // this.titlePercentage);
        resizeTextToBlock(title, title.getAnchor().getHeight());
        // selection of body place holder
        XSLFTextShape body = slide.getPlaceholder(1);

        // clear the existing text in the slide
        body.clearText();
        body.setTextAutofit(TextAutofit.NORMAL);
        // System.out.println("Body before anything");
        // dumpShapeHeight(body);

        // adding new paragraph
        XSLFTextParagraph paragraph = body.addNewTextParagraph();
        paragraph.setBullet(false);
        // XSLFTextBox shape = slide.createTextBox();
        // XSLFTextParagraph p = shape.addNewTextParagraph();
        boolean firstVerse = true;

        for (String verse : verses) {
            if (verse == null) {
                continue;
            }
            if (firstVerse) {
                firstVerse = false;
            } else {
                paragraph.addLineBreak();
            }
            XSLFTextRun run = paragraph.addNewTextRun();
            if (verse.startsWith(ITALICS_TAG)) {
                verse = verse.substring(ITALICS_TAG.length());
                run.setItalic(true);
            }
            run.setText(verse);
            run.setFontFamily(BODY_FONT);
            run.setFontColor(Color.WHITE);
            run.setFontSize(globalMaxSize);

        }
        // dumpShapeHeight(body);
        // resizeTextToBlock(body, this.slideFrameHeight * this.bodyPercentage);

        resizeTextToBlock(body, body.getAnchor().getHeight());
    }

    private void loadTextData(File textFile, ArrayList<String> list) throws FileNotFoundException {
        scanner = new Scanner(textFile);
        // Scanner s = scanner.useDelimiter("\\n");
        while (scanner.hasNextLine()) {
            list.add(scanner.nextLine());
        }
        scanner.close();
    }

    private void dumpShapeHeight(XSLFTextShape shape) {
        double totalHeight = shape.getBottomInset() + shape.getTopInset() + shape.getTextHeight();
        System.out.println("bottom " + shape.getBottomInset() + " top " + shape.getTopInset()
            + " middle " + shape.getTextHeight() + " total: " + totalHeight);
    }

    private void resizeTextToBlock(XSLFTextShape block, double targetHeight) {

        List<XSLFTextParagraph> paragraphs = block.getTextParagraphs();

        System.out.println("Before resize (target height is " + targetHeight + "):");
        dumpShapeHeight(block);

        double fontSize = paragraphs.get(0).getTextRuns().get(0).getFontSize();
        while (((block.getTextHeight()
        // + block.getBottomInset()
        // + block.getTopInset()
        ) > targetHeight)
            // if (blockHeight > targetHeight){
            // fontSize *= targetHeight/blockHeight;
            // setParaFontSize(paragraphs, fontSize);
            // blockHeight = block.getTextHeight();
            // TextAutofit fit = block.getTextAutofit();
            //
            // }
            // while ((blockHeight > targetHeight)
            && fontSize > 2) {
            // shrink by 1/8
            fontSize = fontSize * (1.0 - DECREMENT);
            // (re)set the font size on all text runs to make overall text block
            // fit
            setParaFontSize(paragraphs, fontSize);
            // blockHeight = block.getTextHeight();
            // System.out.println("After resize attempt " + ++iteration);
            // dumpShapeHeight(block);
        }

    }

    private void setParaFontSize(List<XSLFTextParagraph> paragraphs, double fontSize) {
        for (XSLFTextParagraph p : paragraphs) {
            List<XSLFTextRun> texts = p.getTextRuns();
            for (XSLFTextRun r : texts) {
                r.setFontSize(fontSize);
            }
        }
    }

    public void dumpSlideShow(File target) throws Exception {
        FileOutputStream out = null;
        if (target.isDirectory()) {
            out = new FileOutputStream(new File(target, "dump.pptx"));
        } else {
            out = new FileOutputStream(target);
        }
        ppt.write(out);
        out.close();
    }

    private void sniffImageDimensions(byte[] fileData) throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(fileData);
        BufferedImage originalImage = ImageIO.read(in);
        currentImageHeight = originalImage.getHeight();
        currentImageWidth = originalImage.getWidth();
    }

    public void appendSpacedText(File textFile, Boolean callAndResponse, double maxSize) throws Exception {
        ArrayList<String> lines = new ArrayList<String>();
        loadTextData(textFile, lines);

        String titleString = null;
        int titleIndex = 0;
        for (String line : lines) {
            titleIndex++;
            if (line.trim().length() > 0) {
                titleString = line;
                break;
            }
        }
        if (titleString == null) {
            // ignore empty files
            return;
        }
        // clean up used data
        for (int i = 0; i < titleIndex; i++) {
            lines.remove(0);
        }
        // divvy the rest up into pages

        int slideDataIndex = -1;

        List<List<String>> slideData = new ArrayList<List<String>>();

        boolean previousBlank = true;
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                previousBlank = true;
                continue;
            } else {
                if (previousBlank) {
                    // new slide
                    slideData.add(new ArrayList<String>());
                    slideDataIndex++;
                    previousBlank = false;
                }
                slideData.get((slideDataIndex)).add(line.trim());
            }
        }

        slideDataIndex = 1;
        boolean response = false;
        for (List<String> slideDatum : slideData) {
            // more than one page per slide
            String effectiveTitle;
            if (slideData.size() > 1) {
                effectiveTitle = titleString.trim() + " (" + slideDataIndex + "/" + slideData.size()
                    + ")";
            } else {
                effectiveTitle = titleString.trim();
            }
            loadTextIntoSlide(effectiveTitle, slideDatum, response);
            if (callAndResponse){
                response = ! response;
            }
            slideDataIndex++;
        }

    }

    private void loadTextIntoSlide(String titleString, List<String> verses, boolean response) {
        XSLFSlide slide;
        slide = ppt.createSlide(defaultSlideMaster.getLayout(SlideLayout.TITLE_AND_CONTENT));

        // selection of title place holder
        XSLFTextShape title = slide.getPlaceholder(0);
        title.setTextAutofit(TextAutofit.NORMAL);
        title.clearText();
        // System.out.println("Title before anything:");
        // dumpShapeHeight(title);
        XSLFTextParagraph titlePara = title.addNewTextParagraph();
        XSLFTextRun titleRun = titlePara.addNewTextRun();
        titleRun.setText(titleString.trim());
        titleRun.setFontSize(84.0);
        titleRun.setFontColor(Color.WHITE);
        titleRun.setFontFamily(TITLE_FONT);
        // System.out.println("Resizing title");
        // resizeTextToBlock(title, this.slideFrameHeight *
        // this.titlePercentage);
        resizeTextToBlock(title, title.getAnchor().getHeight());
        // selection of body place holder
        XSLFTextShape body = slide.getPlaceholder(1);

        // clear the existing text in the slide
        body.clearText();
        body.setTextAutofit(TextAutofit.NORMAL);
        // System.out.println("Body before anything");
        // dumpShapeHeight(body);

        // adding new paragraph
        XSLFTextParagraph paragraph = body.addNewTextParagraph();
        paragraph.setBullet(false);
        // XSLFTextBox shape = slide.createTextBox();
        // XSLFTextParagraph p = shape.addNewTextParagraph();
        boolean firstVerse = true;

        for (String verse : verses) {
            if (verse == null) {
                continue;
            }
            if (firstVerse) {
                firstVerse = false;
            } else {
                paragraph.addLineBreak();
            }
            XSLFTextRun run = paragraph.addNewTextRun();
            run.setItalic(response);
            run.setText(verse);
            run.setFontFamily(BODY_FONT);
            run.setFontColor(Color.WHITE);
            run.setFontSize(globalMaxSize);

        }
        // dumpShapeHeight(body);
        // resizeTextToBlock(body, this.slideFrameHeight * this.bodyPercentage);

        resizeTextToBlock(body, body.getAnchor().getHeight());
    }

    private static byte[] resizeImage(byte[] fileData, Integer img_width, Integer img_height)
        throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(fileData);

        BufferedImage originalImage = ImageIO.read(in);
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB
            : originalImage.getType();

        if (img_height == 0) {
            img_height = originalImage.getHeight();
        }

        BufferedImage resizedImage = new BufferedImage(img_width, img_height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, img_width, img_height, null);
        g.dispose();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", buffer);
        return buffer.toByteArray();
    }

    public static void main(String[] args) {
        try {
            if (args.length != NUM_PARMS) {
                throw new Exception(USAGE);
            }
            SlideGenerator myGen = new SlideGenerator();
            myGen.setSourceFolder(new File(args[0]));
            myGen.setPpt(new XMLSlideShow());
            myGen.processContentInFolder(myGen.getSourceFolder());
            myGen.dumpSlideShow(myGen.getTargetFolder());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

    }

}
