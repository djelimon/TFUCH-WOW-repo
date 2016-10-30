package com.uuh.wow.ppgen.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import uuh.wow.msword.OfficeTextExtractor;

public class FileUtils {

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (sourceFile.exists()) {
            System.out.println(sourceFile.getAbsolutePath() + " exists");
            System.out.println("Is directory = " + sourceFile.isDirectory());
            System.out.println("Can read = " + sourceFile.canRead());
        }

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            FileInputStream inStream = new FileInputStream(sourceFile);
            source = inStream.getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static void docxToText(File docxFile, File textFile) throws Exception {

        BufferedWriter bufferedWriter = null;
        try {
            String data = OfficeTextExtractor.extractTextFromWordDocx(docxFile.getAbsolutePath());
            String[] lines = data.split("\\n");
            FileWriter fileWriter = new FileWriter(textFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            for (String line : lines) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.flush();
                bufferedWriter.close();
            }
        }

        return;
    }

}
