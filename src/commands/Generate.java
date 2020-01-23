package commands;

import java.io.PrintStream;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import copies.GestionnaireCopies;
import database.DBManager;
import database.tables.CopieDB;
import generation.pdf.CopiePdfImpl;
import picocli.CommandLine;
//import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Command(name = "-g", version = "Version 1.0", sortOptions = false, usageHelpWidth = 60, header = "Generate command - Subject and answer generation", footer = "", description = "\nSubject generation and associated answer with the source document.\n")

public class Generate implements Callable<Void> {

	public Logger logger = LogManager.getLogger(Generate.class);

	@Spec
	Model.CommandSpec spec;

	@Option(names = { "-help" }, arity = "0", order = 1, description = "command help")
	boolean help;

	@Option(names = { "-u" }, arity = "1", order = 2, description = "update mode")
	int step;

	@Option(names = { "-v" }, arity = "0..*", order = 3, defaultValue = "1", description = "verbose mode")
	int vb_level;

	@Option(names = { "-t" }, arity = "1", order = 4, description = "topic")
	String topic_name;

	@Option(names = { "-a" }, arity = "1", order = 5, description = "answer")
	String answer_name; // TODO: voir answer_name + prise en compte lors de la generation

	@Parameters(arity = "0..1", defaultValue = "./source.txt", description = "source path")
	String source_path;

//	public boolean isPdf(String file) {
//		return file.endsWith(".pdf");
//
//	}

	public Generate(PrintStream out) {
	}

	@Override
	public Void call() throws Exception {

		// Niveau de log configuré selon verbosite

		Verbosity verbose = new Verbosity(this.vb_level);
		verbose.configure();

		if (this.help) {
			CommandLine.usage(this.spec, System.out);
			return null;
		} else {

			this.logger.info("\nGenerate mode activated ...\n");
			this.logger.info(
					"Update : " + this.step + "\n" + "Verbose : " + this.vb_level + "\n" + "Topic : " + this.topic_name
							+ "\n" + "Answer : " + this.answer_name + "\n" + "Source : " + this.source_path + "\n");
		}

		this.logger.info("Lecture du fichier source...");

		// Config instanciation (fait dans GestionnaireCopies)
		/*-Config config = Config.getInstance(this.source_path);
		config.readConfig();*/

		// factory and CopiePdfImpl instanciation (fait dans GestionnaireCopies)
		/*-String typeOfCopie = config.getTypeOfCopies();
		CopieFactory factory = FactoryProducer.getFactory(typeOfCopie);
		CopiePdfImpl copiePdf = factory.creerCopie(typeOfCopie, c);*/

		// GestionnaireCopies instanciation
		GestionnaireCopies gestionnaire = new GestionnaireCopies();
		this.logger.info("Génération des copies...");
		gestionnaire.initializeOmr(this.source_path); // TODO: revoir methode + nom (trop de responsabilites)

		// DBManager instanciation
		this.logger.info("Accès à la base de données..."); // TODO: ajouter log dans DBManager
		DBManager manager = DBManager.getInstance("./db/database.db");
		manager.createTables(); // creates tables if not exist

		// insertion of copies in the database
		this.logger.info("Sauvegarde des copies dans la base de données...");
		for (CopiePdfImpl copiePdf : gestionnaire.getListeCopiesPdf()) {
			CopieDB copieDB = GestionnaireCopies.exportDB(copiePdf);
			manager.insertNewCopie(copieDB);
		}

		// save copies on disk
		this.logger.info("Sauvegarde des copies sur le disque dur..."); // TODO: preciser le dossier de destination
		for (CopiePdfImpl copiePdf : gestionnaire.getListeCopiesPdf()) {
			copiePdf.save(this.topic_name + "_" + copiePdf.getCopie().getId() + ".pdf");
		}

		// TODO: ajouter l'option sujet detachable

		this.logger.info("Génération terminée.");
		return null;

	}
}
