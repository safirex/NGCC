package copies.images;

import java.awt.image.BufferedImage;

import net.sourceforge.tess4j.TesseractException;
import ocr.OCR;



public class ImgNumEtu extends Img{

	public ImgNumEtu(BufferedImage img) {
		super(img);
	}

	@Override
	public void applyOcrImg() {
		try {
			setDescription(OCR.applyOcrNumber(getImg()));
		} catch (TesseractException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.sanitizeDesc();
		
	}

}