package omr;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import config.Question;
import config.Reponse;
import copies.Copie;
import generation.IncorrectFormatException;
import generation.pdf.CopiePdfImpl;
import ocr.PdfToImage;

public class OmrRewrite {

	private final double xRatio = (300 / 72);
	private final double yRatio = (300 / 72);
	private final double squareRatio = (300 / 72);// 4.5;

	public OmrRewrite() {

	}

	/**
	 *
	 * @param point coordonnees sur le <code>pdf</code>
	 * @param pdf   document pdf d'ou proviennent les coordonnees
	 * @param img   image du document pdf
	 * @return les coordonnees en abscisse converties de coordonnees de pdf vers
	 *         coordonnees d'image
	 */
	public static int ajustXCoords(Point point, PDRectangle format, BufferedImage img) {
		return (int) ((img.getWidth() * point.getX()) / format.getWidth());
	}

	/**
	 *
	 * @param point coordonnees sur le <code>pdf</code>
	 * @param pdf   document pdf d'ou proviennent les coordonnees
	 * @param img   image du document pdf
	 * @return les coordonnees en ordonnees converties de coordonnees de pdf vers
	 *         coordonnees d'image
	 */
	public static int ajustYCoords(Point point, PDRectangle format, BufferedImage img) {
		return (int) ((img.getHeight()) - ((img.getHeight() * point.getY()) / format.getHeight()));
	}

	/**
	 *
	 * @param point coordonnees sur le <code>pdf</code>
	 * @param pdf   document pdf d'ou proviennent les coordonnees
	 * @param img   image du document pdf
	 * @return les coordonnees du <code>point</code> converties de coordonnees de
	 *         pdf vers coordonnees d'image
	 */
	public static Point ajustPointCoords(Point point, PDRectangle format, BufferedImage img) {
		point.setLocation(ajustXCoords(point, format, img), ajustYCoords(point, format, img));
		return point;
	}

	/**
	 * Sauvegarde les pages du <code>document</code> en images et y ajoute les zones
	 * de verification pour lesquelles seront appelees la methode
	 * <code>isSquareBlack</code>
	 *
	 * @param pdf        pdf sur lequel on veut recuperer les reponses cochees
	 * @param sizeSquare taille du carre
	 */
	public void getOmrVisualization(CopiePdfImpl pdf, int sizeSquare) {
		sizeSquare *= this.squareRatio;
		// on recupere le document
		PDDocument document = pdf.getPdDocument();
		// on recupere les images des pages du pdf
		List<BufferedImage> pdfImg = PdfToImage.convertPagesToBWJPG(document, document.getNumberOfPages());
		// on recupere la copie
		Copie copie = pdf.getCopie();
		// on recupere les questions
		List<Question> questions = copie.getQuestions();
		int pageIndex;
		for (Question q : questions) {
			// pour chaque question
			List<Reponse> reponses = q.getReponses(); // on recupere ses reponses
			for (Reponse r : reponses) {
				// pour chaque reponse de la question
				pageIndex = r.getNumPage();
				Point point = r.getP(); // on recupere ses coordonnees
				// on met a l'echelle ses coordonnees
				int x = OmrRewrite.ajustXCoords(point, pdf.getFormat(), pdfImg.get(pageIndex));
				int y = OmrRewrite.ajustYCoords(point, pdf.getFormat(), pdfImg.get(pageIndex)) - sizeSquare;
				for (int i = 0; i <= sizeSquare; i++) {
					for (int j = 0; j <= sizeSquare; j++) {
						// pour chaque pixel, on le colorie en rouge
						try {
							pdfImg.get(pageIndex).setRGB(x + i, y + j, Color.RED.getRGB());
						} catch (ArrayIndexOutOfBoundsException aiobe) {
							continue;
						}
					}
				}
			}
		}
		// visualize page id
		for (Point point : pdf.getRectIDPos()) {
			// pour chaque carre de l'entete
			// on met a l'echelle ses coordonnees
			int x = OmrRewrite.ajustXCoords(point, pdf.getFormat(), pdfImg.get(0));
			int y = OmrRewrite.ajustYCoords(point, pdf.getFormat(), pdfImg.get(0)) - sizeSquare;
			for (int i = 0; i <= sizeSquare; i++) {
				for (int j = 0; j <= sizeSquare; j++) {
					// pour chaque pixel, on le colorie en rouge
					try {
						pdfImg.get(0).setRGB(x + i, y + j, Color.RED.getRGB());
					} catch (ArrayIndexOutOfBoundsException aiobe) {
						continue;
					}
				}
			}
		}
		PdfToImage.saveOnDisk(pdfImg, "./pdf/TestOMR/omr_visualization_");
	}

	/**
	 * (En cours de developpement - Version test)
	 *
	 * @param matcher
	 * @param sizeSquare
	 * @throws IOException
	 */
	public void getOmrVisualization(CoordsMatcher matcher, int sizeSquare) throws IOException {
		sizeSquare *= this.squareRatio;
		// on recupere l'instance de CopiePdfImpl
		CopiePdfImpl pdf = matcher.getPdf();
		// on recupere le document
		PDDocument document = pdf.getPdDocument();
		// on recupere les images des pages du pdf
		List<BufferedImage> pdfImg = PdfToImage.convertPagesToBWJPG(document, document.getNumberOfPages());
		// on recupere la copie
		Copie copie = pdf.getCopie();
		// on recupere les questions
		List<Question> questions = copie.getQuestions();
		int pageIndex;
		for (Question q : questions) {
			// pour chaque question
			List<Reponse> reponses = q.getReponses(); // on recupere ses reponses
			for (Reponse r : reponses) {
				// pour chaque reponse de la question
				pageIndex = r.getNumPage();
				double[] coords = matcher.getAnswerCoords(q, r);
				Point point = new Point((int) coords[0] - sizeSquare, (int) coords[1]);
				int x = OmrRewrite.ajustXCoords(point, pdf.getFormat(), pdfImg.get(r.getNumPage()));
				int y = OmrRewrite.ajustYCoords(point, pdf.getFormat(), pdfImg.get(r.getNumPage()))
						- (sizeSquare * (int) this.xRatio);
				for (int i = 0; i <= sizeSquare; i++) {
					for (int j = 0; j <= sizeSquare; j++) {
						// pour chaque pixel, on le colorie en rouge
						try {
							pdfImg.get(r.getNumPage()).setRGB(x + i, y - j, Color.RED.getRGB());
						} catch (ArrayIndexOutOfBoundsException aiobe) {
							continue;
						}
					}
				}
				try {
					pdfImg.get(r.getNumPage()).setRGB(x, y, Color.BLUE.getRGB());
					pdfImg.get(r.getNumPage()).setRGB(x + 1, y, Color.BLUE.getRGB());
					pdfImg.get(r.getNumPage()).setRGB(x + 2, y, Color.BLUE.getRGB());
					pdfImg.get(r.getNumPage()).setRGB(x + 3, y, Color.BLUE.getRGB());
					pdfImg.get(r.getNumPage()).setRGB(x + 4, y, Color.BLUE.getRGB());
				} catch (ArrayIndexOutOfBoundsException aiobe) {
					continue;
				}
			}
		}
		PdfToImage.saveOnDisk(pdfImg, "./pdf/TestOMR/omr_visualization_");

	}

	/**
	 * Recupere les questions cochees d'un pdf
	 *
	 * @param pdf        pdf sur lequel on veut recuperer les reponses cochees
	 * @param sizeSquare taille du carre
	 * @param seuil      seuil au dessus duquel le carre est considere comme noir
	 * @return une <code>HashMap<code> avec les questions associees a leur reponses
	 *         cochees
	 */
	public Map<Question, List<Reponse>> getAnsweredReponses(CopiePdfImpl pdf, int sizeSquare, int seuil) {
		Map<Question, List<Reponse>> result = new HashMap<>(); // association des questions avec leurs reponses cochees
		// on recupere le document
		PDDocument document = pdf.getPdDocument();
		// on recupere les images des pages du pdf
		List<BufferedImage> pdfImg = PdfToImage.convertPagesToBWJPG(document, document.getNumberOfPages());
		// on recupere la copie
		Copie copie = pdf.getCopie();
		List<Question> questions = copie.getQuestions();
		int pageIndex = 0;
		for (Question q : questions) {
			// pour chaque question de la copie
			// System.out.println("-----------------------QUESTION : " + q.getTitre());
			List<Reponse> goodAnswers = new ArrayList<>();
			List<Reponse> reponses = q.getReponses();
			for (Reponse r : reponses) {
				// pour chaque reponse de la question
				// System.out.println("-----------REPONSE : " + r.getIntitule());
				pageIndex = r.getNumPage();
				Point point = r.getP(); // on recupere ses coordonnees
				if (this.isQuestionSquareBlack(pdf, pdfImg.get(pageIndex), point, sizeSquare, seuil)) {
					// si la case est cochee
					goodAnswers.add(r); // on l'ajoute a la liste correspondant a la question
				}
			}
			result.put(q, goodAnswers); // on ajoute la question et ses reponses cochees
		}
		return result;
	}

	public Map<Question, List<Reponse>> getAnsweredReponses(Copie copie, BufferedImage image, int numPage,
			int sizeSquare, int seuil) throws IncorrectFormatException {
		Map<Question, List<Reponse>> result = new HashMap<>(); // association des questions avec leurs reponses cochees
		// on recupere les images des pages du pdf
		image = PdfToImage.blackWhiteConvert(image);
		List<Question> questions = copie.getQuestions();
		int pageIndex = 0;
		PDRectangle format = CopiePdfImpl.getFormatFromString(copie.getFormat());
		for (Question q : questions) {
			// pour chaque question de la copie
			List<Reponse> goodAnswers = new ArrayList<>();
			List<Reponse> reponses = q.getReponses();
			for (Reponse r : reponses) {
				// pour chaque reponse de la question
				pageIndex = r.getNumPage();
				Point point = r.getP(); // on recupere ses coordonnees
				if ((pageIndex == numPage) && this.isQuestionSquareBlack(format, image, point, sizeSquare, seuil)) {
					// si la case est cochee
					goodAnswers.add(r); // on l'ajoute a la liste correspondant a la question
				}
			}
			result.put(q, goodAnswers); // on ajoute la question et ses reponses cochees
		}
		return result;
	}

	/**
	 * (En cours de developpement - Version test)
	 *
	 * @param matcher
	 * @param sizeSquare
	 * @param seuil
	 * @return
	 * @throws IOException
	 */
	public Map<Question, List<Reponse>> getAnsweredReponses(CoordsMatcher matcher, int sizeSquare, int seuil)
			throws IOException {
		sizeSquare *= this.squareRatio;
		Map<Question, List<Reponse>> result = new HashMap<>(); // association des questions avec leurs reponses cochees
		// on recupere l'instance de CopiePdfImpl
		CopiePdfImpl pdf = matcher.getPdf();
		// on recupere le document
		PDDocument document = pdf.getPdDocument();
		// on recupere les images des pages du pdf
		List<BufferedImage> pdfImg = PdfToImage.convertPagesToBWJPG(document, document.getNumberOfPages());
		// on recupere la copie
		Copie copie = pdf.getCopie();
		List<Question> questions = copie.getQuestions();
		int pageIndex = 0;
		for (Question q : questions) {
			// pour chaque question de la copie
			// System.out.println("-----------------------QUESTION : " + q.getTitre());
			List<Reponse> goodAnswers = new ArrayList<>();
			List<Reponse> reponses = q.getReponses();
			for (Reponse r : reponses) {
				// pour chaque reponse de la question
				// System.out.println("-----------REPONSE : " + r.getIntitule());
				pageIndex = r.getNumPage();
				double[] coords = matcher.getAnswerCoords(q, r);
				Point point = new Point((int) coords[0] - 18, (int) coords[1]); // TODO: voir 18 = espace entre carre et
																				// reponse
				if (this.isQuestionSquareBlack(pdf, pdfImg.get(pageIndex), point, sizeSquare, seuil)) {
					// si la case est cochee
					goodAnswers.add(r); // on l'ajoute a la liste correspondant a la question
				}
			}
			result.put(q, goodAnswers); // on ajoute la question et ses reponses cochees
		}
		return result;
	}

	/**
	 * Lit les carres de l'entete de la page et renvoie la valeur de l'id sous forme
	 * d'entier
	 *
	 * @param image  image contenant les carre de l'id de page
	 * @param format format de la page
	 * @param seuil  seuil au dessus duquel le carre est considere comme noir
	 * @return un tableau contenant l'id de copie et l'id de page
	 */
	public long[] readId(BufferedImage image, PDRectangle format, int seuil) {
		long[] result = new long[2];
		StringBuilder id = new StringBuilder(); // meilleures performances
		int ID_CARRE_WIDTH = CopiePdfImpl.ID_CARRE_WIDTH;
		int ID_CARRE_TAILLE = CopiePdfImpl.ID_CARRE_TAILLE;
		// recuperation de la position des carres de l'entete
		int height = (int) format.getHeight();
		int ID_CARRE_HEIGHT_FIRSTROW = height - 20;
		int ID_CARRE_HEIGHT_SECONDROW = height - 32;
		List<Point> idCarresPositions = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			// draw first range of rectangles
			idCarresPositions
					.add(new Point(ID_CARRE_WIDTH + (ID_CARRE_TAILLE * i) + (i * 2), ID_CARRE_HEIGHT_FIRSTROW));

		}
		for (int i = 0; i < 12; i++) {
			// draw first range of rectangles
			idCarresPositions
					.add(new Point(ID_CARRE_WIDTH + (ID_CARRE_TAILLE * i) + (i * 2), ID_CARRE_HEIGHT_SECONDROW));

		}
		Collections.reverse(idCarresPositions);
		for (Point point : idCarresPositions) {
			// pour chaque carre de l'entete
			// on met a l'echelle ses coordonnees
			int x = OmrRewrite.ajustXCoords(point, format, image);
			int y = OmrRewrite.ajustYCoords(point, format, image) - ID_CARRE_TAILLE;
			try {
				if (this.isIdSquareBlack(format, image, new Point(x, y), ID_CARRE_TAILLE, seuil)) {
					id.append("1");
				} else {
					id.append("0");
				}
			} catch (ArrayIndexOutOfBoundsException aiobe) {
				continue;
			}
		}
		result[0] = Long.parseLong(id.toString().substring(0, 7)); // on recupere les 6 premiers entiers (id copie)
		result[1] = Long.parseLong(id.toString().substring(6)); // on recupere les entiers apres la 6eme position (id
																// page)
		return result;
	}

	/**
	 * Verifie si le carre est de couleur noire ou pas
	 *
	 * @param image      image contenant le carre
	 * @param point      coordonnee du coin superieur gauche du carre
	 * @param sizeSquare taille du carre
	 * @param seuil      seuil au dessus duquel le carre est considere comme noir
	 * @return <code>true</code> si le carre est noir au dessus du
	 *         <code>seuil<code> sinon <code>false</code>
	 */
	public boolean isQuestionSquareBlack(CopiePdfImpl pdf, BufferedImage image, Point point, int sizeSquare,
			int seuil) {
		// TODO: voir parametres
		// on calcule les coordonnees de la reponse sur la buffered image
		// TODO: revoir formules de mise a l'echelle et notamment 3450
		/*
		 * int x = (int) (this.xRatio * point.getX()); int y = (int) (3450 -
		 * (this.yRatio * point.getY()));
		 */
		int x = OmrRewrite.ajustXCoords(point, pdf.getFormat(), image);
		int y = OmrRewrite.ajustYCoords(point, pdf.getFormat(), image) - sizeSquare;
		// on initialise le compteur de pixels noirs
		int nbBlackPixels = 0;
		int nbPixels = 0;
		// TODO: voir <= ou <
		// pour chaque pixel
		for (int i = 0; i <= sizeSquare; i++) {
			for (int j = 0; j <= sizeSquare; j++) {
				nbPixels += 1;
				int color;
				try {
					color = image.getRGB(x + i, y + j);
				} catch (ArrayIndexOutOfBoundsException aiobe) {
					continue;
				}
				// TODO: verifier validite condition ci-dessous
				if ((color & 0x00FFFFFF) == 0) { // https://stackoverflow.com/a/47253342
					// si la couleur est noire
					nbBlackPixels++;
				}
			}
		}
		int nbMin = (seuil * nbPixels) / 100; // nb minimum de pixels noir pour declarer la case cochee
		// System.out.println(nbBlackPixels + " >= " + nbMin);
		return (nbBlackPixels >= nbMin);
	}

	/**
	 * Verifie si le carre est de couleur noire ou pas
	 *
	 * @param image      image contenant le carre
	 * @param point      coordonnee du coin superieur gauche du carre
	 * @param sizeSquare taille du carre
	 * @param seuil      seuil au dessus duquel le carre est considere comme noir
	 * @return <code>true</code> si le carre est noir au dessus du
	 *         <code>seuil<code> sinon <code>false</code>
	 */
	public boolean isQuestionSquareBlack(PDRectangle format, BufferedImage image, Point point, int sizeSquare,
			int seuil) {
		// TODO: voir parametres
		// on calcule les coordonnees de la reponse sur la buffered image
		// TODO: revoir formules de mise a l'echelle et notamment 3450
		/*
		 * int x = (int) (this.xRatio * point.getX()); int y = (int) (3450 -
		 * (this.yRatio * point.getY()));
		 */
		int x = OmrRewrite.ajustXCoords(point, format, image);
		int y = OmrRewrite.ajustYCoords(point, format, image) - sizeSquare;
		// on initialise le compteur de pixels noirs
		int nbBlackPixels = 0;
		int nbPixels = 0;
		// TODO: voir <= ou <
		// pour chaque pixel
		for (int i = 0; i <= sizeSquare; i++) {
			for (int j = 0; j <= sizeSquare; j++) {
				nbPixels += 1;
				int color;
				try {
					color = image.getRGB(x + i, y + j);
				} catch (ArrayIndexOutOfBoundsException aiobe) {
					continue;
				}
				// TODO: verifier validite condition ci-dessous
				if ((color & 0x00FFFFFF) == 0) { // https://stackoverflow.com/a/47253342
					// si la couleur est noire
					nbBlackPixels++;
				}
			}
		}
		int nbMin = (seuil * nbPixels) / 100; // nb minimum de pixels noir pour declarer la case cochee
		// System.out.println(nbBlackPixels + " >= " + nbMin);
		return (nbBlackPixels >= nbMin);
	}

	/**
	 * Verifie si le carre est de couleur noire ou pas
	 *
	 * @param format     format de la page
	 * @param image      image contenant le carre
	 * @param point      coordonnee du coin superieur gauche du carre
	 * @param sizeSquare taille du carre
	 * @param seuil      seuil au dessus duquel le carre est considere comme noir
	 * @return <code>true</code> si le carre est noir au dessus du
	 *         <code>seuil<code> sinon <code>false</code>
	 */
	public boolean isIdSquareBlack(PDRectangle format, BufferedImage image, Point point, int sizeSquare, int seuil) {
		// TODO: voir parametres
		// on calcule les coordonnees de la reponse sur la buffered image
		// TODO: revoir formules de mise a l'echelle et notamment 3450
		/*
		 * int x = (int) (this.xRatio * point.getX()); int y = (int) (3450 -
		 * (this.yRatio * point.getY()));
		 */
		int x = OmrRewrite.ajustXCoords(point, format, image);
		int y = OmrRewrite.ajustYCoords(point, format, image) - sizeSquare;
		// on initialise le compteur de pixels noirs
		int nbBlackPixels = 0;
		int nbPixels = 0;
		// TODO: voir <= ou <
		// pour chaque pixel
		for (int i = 0; i <= sizeSquare; i++) {
			for (int j = 0; j <= sizeSquare; j++) {
				nbPixels += 1;
				int color;
				try {
					color = image.getRGB(x + i, y + j);
				} catch (ArrayIndexOutOfBoundsException aiobe) {
					continue;
				}
				// TODO: verifier validite condition ci-dessous
				if ((color & 0x00FFFFFF) == 0) { // https://stackoverflow.com/a/47253342
					// si la couleur est noire
					nbBlackPixels++;
				}
			}
		}
		int nbMin = (seuil * nbPixels) / 100; // nb minimum de pixels noir pour declarer la case cochee
		// System.out.println(nbBlackPixels + " >= " + nbMin);
		return (nbBlackPixels >= nbMin);
	}

}
