package commands;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import config.Reponse;
import copies.Copie;
import database.DBManager;
import database.tables.CopieDB;
import database.tables.EtudiantDB;
import database.tables.PageDB;
import net.sourceforge.tess4j.TesseractException;
import ocr.OCR;
import ocr.PdfToImage;
import omr.OmrRewrite;
import picocli.CommandLine;
//import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Command(name = "-a", version = "Version 1.0", sortOptions = false, usageHelpWidth = 60, header = "Analyse command - Questions' answers identification", footer = "", description = "\nAnalyzes scanned copies (pdf files) provided to identify student and questions' answers\n")

public class Analyse implements Callable<Void> {

	private DBManager manager;
	private OmrRewrite omr;

	public Logger logger = LogManager.getLogger(Analyse.class);

	@Spec
	Model.CommandSpec spec;

	@Option(names = { "-help" }, arity = "0", order = 1, description = "command help")
	boolean help;

	@Option(names = { "-u" }, arity = "1", order = 2, description = "update mode")
	int step;

	@Option(names = { "-v" }, arity = "0..*", order = 3, defaultValue = "1", description = "verbose mode")
	int vb_level;

	@Option(names = { "-d" }, arity = "1", order = 4, defaultValue = "copies", description = "directory")
	String directory_name;

	@Parameters(arity = "0..1", defaultValue = "config/source.txt", description = "source path")
	String source_path;

	public Analyse(PrintStream out) {
	}

	private Map<Integer, List<Integer>> recoverIds(List<BufferedImage> images, PDRectangle format)
			throws SQLException, NumberFormatException, TesseractException {
		Map<Integer, List<Integer>> ids = new HashMap<>();
		int idEtudiant;
		EtudiantDB etudiant;
		int idCopie;
		int idPage;
		for (BufferedImage image : images) {
			// TODO: redimmensionner l'image
			// recuperation de l'id etudiant
			idEtudiant = Integer.parseInt(OCR.applyOcrNumber(image)); // reconnaissance id etudiant (OCR)
			etudiant = new EtudiantDB(idEtudiant);
			this.manager.insertNewStudent(etudiant);
			// reconnsaissance des ids de copies et pages
			idCopie = (int) this.omr.readId(image, format, 80)[0]; // voir seuil et cast (type int ou long)
			idPage = (int) this.omr.readId(image, format, 80)[0];
			if (ids.containsKey(idCopie)) {
				ids.get(idCopie).add(idPage);
			} else {
				ids.put(idCopie, new ArrayList<>(Arrays.asList(idPage)));
			}
			// insertion dans la bdd
			this.manager.associateStudentWithCopie(idCopie, idEtudiant);
		}
		return ids;
	}

	private void reconstituePages(Map<Integer, List<Integer>> ids) {
		ids.forEach((idc, idps) -> {
			PageDB page;
			// pour chaque id de copie et liste d'ids de page
			for (int idp : idps) {
				// pour chaque id de page
				page = new PageDB(idc, idp); // on recrée la page
				this.manager.insertNewPage(page); // insertion dans la bdd
			}
		});
	}

	private Map<Integer, List<Reponse>> processQuestions(List<BufferedImage> images, Map<Integer, List<Integer>> ids)
			throws ClassNotFoundException, SQLException, IOException {
		Map<Integer, List<Reponse>> assocIdCopieCheckedAnsw = new HashMap<>();
		List<Reponse> checkedAnswers = new ArrayList<>();
		List<Integer> idCopies = new ArrayList<>();
		int count = 0; // compteur
		for (BufferedImage image : images) {
			idCopies = new ArrayList<>(ids.keySet());
			int idCopie = idCopies.get(count);
			CopieDB c = this.manager.getCopieWithCopieID(idCopie);
			Copie copie = new Copie(c.getQuestions(), "A4"); // TODO: prendre en compte le format
			checkedAnswers = new ArrayList<Reponse>();// .omr.getAnsweredReponses(copie, image, count, 10, 80).values();
														// // TODO:
			// voir
			// seuil
			if (assocIdCopieCheckedAnsw.containsKey(idCopie)) {
				assocIdCopieCheckedAnsw.get(0).addAll(checkedAnswers);
			} else {
				assocIdCopieCheckedAnsw.put(idCopie, checkedAnswers);
			}
		}
		return assocIdCopieCheckedAnsw;
	}

	private void saveAnswersInDatabase(Map<Integer, List<Reponse>> assocIdCopieCheckedAnsw) {
		assocIdCopieCheckedAnsw.forEach((idCopie, checkedAnsw) -> {
			try {
				CopieDB copieDB = this.manager.getCopieWithCopieID(idCopie);
				copieDB.setReponsesCochees(checkedAnsw);
				this.manager.replaceCopieDB(copieDB);
			} catch (ClassNotFoundException | SQLException | IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public Void call() throws Exception {

		PDRectangle format = PDRectangle.A4; // TODO: utiliser la bdd (ajouter sauvegarde lors de la generation)

		// Niveau de log configuré selon verbosite

		Verbosity verbose = new Verbosity(this.vb_level);
		verbose.configure();

		if (this.help) {
			CommandLine.usage(this.spec, System.out);
		} else {

			System.out.println("\nAnalyse mode activated ...\n");
			System.out.println("Update : " + this.step + "\n" + "Verbose : " + this.vb_level + "\n" + "Directory : "
					+ this.directory_name + "\n" + "Source : " + this.source_path + "\n");

		}

		this.logger.info("Accès à la base de données..."); // TODO: ajouter log dans DBManager
		this.manager = DBManager.getInstance("./db/database.db");

		this.logger.info("Lecture des copies...");

		List<String> fnames = PdfToImage.listAllFiles(this.directory_name, ".jpg"); // TODO: gerer le format
		List<BufferedImage> images = PdfToImage.readAllImages(fnames); // lit les images

		this.logger.info("Association des id d'étudiants...");
		Map<Integer, List<Integer>> ids = this.recoverIds(images, PDRectangle.A4);

		this.logger.info("Reconstitution des copies avec leurs pages...");
		this.reconstituePages(ids);

		this.logger.info("Récuperation des réponses cochées...");
		Map<Integer, List<Reponse>> assocIdCopieCheckedAnsw = this.processQuestions(images, ids);

		this.logger.info("Sauvegarde des réponses cochées dans la base de données...");
		this.saveAnswersInDatabase(assocIdCopieCheckedAnsw);
		// TODO: verifier pages manquantes + mauvaise lecture idEtudiant

		this.logger.info("Fin de lecture.");
		return null;

	}
}
