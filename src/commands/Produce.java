package commands;

import java.io.PrintStream;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import config.Config;
import copies.GestionnaireCopies;
import generation.pdf.CopiePdfImpl;
import picocli.CommandLine;
//import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Command(name = "-p", version = "Version 1.0", sortOptions = false, usageHelpWidth = 60, header = "Produce command - Correction subject generation", footer = "\nProduce correction subject associated to the source file.\n", description = "description")

public class Produce implements Callable<Void> {

	public Logger logger = LogManager.getLogger(Produce.class);

	@Spec
	Model.CommandSpec spec;

	@Option(names = { "-help" }, arity = "0", order = 1, description = "command help")
	boolean help;

	@Option(names = { "-u" }, arity = "1", order = 2, description = "update mode")
	int step;

	@Option(names = { "-v" }, arity = "0..*", order = 3, defaultValue = "1", description = "verbose mode")
	int vb_level;

	@Option(names = { "-c" }, arity = "1", order = 4, description = "sheet")
	String sheet_name;

	@Parameters(arity = "0..1", defaultValue = "config/source.txt", description = "source path")
	String source_path;

//	public boolean isPdf(String file) {
//		return file.endsWith(".pdf");
//
//	}

	public Produce(PrintStream out) {
	}

	@Override
	public Void call() throws Exception {

		// Niveau de log configuré selon verbosite

		Verbosity verbose = new Verbosity(this.vb_level);
		verbose.configure();

		if (this.help) {
			CommandLine.usage(this.spec, System.out);
		} else {

			this.logger.info("\nProduce mode activated ...\n");
			this.logger.info("Update : " + this.step + "\n" + "Verbose : " + this.vb_level + "\n" + "Sheet : "
					+ this.sheet_name + "\n" + "Source : " + this.source_path + "\n");
		}

		// config instanciation
		this.logger.info("Lecture du fichier source...");
		Config config = Config.getInstance(this.source_path);
		config.readConfig();
		// on precise dans config que l'on souhaite generer le corrige
		config.setTypeOfCopies(config.getTypeOfCopies() + "corrige");

		// GestionnaireCopies instanciation
		GestionnaireCopies gestionnaire = new GestionnaireCopies();
		this.logger.info("Génération du corrigé...");
		gestionnaire.initializeOmr(this.source_path); // TODO: revoir methode + nom (trop de responsabilites)

		// save copies on disk
		this.logger.info("Sauvegarde du corrigé sur le disque dur..."); // TODO: preciser le dossier de destination
		CopiePdfImpl copiePdf = gestionnaire.getListeCopiesPdf().get(0);
		copiePdf.save(this.sheet_name + ".pdf");

		return null;

	}
}
