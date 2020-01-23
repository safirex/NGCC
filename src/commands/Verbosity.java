package commands;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

public class Verbosity {

	
	private int level =1; // Niveau de verbosité 
	private String[] loggers = {"commands","csv"}; // Noms des loggers Log4j
	private Level mode = Level.FATAL; // Mode des loggers (verbosité)
	
	
	public Verbosity(int lvl) {
		this.level = lvl;
	}
	
	public void configure() {
		
		// Définit le mode selon niveau de verbosité (debug niv max)
		
		if ((level >= 0) && (level <= 2)) {
			mode = Level.FATAL;
		} else if ((level >= 3) && (level <= 4)) {
			mode = Level.ERROR;
		} else if ((level >= 5) && (level <= 6)) {
			mode = Level.WARN;
		} else if ((level >= 7) && (level <= 8)) {
			mode = Level.INFO;
		} else {
			mode = Level.DEBUG;
		}
		
		// Configure les loggers en fonction de leur nom et du mode
		
		for( String s : loggers) {
			Configurator.setLevel(s, mode);
		}
		
		
	}
	
}
