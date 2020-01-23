package omr;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import config.Question;
import config.Reponse;
import generation.pdf.CopiePdfImpl;

public class CoordsMatcher extends PDFTextStripper {

	private CopiePdfImpl pdf;
	private final String coordsFile = "./pdf/TestOMR/coords.csv";

	public CoordsMatcher(CopiePdfImpl pdf) throws IOException {
		super();
		this.pdf = pdf;
	}

	public CopiePdfImpl getPdf() {
		return this.pdf;
	}

	public void setPdf(CopiePdfImpl pdf) {
		this.pdf = pdf;
	}

	/**
	 * Ajoute l'entete decrivant les differents champs du csv dans le fichier en
	 * question
	 *
	 * @throws IOException
	 */
	public void initCsvCoords() throws IOException {
		OutputStreamWriter csvWriter = new OutputStreamWriter(new FileOutputStream(this.coordsFile),
				StandardCharsets.UTF_8);
		csvWriter.append("Text");
		csvWriter.append(",");
		csvWriter.append("X");
		csvWriter.append(",");
		csvWriter.append("Y");
		csvWriter.append("\n");
		csvWriter.close();
	}

	/**
	 * Recupere les coordonnees des chaine de caracteres presentes dans
	 * <code>document</code>
	 *
	 * @param document source of text extraction
	 * @throws IOException
	 */
	public void getCoords() throws IOException {
		this.initCsvCoords();
		this.setSortByPosition(true);
		this.setStartPage(0);
		this.setEndPage(this.pdf.getPdDocument().getNumberOfPages());
		Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
		this.writeText(this.pdf.getPdDocument(), dummy);
	}

	/**
	 * Cherche et retourne les coodonnees d'une reponse dans un document pdf <br>
	 * <b>Attention</b> : <code>getCoords()</code> doit avoir ete appele au
	 * prealable
	 *
	 * @param reponse reponse dont on cherche les coordonnees
	 * @return <code>double[]</code> où <code>double[0] = x</code> et
	 *         <code>double[1] = y</code>
	 * @throws IOException
	 */
	public double[] getAnswerCoords(Question question, Reponse reponse) throws IOException {
		BufferedReader csvReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(this.coordsFile), "UTF-8"));
		// BufferedReader csvReader = new BufferedReader(new
		// FileReader(this.coordsFile));
		String row;
		double[] coords = new double[2];
		firstWhile: while ((row = csvReader.readLine()) != null) {
			String[] data = row.split(",");
			// TODO: changer pour ne pas toujours prendre le premier resultat
			if (data[0].contains(question.getTitre())) {
				while ((row = csvReader.readLine()) != null) {
					data = row.split(",");
					if (data[0].equals(reponse.getIntitule())) {
						coords[0] = Double.parseDouble(data[1]);
						coords[1] = Double.parseDouble(data[2]);
						break firstWhile;
					}
				}
			}
		}
		csvReader.close();
		return coords;
	}

	/**
	 * Override the default functionality of PDFTextStripper. <br>
	 * Save coords of each word contained in pddocument to a csv file.
	 */
	@Override
	protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
		// TODO: voir si l'on ecrit dans un fichier ou si l'on stocke tout dans un
		// attribut List<String> ==> plus performant, moins persistant
		TextPosition firstProsition = textPositions.get(0);
		OutputStreamWriter csvWriter = new OutputStreamWriter(new FileOutputStream(this.coordsFile, true),
				StandardCharsets.UTF_8); // true = append mode
		csvWriter.append(string);
		csvWriter.append(",");
		csvWriter.append(Double.toString(firstProsition.getX()));
		csvWriter.append(",");
		// csvWriter.append(Double.toString(firstProsition.getY()));
		csvWriter.append(Double.toString(firstProsition.getTextMatrix().getTranslateY() + 22));
		csvWriter.append("\n");
		csvWriter.close();
	}

}
