package config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

public class TestQuestionBoite {
	@Test
	void testQuestionBoite() {
		QuestionBoite q=new QuestionBoite("Quelles sont les bonnes r�ponses",false,1,false,3);
		assertEquals(3,q.getNbligne());
		assertEquals(false,q.isMultiple());
		assertEquals("Quelles sont les bonnes r�ponses",q.getTitre());
		assertNotEquals("quelles sont les bonnes r�ponses",q.getTitre());
		q.setNbligne(2);
	}
	
	@Test
	public void testCaseNcoche() {
		QuestionBoite q=new QuestionBoite("t",false,1,false,2);
		q.addReponse("+N");
        assertTrue(q.caseNcoche());
        QuestionBoite q1=new QuestionBoite("t",false,1,false,2);
        q1.addReponse("-N");
        assertFalse(q1.caseNcoche());
        



	}
	@Test
	public void testCasecoche() {
		QuestionBoite q=new QuestionBoite("t",false,1,false,2);
		q.addReponse("-N");
		q.addReponse("+P");
		q.addReponse("-TQP");
		q.addReponse("-DP");
		q.addReponse("-DDP");
		q.addReponse("-0P");
		assertEquals("P",q.caseCoche());
		QuestionBoite q2=new QuestionBoite("t",false,1,false,2);
		q2.addReponse("-N");
		q2.addReponse("-P");
		q2.addReponse("-TQP");
		q2.addReponse("-DP");
		q2.addReponse("-DDP");
		q2.addReponse("+0P");
		assertEquals("0P",q2.caseCoche());

	}
	@Test
	public void testVerifCaseQuestion() {
		QuestionBoite q=new QuestionBoite("t",false,1,false,2);
		q.addReponse("-N");
		q.addReponse("+P");
		q.addReponse("-TQP");
		q.addReponse("-DP");
		q.addReponse("-DDP");
		q.addReponse("-0P");
	assertTrue(q.verifCaseQuestionO());
	QuestionBoite q2=new QuestionBoite("t",false,1,false,2);
	q2.addReponse("+N");
	q2.addReponse("-P");
	q2.addReponse("-TQP");
	q2.addReponse("-DP");
	q2.addReponse("-DDP");
	q2.addReponse("+0P");
	assertTrue(q.verifCaseQuestionO());
	QuestionBoite q3=new QuestionBoite("t",false,1,false,2);
	q3.addReponse("+N");
	q3.addReponse("-P");
	q3.addReponse("+TQP");
	q3.addReponse("-DP");
	q3.addReponse("-DDP");
	q3.addReponse("+0P");
	assertFalse(q3.verifCaseQuestionO());

	}
}
