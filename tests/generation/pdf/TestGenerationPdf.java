package generation.pdf;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import config.Config;
import config.Groupe;
import config.Question;
import copies.Copie;
import copies.exceptions.IdCopieOutOfBoundsException;
import copies.exceptions.IdTestOutOfBoundsException;

public class TestGenerationPdf {

	/*
	 * TODO: regler les bugs suivants :
	 *
	 * BUG : Cas: application du Singleton Description : les bodies ne sont pas
	 * generes sauf pour le premier test execute
	 *
	 */

	private final String sourcePath = "./config/source.txt";
	private final String saveLocation = "./pdf/TestGenerationV2/";
	private Config config;
	private Copie copie;
	private CopiePdfImpl copiePdf;
	
	int nbQ;

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

	@Before
	public void setUp() {
		// this.config = Config.getInstance(this.sourcePath);
		this.config = Config.getInstance(sourcePath) ;// ne prend pas en compte le Singleton
		this.config.readConfig();
		this.copie = new Copie(this.getQuestions(), "A4");
		// VIDE POUR L'INSTANT
		//nbQ = copie.getQuestions().size();
	}

	@After
	public void clear() {
		this.copie = null;
		this.copiePdf = null;
	}

	@Test
	@DisplayName("Test la generation de la feuille de reponses.")
	public void testGenerationFeuilleReponses() throws IdCopieOutOfBoundsException, IdTestOutOfBoundsException {
		this.copiePdf = new FeuilleReponses(this.copie);
		this.copiePdf.generatePDF(this.config);
		assertNotNull(this.copiePdf);
		this.copiePdf.save(this.saveLocation + "FeuilleReponses/FeuilleReponses.pdf");
	}

	@Test
	@DisplayName("Test la generation de la feuille de reponses corrigee.")
	public void testGenerationFeuilleReponsesCorrige() throws IdCopieOutOfBoundsException, IdTestOutOfBoundsException {
		this.copiePdf = new FeuilleReponsesCorrige(this.copie);
		this.copiePdf.generatePDF(this.config);
		assertNotNull(this.copiePdf);
		this.copiePdf.save(this.saveLocation + "FeuilleReponsesCorrige/FeuilleReponsesCorrige.pdf");
	}

	@Test
	@DisplayName("Test la generation du fusion.")
	public void testGenerationFusion() throws IdCopieOutOfBoundsException, IdTestOutOfBoundsException {
		this.copiePdf = new Fusion(this.copie);
		this.copiePdf.generatePDF(this.config);
		assertNotNull(this.copiePdf);
		this.copiePdf.save(this.saveLocation + "Fusion/Fusion.pdf");
	}

	@Test
	@DisplayName("Test la generation du fusion corrige.")
	public void testGenerationFusionCorrige() throws IdCopieOutOfBoundsException, IdTestOutOfBoundsException {
		this.copiePdf = new FusionCorrige(this.copie);
		this.copiePdf.generatePDF(this.config);
		assertNotNull(this.copiePdf);
		this.copiePdf.save(this.saveLocation + "FusionCorrige/FusionCorrige.pdf");
	}

	@Test
	@DisplayName("Test la generation du sujet.")
	public void testGenerationSujet() throws IdCopieOutOfBoundsException, IdTestOutOfBoundsException {
		this.copiePdf = new Sujet(this.copie);
		this.copiePdf.generatePDF(this.config);
		assertNotNull(this.copiePdf);
		this.copiePdf.save(this.saveLocation + "Sujet/Sujet.pdf");

	}
}
