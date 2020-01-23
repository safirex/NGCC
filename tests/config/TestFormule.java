package config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.Test;

public class TestFormule {
	private String sourcePath = "./config/shortSource.txt";

	@Test
	public void testCreateFormuleM() throws IncorrectParamException {
		Config c = Config.getInstance("");
		c.setDefaultScoringM("e=0,v=0,p=-1");
		Formule.createFormuleM(c.getHmDefaultScoringM());
		assertEquals("(NBC/NB)-(NMC/NM)", Formule.getFormulaM());

		c.setDefaultScoringM("e=0,v=0,p=-1,formula=(NBC/NB)");
		Formule.createFormuleM(c.getHmDefaultScoringM());
		assertEquals("(NBC/NB)", Formule.getFormulaM());
	}

	// RAPPELS: Formule.calcFormuleS/M(NBC, NB, NMC, NM, HashMap)
	// NBC = nombre de bonnes réponses cochées.
	// NB = nombre de bonne réponse à la question (ici tjs =1)
	// NMC = nombre de mauvaise réponse cochées.
	// NM = nombre de mauvaise réponse à la question. NM >=1

	@Test
	public void testCalcFormS() throws IncorrectParamException {
		Config c = Config.getInstance(sourcePath);
		c.setDefaultScoringS("e=0,v=0,p=-1,b=1,m=-1,d=0");
		// test if (NBC == 0 && NMC == 0)
		assertEquals(c.getBaremeSimple("v"), Formule.calcFormuleS(0, 1, 0, 3, c.getHmDefaultScoringS()));
		// test if (NBC + NMC > 1) (2 cases cochées)
		assertEquals(c.getBaremeSimple("e"), Formule.calcFormuleS(1, 1, 1, 3, c.getHmDefaultScoringS()));
		assertEquals(c.getBaremeSimple("e"), Formule.calcFormuleS(2, 1, 0, 3, c.getHmDefaultScoringS()));
		// test if (NBC/NB) =1 (bonne réponse)
		assertEquals(c.getBaremeSimple("b"), Formule.calcFormuleS(1, 1, 0, 3, c.getHmDefaultScoringS()));
		// test valeur m
		assertEquals(c.getBaremeSimple("m"), Formule.calcFormuleS(0, 1, 1, 3, c.getHmDefaultScoringS()));
		// test valeur plancher
		c.setDefaultScoringS("e=0,v=0,p=0,b=1,m=-1,d=0");
		assertEquals(c.getBaremeSimple("p"), Formule.calcFormuleS(0, 1, 1, 3, c.getHmDefaultScoringS()));
		// test valeur d
		c.setDefaultScoringS("e=0,v=0,p=-1,b=0.75,m=-1,d=0.25");
		assertEquals(1, Formule.calcFormuleS(1, 1, 0, 3, c.getHmDefaultScoringS()));
	}

	@Test
	public void testCalcFormM() throws IncorrectParamException {
		Config c = Config.getInstance(sourcePath);
		c.getHmDefaultScoringM().clear();
		c.setDefaultScoringM("e=-0.25,v=-0.75,p=-1");
		assertEquals(c.getBaremeMultiple("v"), Formule.calcFormuleM(0, 2, 0, 2, c.getHmDefaultScoringM()));

		// test haut
		c.setDefaultScoringM("e=-0.75,v=0,p=-0.5,haut=0.5,0.25");
		assertEquals(0.5, Formule.calcFormuleM(2, 2, 0, 2, c.getHmDefaultScoringM()));
		assertEquals(-0.25, Formule.calcFormuleM(1, 2, 3, 3, c.getHmDefaultScoringM()));
		// test haut ET d
		c.setDefaultScoringM("e=-0.75,v=0,p=-0.5,d=0.25,haut=0.5,0.25");
		assertEquals(0.75, Formule.calcFormuleM(2, 2, 0, 2, c.getHmDefaultScoringM()));
		// test avec d + v1 >1
		c.setDefaultScoringM("e=-0.75,v=0,p=-0.5,d=0.75,haut=0.5,0.25");
		assertEquals(1, Formule.calcFormuleM(2, 2, 0, 2, c.getHmDefaultScoringM()));
		// test mz
		c.getHmDefaultScoringM().clear();
		c.setDefaultScoringM("e=-0.75,v=0,p=-0.5,mz=0.75,0");
		assertEquals(0.75, Formule.calcFormuleM(3, 3, 0, 1, c.getHmDefaultScoringM()));
		// 2 bonnes rép cochées mais une rép fausse cochée aussi. => on prend la v2 de
		// MZ
		assertEquals(0, Formule.calcFormuleM(2, 2, 1, 3, c.getHmDefaultScoringM()));
		// test avec me et d
		c.getHmDefaultScoringM().clear();
		c.setDefaultScoringM("e=-0.75,v=0,p=-0.5,d=0.25,mz=0.75,0");
		assertEquals(1, Formule.calcFormuleM(3, 3, 0, 1, c.getHmDefaultScoringM()));
		// test avec b et m
		c.getHmDefaultScoringM().clear();
		c.setDefaultScoringM("e=-0.5,v=-0.25,p=-0.5,b=0.5,m=-1");
		assertEquals(0.5, Formule.calcFormuleM(3, 3, 0, 1, c.getHmDefaultScoringM()));
		assertEquals(-1, Formule.calcFormuleM(2, 3, 0, 1, c.getHmDefaultScoringM()));
		assertEquals(-1, Formule.calcFormuleM(3, 3, 1, 1, c.getHmDefaultScoringM()));
		// test avec d
		c.setDefaultScoringM("e=-0.5,v=-0.25,p=-0.5,b=0.5,m=-1,d=0.25");
		assertEquals(0.75, Formule.calcFormuleM(3, 3, 0, 1, c.getHmDefaultScoringM()));
		// test avec e
		c.getHmDefaultScoringM().clear();
		c.setDefaultScoringM("e=-0.25,v=-0.75,p=-1");
		assertEquals(-0.25, Formule.calcFormuleM(2, 2, 1, 2, c.getHmDefaultScoringM()));

		c.getHmDefaultScoringM().clear();
		c.setDefaultScoringM("e=-0.5,v=-0.25,p=0,d=0,formula=(NBC/NB)-(NMC/NM)");
		assertEquals(0.5,Formule.calcFormuleM(2, 2, 1, 2, c.getHmDefaultScoringM()));
		assertEquals(1,Formule.calcFormuleM(2, 2, 0, 2, c.getHmDefaultScoringM()));
		//note plancher
		c.setDefaultScoringM("e=-0.5,v=-0.25,p=-0.5,d=0,formula=(NBC/NB)-(NMC/NM)");
		assertEquals(-0.5,Formule.calcFormuleM(0, 2, 2, 3, c.getHmDefaultScoringM()));
	}

	@Test
	public void testCalcFormO() {
		assertTrue(-1 == Formule.calcFormuleO(true, "P", 1));
		assertTrue(1 == Formule.calcFormuleO(false, "P", 1));
		assertTrue(-1 == Formule.calcFormuleO(true, "p", 1));
		assertTrue(0 == Formule.calcFormuleO(true, "0P", 1));
		assertTrue(0.75 == Formule.calcFormuleO(false, "TQP", 1));
		assertTrue(1.5 == Formule.calcFormuleO(false, "TQP", 2));
	}
}
