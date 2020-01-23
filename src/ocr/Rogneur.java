package ocr;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import copies.images.Img;
import copies.images.ImgNote;
import copies.images.ImgNumEtu;
import generation.IncorrectFormatException;
import generation.pdf.CopiePdfImpl;
import omr.OmrRewrite;

public abstract class Rogneur {

	// Retourne une hashmap contenant une image et la description de son contenu
	public static Map<String, Img> createHMapImgs(BufferedImage imgOriginale) {

		Map<String, Img> temp = new HashMap<>();
		temp.put("NumEtu", rogneurNumEtu(imgOriginale));
		temp.put("Note", rogneurNote(imgOriginale));
		// temp.put("FormatNote", rogneurFormatNote(imgOriginale));

		return temp;
	}

	// Image rognee pour le numero etudiant
	public static Img rogneurNumEtu(BufferedImage imgOriginale) {

		Map<String, Point[]> coords = null;
		try {
			coords = CopiePdfImpl.getEncadreCoords("A4");
		} catch (IncorrectFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Point numEtuPointA = OmrRewrite.ajustPointCoords(coords.get("numEtu")[0], PDRectangle.A4, imgOriginale);
		Point numEtuPointB = OmrRewrite.ajustPointCoords(coords.get("numEtu")[1], PDRectangle.A4, imgOriginale);

		// Cree une sous-image qui sera detectee par l'OCR (rognage)
		BufferedImage temp = imgOriginale.getSubimage((int) numEtuPointA.getX() + 2, (int) numEtuPointA.getY() - 125,
				(int) (numEtuPointB.getX() - (numEtuPointA.getX() * 1.05)),
				(int) (numEtuPointA.getY() - (numEtuPointB.getY() * 1.1)));

//		// Afficher zone de detection par l'OCR
//		JFrame frame = new JFrame();
//		frame.getContentPane().add(new JLabel(new ImageIcon(temp)));
//		frame.setVisible(true);
//
//		for(;;);

		return new ImgNumEtu(temp);
	}

	// Image rognee pour la note
	public static Img rogneurNote(BufferedImage imgOriginale) {

		Map<String, Point[]> coords = null;
		try {
			coords = CopiePdfImpl.getEncadreCoords("A4");
		} catch (IncorrectFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Point notePointA = OmrRewrite.ajustPointCoords(coords.get("noteEtu")[0], PDRectangle.A4, imgOriginale);
		Point notePointB = OmrRewrite.ajustPointCoords(coords.get("noteEtu")[1], PDRectangle.A4, imgOriginale);

		// Cree une sous-image qui sera detectee par l'OCR (rognage)
		BufferedImage temp = imgOriginale.getSubimage((int) notePointA.getX() + 2, (int) notePointA.getY() - 160,
				(int) (notePointB.getX() - (notePointA.getX() * 1.52)),
				(int) (notePointA.getY() - (notePointB.getY() * 1.01)));

		// Masque le texte "Note" sur la copie pour améliorer la reconnaissance
		for (int i = (int) notePointA.getX(); i < (notePointB.getX() - 530); i++) {
			for (int j = (int) notePointA.getY() - 100; j > (notePointB.getY() + 3); j--) {
				imgOriginale.setRGB(i + 3, j, Color.WHITE.getRGB());
			}
		}

		// Affiche zone de detection par l'OCR
//		JFrame frame = new JFrame();
//		frame.getContentPane().add(new JLabel(new ImageIcon(temp)));
//		frame.setVisible(true);

		return new ImgNote(temp);
	}

}
