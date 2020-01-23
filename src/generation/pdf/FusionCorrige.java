package generation.pdf;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import config.Config;
import config.Question;
import config.QuestionBoite;
import config.Reponse;
import copies.Copie;
import copies.id.PDDocumentID;

public class FusionCorrige extends Fusion implements Corrige {

	public FusionCorrige(Copie c) {
		super(c);
	}

	@Override
	public void generateHeader(Config config) {
		// TODO: ajouter "Corrige" dans le titre
		try {
			PDPage page = this.pdDocument.getPage(0);

			PDPageContentStream pdPageContentStream = new PDPageContentStream(this.pdDocument, page,
					PDPageContentStream.AppendMode.APPEND, true);
			PDFont font = PDType1Font.TIMES_ROMAN;

			// float heightOffset = this.height - 160;

			pdPageContentStream.setFont(font, 10);

			// Sujet
			// center text : https://stackoverflow.com/a/6531362
			String subject = Config.clearString(config.getParam().get("Title"));
			int fontSize = 12;
			pdPageContentStream.setFont(font, fontSize);
			float titleWidth = CopiePdfImpl.getStrLenWithFont(subject, font, fontSize);
			float titleHeight = (font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000) * fontSize;

			float heightOffset = this.height - 80 - titleHeight;

			for (String sub : CopiePdfImpl.setTextOnMultLines(subject, this.widthMargin + 125, this.pdDocument, font,
					fontSize)) {
				titleWidth = CopiePdfImpl.getStrLenWithFont(sub, font, fontSize);
				CopiePdfImpl.writeText(pdPageContentStream, subject, (this.width - titleWidth) / 2, heightOffset);
				heightOffset -= 12;
			}

			heightOffset -= 30; // on descend d'une ligne

			String subtitle = Config.clearString(config.getParam().get("Presentation"));
			// titleWidth = CopiePdf.getStrLenWithFont(subtitle, font, fontSize);
			for (String presentation : CopiePdfImpl.setTextOnMultLines(subtitle, this.widthMargin + 125,
					this.pdDocument, font, fontSize)) {
				titleWidth = CopiePdfImpl.getStrLenWithFont(presentation, font, fontSize);
				CopiePdfImpl.writeText(pdPageContentStream, presentation, ((this.width) - titleWidth) / 2,
						heightOffset);
				heightOffset -= 12;
			}
			heightOffset -= 30;
			String dateSentence = "Examen du " + config.getParam().get("AnswerSheetDate");
			float datewidth = CopiePdfImpl.getStrLenWithFont(dateSentence, font, fontSize);
			CopiePdfImpl.writeText(pdPageContentStream, dateSentence, ((this.width) - datewidth) / 2, heightOffset);
			heightOffset -= 20;

			// draw the separative line
			// TODO: mettre a la fin
			pdPageContentStream.moveTo(28, heightOffset);
			pdPageContentStream.lineTo(this.width - 28, heightOffset);
			// pdPageContentStream.lineTo(width - 28, height - 239);
			pdPageContentStream.closeAndStroke(); // stroke

			fontSize = 10;
			pdPageContentStream.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public float generateOpenQ(QuestionBoite q, int qIndex, PDDocumentID pdDocument, PDPage curPage, float widthOffset,
			float heightOffset, int numPage) {
		try {
			PDPageContentStream pdPageContentStream = new PDPageContentStream(pdDocument, curPage,
					PDPageContentStream.AppendMode.APPEND, true);
			PDFont font = PDType1Font.TIMES_ROMAN;
			int fontSize = 10;
			pdPageContentStream.setFont(font, fontSize);
			float titleLength = CopiePdfImpl.getStrLenWithFont(q.getTitre(), font, fontSize);
			float lastLineLenght = titleLength;

			// String qMark = this.config.getParam().get("Question");
			String qMark = "Q.";

			if (titleLength > (this.width - 20)) {
				// Si titre plus long que largeur page -> mise sur plusieurs lignes
				CopiePdfImpl.writeText(pdPageContentStream, "Q." + qIndex + " - ", widthOffset,
						this.height - heightOffset);

				for (String line : setTextOnMultLines(q.getTitre(), (int) widthOffset, pdDocument, font, fontSize)) {
					// pour chaque ligne de la question
					CopiePdfImpl.writeText(pdPageContentStream, line, widthOffset + 22, this.height - heightOffset);
					heightOffset += 20; // on met a jour le offset vertical (une ligne = 20)
					lastLineLenght = getStrLenWithFont(line, font, fontSize);
				}
				heightOffset -= 20;
			} else {
				CopiePdfImpl.writeText(pdPageContentStream, qMark + qIndex + " - " + q.getTitre(), widthOffset,
						this.height - heightOffset);
			}

			// on cherche a savoir a partir d'ou on peut mettre le texte des reponses pour
			// etre le plus a droite possible en fonction du nombre de reponses associees
			int maxX = this.width - (int) widthOffset;

			for (Reponse r : q.getReponses()) {
				// pour chaque reponse
				maxX -= (CopiePdfImpl.getStrLenWithFont(r.getIntitule(), font, fontSize) + this.widthMargin);
			}

			if ((lastLineLenght + this.widthMargin) > maxX) {
				// si la reponse est trop longue pour pouvoir mettre les reponses sur la meme
				// ligne
				heightOffset += 20; // TODO: revoir 20 avec largeur police
			}

			// on affiche les reponses et les cases a cocher associees
			pdPageContentStream.addRect(maxX - 5, this.height - heightOffset - 5,
					((this.width - this.widthMargin) - (maxX) - 5), 20); // cadre gris
			pdPageContentStream.setNonStrokingColor(Color.LIGHT_GRAY);
			pdPageContentStream.fill();
			pdPageContentStream.setNonStrokingColor(Color.BLACK);
			for (Reponse r : q.getReponses()) {
				// pour chaque reponse
				r.setP(new Point(maxX, (int) (this.height - heightOffset)));
				r.setNumPage(numPage);
				pdPageContentStream.addRect(maxX, this.height - heightOffset, 10, 10);
				CopiePdfImpl.writeText(pdPageContentStream, r.getIntitule(), maxX + 12, this.height - heightOffset);

				if (r.isJuste()) {
					pdPageContentStream.closeAndFillAndStroke(); // carre noirci
				} else {
					pdPageContentStream.setNonStrokingColor(Color.WHITE);
					pdPageContentStream.closeAndFillAndStroke(); // carre vide
					pdPageContentStream.setNonStrokingColor(Color.BLACK);
				}

				maxX += CopiePdfImpl.getStrLenWithFont(r.getIntitule(), font, fontSize) + 20;
				// taille de la reponse plus du carre
			}

			int linesLenght = q.getNbligne() * 20; // nb de lignes * taille d'une ligne (20)
			int xLenght = this.width - this.widthMargin - 50;

			// zone d'ecriture
			heightOffset += 10;
			pdPageContentStream.addRect(widthOffset + 20, this.height - heightOffset - linesLenght, xLenght,
					linesLenght);
			pdPageContentStream.closeAndStroke();

			// doted lines
			int yCursor = this.height - (int) heightOffset - 16;
			for (int i = 0; i < q.getNbligne(); i++) {
				yCursor = this.height - (int) heightOffset - 16 - (i * 2); // on descends y de la taille
																			// d'une ligne
				// CopiePdf.drawDotedLine(pdPageContentStream, widthOffset + 23,
				// this.height - heightOffset - (20 * i), yLenght - 23, this.height -
				// heightOffset - (20 * i));
			}

			heightOffset += linesLenght; // largeur de la zone d'ecriture
			heightOffset += 20; // espace blanc entre 2 questions

			pdPageContentStream.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return heightOffset;
	}

	@Override
	public float generateQCM(Question q, int qIndex, PDDocumentID pdDocument, PDPage curPage, float widthOffset,
			float heightOffset, Config config, int numPage) {
		try (PDPageContentStream pdPageContentStream = new PDPageContentStream(pdDocument, curPage,
				PDPageContentStream.AppendMode.APPEND, true);) {
			// Il faut rendre plus générale la génération des Q (en fonction du nombre
			// de
			// réponses, recycler le heightOffset)
			PDFont font = PDType1Font.TIMES_ROMAN;
			int fontSize = 10;
			pdPageContentStream.setFont(font, fontSize);
			float titleLength = CopiePdfImpl.getStrLenWithFont(q.getTitre(), font, fontSize);

			// String qMark = this.config.getParam().get("Question");
			String qMark = "Q.";

			// Titre

			if (titleLength > (this.width - 20)) {
				CopiePdfImpl.writeText(pdPageContentStream, qMark + qIndex + " - ", widthOffset,
						this.height - heightOffset);

				for (String line : setTextOnMultLines(q.getTitre(), (int) widthOffset, pdDocument, font, fontSize)) {
					CopiePdfImpl.writeText(pdPageContentStream, line, widthOffset + 22, this.height - heightOffset);
					// TODO: change widthOffset + 22 par len(qMarks+qIndex)
					heightOffset += 20;
				}
			} else {
				CopiePdfImpl.writeText(pdPageContentStream, qMark + qIndex + " - " + q.getTitre(), widthOffset,
						this.height - heightOffset);
			}

			List<Reponse> reponses = q.getReponses();
			// si les reponses doivent etre melangees
			if (config.getParam().get("ShuffleAnswers") == "1") {
				Collections.shuffle(reponses);
			}
			// pour chaque reponse
			for (Reponse r : reponses) {
				// on ecrit la reponse
				CopiePdfImpl.writeText(pdPageContentStream, r.getIntitule(), widthOffset + 40,
						this.height - heightOffset - 15);
				// on ajoute une zone pour cocher (carre)
				r.setP(new Point((int) widthOffset + 20, (int) (this.height - heightOffset - 16)));
				r.setNumPage(numPage);
				pdPageContentStream.addRect(widthOffset + 20, this.height - heightOffset - 16, ID_CARRE_TAILLE,
						ID_CARRE_TAILLE);
				// si c'est une bonne reponse
				if (r.isJuste()) {
					pdPageContentStream.closeAndFillAndStroke(); // carre noirci
				} else {
					pdPageContentStream.closeAndStroke(); // carre vide
				}

				heightOffset += 17.5;
			}
			heightOffset += 20; // espace blanc entre 2 questions
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return heightOffset;
	}

}
