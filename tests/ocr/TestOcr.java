package ocr;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.tess4j.TesseractException;

class TestOcr {

	BufferedImage img;

	@BeforeEach
	void init() {
		this.img = new BufferedImage(10, 10, 10);
	}

	@Test
	void testApplyOcrNumber() {
		// Verifie qu'il n'y a pas d'exceptions
		String message = assertDoesNotThrow(() -> {
			OCR.applyOcrNumber(this.img);
			return "ok";
		});
		assertEquals("ok", message);

		// Verifie qu'il y a une exception avec une image null
		assertThrows(TesseractException.class, () -> {
			OCR.applyOcrNumber(null);
		});

	}
}
