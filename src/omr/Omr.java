package omr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

import config.Reponse;
import copies.Copie;
import generation.pdf.CopiePdfImpl;

public abstract class Omr {

	private static final float BLACKCOLOR = 10; // Doit etre compris entre 0 et 255, 0 etant noir

	private final static int DETECTIONB = 4 / 5; // Doit etre compris entre 0 et 1

	// Le changement de proportion des coordonnees lors du passage d'un document PDF
	// à une BufferedImage
	private final static double IMGPDFRAPPORT = 300 / 72;

	// Taille d'un carré
	private final static int SQUARESIZE = (int) (10 * IMGPDFRAPPORT);

	public static void OMRCopie(BufferedImage img, Copie copie) throws NullBufferedImageException, Exception {

		for (int i = 0; i < copie.getQuestions().size(); i++) {
			for (Reponse r : copie.getQuestions().get(i).getReponses()) {
				r.setJuste(isSquareBlack(img, r.getP(), SQUARESIZE, DETECTIONB));
			}
		}

	}

	public void OMRBinary(CopiePdfImpl pdf, BufferedImage img) throws NullBufferedImageException {
		Copie copie = pdf.getCopie();
		List<Point> L = pdf.getRectIDPos();
		int id = 0;
		int width = img.getWidth(), height = img.getHeight();

		// Liste des points inversees
		Collections.sort(L, Collections.reverseOrder());

		for (int i = 0; i < L.size(); i++) {
			if (isBlack(img.getSubimage(L.get(i).x, L.get(i).y, SQUARESIZE, SQUARESIZE), DETECTIONB)) {
				id++;
			}
		}

		copie.setId(id);
	}

	/*
	 * Méthode permettant de modifier la taille d'une BufferedImage.
	 *
	 * @param img image L'image que l'on souhaite resize
	 *
	 * @param newW newWidth Nouvelle largeur
	 *
	 * @param newH newHeight Nouvelle hauteur
	 *
	 * @return L'image modifiee
	 */
	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	private static boolean isBlack(BufferedImage img, double d) throws NullBufferedImageException {
		Color couleur;
		double nbPixels = 0;
		double nbBlackPixels = 0;

		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {

				nbPixels++;
				couleur = new Color(img.getRGB(x, y));

				if (equalsColorBlack(couleur)) {
					nbBlackPixels++;
				}
			}
		}

		return (nbBlackPixels / nbPixels) >= d;
	}

	private static boolean equalsColorBlack(Color c) {
		return (c.getBlue() < BLACKCOLOR) && (c.getGreen() < BLACKCOLOR) && (c.getRed() < BLACKCOLOR);
	}

	private static BufferedImage getSquare(BufferedImage img, Point p, int sizeSquare) throws Exception {
		if (img == null) {
			throw new NullBufferedImageException("Image null");
		}

		return img.getSubimage((int) p.getX(), (int) p.getY(), sizeSquare, sizeSquare);
	}

	/*
	 * Regarde si un carre de taille sizeSquare, aux coordonées du point p est noir.
	 *
	 * @param img image L'image sur laquelle se trouve le carre
	 *
	 * @param p point Les coordonées du carre, x en abscisses, y en ordonnees
	 *
	 * @param sizeSquare La taille du carre
	 *
	 * @param d detection Le niveau de détection, 0 etant le moins strict, 1 etant
	 * le plus strict
	 *
	 */
	public static boolean isSquareBlack(BufferedImage img, Point p, int sizeSquare, double d)
			throws NullBufferedImageException, Exception {
		return isBlack(getSquare(img, p, sizeSquare), d);
	}

}
