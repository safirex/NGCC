package config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestConfig {
	private String sourcePath = "./config/shortSource.txt";
	private Config c;
	
	@BeforeEach
	public void setUp() {
		this.c = Config.getInstance(sourcePath);
		this.c.readConfig();
	}

	@Test
	void testMakeQuestion() {
		Question q = new Question("Je suis le titre", false, 1, true);
		q = c.makeQuestion(
				"* Dans M.A.S.K. qui sont les pilotes ou copilote de Rhino (un camion tracteur Kenworth w900)?"); // Question
																													// simple
		assertEquals("Dans M.A.S.K. qui sont les pilotes ou copilote de Rhino (un camion tracteur Kenworth w900)?",
				q.getTitre());
		q = c.makeQuestion(
				"*{frozen} Dans M.A.S.K. qui sont les pilotes ou copilote de Rhino (un camion tracteur Kenworth w900)?"); // test
																															// titre
																															// pour
																															// frozen
		assertEquals("Dans M.A.S.K. qui sont les pilotes ou copilote de Rhino (un camion tracteur Kenworth w900)?",
				q.getTitre());
		Question q3 = new Question("Quelles sont les bonnes réponses", false, 1, false);
		q3 = c.makeQuestion(
				"*{lines=1} Cette jeune fille vient d'emménager à Sunnydale avec sa mère et rencontre son nouvel observateur. Quel est le nom de ce dernier ?"); // test
																																									// question
																																									// boite
		assertEquals(
				"Cette jeune fille vient d'emménager à Sunnydale avec sa mère et rencontre son nouvel observateur. Quel est le nom de ce dernier ?",
				q3.getTitre());

		Question q2 = new Question(
				"Selon la serie diffusée en 1991 sur TF1, où le petit Nicolas doit il travailler et s'appliquer ?",
				false, 1, false);
		assertEquals("Selon la serie diffusée en 1991 sur TF1, où le petit Nicolas doit il travailler et s'appliquer ?",
				q2.getTitre());

		q2 = c.makeQuestion(
				"** Dans M.A.S.K. qui sont les pilotes ou copilote de Rhino (un camion tracteur Kenworth w900)?"); // test
																													// question
																													// multiple
		assertEquals("Dans M.A.S.K. qui sont les pilotes ou copilote de Rhino (un camion tracteur Kenworth w900)?",
				q2.getTitre());
	}

	/*
	 * PARTIE DefaultScoriNG S et M ==>donne le barème des questions simples et
	 * multiples.
	 */
	
	@Test
	void testGetBaremeSimple() throws IncorrectParamException {
		c.setDefaultScoringS("e=-0.25,v=-1,p=-0.5,b=1,m=0.00,d=0.75");
		assertEquals("e=-0.25,v=-1,p=-0.5,b=1,m=0.00,d=0.75", c.getParam().get("DefaultScoringS"));
		assertEquals(-0.25, c.getBaremeSimple("e"));
		assertEquals(-1.0, c.getBaremeSimple("v"));
		assertEquals(-0.5, c.getBaremeSimple("p"));
		assertEquals(1.0, c.getBaremeSimple("b"));
		assertEquals(0.00, c.getBaremeSimple("m"));
		assertEquals(0.75, c.getBaremeSimple("d"));
	}

	@Test
	void testErrDefaultScoringS() throws IncorrectParamException {
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringS("e=-0.25,v=titi,p=-0.5,b=1,m=0,d=toto");});
		assertNotEquals("e=-0.25,v=titi,p=-0.5,b=1,m=0,d=toto", c.getParam().get("DefaultScoringS"));
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringS("e=7.0,v=-1,p=-0.5,b=1,m=-0.25,d=0.75");});
	}

	@Test
	void testDefaultScoringM() throws IncorrectParamException {
		//e,v,p obligatoire
		c.setDefaultScoringM("e=-0.25,v=-0.75,p=-1,formula=(NBC/NB)-(NMC/NM)");
		assertEquals("e=-0.25,v=-0.75,p=-1,formula=(NBC/NB)-(NMC/NM)", c.getParam().get("DefaultScoringM"));
		//mz
		c.setDefaultScoringM("e=-0.25,v=0,p=-0.5,mz=0.75,0");
		assertEquals("e=-0.25,v=0,p=-0.5,mz=0.75,0", c.getParam().get("DefaultScoringM"));
		// d,mz
		c.setDefaultScoringM("e=0,v=-0.5,p=-0.75,d=0.25,mz=0.75,0");
		assertEquals("e=0,v=-0.5,p=-0.75,d=0.25,mz=0.75,0", c.getParam().get("DefaultScoringM"));
		// d,b,m
		c.setDefaultScoringM("e=-0.5,v=-0.25,p=-0.5,b=0.5,m=-1,d=0.25");
		assertEquals("e=-0.5,v=-0.25,p=-0.5,b=0.5,m=-1,d=0.25", c.getParam().get("DefaultScoringM"));
		//haut
		c.setDefaultScoringM("e=-0.75,v=0,p=-0.5,d=1,haut=1,0.25");
		assertEquals("e=-0.75,v=0,p=-0.5,d=1,haut=1,0.25", c.getParam().get("DefaultScoringM"));
		//formula
		c.setDefaultScoringM("e=-0.5,v=-0.25,p=0,d=0,haut=1,0.25,formula=(NBC/NB)-(NMC/NM)");
		assertEquals("e=-0.5,v=-0.25,p=0,d=0,haut=1,0.25,formula=(NBC/NB)-(NMC/NM)", c.getParam().get("DefaultScoringM"));
	}

	@Test
	void testErrDefaultScoringM() throws IncorrectParamException {
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringM("e=-0.25,v=toi,p=-1,formula=(NBC/NB)-(NMC/NM)");});
		assertNotEquals("e=-0.25,v=toi,p=-1,formula=(NBC/NB)-(NMC/NM)", c.getParam().get("DefaultScoringM"));
		// t = faux paramètre
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringM("e=-0.25,v=-1,t=-1.0,p=-1,(NBC/NB)-(NMC/NM)");});
		assertNotEquals("e=-0.25,v=-1,t=-1.0,p=-1,formula=(NBC/NB)-(NMC/NM)", c.getParam().get("DefaultScoringM"));
		//formula fausse
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringM("e=-0.25,v=-1,p=-1.0,formula=NGCC/NB-NMC/NM");});
		assertNotEquals("e=-0.25,v=-1,p=-1.0,formula=NGCC/NB-NMC/NM", c.getParam().get("DefaultScoringM"));
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringM("e=-0.25,v=1.0,p=-15,formula=(NBC/NB)-(NMC/NM)");});
		assertNotEquals("e=-0.25,v=1.0,p=-100,formula=(NBC/NB)-(NMC/NM)", c.getParam().get("DefaultScoringM"));
		//p faux != [-1;1]
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringM("e=-0.25,v=1.0,p=2,formula=(NBC/NB)-(NMC/NM)");});
		//e faux != [-1;1]
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringM("e=-1.25,v=1.0,p=0.0,mz=0.75,1,formula=(NBC/NB)-(NMC/NM)");});
		//haut faux
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringM("e=-0.25,v=1.0,p=0.75,haut=1,-0.25,formula=(NBC/NB)-(NMC/NM)");});
		//haut faux v2 = v1
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringM("e=-0.25,v=1.0,p=0.0,haut=1,1,formula=(NBC/NB)-(NMC/NM)");});
		//mz aux, v2 > v1
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringM("e=-0.25,v=1.0,p=0.0,mz=0.75,1,formula=(NBC/NB)-(NMC/NM)");});
		// =10 (auparavant possible maintenant pris en compte)
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringM("e=-0.25,v=1.0,p=0.0,mz=10,0,formula=(NBC/NB)-(NMC/NM)");});
		// =100 (auparavant possible maintenant pris en compte)
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringM("e=-0.25,v=1.0,p=0.0,mz=100,0,formula=(NBC/NB)-(NMC/NM)");});
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringM("e=-0.25,v=1.0,p=0.0,mz=-100,0,formula=(NBC/NB)-(NMC/NM)");});
	}
	
	@Test
	void testGetbaremeMultiple() throws IncorrectParamException {
		c.setDefaultScoringM("e=0.25,v=-0.75,p=-1,b=1,m=-0.25,d=1,formula=(NBC/NB)-(NMC/NM)");
		assertEquals(0.25, c.getBaremeMultiple("e"));
		assertEquals(-0.75, c.getBaremeMultiple("v"));
		assertEquals(-1.0, c.getBaremeMultiple("p"));
		assertEquals(1.0, c.getBaremeMultiple("b"));
		assertEquals(-0.25, c.getBaremeMultiple("m"));
		assertEquals(1.0, c.getBaremeMultiple("d"));
		assertEquals("(NBC/NB)-(NMC/NM)", c.getBaremeMultiple("formula"));
		c.setDefaultScoringM("e=0.25,v=-0.75,p=-1,d=1,mz=1,0.25,formula=(NBC/NB)-(NMC/NM)");
		assertEquals("1,0.25", c.getBaremeMultiple("mz"));
		c.setDefaultScoringM("e=0.25,v=-0.75,p=-1,d=1,haut=1,0.25,formula=(NBC/NB)-(NMC/NM)");
		assertEquals("1,0.25", c.getBaremeMultiple("haut"));
	}
	
	@Test
	void testEmptyDs() throws IncorrectParamException {
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringS("e=-0.25,v=-1,p=-0.5,b=1,m=,d=0.75");});
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringM("e=0,v=-0.5,p=,formula=(NBC/NB)");});
	}

	@Test
	void testFormulaDSM() throws IncorrectParamException {
		// test= N NB NBC NM NMC + - * / ( )
		c.setDefaultScoringM("e=0,v=-0.5,p=-1,formula=(NBC-NB)");
		assertEquals("(NBC-NB)", c.getBaremeMultiple("formula"));
		c.setDefaultScoringM("e=0,v=-0.5,p=-1,formula=NB+NBC");
		assertEquals("NB+NBC", c.getBaremeMultiple("formula"));

		c.setDefaultScoringM("e=0,v=-0.5,p=-1,formula=(NBC/NB)-(NMC/NM)");
		assertEquals("(NBC/NB)-(NMC/NM)", c.getBaremeMultiple("formula"));

		c.setDefaultScoringM("e=0,v=-0.5,p=-1,formula=N-NBC+NM/NB");
		assertEquals("N-NBC+NM/NB", c.getBaremeMultiple("formula"));

		c.setDefaultScoringM("e=0,v=-0.5,p=-1,formula=(N-NBC+NM)/NB");
		assertEquals("(N-NBC+NM)/NB", c.getBaremeMultiple("formula"));

		c.setDefaultScoringM("e=0,v=-0.5,p=-1,formula=N+NB+NBC-NM-NMC*(N/NBC)");
		assertEquals("N+NB+NBC-NM-NMC*(N/NBC)", c.getBaremeMultiple("formula"));

		c.setDefaultScoringM("e=0,v=-0.5,p=-1,formula=(NB)-(NBC)");
		assertEquals("(NB)-(NBC)", c.getBaremeMultiple("formula"));

		c.setDefaultScoringM("e=0,v=-0.5,p=-1,formula=((NB)-(NBC))");
		assertEquals("((NB)-(NBC))", c.getBaremeMultiple("formula"));

		c.setDefaultScoringM("e=0,v=-0.5,p=-1,formula=((NB)-(NBC))*N");
		assertEquals("((NB)-(NBC))*N", c.getBaremeMultiple("formula"));

		c.setDefaultScoringM("e=0,v=-0.5,p=-1,formula=((NB))-((NBC))");
		assertEquals("((NB))-((NBC))", c.getBaremeMultiple("formula"));

		c.setDefaultScoringM("e=0,v=-0.5,p=-1,formula=((NM))-((NBC*N/(NBC)))");
		assertEquals("((NM))-((NBC*N/(NBC)))", c.getBaremeMultiple("formula"));

		c.setDefaultScoringM("e=0,v=-0.5,p=-1,formula=((NM*NBC-(NB)))-((NBC*N/(NBC)))");
		assertEquals("((NM*NBC-(NB)))-((NBC*N/(NBC)))", c.getBaremeMultiple("formula"));
	}

	@Test
	void testErrFormula() throws IncorrectParamException {
		// correct N NB NBC NM NMC + - * / ( ) sinon erreur
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringM("e=0,v=-0.25,p=-1,formula=NB+/NBC");});
		assertNotEquals("NB+/NBC", c.getBaremeMultiple("formula"));
		assertThrows(IncorrectParamException.class, () -> {c.setDefaultScoringM("e=0,v=-0.25,p=-1,formula=NB-ID");});	
		assertNotEquals("NB-ID", c.getBaremeMultiple("formula"));
	}
	
	@Test
	/*
	 * test des setters de paramètres. et (si disponible) test si valeurs par défaut
	 * sont bien mise en place.
	 */
	void testParam() {

		c.setPaperSize("A19");
		assertNotEquals("A19", c.getParam().get("PaperSize"));
		c.setPaperSize("A4");
		assertEquals("A4", c.getParam().get("PaperSize"));
		c.setTitle("un titre d'exam");
		assertEquals("un titre d'exam", c.getParam().get("Title"));
		c.setPresentation("une expliquation d'exam");
		assertEquals("une expliquation d'exam", c.getParam().get("Presentation"));
		assertEquals("1", c.getParam().get("ShuffleQuestions"));
		c.setShuffleQuestions("0");
		assertEquals("0", c.getParam().get("ShuffleQuestions"));
		assertEquals("1", c.getParam().get("ShuffleAnswers"));
		c.setShuffleAnswers("0");
		assertEquals("0", c.getParam().get("ShuffleAnswers"));
		c.setCode("17");
		assertNotEquals("17", c.getParam().get("Code"));
		c.setCode("8");
		assertEquals("8", c.getParam().get("Code"));
		c.setMarkFormat("20e");
		assertNotEquals("20e", c.getParam().get("MarkFormat"));
		assertEquals("Nom et Prénom", c.getParam().get("NameField"));
		c.setNameField("Mettez votre nom et prénom");
		assertEquals("Mettez votre nom et prénom", c.getParam().get("NameField"));
		assertNotEquals("", c.getParam().get("StudentIdField"));
		c.setStudentIdField("Remplissez votre nom et votre numéro");
		assertEquals("Remplissez votre nom et votre numéro", c.getParam().get("StudentIdField"));
		assertEquals("Ecrivez votre numéro étudiant ici", c.getParam().get("MarkField"));
		c.setMarkField("Ecrire son numéro étudiant ci-contre");
		assertEquals("Ecrire son numéro étudiant ci-contre", c.getParam().get("MarkField"));
		c.setSeparateAnswerSheet("0");
		assertEquals("0", c.getParam().get("SeparateAnswerSheet"));
		c.setAnswerSheetTitle("un titre");
		assertEquals("un titre", c.getParam().get("AnswerSheetTitle"));
		c.setAnswerSheetPresentation("un answer sheet presentation");
		assertEquals("un answer sheet presentation", c.getParam().get("AnswerSheetPresentation"));
		assertEquals("0", c.getParam().get("SingleSided"));
		c.setSingleSided("55");
		assertEquals("0", c.getParam().get("SingleSided"));
		c.setSingleSided("1");
		assertEquals("1", c.getParam().get("SingleSided"));
		assertEquals("1", c.getParam().get("Columns"));
		c.setColumns("10");
		assertEquals("1", c.getParam().get("Columns"));
		c.setColumns("3");
		assertEquals("3", c.getParam().get("Columns"));
		assertEquals("1", c.getParam().get("QuestionBlocks"));
		c.setQuestionBlocks("0");
		assertEquals("0", c.getParam().get("QuestionBlocks"));
		c.setQuestionBlocks("5");
		assertEquals("0", c.getParam().get("QuestionBlocks"));
		assertEquals("0", c.getParam().get("ManualDuplex"));
		c.setManualDuplex("1");
		assertEquals("1", c.getParam().get("ManualDuplex"));
		c.setDate("Jeudi 17 juin 2017");
		assertEquals("Jeudi 17 juin 2017", c.getParam().get("Date"));
		c.setAnswerSheetDate("Jeudi 28 septembre 2022");
		assertEquals("Jeudi 28 septembre 2022", c.getParam().get("AnswerSheetDate"));
		c.setNumberOfCopiesToCreate("20");
		assertEquals("20", c.getParam().get("NumberOfCopiesToCreate"));
		c.setNumberOfCopiesToCreate("20.5");
		assertNotEquals("20.5", c.getParam().get("NumberOfCopiesToCreate"));
		c.setNumberOfCopiesToCreate("-10");
		assertNotEquals("-20", c.getParam().get("NumberOfCopiesToCreate"));
	}

	@Test
	void testMarkFormat() {
		c.setMarkFormat("20/4");
		assertEquals("20/4", c.getParam().get("MarkFormat"));
		c.setMarkFormat("100");
		assertEquals("100", c.getParam().get("MarkFormat"));
		c.setMarkFormat("10/2");
		assertNotEquals("10/2", c.getParam().get("MarkFormat"));
		c.setMarkFormat("10tt");
		assertNotEquals("10tt", c.getParam().get("MarkFormat"));
	}

	@Test
	void testClearString() {
		String string = "  \ntest \r testù   ";
		assertEquals("test  testù", Config.clearString(string));
		String string2 = "\n\r";
		assertEquals("", Config.clearString(string2));
		assertTrue(Config.clearString(string2).isEmpty());
	}

	/* TEST MAKE GROUP */
	@Test
	void testMakeGroup() {
		Groupe g1 = c.makeGroupe("n=5");
		Groupe g2 = c.makeGroupe("Cette ligne n'a pas d'égale 5");
		assertEquals(5, g1.getNbQtChoisie());
		assertNotEquals(5, g2.getNbQtChoisie());
		/*-----------------*/
		/*frozen ou mixed? */
		String s= "((n=2 frozen";
		String s1= "((n=21 frozen";
		assertEquals("frozen",s.substring(6,12));
		assertEquals("frozen",s1.substring(7,13));	
		assertTrue(s.contains("frozen"));
		assertTrue(s1.contains("frozen"));
	}
}
