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
import org.junit.Test;
import org.opencv.core.Core;

import config.Config;
import config.Groupe;
import config.Question;
import config.Reponse;
import copies.Copie;
import copies.exceptions.IdCopieOutOfBoundsException;
import copies.exceptions.IdTestOutOfBoundsException;
import generation.pdf.CopiePdfImpl;
import generation.pdf.FusionCorrige;

public class TestOmrOpenCV {
	private final String sourcePath = "./config/source.txt";
	private Config config;
	private Copie copie;
	private CopiePdfImpl copiePdf;

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
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		this.config = Config.getInstance(this.sourcePath);
		this.config.readConfig();
		this.copie = new Copie(this.getQuestions(), "A4");
		this.copiePdf = new FusionCorrige(this.copie);
		this.copiePdf.generatePDF(this.config);
	}

	@After
	public void clear() {
		this.copie = null;
		this.copiePdf = null;

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
	public void testGetAnsweredReponses() throws IOException {
		// TODO: voir seuils < 50
		Map<Question, List<Reponse>> goodAnswers = this.getGoodAnswers();
		this.copiePdf.save("./pdf/TestOMR/testOmrTemplate.pdf");

		Map<Question, List<Reponse>> answered;
		/*
		 * for (int i = 50; i <= 100; i += 10) { // i = seuil // answered =
		 * this.analyzer.getAnsweredReponses(this.copiePdf, 10, i); //
		 * assertEquals(goodAnswers, answered); }
		 */
		answered = DetectCase.getAnsweredReponses(this.copiePdf);
		System.out.println(goodAnswers.size() + "  " + answered.size());
		for (Question q : goodAnswers.keySet()) {
			for (Reponse r : q.getReponses()) {
				System.out.println(r);
				assertTrue(answered.containsKey(q));
				// assertTrue(answered.get(q).contains(r));
			}
		}
		// Collections.sort(answered);
		System.out.println("FIN FOR");
		assertEquals(goodAnswers, answered);
	}

}
