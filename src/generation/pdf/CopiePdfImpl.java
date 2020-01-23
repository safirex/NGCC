package generation.pdf;

import java.awt.Point;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import config.Config;
import config.Question;
import config.QuestionBoite;
import config.Reponse;
import copies.Copie;
import copies.exceptions.IdCopieOutOfBoundsException;
import copies.exceptions.IdTestOutOfBoundsException;
import copies.id.GestionnaireIds;
import copies.id.PDDocumentID;
import copies.id.PDPageID;
import generation.IncorrectFormatException;

public abstract class CopiePdfImpl implements CopiePdf, Serializable {

	private static final long serialVersionUID = -8757049277488844740L; // TODO: a verifier
	// TODO: revoir attributs (voir inutiles)
	protected Copie copie;
	//
	protected PDDocumentID pdDocument;

	protected PDRectangle format;
	protected int height;
	protected int width;
	protected int widthMargin;
	protected float footerSize;
	protected int nbPages;

	private List<Point> rectIDPos;

	public static final int ID_CARRE_TAILLE = 10;
	public static final int ID_CARRE_WIDTH = 170;
	
	PDPageID debNbQ;

	public CopiePdfImpl(Copie c) {
		this.copie = c;
		this.pdDocument = new PDDocumentID();
		try {
			this.format = CopiePdfImpl.getFormatFromString(c.getFormat());
		} catch (IncorrectFormatException ife) {
			System.out.println("Format non reconnu. Format défini par défaut sur A4"); // TODO: changer par le logger
			this.format = PDRectangle.A4;
		}

//		//
//		try {
//			this.pdDocument.addPage(new PDPageID(this.format, GestionnaireIds.nextId(0)));
//		} catch (IdCopieOutOfBoundsException | IdTestOutOfBoundsException e) {
//			e.printStackTrace();
//		}

		// TODO: gérer l'ajout de la premier page en pdPageID
		// Pour l'instant l'id ne s'affiche pas sur la première page
		// PDPageIDTree? Override getPage(index)?
		// Pb vient de generateHeader de FeuillesRep, Fusion
		try {
			this.pdDocument.addPage(new PDPageID(this.format, GestionnaireIds.nextId(0)));
		} catch (IdCopieOutOfBoundsException | IdTestOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		debNbQ = pdDocument.getPage(0);
		// Les tests gen. pdf ne passent pas si on laisse le bloc au dessus
		// Pas d'ajout de page sur l'objet?

		this.height = (int) this.format.getHeight();
		this.width = (int) this.format.getWidth();
		this.widthMargin = 20; // TODO: adapter au format
		this.footerSize = this.height - 10;
		this.nbPages = 1;
		this.rectIDPos = new ArrayList<>();
	}

	@Override
	public void generatePDF(Config config) throws IdCopieOutOfBoundsException, IdTestOutOfBoundsException {
		// System.out.println("Title : " + config.getTitle());
		this.generateBody(config);
		this.generateFooter();
		this.generateMarks();
		this.generateHeader(config);
		this.generateRectID();
	}

	@Override
	public abstract void generateHeader(Config config);

	@Override
	public abstract float generateBody(Config config);

	@Override
	public abstract float generateOpenQ(QuestionBoite q, int qIndex, PDDocumentID pdDocument, PDPage curPage,
			float widthOffset, float heightOffset, int numPage);

	@Override
	public abstract float generateQCM(Question q, int qIndex, PDDocumentID pdDocument, PDPage curPage, float widthOffset,
			float heightOffset, Config config, int numPage);

	@Override
	public void generateMarks() throws IdCopieOutOfBoundsException, IdTestOutOfBoundsException {
		// TODO: renommer les variables en fonction de :
		// signification id page
		int max = 60;
		int digA = 1;
		int digB = 1;
		int digC = max;
		for (PDPage page : this.pdDocument.getPages()) {
			try (PDPageContentStream pdPageContentStream = new PDPageContentStream(this.pdDocument, page,
					PDPageContentStream.AppendMode.APPEND, true)) {

				PDFont font = PDType1Font.TIMES_ROMAN;

				pdPageContentStream.setFont(font, 9);

				// generate page's ID
				// this.generateRectID(pdPageContentStream, 0);

				// generate circle marks in corners
				CopiePdfImpl.drawCircle(pdPageContentStream, 20, this.height - 30, 5); // top left
				CopiePdfImpl.drawCircle(pdPageContentStream, this.width - 20, this.height - 30, 5); // top right
				CopiePdfImpl.drawCircle(pdPageContentStream, 20, 30, 5); // bottom left
				CopiePdfImpl.drawCircle(pdPageContentStream, this.width - 20, 30, 5); // bottom right

				// number (top of page) +n/n/nn+
				CopiePdfImpl.writeText(pdPageContentStream, "+" + digA + "/" + digB + "/" + digC + "+",
						(this.width / 2) + 86, this.height - 30);

				if (digB == max) {
					digB = 1;
					digC = max;
					digA++;
				} else {
					digB++;
					digC--;
				}

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	@Override
	public void generateRectID() {
		for (PDPageID pageID : this.pdDocument.getPdPageIdList()) {
			try (PDPageContentStream pdPageContentStream = new PDPageContentStream(this.pdDocument, pageID,
					PDPageContentStream.AppendMode.APPEND, true)) {

				int idPage = pageID.getId();
				int idCopie = copie.getId();

				
				String binPageID = Integer.toBinaryString(idPage);
				String binCopieID = Integer.toBinaryString(idCopie);
				

				while (binPageID.length() < 18) {
					binPageID = "0" + binPageID;
				}
				
				while (binCopieID.length() < 6) {
					binCopieID = "0" + binCopieID;
				}
								

				// TODO: voir si on met ces variables en static
				int ID_CARRE_HEIGHT_FIRSTROW = this.height - 20;
				int ID_CARRE_HEIGHT_SECONDROW = this.height - 32;

				pdPageContentStream.setLineWidth(0.6f); // largeur du contour des rectangles

				int startP = 0;
				int startC = 0;
				
				String strBinPageID = "";
				String strBinCopieID = "";

				// generate rectangles id (?)
				for (int i = 0; i < 12; i++) {
					// draw first range of rectangles
					this.rectIDPos
							.add(new Point(ID_CARRE_WIDTH + (ID_CARRE_TAILLE * i) + (i * 2), ID_CARRE_HEIGHT_FIRSTROW));
					pdPageContentStream.addRect(ID_CARRE_WIDTH + (ID_CARRE_TAILLE * i) + (i * 2),
							ID_CARRE_HEIGHT_FIRSTROW, ID_CARRE_TAILLE, ID_CARRE_TAILLE);

					// Ignorer les 6 premiers rect de la premiere ligne (reserves aux tests)
					if (i < 6) {
						strBinCopieID = binCopieID.substring(startC, startC + 1);
						if (Integer.parseInt(strBinCopieID) == 1) {
							pdPageContentStream.fill();
						}
						startC++;
					} else {
						strBinPageID = binPageID.substring(startP, startP + 1);
						if (Integer.parseInt(strBinPageID) == 1) {
							pdPageContentStream.fill();
						}
						startP++;
					}
					
					pdPageContentStream.stroke();
				}

				for (int i = 0; i < 12; i++) {
					// + 9 * i = position du ieme carre
					// draw second range of rectangles
					this.rectIDPos.add(
							new Point(ID_CARRE_WIDTH + (ID_CARRE_TAILLE * i) + (i * 2), ID_CARRE_HEIGHT_SECONDROW));
					pdPageContentStream.addRect(ID_CARRE_WIDTH + (ID_CARRE_TAILLE * i) + (i * 2),
							ID_CARRE_HEIGHT_SECONDROW, ID_CARRE_TAILLE, ID_CARRE_TAILLE);

					strBinPageID = binPageID.substring(startP, startP + 1);
					if (Integer.parseInt(strBinPageID) == 1) {
						pdPageContentStream.fill();
						// pdPageContentStream.closeAndFillAndStroke();
					}
					pdPageContentStream.stroke();
					startP++;
				}
				Collections.reverse(this.rectIDPos);
				// pdPageContentStream.stroke(); // stroke

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	@Override
	public void generateFooter() {
		int nbTotal = this.pdDocument.getNumberOfPages();
		int count = 1;

		try {
			for (PDPage page : this.pdDocument.getPages()) {
				PDPageContentStream pdPageContentStream = new PDPageContentStream(this.pdDocument, page,
						PDPageContentStream.AppendMode.APPEND, true);
				PDFont font = PDType1Font.TIMES_ROMAN;
				int width = (int) page.getMediaBox().getWidth();
				int fontSize = 10;
				pdPageContentStream.setFont(font, fontSize);
				String footer = String.valueOf(count) + " / " + String.valueOf(nbTotal);
				float titleWidth = CopiePdfImpl.getStrLenWithFont(footer, font, fontSize);
				float titleHeight = (font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000) * fontSize;
				CopiePdfImpl.writeText(pdPageContentStream, footer, (width - titleWidth) / 2, (35 - titleHeight));
				pdPageContentStream.close();
				count++;
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static PDRectangle getFormatFromString(String format) throws IncorrectFormatException {
		switch (format) {
		case "A0":
			return PDRectangle.A0;
		case "A1":
			return PDRectangle.A1;
		case "A2":
			return PDRectangle.A2;
		case "A3":
			return PDRectangle.A3;
		case "A4":
			return PDRectangle.A4;
		case "A5":
			return PDRectangle.A5;
		case "A6":
			return PDRectangle.A6;
		default:
			throw new IncorrectFormatException("Le format spécifié n'est pas supporté.");
		}
	}

	public static void drawDotedLine(PDPageContentStream pdPageContentStream, int xi, int yi, int xf, int yf)
			throws IOException {
		pdPageContentStream.moveTo(xi, yi);
		for (int i = xi; i <= xf; i += 3) {
			pdPageContentStream.lineTo(i, yf);
			pdPageContentStream.moveTo(i + 2, yf);
		}
	}

	public static void drawCircle(PDPageContentStream pdPageContentStream, int cx, int cy, int r) throws IOException {
		// https://stackoverflow.com/a/42836210
		final float k = 0.552284749831f;
		pdPageContentStream.moveTo(cx - r, cy);
		pdPageContentStream.curveTo(cx - r, cy + (k * r), cx - (k * r), cy + r, cx, cy + r);
		pdPageContentStream.curveTo(cx + (k * r), cy + r, cx + r, cy + (k * r), cx + r, cy);
		pdPageContentStream.curveTo(cx + r, cy - (k * r), cx + (k * r), cy - r, cx, cy - r);
		pdPageContentStream.curveTo(cx - (k * r), cy - r, cx - r, cy - (k * r), cx - r, cy);
		pdPageContentStream.fill();
	}

	public static void writeText(PDPageContentStream pdPageContentStream, String text, float x, float y)
			throws IOException {
		pdPageContentStream.beginText();
		pdPageContentStream.newLineAtOffset(x, y);
		pdPageContentStream.showText(text);
		pdPageContentStream.endText();
	}

	public static List<String> setTextOnMultLines(String text, int widthOffset, PDDocument pdDocument, PDFont font,
			int fontSize) {
		// TODO: voir
		// https://pdfbox.apache.org/docs/2.0.8/javadocs/org/apache/pdfbox/pdmodel/PDPageContentStream.html#newLine()
		int width = (int) pdDocument.getPage(0).getMediaBox().getWidth();

		ArrayList<String> lines = new ArrayList<>();

		if (text.isBlank()) {
			// si le texte est une chaine vide ou ne contenant que des espaces
			return lines;
		}

		int widthMargin = 60;
		int textSize = 0;

		textSize = (int) CopiePdfImpl.getStrLenWithFont(text, font, fontSize); // taille totale du texte

		int availableWidth = width - widthOffset - widthMargin; // largeur disponible (marge enlevee)
		int index = 0;

		while (textSize > availableWidth) {
			// tant que la taille du texte restant depasse en largeur
			while ((int) CopiePdfImpl.getStrLenWithFont(text.substring(0, index), font, fontSize) < availableWidth) {
				// tant que la taille de la sous chaine de caractere ne depasse pas
				index++;
			}
			if (text.substring(0, index).endsWith(" ") || text.substring(0, index).endsWith("-")
					|| text.substring(index - 1, text.length() - 1).startsWith(" ")
					|| text.substring(index - 1, text.length() - 1).startsWith("-")) {

				lines.add(text.substring(0, index));
				text = text.substring(index);
			} else if (text.substring(0, index - 1).endsWith(" ") || text.substring(0, index - 1).endsWith("-")) {
				lines.add(text.substring(0, index - 1));
				text = text.substring(index - 1);
			} else {
				// sinon on coupe le mot
				lines.add(text.substring(0, index - 1) + "-");
				text = text.substring(index - 1);

			}
			textSize = (int) CopiePdfImpl.getStrLenWithFont(text, font, fontSize);
		}
		lines.add(text);
		return lines;
	}

	public static float getStrLenWithFont(String text, PDFont font, int fontSize) {
		try {
			return (font.getStringWidth(text) / 1000) * fontSize;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			// System.err.println("Erreur : getStrLenWithFont : Impossible d'obtenir la
			// longueur de la chaine avec cette police");
			return -1;
		}

	}

	public static float getFontHeight(PDFont font, int fontSize) {
		return ((font.getFontDescriptor().getCapHeight()) / 1000) * fontSize;
	}

	public static boolean questionWillFit(Question q, double heightOffset, double maxHeightOffset, double widthOffset,
			PDDocument pdDocument, PDFont font, int fontSize) {
		// TODO: voir si on laisse en static ou non
		// ATTENTION : limite = si la question prend plus d'une page a elle seule
		for (String element : CopiePdfImpl.setTextOnMultLines(q.getTitre(), (int) widthOffset, pdDocument, font,
				fontSize)) {
			// pour chaque ligne de la question
			// heightOffset += Copie.getFontHeight(font, fontSize); // on met a
			// jour l'espace vertical pris
			heightOffset += 20;
		}
		for (Reponse reponse : q.getReponses()) {
			// pour chaque reponse a la question
			for (String element : CopiePdfImpl.setTextOnMultLines(reponse.getIntitule(), (int) widthOffset, pdDocument,
					font, fontSize)) {
				// pour chaque ligne de la reponse
				// heightOffset += Copie.getFontHeight(font, fontSize); // on met a
				// jour l'espace vertical pris
				heightOffset += 17.5;
			}
		}
		if (q instanceof QuestionBoite) {
			heightOffset += ((QuestionBoite) q).getNbligne() * 20;
		}
		return (heightOffset < maxHeightOffset);
	}

	@Override
	public void generateNumEtudArea(Config config) {
		Map<String, Point[]> coords = new HashMap<>();
		try {
			PDPage page = this.pdDocument.getPage(0);
			PDPageContentStream pdPageContentStream = new PDPageContentStream(this.pdDocument, page,
					PDPageContentStream.AppendMode.APPEND, true);
			PDFont font = PDType1Font.TIMES_ROMAN;

			// Set a Color for the marks
			pdPageContentStream.setFont(font, 9);

			// num rectangle
			pdPageContentStream.addRect(this.width / 4, this.height - 146, 150, 40);
			String numEtudCons = Config.clearString(config.getParam().get("MarkField"));
			CopiePdfImpl.writeText(pdPageContentStream, numEtudCons, (this.width / 4) + 2, this.height - 115);

			// note rectangle
			pdPageContentStream.addRect(this.width / 4, this.height - 200, 150, 40);
			CopiePdfImpl.writeText(pdPageContentStream, "Note", (this.width / 4) + 2, this.height - 170);

			pdPageContentStream.moveTo((this.width / 4) + 75, this.height - 160);
			pdPageContentStream.lineTo((this.width / 4) + 75, this.height - 200);

			// dotedlines

			// CopiePdf.drawDotedLine(pdPageContentStream, width - 233, height -
			// 175, width - 90, height - 175);

			pdPageContentStream.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public void generateNameArea() {
		try {
			PDPage page = this.pdDocument.getPage(0);
			PDPageContentStream pdPageContentStream = new PDPageContentStream(this.pdDocument, page,
					PDPageContentStream.AppendMode.APPEND, true);
			PDFont font = PDType1Font.TIMES_ROMAN;

			pdPageContentStream.setFont(font, 9);

			// generate rectangle nom
			pdPageContentStream.addRect(this.width - 238, this.height - 196, 155, 50); // RECT

			// text
			CopiePdfImpl.writeText(pdPageContentStream, "Ecrivez votre Nom", this.width - 236, this.height - 156);

			// dot lines
			// TODO: Enleve car difficulte d'ocr du fait de la presence des pointilles
			// CopiePdf.drawDotedLine(pdPageContentStream, width - 233, height -
			// 175, width - 90, height - 175);
			// CopiePdf.drawDotedLine(pdPageContentStream, width - 233, height -
			// 190, width - 90, height - 190);

			// TODO: trier
			pdPageContentStream.stroke(); // stroke
			pdPageContentStream.closeAndStroke();
			pdPageContentStream.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static Map<String, Point[]> getEncadreCoords(String format) throws IncorrectFormatException {
		PDRectangle pdrectformat = CopiePdfImpl.getFormatFromString(format);
		Map<String, Point[]> coords = new HashMap<>();
		// sauvegarde des coordonnees de l'encadre du numero d'etudiant
		Point[] pointsNum = new Point[2];
		pointsNum[0] = new Point((int) pdrectformat.getWidth() / 4, (int) pdrectformat.getHeight() - 146);
		pointsNum[1] = new Point(((int) pdrectformat.getWidth() / 4) + 150,
				(int) (pdrectformat.getHeight() - 146) + 40);
		coords.put("numEtu", pointsNum);
		// sauvegarde des coordonnees de l'encadre de la note
		Point[] pointsNote = new Point[2];
		pointsNote[0] = new Point((int) pdrectformat.getWidth() / 4, (int) pdrectformat.getHeight() - 200);
		pointsNote[1] = new Point(((int) pdrectformat.getWidth() / 4) + 150,
				(int) (pdrectformat.getHeight() - 200) + 40);
		coords.put("noteEtu", pointsNote);
		return coords;
	}

	public Copie getCopie() {
		return this.copie;
	}

	public void setCopie(Copie c) {
		this.copie = c;
	}

	public PDDocument getPdDocument() {
		return this.pdDocument;
	}

	public void setPdDocument(PDDocumentID pdDocument) {
		this.pdDocument = pdDocument;
	}

	public PDRectangle getFormat() {
		return this.format;
	}

	public void setFormat(PDRectangle format) {
		this.format = format;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidthMargin() {
		return this.widthMargin;
	}

	public void setWidthMargin(int widthMargin) {
		this.widthMargin = widthMargin;
	}

	public float getFooterSize() {
		return this.footerSize;
	}

	public void setFooterSize(float footerSize) {
		this.footerSize = footerSize;
	}

	public List<Point> getRectIDPos() {
		return this.rectIDPos;
	}

	public void setRectIDPos(List<Point> rectIDPos) {
		this.rectIDPos = rectIDPos;
	}

	@Override
	public void save(String dest) {
		// TODO: add mkdir if dest not exists
		try {
			this.pdDocument.save(dest);
			// this.pdDocument.close();
			System.out.println("pdf saved to location !"); // TODO: remplacer par le logger
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public int getNumberOfPages() {
		return this.pdDocument.getNumberOfPages();
	}

	public void addPage(PDPageID page) {
		this.pdDocument.addPage(page);
	}

}
