package uuh.wow.msword;

import java.io.FileInputStream;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class OfficeTextExtractor {

    public static String extractTextFromWordDocx(String path) throws Exception {
        XWPFDocument docx = new XWPFDocument(new FileInputStream(path));
        // using XWPFWordExtractor Class
        XWPFWordExtractor we = new XWPFWordExtractor(docx);
        String returnString = we.getText();
        we.close();
        return returnString;

    }
}
