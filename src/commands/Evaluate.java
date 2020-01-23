package commands;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import config.Reponse;
import database.DBManager;
import database.tables.CopieDB;
import picocli.CommandLine;
//import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Command(name = "-e", version = "Version 1.0", sortOptions = false, usageHelpWidth = 60, header = "Evaluate command - Mark evaluation ", footer = "", description = "\nEvaluate each copies' mark based on the analysis performed previously."
		+ " Can be executed several times to take changes into account.\n")

public class Evaluate implements Callable<Void> {

	public Logger logger = LogManager.getLogger(Evaluate.class);

	@Spec
	Model.CommandSpec spec;

	@Option(names = { "-help" }, arity = "0", order = 1, description = "command help")
	boolean help;

	@Option(names = { "-u" }, arity = "1", order = 2, description = "update mode")
	int step;

	@Option(names = { "-v" }, arity = "0..*", order = 3, defaultValue = "1", description = "verbose mode")
	int vb_level;

	@Option(names = { "-o" }, arity = "1", order = 4, defaultValue = "result.csv", description = "result")
	String result_name;

	@Parameters(arity = "0..1", defaultValue = "./source.txt", description = "source path")
	String source_path;

//	public boolean isCsv(String file) {
//		return file.endsWith(".csv");
//
//	}

	public Evaluate(PrintStream out) {
	}

	@Override
	public Void call() throws Exception {

		// Niveau de log configuré selon verbosite

		Verbosity verbose = new Verbosity(this.vb_level);
		verbose.configure();

		if (this.help) {
			CommandLine.usage(this.spec, System.out);
		} else {

			System.out.println("\nEvaluate mode activated ...\n");
			System.out.println("Update : " + this.step + "\n" + "Verbose : " + this.vb_level + "\n" + "Result : "
					+ this.result_name + "\n" + "Source : " + this.source_path + "\n");
		}

		this.logger.info("Accès à la base de données..."); // TODO: ajouter log dans DBManager
		DBManager manager = DBManager.getInstance("./db/database.db");

		List<Reponse> checkedAnswers = null;
		this.logger.info("Notation des copies...");
		// TODO: voir appel a GenerateCSV
		for (CopieDB copie : manager.getAllCopies()) {
			checkedAnswers = copie.getReponsesCochees();
			// TODO: double note = appel a SubjectCorrector pour definir la note
			// manager.markCopie(copie.getIdCopie(), note);
		}

		return null;

	}
}
