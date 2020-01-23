
import java.util.concurrent.Callable;

import org.apache.logging.log4j.core.LoggerContext;

import commands.Analyse;
import commands.Build;
import commands.Evaluate;
import commands.Generate;
import commands.Produce;
import commands.Read;
import picocli.CommandLine;
import picocli.CommandLine.*;

@Command(
		name = "Exec",
		version = "Version 1.0",
		sortOptions = false,
		usageHelpWidth = 60,
		header = "\n ----  Nicely Generated and Corrected Copies  ---- \n\n",
		footer = "\n\n  ---- Provided by IUT Info Nice S3T-G4 ---- \n",
		description = ""		
	)


public class Exec implements Callable <Void> {
	
	@Spec
	Model.CommandSpec spec;  // Permet de tager la commande pour l'appeler dans la surcharge de call()
	
	// Système d'options et paramètres de commande de l'API
	
	@Option(names= {"-v","--version"}, versionHelp = true, arity = "0", order = 1, description = "Displays version info")
	boolean version; 	// Paramètre associé (versionHelp génere l'aide de version automatiquement)


	public static void main(String[] args) throws InterruptedException {
		
		// Système de subcommand semblable à celui de git permettant d'appeler les classes relatives
		
		CommandLine cmd = new CommandLine (new Exec())
				.addSubcommand("-r", new Read(System.out)) 			// nom commande, objet commande
				.addSubcommand("-b", new Build(System.out))
				.addSubcommand("-g", new Generate(System.out))
				.addSubcommand("-p", new Produce(System.out))
				.addSubcommand("-a", new Analyse(System.out))
				.addSubcommand("-e", new Evaluate(System.out))	
				.addSubcommand("help", new HelpCommand()); 			// Aide générée automatiquement par l'API
		
		cmd.execute(args);  
		
	}
	

	@Override
	public Void call() throws Exception {
		
		//CommandLine.usage(this.spec, System.out); // Retourne l'aide générée par l'API s'il n'y a pas d'arguments
		
		// Comportement en absence d'arguments
		System.out.println("NGCC : Veuillez indiquer un argument dans la ligne de commande ('help' pour plus d'informations)");
		
		return null;
	}
		
}
