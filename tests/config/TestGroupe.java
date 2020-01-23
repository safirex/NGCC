package config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestGroupe {
	@Test
	public void testGroupe() {
		Groupe g = new Groupe();
		assertEquals(200, g.getNbQtChoisie());
		g.setNbQtChoisie(4);
		Groupe g2 = new Groupe(3);
		assertEquals(3, g2.getNbQtChoisie());
	}

	@Test
	public void testAddQuestion() {
		Groupe g = new Groupe();
		assertEquals(200, g.getNbQtChoisie());
		Question q = new Question("titre", false, 1, false);
		Question q2 = new Question("titre2", false, 1, false);

		g.addQuestions(q);
		assertEquals(g.getQuestions().get(0), q);
		g.addQuestions(q2);
		assertEquals(g.getQuestions().get(1), q2);

		Groupe g2 = new Groupe(3);
		assertEquals(3, g2.getNbQtChoisie());
		g2.addQuestions(q);
		g2.addQuestions(q2);
		assertEquals(g.getQuestions().get(0), q);
		assertEquals(g.getQuestions().get(1), q2);
	}
}
