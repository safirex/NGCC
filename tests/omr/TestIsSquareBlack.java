package omr;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import config.Config;
import copies.Copie;
import generation.pdf.CopiePdfImpl;
import generation.pdf.Fusion;
import ocr.PdfToImage;

public class TestIsSquareBlack {

	// TODO: tester les performances

	private final String sourcePath = "./config/source.txt";
	private Config config;
	private OmrRewrite analyzer;
	private CopiePdfImpl copiePdf;

	@Before
	public void setUp() throws IOException {
		// this.config = Config.getInstance(this.sourcePath);
		this.config = Config.getInstance(this.sourcePath); // ne prend pas en compte le Singleton
		this.config.readConfig();
		this.analyzer = new OmrRewrite();
		this.copiePdf = new Fusion(new Copie("A4"));
	}

	@After
	public void clear() {
		this.analyzer = null;
	}

	private BufferedImage createBlackPage() {
		PDDocument blackPdf = new PDDocument();
		PDRectangle format = PDRectangle.A4;
		blackPdf.addPage(new PDPage(format));
		try (PDPageContentStream pdPageContentStream = new PDPageContentStream(blackPdf, blackPdf.getPage(0),
				PDPageContentStream.AppendMode.APPEND, true);) {
			pdPageContentStream.setNonStrokingColor(Color.BLACK);
			pdPageContentStream.addRect(0, 0, 1000, 1000);
			pdPageContentStream.closeAndFillAndStroke();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return PdfToImage.convertPagesToBWJPG(blackPdf, 1).get(0);
	}

	private BufferedImage createWhitePage() {
		PDDocument whitePdf = new PDDocument();
		PDRectangle format = PDRectangle.A4;
		whitePdf.addPage(new PDPage(format));
		return PdfToImage.convertPagesToBWJPG(whitePdf, 1).get(0);
	}

	@Test
	public void testDetectionNoir() {
		// test black square
		BufferedImage blackImage = this.createBlackPage();
		for (int i = 0; i < 101; i++) {
			assertTrue(this.analyzer.isSquareBlack(this.copiePdf, blackImage, new Point(0, 0), 10, i));
		}
	}

	@Test
	public void testDetectionBlanc() {
		// test white square
		BufferedImage whiteImage = this.createWhitePage();
		assertTrue(this.analyzer.isSquareBlack(this.copiePdf, whiteImage, new Point(0, 0), 10, 0));
		for (int i = 1; i < 101; i++) {
			assertFalse(this.analyzer.isSquareBlack(this.copiePdf, whiteImage, new Point(0, 0), 10, i));
		}
	}
}
