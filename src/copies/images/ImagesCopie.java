package copies.images;

import java.awt.image.BufferedImage;
import java.util.Map;

import ocr.Rogneur;

public class ImagesCopie {
	private Map<String, Img> hMapImgs;

	public ImagesCopie(BufferedImage imgOriginale) {
		this.hMapImgs = Rogneur.createHMapImgs(imgOriginale);
	}

	public void applyOcrForEach() {

		for (Img s : this.hMapImgs.values()) {
			s.applyOcrImg();

		}

	}

	public Map<String, Img> gethMapImgs() {
		return this.hMapImgs;
	}

	public void sethMapImgs(Map<String, Img> hMapImgs) {
		this.hMapImgs = hMapImgs;
	}
}