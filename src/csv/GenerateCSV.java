package csv;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenerateCSV {

	Map<String, String> etudiants;
	int nLength;
	double mType;
	String path = "export";
	Logger logger = LogManager.getLogger(GenerateCSV.class);

	public GenerateCSV(Map<String, String> map, String numLength,String markType, String pth) {
		this.etudiants = map;
		this.nLength = Integer.parseInt(numLength);
		this.mType = Integer.parseInt(markType);
		this.path = path + "/" + pth;
		
	}
	

	// Teste validité du numero etudiant (selon param de la config passé :
	// numLength)

	public boolean isNumValid(String s) {
		int i = 0;
		logger.debug("Checking string validity");
		if (s.length() == this.nLength) {
			while (i < s.length()) {

				int nb = Character.getNumericValue(s.charAt(i));

				
				if (nb < 0 || nb > 9) {
					
					logger.fatal("Student id's characters are not recognized");
					return false;
				} else {
					i++;
				}

			}
			logger.debug("String validity ok");
			return true;
			
		} else if (s.length()==0 || s==null){
			
			logger.fatal("Id not recognized");
			return false;
			
		} else {
			logger.fatal("Student id's length is not correct");
			return false;
		}
	}
	
	//verifi que la note est valide
	public boolean isMarkValid(String s) {
		logger.debug("Checking mark validity");
		if(s.contains(",")) {
			s = s.replace(",",".");
			System.out.println(s);
		}
		
		double nb = Double.parseDouble(s);
		
		if (nb>= 0 && nb <=this.mType) {
			logger.debug("Mark format ok");
			return true;
		}
		else {
			logger.fatal("Mark format is not correct");
			return false;
		}
		
	}
	
	
	public boolean alreadyExists(String etd) {
		int count = 0;
		
		for(String etud : etudiants.keySet()) {
			if (etud.contentEquals(etd)) {
				count++;
			}	
		}
		
		if(count >= 1) {	
			logger.info("Student's id "+etd+" already checked");
			return true;
		}
		else {
			return false;
		}	
		
	}

	public void createFile() {
		try (PrintWriter writer = new PrintWriter(new File(this.path))) {

			logger.info("Creating csv file");
			StringBuilder sb = new StringBuilder();
			sb.append("Student number");
			sb.append(';');
			sb.append("Grade");
			sb.append(System.getProperty("line.separator"));

			writer.write(sb.toString());

			//Si l'id n'est pas vide
			if (!etudiants.isEmpty()) {

				for (String etud : this.etudiants.keySet()) {

					// Si etudiant HashMap est null, pas ecrit
					if (etud != null) {

						// Si n'existe pas deja (pas de double)
						//if(!alreadyExists(etud)) {
							
							if (this.isNumValid(etud) && this.isMarkValid(etudiants.get(etud))) {
								writer.write(etud + ";" + etudiants.get(etud) + System.getProperty("line.separator"));
								logger.debug("Added "+etud+" to csv");
							}
							else {
								logger.debug("Invalid pair for "+etud+" and "+etudiants.get(etud)+" not added to csv");
							}
							
//						}
//						else {
//							logger.debug("Already existing id not added to csv");
//						}

					} 
					else {

						logger.debug("Null id not added to csv");
					}

				}

				logger.info("File creation succeed");
			} else {
				logger.fatal("Students list for csv generation is empty");
			}

		} catch (FileNotFoundException e) {
			logger.fatal(e.getMessage());
		}
	}

}
