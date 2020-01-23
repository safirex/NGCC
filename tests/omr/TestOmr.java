package omr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestOmr {

	BufferedImage imgTestBlack = new BufferedImage(500, 500, 1);
	BufferedImage imgTestBlack50 = new BufferedImage(500, 500, 1);
	int maxPixels = 0;

	@BeforeEach
	void initBlackImage() {

		Color white = new Color(255, 255, 255);
		for (int i = 0; i < this.imgTestBlack.getHeight(); i++) {

			for (int j = 0; j < this.imgTestBlack.getWidth(); j++) {

				this.imgTestBlack.setRGB(i, j, 0);
			}
		}

		for (int i = 0; i < this.imgTestBlack.getHeight(); i++) {
			for (int j = 0; j < this.imgTestBlack.getWidth(); j++) {
				if (this.maxPixels < (Math.pow(this.imgTestBlack.getHeight(), 2) / 2)) {
					this.imgTestBlack50.setRGB(i, j, 0);
				} else {
					this.imgTestBlack50.setRGB(i, j, white.getRGB());
				}
				this.maxPixels++;
			}
		}
	}

	// On regarde si la taille de la bufferedImage a bien chang�e
	@Test
	void testResize_sizeHasChanged() {
		BufferedImage imgTest = new BufferedImage(1000, 1000, 1);
		BufferedImage imgResult;

		imgResult = Omr.resize(imgTest, 500, 500);

		assertEquals(imgResult.getWidth(), 500);
		assertEquals(imgResult.getHeight(), 500);

	}

	@Test
	void testIsSquareBlack_thePageIsEntirelyBlack_DetectionIsOverTheMaximum()
			throws NullBufferedImageException, Exception {
		assertFalse(Omr.isSquareBlack(this.imgTestBlack, new Point(0, 0), this.imgTestBlack.getHeight(), 2.0));
	}

	@Test
	void testIsSquareBlack_thePageIsEntirelyBlack_DetectionIsZero() throws NullBufferedImageException, Exception {
		assertTrue(Omr.isSquareBlack(this.imgTestBlack, new Point(0, 0), this.imgTestBlack.getHeight(), 0));
	}

	@Test
	void testIsSquareBlack_thePageIs50PercentBlack_DetectionIsOver50() throws NullBufferedImageException, Exception {
		assertFalse(Omr.isSquareBlack(this.imgTestBlack50, new Point(0, 0), this.imgTestBlack50.getHeight(), 0.6));

	}

	@Test
	void testIsSquareBlack_thePageIs50PercentBlack_DetectionIsUnder50() throws NullBufferedImageException, Exception {
		assertTrue(Omr.isSquareBlack(this.imgTestBlack50, new Point(0, 0), this.imgTestBlack50.getHeight(), 0.4));
	}

}
