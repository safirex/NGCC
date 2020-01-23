package config;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commands.Read;

public class Config implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -1484228788567692458L;

	private static Config INSTANCE = null;
	
	public Logger logger = LogManager.getLogger(Config.class);

	private HashMap<String, String> param = new HashMap<String, String>(); // Hashmap des parametres de config, Key =
																			// nom paramn,
	private ArrayList<Groupe> groupes = new ArrayList<Groupe>();

	private String source; // Chemin vers le fichier source

	public final static Config getInstance(String source) {
		// Singleton design pattern
		if (Config.INSTANCE == null) {

			synchronized (Config.class) {
				if (Config.INSTANCE == null) {
					Config.INSTANCE = new Config(source);
					Config.INSTANCE.readConfig();
				}
			}
		}
		return Config.INSTANCE;
	}

	// Getters et setters
	public HashMap<String, String> getParam() {
		return this.param;
	}

	public void setParam(HashMap<String, String> param) {
		this.param = param;
	}

	public boolean isParsable(String s) {
		try {
			Integer.valueOf(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public ArrayList<Groupe> getGroupes() {
		return this.groupes;
	}

	public void rereadConfig() {
		this.groupes.clear();
		this.readConfig();
	}

	public static String clearString(String string) {
		// Sources :
		// https://howtodoinjava.com/regex/java-clean-ascii-text-non-printable-chars/
		// https://stackoverflow.com/a/52559280
		// https://docs.oracle.com/javase/10/docs/api/java/lang/String.html#trim()
		// https://stackoverflow.com/a/33724262

		// strips off all non-ASCII characters
		string = string.replace("\n", "");
		string = string.replaceAll("[^\\x00-\\xFF]", " ");
		// erases all the ASCII control characters
		string = string.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
		// removes non-printable characters from Unicode
		string = string.replaceAll("\\p{C}", "");
		return string.trim(); // trim() : suppr. les espaces inutiles
	}

	private Config(String s) {
		// Constructeur, prend en parametre le chemin vers le fichier source
		this.source = s;
		// Initialisation des parametres avec les valeurs par défaut.
		// Les élements avec des placeholders en valeur sont des élements non remplis
		this.param.put("PaperSize", "A4"); // A3 A4 A5 letter sont les valeurs possibles
		this.param.put("Title", ""); // titre de l'examen
		this.param.put("Presentation", ""); // texte de consignes
		this.param.put("DocumentModel", ""); // nom du fichier du modéle
		this.param.put("ShuffleQuestions", "1"); // 1 = question mélangées, 0 = non mélangées
		this.param.put("ShuffleAnswers", "1"); // 1= propositions réponse mélangées, 0= non mélangé
		this.param.put("Code", "8"); // code étudiant (ici = 8 chiffres) (valeur possible: entre 1 et 16)
		this.param.put("MarkFormat", "20"); // expl "20/4" pour des notes entre 0 et 20 notées à 0.25 points
											// format assez libre.
											// laisser 20 par défaut car defaultScoring without decimal point.
		this.param.put("NameField", "Nom et Prénom"); // remplace le texte demandant d'écrire le nom et prenom
		this.param.put("StudentIdField",
				"Veuillez coder votre numéro\r\n d'étudiant et écrire votre nom \r\n dans la case ci-dessous");
		// sert à remplacer le petit texte qui demande de coder son numéro
		// déétudiant et
		// inscrire son nom
		this.param.put("MarkField", "Ecrivez votre numéro étudiant ici");
		this.param.put("SeparateAnswerSheet", "1"); // si 1 = feuille de réponse séparée.
		this.param.put("AnswerSheetTitle", this.getTitle()); // titre à inscrire en tete de la feuille de réponse (de
																// base le même que title)
		this.param.put("AnswerSheetPresentation", this.getPresentation()); // Donne le texte de présentation de la
																			// feuille de
																			// réponse
		this.param.put("SingleSided", "0");// si valeur = 1, aucune page blanche entre feuille de sujet et de réponse
		this.param.put("DefaultScoringS", "e=0,v=0,p=-1,b=1,m=-1,d=0");// Donne le barème par défaut pour les
																		// questions
																		// simples

		this.param.put("DefaultScoringM", "e=0,v=0,p=-1,formula=(NBC/NB)-(NMC/NM)");// Donne le barème par défaut
																					// pour
																					// les questions à multiple
																					// réponses correctes
		this.param.put("QuestionBlocks", "1");// si 1 = questions sont non coupées entre 2 pages si 0, possibilité
		// qu'elles soient coupées.

		/***** BONUS ****/
		this.param.put("Columns", "1");// questionnaire écrit sur n (nbe entier) colonnes < 5.
		this.param.put("Date", new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()));// date de
																											// l'exam,
																											// defaut =
																											// date du
																											// jour
		this.param.put("AnswerSheetDate", this.getDate()); // date copie réponse, de base la mm que "Date"

		/****
		 * MANUALDUPLEX et ANSWERSHEETMANUALDUPLEX sont utiles pour imprimer les pdf
		 * sans avoir un recto d'une copie sur le verso d'une autre copie (mettre valeur
		 * à 1 pour cela)
		 ****/

		this.param.put("ManualDuplex", "0");// le sujet sera écrit sur un nbe pair de page si ManualDuplex = 1
											// si =0, le début d’une copie pourrait être sur le verso d'une autre

		this.param.put("AnswerSheetManualDuplex", this.getManualDuplex());// chaque feuille de réponse a nombre pair si

		this.param.put("NumberOfCopiesToCreate", "1");

		this.param.put("TypeOfCopies", "FUSION");

		// AnswerSheetManualDuplex = 1,
		// par défaut, la valeur de ManualDuplex sera utilisée.
		/*** FIN BONUS *///

		// groupe aléatoire par défaut au cas où l'utilisateur n'en crée pas
		this.groupes.add(new Groupe());
	}

	public void readConfig() {
		/*
		 * Methode pour lire le fichier config en chemin dans la variable source Si une
		 * ligne du fichier correspond à un parametre, changer la valeur du parametre
		 * avec celle dans le fichier (si valeur valide) Gere aussi les questions dans
		 * le fichier source et les mets dans une liste de questions.
		 */
		boolean isFrozen = false;
		try {
			Scanner scan = new Scanner(new File(this.source), "UTF-8");
			String ligne;
			Question q;
			Groupe g = null;
			boolean isOpen = false;

			ligne = scan.nextLine();
			// ligne pour gerer le code FEFF en UTF-8 BOM qui peut apparaitre si le fichier
			// txt est edité avec windows notepad
			// ce caractere apparait uniquement en debut de fichier
			if (ligne.startsWith("\uFEFF")) {
				ligne = ligne.substring(1);
			}
			while (scan.hasNext() || !ligne.isEmpty() || ligne.equals(System.lineSeparator())) {
				if (!ligne.equals("")) {
					if (!(ligne.substring(0, 1).equals("#"))) // si ligne commence par un # c'est un commentaire, donc
																// on
																// l'ignore
					{
						if (ligne.substring(0, 2).equals("((")) {
							g = this.makeGroupe(ligne.substring(3));
							isOpen = true;
							String s = ligne.substring(6, 12);
							String s1 = ligne.substring(7, 13);
							if (s.contains("frozen") || s1.contains("frozen")) {
								isFrozen = true;
							}
						}
						if (ligne.trim().equals("))") && isOpen) {
							this.groupes.add(g);
							g = null;
							isOpen = false;
						}

						if (ligne.substring(0, 1).equals("*")) // si ligne commence par une *, c'est une question
						{

							q = this.makeQuestion(clearString(ligne));
							ligne = scan.nextLine();
							while (!ligne.equals("") && !ligne.trim().equals("))")) // tant que la ligne n'est pas vide,
																					// on lit la
																					// suite qui est supposée etre les
																					// reponses
							{
								q.addReponse(clearString(ligne));
								ligne = scan.nextLine();
							}
							if (g != null) {
								g.addQuestions(q);
							} else {
								this.groupes.get(0).addQuestions(q);
							}

						} else // si c'est pas une *, alors c'est un parametre
						{
							this.lireParam(ligne);
						}
					}
				}
				ligne = scan.nextLine();
				while (ligne.equals("")) {
					ligne = scan.nextLine();
				}
			}
			scan.close();
		} catch (NoSuchElementException nsee) {
			System.out.println("Fin de lecture du fichier."); // TODO: afficher dans les logs
		} catch (Exception e) {
		}
		// si les questions doivent etre "mixed" => !(frozen)
		if (!(isFrozen)) {
			Collections.shuffle(this.groupes);
		}
	}

	// methode pour creer une question
	// methode utilisée é partir d'un string supposé lu sur un fichier config
	public Question makeQuestion(String ligne) {
		Question q;
		String s = ligne.substring(1, 2); // on lit le caractere qui differencie chaque type de question
		boolean frozen = false;
		int line = -1;
		int coeff = 1;
		boolean choixmultiple = false;
		if (s.equals("*")) {
			choixmultiple = true;
		}
		int debutopt = ligne.indexOf("{");
		int finopt = ligne.indexOf("}");
		if ((debutopt != -1) && (finopt != -1)) {
			String options = ligne.substring(debutopt + 1, finopt);
			String spl[] = options.split(",");
			for (String element : spl) {
				int n = element.indexOf("=");
				if ((n == -1) && element.equals("frozen")) {
					frozen = true;
				} else if (n != -1) {
					String spl2[] = { element.substring(0, n), element.substring(n + 1, element.length()) };
					switch (spl2[0].toLowerCase()) {
					case "lines":
						if (this.isParsable(spl2[1])) {
							line = Integer.parseInt(spl2[1]);
						}
						break;

					case "coeff":
						if (this.isParsable(spl2[1])) {
							coeff = Integer.parseInt(spl2[1]);
						}
						break;
					default:
					}
				}
			}
		}
		if ((line != -1) || (line >= 1)) {
			q = new QuestionBoite(ligne.substring(finopt + 2, ligne.length()), false, coeff, frozen, line);
		} else {
			if (finopt == -1) {
				if (choixmultiple) {
					q = new Question(ligne.substring(3, ligne.length()), choixmultiple, coeff, frozen);
				} else {
					q = new Question(ligne.substring(2, ligne.length()), choixmultiple, coeff, frozen);
				}
			} else {
				q = new Question(ligne.substring(finopt + 2, ligne.length()), choixmultiple, coeff, frozen);
			}
		}
		return q;
	}

	public Groupe makeGroupe(String ligne) {
		int n = ligne.indexOf("="); // recherche de position du premier "=" pour pouvoir separer le parametre n de
		// sa valeur
		if (n != -1) // si -1 alors il n'y a pas de : et donc ce n'est pas un paramètre
		{
			String val = ligne.substring(n + 1);
			if (this.isParsable(val)) {
				return new Groupe(Integer.parseInt(val));
			}
		}
		return new Groupe();
	}

	// modification des valeurs du hashmap param
	// lecture d'un string supposé lu sur un fichier config
	public void lireParam(String s) throws IncorrectParamException {
		int n = s.indexOf(":"); // recherche de position du premier ":" pour pouvoir separer le nom du param de
								// sa valeur
		if (n != -1) // si -1 alors il n'y a pas de : et donc ce n'est pas un paramètre
		{
			String spl[] = { s.substring(0, n), s.substring(n + 1, s.length()) };
			if (spl[1].isEmpty()) {
				return;
			}
			while (spl[1].substring(0, 1).equals(" ")) {
				spl[1] = spl[1].substring(1, spl[1].length());
			}
			spl[0] = spl[0].toUpperCase().trim(); // pour eviter la casse, on met tout en upper case
			switch (spl[0]) // chaque case correspond à un parametre, pour le moment on ignore tout
							// parametre qui n'est pas utile au programme.
			{
			case "PAPERSIZE":
				this.setPaperSize(spl[1]);
				break;

			case "TITLE":
				this.setTitle(spl[1]);
				break;

			case "PRESENTATION":
				this.setPresentation(spl[1]);
				break;

			case "DOCUMENTMODEL":
				this.setDocumentModel(spl[1]);
				break;

			case "SHUFFLEQUESTIONS":
				this.setShuffleQuestions(spl[1]);
				break;

			case "SHUFFLEANSWERS":
				this.setShuffleAnswers(spl[1]);
				break;

			case "CODE":
				this.setCode(spl[1]);
				break;

			case "MARKFORMAT":
				this.setMarkFormat(spl[1]);
				break;

			case "NAMEFIELD":
				this.setNameField(spl[1]);
				break;

			case "STUDENTIDFIELD":
				this.setStudentIdField(spl[1]);
				break;

			case "MARKFIELD":
				this.setMarkField(spl[1]);
				break;

			case "SEPARATEANSWERSHEET":
				this.setSeparateAnswerSheet(spl[1]);
				break;

			case "ANSWERSHEETTITLE":
				this.setAnswerSheetTitle(spl[1]);
				break;

			case "ANSWERSHEETPRESENTATION":
				this.setAnswerSheetPresentation(spl[1]);
				break;

			case "SINGLESIDED":
				this.setSingleSided(spl[1]);
				break;

			case "DEFAULTSCORINGS":
				this.setDefaultScoringS(spl[1]);
				break;

			case "DEFAULTSCORINGM":
				this.setDefaultScoringM(spl[1]);
				break;

			case "COLUMNS":
				this.setColumns(spl[1]);
				break;

			case "QUESTIONBLOCKS":
				this.setQuestionBlocks(spl[1]);
				break;

			case "MANUALDUPLEX":
				this.setQuestionBlocks(spl[1]);
				break;

			case "ANSWERSHEETMANUALDUPLEX":
				this.setAnswerSheetManualDuplex(spl[1]);
				break;

			case "ANSWERSHEETDATE":
				this.setAnswerSheetDate(spl[1]);
				break;

			case "DATE":
				this.setDate(spl[1]);
				break;

			case "NUMBEROFCOPIESTOCREATE":
				this.setNumberOfCopiesToCreate(spl[1]);
				break;

			case "TYPEOFCOPIES":
				this.setTypeOfCopies(spl[1]);
				break;

			default: // erreur de paramètre, soit inexistant soit mal orthographié.

				throw new IncorrectParamException("Ce paramètre ne peux pas être pris en compte: " + s
						+ " Vérifiez son orthographe ou vérifiez s'il existe bien.\n");
			}
		}
	}

	public void setTypeOfCopies(String s) {
		if(getSeparateAnswerSheet().equals("1"))
			this.param.replace("TypeOfCopies", "REPONSES");
		if(getSeparateAnswerSheet().equals("0"))
			this.param.replace("TypeOfCopies", "FUSION");		
	}

	// TODO
	// possibilité d'afficher sur la console messages de valeur invalide et valeur
	// par défaut utilisée en cas d'erreur si verbose
	public void setPaperSize(String s) {
		s = s.toUpperCase();
		s = s.trim();
		if (s.equals("A3") || s.equals("A4") || s.equals("A5") || s.equals("LETTER")) {
			this.param.replace("PaperSize", s);
			this.logger.debug("PaperSize configuration parameter set to "+s);
		}
		this.logger.warn("PaperSize configuration parameter "+s+" not recognized");
	}

	public void setTitle(String s) {
		if (!s.equals("")) {
			this.param.replace("Title", s);
			this.logger.debug("Title configuration parameter set to "+s);
		}
		this.logger.warn("Title configuration parameter "+s+" not recognized");
	}

	public void setPresentation(String s) {
		if (!s.equals("")) {
			this.param.replace("Presentation", s);
			this.logger.debug("Presentation configuration parameter set to "+s);
		}
		this.logger.warn("Presentation configuration parameter "+s+" not recognized");
	}

	public void setDocumentModel(String s) {
		if (!s.equals("")) {
			this.param.replace("DocumentModel", s);
			this.logger.debug("DocumentModel configuration parameter set to "+s);
		}
		this.logger.warn("DocumentModel configuration parameter "+s+" not recognized");
	}

	public void setShuffleQuestions(String s) {
		if (s.equals("0")) {
			this.param.replace("ShuffleQuestions", s);
			this.logger.debug("ShuffleQuestions configuration parameter set to "+s);
		}
		this.logger.warn("ShuffleQuestions configuration parameter "+s+" not recognized");
		
	}

	public void setShuffleAnswers(String s) {
		if (s.equals("0")) {
			this.param.replace("ShuffleAnswers", s);
			this.logger.debug("ShuffleAnswers configuration parameter set to "+s);
		}
		this.logger.warn("ShuffleAnswers configuration parameter "+s+" not recognized");
	}

	public void setCode(String s) {
		s = s.trim();
		if (this.isParsable(s)) {
			int n = Integer.parseInt(s);
			if ((n >= 1) && (n <= 16)) {
				this.param.replace("Code", s);
				this.logger.debug("Code configuration parameter set to "+s);
			}
			this.logger.warn("Code configuration parameter "+s+" not recognized");
		}
		this.logger.warn("Code configuration parameter "+s+" not recognized");
	}

	public void setMarkFormat(String s) {
		s = s.trim();
		if (s.equals("20/4") || s.equals("100")) {
			this.param.replace("MarkFormat", s);
			this.logger.debug("MarkFormat configuration parameter set to "+s);
		} else {
			// format libre selon souhait de l'examinateur mais entier demandé.
			if (this.isParsable(s)) {
				this.param.replace("MarkFormat", s);
				this.logger.debug("MarkFormat configuration parameter set to "+s);
			}
		}
		this.logger.warn("MarkFormat configuration parameter "+s+" not recognized");
	}

	public void setNameField(String s) {
		if (!s.equals("")) {
			this.param.replace("NameField", s);
			this.logger.debug("NameField configuration parameter set to "+s);
		}
		this.logger.warn("NameField configuration parameter "+s+" not recognized");
	}

	public void setStudentIdField(String s) {
		if (!s.equals("")) {
			this.param.replace("StudentIdField", s);
			this.logger.debug("StudentIdField configuration parameter set to "+s);
		}
		this.logger.warn("StudentIdField configuration parameter "+s+" not recognized");
	}

	public void setMarkField(String s) {
		if (!s.equals("")) {
			this.param.replace("MarkField", s);
			this.logger.debug("Markfield configuration parameter set to "+s);
		}
		this.logger.warn("MarkField configuration parameter "+s+" not recognized");
	}

	public void setSeparateAnswerSheet(String s) {
		if (s.equals("0")) {
			this.param.replace("SeparateAnswerSheet", s);
			this.logger.debug("SeparateAnswerSheet configuration parameter set to "+s);
		}
		this.logger.warn("SeparateAnswerSheet configuration parameter "+s+" not recognized");
	}

	public void setAnswerSheetTitle(String s) {
		if (!s.equals("")) {
			this.param.replace("AnswerSheetTitle", s);
			this.logger.debug("AnswerSheetTitle configuration parameter set to "+s);
		}
		this.logger.warn("AnswerSheetTitle configuration parameter "+s+" not recognized");
	}

	public void setAnswerSheetPresentation(String s) {
		if (!s.equals("")) {
			this.param.replace("AnswerSheetPresentation", s);
			this.logger.debug("AnswerSheetPresentation configuration parameter set to "+s);
		}
		this.logger.warn("AnswerSheetPresentation configuration parameter "+s+" not recognized");
	}

	public void setSingleSided(String s) {
		if (s.equals("1")) {
			this.param.replace("SingleSided", s);
			this.logger.debug("SingleSided configuration parameter set to "+s);
		}
		this.logger.warn("SingleSided configuration parameter "+s+" not recognized");
	}

	private HashMap<String, String> hmDefaultScoringS = new HashMap<String, String>();
	private HashMap<String, String> hmDefaultScoringM = new HashMap<String, String>();

	public HashMap<String, String> getHmDefaultScoringS() {
		return this.hmDefaultScoringS;
	}

	public HashMap<String, String> getHmDefaultScoringM() {
		return this.hmDefaultScoringM;
	}

	// expl "DefaultScoringS", "e=0,v=0,p=-1,b=1,m=-1,d=0" de base
	public void setDefaultScoringS(String s) throws IncorrectParamException {
		boolean b = Pattern.matches(
				"e=[+-]?\\d+(\\.\\d+)?,v=[+-]?\\d+(\\.\\d+)?(,p=[+-]?\\d+(\\.\\d+)?,b=\\d+(\\.\\d+)?)?,m=[+-]?\\d+(\\.\\d+)?,d=\\d+(\\.\\d+)?",
				s);
		if (b) {
			this.param.replace("DefaultScoringS", s);
			this.logger.debug("DefaultScoring configuration parameter set to "+s);
			this.setHMDefaultScorigS();
		} else {
			this.param.replace("DefaultScoringS", "e=0,v=0,p=-1,b=1,m=-1,d=0");
			this.logger.debug("DefaultScoringS configuration parameter set to default value");
			throw new IncorrectParamException(
					s + ": n'a pas pu être utilisé pour defaultScoringS. Vérifiez le fichier source. \n");
		}
	}

	public void setHMDefaultScorigS() throws IncorrectParamException {
		// on split pour récupérer chaque élément qu'on place dans hmDefaultScoringS
		String[] arrOfStr = this.param.get("DefaultScoringS").split(",");
		for (String st : arrOfStr) {
			String p = st.split("=")[0];
			Double d = Double.parseDouble(st.split("=")[1]);
			if ((d >= (-1.0)) && (d <= 1.0)) {
				this.hmDefaultScoringS.put(st.split("=")[0], (st.split("=")[1]));
			} else {
				this.getHmDefaultScoringS().clear();
				throw new IncorrectParamException("\"" + d + "\" ne peut pas être utilisé pour le paramètre \"" + p
						+ "\". Choisissez une valeur entre [-1;1] pour le paramètre: " + p);
			}
		}
	}

	public void setDefaultScoringM(String s) throws IncorrectParamException {
		// obligatoire e,v,p possibilité d,b,m,mz,haut
		boolean b = Pattern.matches(
				"e=[-+]?(1|0)+(.0|.25|.5|.75)?0*,v=[-+]?(1|0)+(.0|.25|.5|.75)?0*(,p=[-+]?(1|0)+(.0|.25|.5|.75)?0*)?(,b=(1|0)+(.0|.25|.5|.75)?0*)?(,m=[-+]?(1|0)+(.0|.25|.5|.75)?0*)?(,d=(1|0)+(.0|.25|.5|.75)?0*)?(,mz=[-+]?(1|0)+(.0|.25|.5|.75)?0*,[-+]?0+(.0|.25|.5|.75)?0*)?(,haut=[-+]?(1|0)+(.0|.25|.5|.75)?0*,0+(.0|.25|.5|.75)?0*)?(,formula=\\(?\\(?(N|NB|NBC|NM|NMC)+\\)?\\)?[-+*/]{1}\\(?\\(?(N|NB|NBC|NM|NMC)+\\)?\\)?.*)?",
				s);
		if (b) {// possibilité de la regex de laisser passer des erreurs donc on revérifie.
			if (!s.contains("10") && !s.contains("100") && !s.contains("1.2") && !s.contains("1.5")
					&& !s.contains("1.7")) {
				this.param.replace("DefaultScoringM", s);
				this.setHMDefaultScoringM();
			} else {
				this.param.replace("DefaultScoringM", "e=0,v=0,p=-1,formula=(NBC/NB)-(NMC/NM)");
				throw new IncorrectParamException(s
						+ ": erreur= Un paramètre a une valeur non comprise entre [-1;1] et multiple de 0.25! \n Vérifiez le fichier source. \n");
			}
		} else {
			this.param.replace("DefaultScoringM", "e=0,v=0,p=-1,formula=(NBC/NB)-(NMC/NM)");
			this.logger.debug("DefaultScoringM configuration parameter set to "+s);
			this.getHmDefaultScoringM().clear();
			throw new IncorrectParamException(s
					+ ": n'a pas pu être utilisé pour defaultScoringM. Un paramètre inexistant a été utilisé ou sa valeur est impossible. \n Vérifiez le fichier source. \n");
			
		}
	}

	public void setHMDefaultScoringM() {
		// on split pour récupérer chaque élément qu'on place dans hmDefaultScoringM
		// de façon à avoir HashMap<param, valeur>
		String[] arrOfStr = this.param.get("DefaultScoringM").split(",");

		int cpt = 0;
		for (String st : arrOfStr) {
			if (cpt == 1) {
				this.hmDefaultScoringM.merge("mz", "," + st, String::concat);

			} else if (cpt == 2) {
				this.hmDefaultScoringM.merge("haut", "," + st, String::concat);
			} else {
				this.hmDefaultScoringM.put(st.split("=")[0], (st.split("=")[1]));
			}
			cpt = 0;

			if (st.contains("mz")) {
				cpt = 1;
			}
			if (st.contains("haut")) {
				cpt = 2;
			}
		}
	}

	public Double getBaremeSimple(String s) {
		return Double.parseDouble(this.getHmDefaultScoringS().get(s));
	}

	public Object getBaremeMultiple(String s) {
		if ((!s.equals("formula")) && (!s.equals("mz") && (!s.equals("haut")))) {
			return Double.parseDouble(this.getHmDefaultScoringM().get(s));
		} else {
			return this.getHmDefaultScoringM().get(s);
		}
	}

	public void setColumns(String s) {
		s = s.trim();
		if (this.isParsable(s)) {
			int n = Integer.parseInt(s);
			if ((n > 1) && (n < 5)) {
				this.param.replace("Columns", s);
				this.logger.debug("Columns configuration parameter set to "+s);
			}
			this.logger.warn("Columns configuration parameter "+s+" not recognized");
		}
		this.logger.warn("Columns configuration parameter "+s+" not recognized");
	}

	public void setQuestionBlocks(String s) {
		if (s.equals("0")) {
			this.param.replace("QuestionBlocks", s);
			this.logger.debug("QuestionsBlocks configuration parameter set to "+s);
		}
		this.logger.warn("QuestionsBlocks configuration parameter "+s+" not recognized");
	}

	public void setManualDuplex(String s) {
		if (s.equals("1")) {
			this.param.replace("ManualDuplex", s);
			this.logger.debug("ManualDuplex configuration parameter set to "+s);
		}
		this.logger.warn("ManualDuplex configuration parameter "+s+" not recognized");
	}

	public void setNumberOfCopiesToCreate(String s) {
		s = s.trim();
		if (!s.equals("") && this.isParsable(s)) {
			this.param.replace("NumberOfCopiesToCreate", s);
			this.logger.debug("NumberOfCopiesToCreate configuration parameter set to "+s);
		}
		this.logger.warn("NumberOfCopesToCreate configuration parameter "+s+" not recognized");
	}

	public void setAnswerSheetManualDuplex(String s) {
		if (!s.equals("")) {
			this.param.replace("AnswerSheetManualDuplex", s);
			this.logger.debug("AnswerSheetManualDuplex configuration parameter set to "+s);
		}
		this.logger.warn("AnswerSheetManualDuplex configuration parameter "+s+" not recognized");
	}

	public void setAnswerSheetDate(String s) {
		if (!s.equals("")) {
			this.param.replace("AnswerSheetDate", s);
			this.logger.debug("AnswerSheetDate configuration parameter set to "+s);
		}
		this.logger.warn("AnswerSheetDate configuration parameter "+s+" not recognized");
	}

	public void setDate(String s) {
		if (!s.equals("")) {
			this.param.replace("Date", s);
			this.logger.debug("Date configuration parameter set to "+s);
		}
		this.logger.warn("Date configuration parameter "+s+" not recognized");
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	/*
	 * Getters si jamais AnswerSheetTitle, AnswerSheetPresentation,
	 * AnswerSheetManualDuplex, AnswerSheetDate non précisé(s).
	 */
	public String getTitle() {
		return this.param.get("Title");
	}
	public String getSeparateAnswerSheet() {
		return this.param.get("SeparateAnswerSheet");
	}


	public String getPresentation() {
		return this.param.get("Presentation");
	}

	private String getManualDuplex() {
		return this.param.get("ManualDuplex");
	}

	private String getDate() {
		return this.param.get("Date");
	}

	public String getTypeOfCopies() {
		return this.param.get("TypeOfCopies");
	}

	public int getNumberOfCopiesToCreate() {
		return Integer.parseInt(this.param.get("NumberOfCopiesToCreate"));
	}
}
