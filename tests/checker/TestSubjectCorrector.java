package checker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import config.Config;
import config.Formule;
import config.IncorrectParamException;
import config.Question;
import copies.Copie;

public class TestSubjectCorrector {
	
	@Test
	public void testGetNoteQuestionSimple() throws IncorrectParamException {
		Config c = Config.getInstance("./config/shortSource.txt");
		c.setDefaultScoringS("e=0,v=0,p=-1,b=0.75,m=-1,d=0");

		// Question simple coeff 1 non frozen
		Question q1Sujet = new Question("Quel est le type de Pikachu", false, 1, false);
		q1Sujet.addReponse("- Glace");
		q1Sujet.addReponse("- Plante");
		q1Sujet.addReponse("+ Electrique");
		q1Sujet.addReponse("- Roche");
		
		Question q1Etudiant = new Question("Quel est le type de Pikachu", false, 1, false);
		q1Etudiant.addReponse("- Glace");
		q1Etudiant.addReponse("- Plante");
		q1Etudiant.addReponse("+ Electrique");
		q1Etudiant.addReponse("- Roche");
		assertTrue(q1Sujet.equalsIntitule(q1Etudiant));

		// etu 1 a bonne réponse, donc doit avoir 0.75 car coeff 1
		PaireNoteCoef p = SubjectCorrector.getNoteQuestion(q1Sujet, q1Etudiant, c);
		PaireNoteCoef pnc = new PaireNoteCoef(0.75, 1);
		assertEquals(p.getCoef(),pnc.getCoef());
		assertEquals(p.getNote(),pnc.getNote());
		
		//etudiant a faux
		Question q1Etu2 = new Question("Quel est le type de Pikachu", false, 1, false);
		q1Etu2.addReponse("- Glace");
		q1Etu2.addReponse("+ Plante");
		q1Etu2.addReponse("- Electrique");
		q1Etu2.addReponse("- Roche");
		
		PaireNoteCoef p2=SubjectCorrector.getNoteQuestion(q1Sujet, q1Etu2, c);
		PaireNoteCoef pnc2 = new PaireNoteCoef(-1, 1);
		assertEquals(p2.getNote(),pnc2.getNote());
	}

	@Test
	public void testGetNoteQuestionMultiple() throws IncorrectParamException, QuestionLikenessExeption {
		Config c = Config.getInstance("./config/shortSource.txt");
		c.setDefaultScoringM("e=-0.5,v=-0.25,formula=(NBC/NB)-(NMC/NM)");
		Formule.createFormuleM(c.getHmDefaultScoringM());

		/// Question à choix multiple avec coeff 4
		Question q2Sujet = new Question("Quel(s) type(s) a le  pokemon Cizayox", true, 4, false);
		q2Sujet.addReponse("- Glace");
		q2Sujet.addReponse("- Plante");
		q2Sujet.addReponse("- Electrique");
		q2Sujet.addReponse("- Roche");
		q2Sujet.addReponse("+ Insecte");
		q2Sujet.addReponse("+ Acier");

		Question q2Etu1 = new Question("Quel(s) type(s) a le  pokemon Cizayox", true, 4, false);
		q2Etu1.addReponse("- Glace");
		q2Etu1.addReponse("- Plante");
		q2Etu1.addReponse("- Electrique");
		q2Etu1.addReponse("- Roche");
		q2Etu1.addReponse("+ Insecte");
		q2Etu1.addReponse("+ Acier");

		PaireNoteCoef p = SubjectCorrector.getNoteQuestion(q2Sujet, q2Etu1, c);
		//étudiant a tout juste
		PaireNoteCoef pnc2 = new PaireNoteCoef(1, 4);
		assertEquals(p.getCoef(),pnc2.getCoef());
		assertEquals(p.getNote(),pnc2.getNote());
		
		//étudiant n'a pas tout juste
		Question q2Etu2 = new Question("Quel(s) type(s) a le  pokemon Cizayox", true, 4, false);
		q2Etu2.addReponse("- Glace");
		q2Etu2.addReponse("- Plante");
		q2Etu2.addReponse("- Electrique");
		q2Etu2.addReponse("- Roche");
		q2Etu2.addReponse("- Insecte");
		q2Etu2.addReponse("+ Acier");

		PaireNoteCoef p2 = SubjectCorrector.getNoteQuestion(q2Sujet, q2Etu2, c);
		PaireNoteCoef pnc3 = new PaireNoteCoef(0.5, 4);
		assertEquals(p2.getNote(),pnc3.getNote());
	}
	
	@Test
	public void testGetNoteCopie() throws IncorrectParamException, QuestionLikenessExeption {
		Config c = Config.getInstance("./config/shortSource.txt");
		c.setDefaultScoringM("e=-0.5,v=-0.25,formula=(NBC/NB)-(NMC/NM)");
		Formule.createFormuleM(c.getHmDefaultScoringM());

		Question q1Sujet = new Question("FROZENANSWER: Qui sont les 2 meilleurs dresseurs pour vous", true, 1, true);
		q1Sujet.addReponse("+ Red");
		q1Sujet.addReponse("+ Pierre");
		q1Sujet.addReponse("- Sacha");
		q1Sujet.addReponse("- Ondine");
	
		Question q1Etu1 = new Question("FROZENANSWER: Qui sont les 2 meilleurs dresseurs pour vous", true, 1, true);
		q1Etu1.addReponse("+ Red");
		q1Etu1.addReponse("+ Pierre");
		q1Etu1.addReponse("- Sacha");
		q1Etu1.addReponse("- Ondine");
		
		Question q1Etu2 = new Question("FROZENANSWER: Qui sont les 2 meilleurs dresseurs pour vous", true, 1, true);
		q1Etu1.addReponse("- Red");
		q1Etu1.addReponse("- Pierre");
		q1Etu1.addReponse("+ Sacha");
		q1Etu1.addReponse("+ Ondine");
		
		Question q2Sujet = new Question("Quel jeu PKMN a existé?", true, 2, false);
		q2Sujet.addReponse("+ Rubis");
		q2Sujet.addReponse("+ Emeraude");
		q2Sujet.addReponse("+ Saphir");
		q2Sujet.addReponse("- Jade");
		
		Question q2Etu1 = new Question("Quel jeu PKMN a existé?", true, 2, false);
		q2Etu1.addReponse("+ Rubis");
		q2Etu1.addReponse("+ Emeraude");
		q2Etu1.addReponse("+ Saphir");
		q2Etu1.addReponse("- Jade");
		
		Question q2Etu2 = new Question("Quel jeu PKMN a existé?", true, 2, false);
		q2Etu2.addReponse("+ Rubis");
		q2Etu2.addReponse("+ Emeraude");
		q2Etu2.addReponse("+ Saphir");
		q2Etu2.addReponse("+ Jade");
		
		List<Question> etu1 =new ArrayList<>();
		List<Question> etu2 =new ArrayList<>();
		List<Question> qtsSujet =new ArrayList<>();
		qtsSujet.add(q1Sujet);
		qtsSujet.add(q2Sujet);
		etu1.add(q1Etu1); //frozen
		etu1.add(q2Etu1);
		etu2.add(q1Etu2);//frozen
		etu2.add(q2Etu2);
		Copie copieEtu1 = new Copie(etu1,"A4");
		Copie copieEtu2 = new Copie(etu2,"A4");
		Copie copieSujet = new Copie(qtsSujet,"A4");
		//RECAP: ETUDIANT1 (une qt frozen)
		//note = 1, coeff =2, bareme = 20 DONC 1*20/2 =10
		assertEquals(10.0,SubjectCorrector.getNoteCopie(copieSujet, copieEtu1, 20, c));
		//RECAP:ETUDIANT2 a une erreur, note = 0, coeff =2, bareme = 20 DONC 0
		assertEquals(0,SubjectCorrector.getNoteCopie(copieSujet, copieEtu2, 20, c));
	}
	
	@Test
	public void testException() throws IncorrectParamException, QuestionLikenessExeption {
		Config c = Config.getInstance("./config/shortSource.txt");
		Question q1Sujet = new Question("Quel est le type de Pikachu", false, 1, false);
		q1Sujet.addReponse("+ Electrique");
		q1Sujet.addReponse("- Roche");	
		Question q1Etu1 = new Question("Quel est le type de Raichu", false, 1, false);
		q1Etu1.addReponse("+ Electrique");
		q1Etu1.addReponse("- Roche");
		//^------ les questions ne sont pas les mêmes
		List<Question> etu1 =new ArrayList<>();
		List<Question> qtsSujet =new ArrayList<>();
		qtsSujet.add(q1Sujet);
		etu1.add(q1Etu1); 
		Copie copieEtu1 = new Copie(etu1,"A4");
		Copie copieSujet = new Copie(qtsSujet,"A4");
		assertThrows(QuestionLikenessExeption.class, () -> {SubjectCorrector.getNoteCopie(copieSujet, copieEtu1, 20, c);});
	}
}