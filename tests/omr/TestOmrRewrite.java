package omr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import config.Config;
import config.Groupe;
import config.Question;
import config.Reponse;
import copies.Copie;
import copies.exceptions.IdCopieOutOfBoundsException;
import copies.exceptions.IdTestOutOfBoundsException;
import generation.pdf.CopiePdfImpl;
import generation.pdf.FusionCorrige;

public class TestOmrRewrite {
	private final String sourcePath = "./config/source.txt";
	private Config config;
	private Copie copie;
	private CopiePdfImpl copiePdf;
	private OmrRewrite analyzer;
	private CoordsMatcher matcher;

	private void saveObjectOnDisk(Object o) {
		// servira pour les tests "concrets"
		try {
			FileOutputStream fs = new FileOutputStream("saveCopiePdfImpl.ser");
			ObjectOutputStream os = new ObjectOutputStream(fs);
			os.writeObject(o); // 3z
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<Question, List<Reponse>> getGoodAnswers() {
		Map<Question, List<Reponse>> result = new HashMap<>();
		for (Question q : this.copie.getQuestions()) {
			List<Reponse> goodAnswers = new ArrayList<>();
			for (Reponse r : q.getReponses()) {
				if (r.isJuste()) {
					goodAnswers.add(r);
				}
			}
			result.put(q, goodAnswers);
		}
		return result;
	}

	@Before
	public void setUp() throws IOException, IdCopieOutOfBoundsException, IdTestOutOfBoundsException {
		// this.config = Config.getInstance(this.sourcePath);
		this.config = Config.getInstance(this.sourcePath); // ne prend pas en compte le Singleton
		this.config.readConfig();
		this.copie = new Copie(this.getQuestions(), "A4");
		this.copiePdf = new FusionCorrige(this.copie);
		this.copiePdf.generatePDF(this.config);
		this.analyzer = new OmrRewrite();
		this.matcher = new CoordsMatcher(this.copiePdf);
	}

	@After
	public void clear() {
		this.copie = null;
		this.copiePdf = null;
		this.analyzer = null;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private List<Question> getQuestions() {
		// adapation de Gestionnaire.Copies.creerSujet()
		Random r = new Random();
		List<Question> listeQuestions = new ArrayList<>();

		for (Groupe g : this.config.getGroupes()) {
			List<Question> tempQuestions = (ArrayList<Question>) g.getQuestions().clone();
			int rand;
			while (tempQuestions.size() > g.getNbQtChoisie()) {
				rand = r.nextInt(tempQuestions.size());
				tempQuestions.remove(r.nextInt(tempQuestions.size()));
			}
			listeQuestions.addAll(tempQuestions);
		}
		return listeQuestions;
	}

	@Test
	public void testGetAnsweredReponses() {
		// TODO: voir seuils < 50
		Map<Question, List<Reponse>> goodAnswers = this.getGoodAnswers();
		this.copiePdf.save("./pdf/TestOMR/testOmrTemplate.pdf");
		this.analyzer.getOmrVisualization(this.copiePdf, 9);
		Map<Question, List<Reponse>> answered;
		for (int i = 50; i <= 100; i += 10) {
			// i = seuil
			answered = this.analyzer.getAnsweredReponses(this.copiePdf, 10, i);
			assertEquals(goodAnswers, answered);
		}
		answered = this.analyzer.getAnsweredReponses(this.copiePdf, 10, 20);
		System.out.println(goodAnswers.size() + "  " + answered.size());
		for (Question q : goodAnswers.keySet()) {
			for (Reponse r : q.getReponses()) {
				System.out.println(r);
				assertTrue(answered.containsKey(q));
				assertTrue(answered.get(q).contains(r));
			}
		}
		// Collections.sort(answered);
		System.out.println("FIN FOR");
		assertEquals(goodAnswers, answered);
	}

	@Ignore
	@Test
	public void testMatcher() throws IOException {
		this.matcher.getCoords();
		Question question = new Question("Est ce que la lumière noire existe?", false, 1, false);
		Reponse reponseOk = new Reponse("Ok", false);
		Reponse reponseErreur = new Reponse("Erreur", false);
		double[] okCoords = this.matcher.getAnswerCoords(question, reponseOk);
		double[] errerCoords = this.matcher.getAnswerCoords(question, reponseErreur);
		assertTrue(okCoords[0] < errerCoords[0]);
		assertEquals(okCoords[1], errerCoords[1]);
	}

	@Ignore
	@Test
	public void testGetAnsweredReponsesWithMatcher() throws IOException {
		this.matcher.getCoords();
		Map<Question, List<Reponse>> goodAnswers = this.getGoodAnswers();
		this.copiePdf.save("./pdf/TestOMR/testOmrTemplate.pdf");
		this.analyzer.getOmrVisualization(this.matcher, 10);
		Map<Question, List<Reponse>> answered = this.analyzer.getAnsweredReponses(this.matcher, 10, 50);
		assertEquals(goodAnswers, answered);
	}
}
