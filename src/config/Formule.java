package config;

import java.util.HashMap;

import com.expression.parser.Parser;
import com.expression.parser.util.*;

public abstract class Formule {
	// classe abstraite pour calculer les notes à partir des formules de config
	// pas instanciable, utiliser les méthodes statiques

	private static String formulaM = ""; // formule par défaut pour questions multiples
	private static String formulaO = "(P+TQP+DP+DDP)*N";

	// méthode à appeler pour prendre les formules de config, si pas de formule
	// saisie sur la source alors rien se passe
	public static void createFormuleM(HashMap<String, String> scoringM) {
		if ((scoringM.containsKey("b") && scoringM.containsKey("m")) || scoringM.containsKey("mz")
				|| scoringM.containsKey("haut"))
			;
		else if (scoringM.containsKey("formula"))
			formulaM = scoringM.get("formula");
		else
			formulaM = "=(NBC/NB)-(NMC/NM)";
	}

	// Legende pour les paramètres des méthodes de calcul (voir aussi doc config)
	// NBC = nombre de bonne réponses cochées
	// NB = nombre de bonne réponses totales dans la question
	// NMC = nombre de mauvaises réponses cochées
	// NM = nombre de mauvaises réponses totales dans la question

	// méthode à appeler pour calculer les points d'une question simple (une seule
	// bonne réponse) à partir de la formule
	public static double calcFormuleS(double NBC, double NB, double NMC, double NM, HashMap<String, String> m) {
		Double b = Double.parseDouble((String) m.get("b"));
		Double d = Double.parseDouble((String) m.get("d"));
		Double p = Double.parseDouble((String) m.get("p"));
		Double mau = Double.parseDouble((String) m.get("m"));

		if (NBC == 0 && NMC == 0) {
			return Double.parseDouble((String) m.get("v"));
		}
		if (NBC + NMC > 1) {
			return Double.parseDouble((String) m.get("e"));
		}

		if ((NBC / NB) == 1 && NMC == 0) {
			if (b + d < p) {
				return p;
			} else {
				if (d + b > 1) {
					return 1;
				} else {
					return b + d;
				}
			}
		} else if (mau + d < p)
			return p;
		else
			return mau + d;
	}

	// méthode à appeler pour calculer les points d'une questions multiple à partir
	// de la formule
	public static double calcFormuleM(double NBC, double NB, double NMC, double NM, HashMap<String, String> m) {
		if (!(m.containsKey("formula"))) {
			if (NBC == 0 && NMC == 0) {
				return Double.parseDouble((String) m.get("v"));
			}

			if (m.containsKey("mz")) {
				String[] arrOfMz = ((String) m.get("mz")).split(",");
				Double v1 = Double.parseDouble(arrOfMz[0]);
				Double v2 = Double.parseDouble(arrOfMz[1]);
				if (NBC / NB == 1 && NMC == 0) {
					if (m.containsKey("d")) {
						Double d = Double.parseDouble((String) m.get("d"));
						if ((d + v1) < 1) {
							return d + v1;
						} else {
							return 1;
						}
					} else {
						return v1;
					}
				} else {
					return v2;
				}
			}
			if (m.containsKey("haut")) {
				String[] arrOfHaut = ((String) m.get("haut")).split(",");
				Double v1 = Double.parseDouble(arrOfHaut[0]);
				Double v2 = Double.parseDouble(arrOfHaut[1]);
				if ((NBC / NB == 1) && (NMC == 0)) {
					if (m.containsKey("d")) {
						Double d = Double.parseDouble((String) m.get("d"));
						if ((d + v1) < 1) {
							return d + v1;
						} else {
							return 1;
						}
					} else {
						return v1;
					}
				} else {
					if (v1 - v2 * NMC < -1) {
						return -1;
					} else
						return v1 - v2 * NMC;
				}
			}
			if (m.containsKey("b") && m.containsKey("m")) {
				if (NBC / NB == 1 && NMC == 0) {
					if (m.containsKey("d")) {
						Double d = Double.parseDouble((String) m.get("d"));
						Double b = Double.parseDouble((String) m.get("b"));
						if ((d + b) < 1) {
							return d + b;
						} else
							return 1;
					} else
						return Double.parseDouble((String) m.get("b"));
				} else {
					return Double.parseDouble((String) m.get("m"));
				}
			}
			if (NBC / NB == 1 && NMC > 0) {
				return Double.parseDouble((String) m.get("e"));
			}
		}

		// else
		createFormuleM(m);
		formulaM = formulaM.replace("NBC", "x");
		formulaM = formulaM.replace("NB", "y");
		formulaM = formulaM.replace("NMC", "z");
		formulaM = formulaM.replace("NM", "w");

		Point nbc = new Point("x", NBC);
		Point nb = new Point("y", NB);
		Point nmc = new Point("z", NMC);
		Point nm = new Point("w", NM);

		ParserResult resultat = Parser.eval(formulaM, nbc, nb, nmc, nm);
		Double r = resultat.getValue();
		double p;
		if (m.containsKey("p")) {
			p = Double.parseDouble((String) m.get("p"));
		} else {
			p = 0;
		}
		// si dfM contient "d" (décaage) on vérif que c'est pas plus grand que 1 et on
		// vérif que c'est moins que la
		// valeur plancher (sinon on met la valeur plancher).
		if (m.containsKey("d")) {
			Double d = Double.parseDouble((String) m.get("d"));
			if ((d + r) < 1) {
				if ((r + d < p))
					r = p;
				else
					r += d;
			} else
				r = 1.0;
		}
		return Math.round(r * 100.0) / 100.0;
	}

	public static double calcFormuleO(boolean N, String s, int coeff) {
		formulaO = formulaO.replace("DDP", "a");
		formulaO = formulaO.replace("TQP", "b");
		formulaO = formulaO.replace("DP", "y");
		formulaO = formulaO.replace("P", "x");
		formulaO = formulaO.replace("N", "n");
		formulaO = formulaO.replace("O", "v");

		Point p = new Point("x", 0.0);
		Point tqp = new Point("b", 0.0);
		Point dp = new Point("y", 0.0);
		Point ddp = new Point("a", 0.0);
		Point n = new Point("n", 1.0);

		switch (s.toUpperCase()) {
		case "P":
			p = new Point("x", 1.0);
			break;
		case "DP":
			dp = new Point("y", 0.5);
			break;
		case "DDP":
			ddp = new Point("a", 0.25);
			break;
		case "TQP":
			tqp = new Point("b", 0.75);
			break;
		}

		if (N == true) {
			n = new Point("n", -1.0);
		}

		ParserResult resultat = Parser.eval(formulaO, p, tqp, dp, ddp, n);

		return Math.round(resultat.getValue() * coeff * 100.0) / 100.0;
	}

	public static String getFormulaM() {
		return formulaM;
	}

	public static String getFormulaO() {
		return formulaO;
	}
}
