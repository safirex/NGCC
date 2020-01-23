package lecturePdf;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.junit.jupiter.api.Test;

import ocr.PdfToImage;

class TestRecadrage {

	@Test
	void testEstDroit1() {
		try {
			PdfToImage pdfAnalyzer = new PdfToImage();
			File pdfFile;
			PDDocument document = null;
			//LISTE DES IMAGES 
			List<BufferedImage> images = new ArrayList<>(); // stockera les images (resultat)
			// CONVERT PAGES TO IMAGES
			String path=".\\pdf\\TestBleu.pdf";
			pdfFile = new File(path);
			
			document = PDDocument.load(pdfFile);
			
			images.addAll(pdfAnalyzer.convertPagesToBWJPG(document,1));
			Recadrage rec =new Recadrage(images.get(0));
			assertTrue(rec.estDroite1());
		} catch (InvalidPasswordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void testSearchAngle() throws InvalidPasswordException, IOException {
	
			PdfToImage pdfAnalyzer = new PdfToImage();
			File pdfFile;
			PDDocument document = null;
			//LISTE DES IMAGES 
			List<BufferedImage> images = new ArrayList<>(); // stockera les images (resultat)
			// CONVERT PAGES TO IMAGES
			String path=".\\pdf\\TestBleu.pdf";
			pdfFile = new File(path);
			
			document = PDDocument.load(pdfFile);
			
			images.addAll(pdfAnalyzer.convertPagesToBWJPG(document,1));
			Recadrage rec =new Recadrage(images.get(0));
			rec.img=Recadrage.rotate(rec.img, 1);
			rec.save("decalée");
			double ept=rec.searchAngle();
			System.out.println("angle");
			System.out.println(ept);
		}

			
	@Test
	void testaLenvers() {
		try {
			PdfToImage pdfAnalyzer = new PdfToImage();
			File pdfFile;
			PDDocument document = null;
			//LISTE DES IMAGES 
			List<BufferedImage> images = new ArrayList<>(); // stockera les images (resultat)
			// CONVERT PAGES TO IMAGES
			String path=".\\pdf\\TestRetourne.pdf";
			pdfFile = new File(path);
			
			document = PDDocument.load(pdfFile);
			
			images.addAll(pdfAnalyzer.convertPagesToBWJPG(document,1));
			Recadrage rec =new Recadrage(images.get(0));
			
			assertFalse(rec.estDroite1());		
			rec.img = Recadrage.rotate(rec.img, 180);
			
			assertTrue(rec.estDroite1());
			assertFalse(rec.aLEnvers());
	
		} catch (InvalidPasswordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
