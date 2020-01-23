package ocr;

import java.awt.image.BufferedImage;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public abstract class OCR {

	public static String applyOcrNumber(BufferedImage img) throws TesseractException{
		Tesseract tesseract = new Tesseract();
		String str = "";
			
			//tessedit_char_whiteliste -> Autorise la détection d'un nombre limité de caracatères 
			 
			tesseract.setDatapath("config");
			tesseract.setLanguage("eng");
			tesseract.setTessVariable("tessedit_char_whitelist", ",0123456789");
			tesseract.setTessVariable("user_defined_dpi", "270"); // Definition DPI resoud probleme de log
			str = tesseract.doOCR(img);
	
		return str;
	}
}
