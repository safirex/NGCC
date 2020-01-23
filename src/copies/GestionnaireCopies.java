package copies;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.pdfbox.pdmodel.PDDocument;

import checker.QuestionLikenessExeption;
import checker.SubjectCorrector;
import config.Config;
import config.Groupe;
import config.Question;
import copies.exceptions.IdCopieOutOfBoundsException;
import copies.exceptions.IdTestOutOfBoundsException;
import database.tables.CopieDB;
import generation.IncorrectCopieTypeException;
import generation.factories.CopieAbstractFactory;
import generation.factories.FactoryProducer;
import generation.pdf.CopiePdfImpl;
import ocr.PdfToImage;

public class GestionnaireCopies {

	private List<Copie> listeCopies;
	private Copie sujet;
	private Config conf;
	private CopieAbstractFactory copieFacPdf;
	
	private static int idsCopie;

	private List<CopiePdfImpl> listeCopiesPdf;

	public GestionnaireCopies() throws IncorrectCopieTypeException {

		this.listeCopies = new ArrayList<Copie>();
		this.sujet = new Copie();
		this.listeCopiesPdf = new ArrayList<CopiePdfImpl>();
		
		idsCopie = 0;

	}
	
	public static int nextCopieId() {
		idsCopie++;
		return idsCopie - 1;
	}

	public static CopieDB exportDB(CopiePdfImpl copiePdf) {
		Copie copie = copiePdf.getCopie();
		return new CopieDB(copie.getId(), copiePdf.getNumberOfPages(), copie.getQuestions());
	}

	public void initializeOmr(String pathSource)
			throws IncorrectCopieTypeException, IdCopieOutOfBoundsException, IdTestOutOfBoundsException {
		this.conf = Config.getInstance(pathSource);
		this.copieFacPdf = FactoryProducer.getFactory(this.conf.getTypeOfCopies());
		this.creerSujet();
		this.creerCopies();
		this.creerCopiesPdf();
	}

	private void creerCopies() {

		int nbCopies = this.conf.getNumberOfCopiesToCreate();

		for (int i = 0; i < nbCopies; i++) {

			this.listeCopies.add(this.sujet.getEmptyCopy());
		}
	}

	private void creerCopiesPdf()
			throws IncorrectCopieTypeException, IdCopieOutOfBoundsException, IdTestOutOfBoundsException {

		for (Copie c : this.listeCopies) {
			CopiePdfImpl cpi = this.copieFacPdf.creerCopie(this.conf.getTypeOfCopies(), c);
			cpi.generatePDF(this.conf);
			this.listeCopiesPdf.add(cpi);
		}
	}

	private void creerSujet() {
		Random r = new Random();
		int rand;
		List<Question> listeQuestions = new ArrayList<>();

		for (Groupe g : this.conf.getGroupes()) {
			List<Question> tempQuestions = (List<Question>) g.getQuestions().clone();

			while (tempQuestions.size() > g.getNbQtChoisie()) {
				rand = r.nextInt(tempQuestions.size());
				tempQuestions.remove(rand);
			}
			listeQuestions.addAll(tempQuestions);
		}

		this.sujet = new Copie(listeQuestions, this.conf.getParam().get("PaperSize")); // TODO: voir Singleton
	}

	public void corrigerCopies() throws QuestionLikenessExeption {

		int NB_SUJET = this.conf.getNumberOfCopiesToCreate();
		for (int i = 0; i < NB_SUJET; i++) {
			for (Copie copie : this.listeCopies) {
				int bareme = Integer.parseInt(this.conf.getParam().get("MarkFormat"));
				copie.setNote(SubjectCorrector.getNoteCopie(this.sujet, copie, bareme, this.conf));
			}
		}
	}

	// Cree une copie vide ayant les memes questions que le sujet en ayant des

	// Ocr
	public void applyOcr(String chemin) {

		List<BufferedImage> copies = this.createImagesCopies(chemin);
		// TODO: this.listeCopies = new PaireSujetCopie<Copie, List<Copie>>();

		for (BufferedImage i : copies) {
			// TODO: this.listeCopies.addCopie(new Copie(i));

		}
		// TODO: this.applyOcr();

	}

	public List<BufferedImage> createImagesCopies(String path) {

		PdfToImage pdfAnalyzer = new PdfToImage();
		File pdfFile;
		PDDocument document = null;
		// LISTE DES IMAGES
		List<BufferedImage> images = new ArrayList<>(); // stockera les images (resultat)
		// CONVERT PAGES TO IMAGES

		try {

			// nom du fichier pdf à ouvrir (TODO: changer le chemin)
			List<String> files = pdfAnalyzer.listAllFiles(path, ".pdf");

			// Recadrage recadrage = new Recadrage();

			for (String fname : files) {
				pdfFile = new File(fname);
				document = PDDocument.load(pdfFile); // charge le fichier pdf cree pour le traiter

				images.addAll(PdfToImage.convertPagesToBWJPG(document, 1));

				// appelle la methode qui convertit les pages en images (jpg) noir et blanches
				document.close();
			}

		} catch (IOException e) {
			System.out.println(e);
		}

		return images;

	}

	public Map<String, String> createHashMapforCSV() {

		HashMap<String, String> temp = new HashMap<>();

		/*
		 * TODO: for (Copie c : this.listeCopies.getListeCopies()) {
		 *
		 * String numEtu = c.getBase().gethMapImgs().get("NumEtu").getDescription();
		 * String noteEtu = c.getBase().gethMapImgs().get("Note").getDescription();
		 *
		 * temp.put(numEtu, noteEtu); }
		 */

		return temp;

	}

	/*
	 * TODO: public void applyOcr() { for (Copie c :
	 * this.listeCopies.getListeCopies()) { c.getBase().applyOcrForEach(); } }
	 *
	 * public Copie getSujet() { return this.sujet; }
	 *
	 * public void setSujet(Copie sujet) { this.sujet = sujet; }
	 */

	public Config getConf() {
		return this.conf;
	}

	public void setConf(Config conf) {
		this.conf = conf;
	}

	public List<CopiePdfImpl> getListeCopiesPdf() {
		return this.listeCopiesPdf;
	}

	public void setListeCopiesPdf(List<CopiePdfImpl> listeCopiesPdf) {
		this.listeCopiesPdf = listeCopiesPdf;
	}

	public List<Copie> getListeCopies() {
		return this.listeCopies;
	}

	public Copie getSujet() {
		return this.sujet;
	}

	public void setSujet(Copie sujet) {
		this.sujet = sujet;
	}

}
