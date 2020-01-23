package checker;

import java.util.ArrayList;
import java.util.List;

import config.Config;
import config.Formule;
import config.Question;
import config.QuestionBoite;
import config.Reponse;
import copies.Copie;

public class SubjectCorrector {

	public static double getNoteCopie(Copie sujet, Copie c, int bareme, Config cg) throws QuestionLikenessExeption {

		double note = 0;
		double coef = 0;
		List<Question> questionsCopie = c.getQuestions();
		List<Question> questionsSujet = sujet.getQuestions();
		List<PaireNoteCoef> paires = new ArrayList<>();

		for (int i = 0; i < questionsCopie.size(); i++) {
			if (!(questionsCopie.get(i).equalsIntitule(questionsSujet.get(i)))) {
				throw new QuestionLikenessExeption("La question copie:\n"+questionsCopie.get(i).getTitre()+"\ndiffère de la question sujet:\n"+questionsSujet.get(i).getTitre());
			}
			if (! (questionsSujet.get(i).isFrozenanswer())  ) {
				paires.add(getNoteQuestion(questionsSujet.get(i), questionsCopie.get(i), cg));
			}
		}

		for (PaireNoteCoef p : paires) {
			coef += p.getCoef();
			note += p.getNote();
		}
		if (note < 0) {
			note = 0;
		}

		return (note * bareme) / coef;

	}

	public static PaireNoteCoef getNoteQuestion(Question questionSujet, Question questionEtu, Config c) {

		double coefQuestion = questionSujet.getCoeff();
		List<Reponse> repSujet = questionSujet.getReponses();
		List<Reponse> repEtu = questionEtu.getReponses();

		double note = 0;
		int nb = 0;
		int nm = 0;
		int nbc = 0;
		int nmc = 0;

		for (Reponse r : questionSujet.getReponses()) {
			if (r.isJuste()) {
				nb++;
			} else {
				nm++;
			}
		}

		for (int i = 0; i < repSujet.size(); i++) {
			int result = checkPointAnswer(repSujet.get(i).isJuste(), repEtu.get(i).isJuste());
			if (result == -1) {
				nmc++;
			}
			if (result == 1) {
				nbc++;
			}
		}
		if (questionEtu instanceof QuestionBoite) {
			boolean n = ((QuestionBoite) questionEtu).caseNcoche();
			String s = ((QuestionBoite) questionEtu).caseCoche();
			note = Formule.calcFormuleO(n, s, questionEtu.getCoeff());
		}
		if (questionSujet.isMultiple()) {
			note = Formule.calcFormuleM(nbc, nb, nmc, nm, c.getHmDefaultScoringM());
		} else {
			note = Formule.calcFormuleS(nbc, nb, nmc, nm, c.getHmDefaultScoringS());
		}

		return new PaireNoteCoef(note, coefQuestion);
	}

	/**
	 * <p>
	 * <a>Si la reponse a la question == false and repStud == false rien</a></br>
	 * <a>Si la reponse a la question == true and repStud == false rien</a></br>
	 * <a>Si la reponse a la question == false and repStud == true -1</a></br>
	 * <a>Si la reponse a la question == true and repStud == true +1</a>
	 * </p>
	 *
	 * @param La reponse du sujet et la reponse correspondant de l etudiant
	 * @return La note a la question
	 */
	private static int checkPointAnswer(boolean aSubject, boolean aStudent) {
		if (aSubject && aStudent) {
			return 1;
		}
		if (!aSubject && aStudent) {
			return -1;
		}
		return 0;
	}

}
