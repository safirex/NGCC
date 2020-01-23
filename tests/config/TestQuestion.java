package config;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TestQuestion {
	@Test
	public void testQuestion() {
		Question q=new Question("Je suis le titre",false,1,false);
		 assertEquals("Je suis le titre",q.getTitre());
		 assertEquals(false,q.isMultiple());
		 
		 Question q2=new Question("Je suis Q2",true,1,false);
		 assertNotEquals("Je suis q2",q2.getTitre());
		 assertEquals("Je suis Q2",q2.getTitre());
		 assertNotEquals(false,q2.isMultiple());
		 assertEquals(true,q2.isMultiple());
		 q2.getEmptiedQuestion();
		 assertEquals(false,q.isFrozenanswer());
		 q.setFrozenanswer(true);
		 assertTrue(q.isFrozenanswer());
		 q.setCoeff(2);
		 assertEquals(2,q.getCoeff());
		 q.setTitre("mon nouveau titre");
		 assertEquals("mon nouveau titre", q.getTitre());
		 q.setMultiple(true);
		 assertTrue(q.isMultiple());
		 }
	@Test
	public void testAuMoinsUneBonneRep() {
		Question q=new Question("D'après le générique de 1978, jusqu'où bondit le merveilleux génie ?",false,1,false);		
		Reponse r1=new Reponse("Jupiter",true);
		Reponse r2=new Reponse("La Terre",false);
		Reponse r3=new Reponse("Le fond de l'univers",false);
		Reponse r4=new Reponse("Neugebauer",false);
		q.addReponse("+ "+r1.getIntitule());
		q.addReponse("- "+r2.getIntitule());
		q.addReponse("- "+r3.getIntitule());
		q.addReponse("- "+r4.getIntitule());
		assertTrue(q.auMoinsUneBonneRep());
		Question q2=new Question("D'après le générique de 1978, jusqu'où bondit le merveilleux génie ?",false,1,false);	
		q2.addReponse("- "+r1.getIntitule());
		q2.addReponse("- "+r2.getIntitule());
		q2.addReponse("- "+r3.getIntitule());
		q2.addReponse(" "+r4.getIntitule());
		assertFalse(q2.auMoinsUneBonneRep());
				
		
	}
	@Test
	public void testVerifQuestionSimple() {
		Question q=new Question("D'après le générique de 1978, jusqu'où bondit le merveilleux génie ?",false,1,false);	
		Reponse r1=new Reponse("Jupiter",true);
		Reponse r2=new Reponse("La Terre",false);
		Reponse r3=new Reponse("Le fond de l'univers",false);
		Reponse r4=new Reponse("Neugebauer",false);
		Reponse r5=new Reponse("Neugebauer",true);
		q.addReponse("+ "+r1.getIntitule());
		q.addReponse("- "+r2.getIntitule());
		q.addReponse("- "+r3.getIntitule());
		q.addReponse("- "+r4.getIntitule());
		assertTrue(q.verifQuestionSimple());
	    q.addReponse("+ "+r5.getIntitule());
	    assertFalse(q.verifQuestionSimple());
				
		
	}
	
	
}
