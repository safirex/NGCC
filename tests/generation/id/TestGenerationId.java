package generation.id;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import config.Config;
import config.Groupe;
import config.Question;
import copies.exceptions.IdCopieOutOfBoundsException;
import copies.exceptions.IdTestOutOfBoundsException;
import copies.id.GestionnaireIds;
import copies.id.PDPageID;
import generation.pdf.CopiePdfImpl;
import generation.pdf.FeuilleReponses;
import copies.Copie;

public class TestGenerationId {
	private final String sourcePath = "./config/source.txt";
	private final String saveLocation = "./pdf/TestGenerationV2/";
	private Config config;
	private Copie copie;
	private CopiePdfImpl copiePdf;
	private HashMap<Integer, Integer> mapTestCurrId;

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
		// Ajout dans hashMap de gestioId 
		mapTestCurrId = new HashMap<>();
		this.mapTestCurrId.put(0, 0);
		GestionnaireIds.setMapTestCurrId(mapTestCurrId);
	}

	@After
	public void clear() {
		this.copie = null;
		this.copiePdf = null;
	}

	@Test
	@DisplayName("Teste la generation des zones d'ID en haut de page")
	public void testGenerationID() throws IdCopieOutOfBoundsException, IdTestOutOfBoundsException {
		this.copiePdf = new FeuilleReponses(this.copie);
		for (int i = 0; i < 10; i++) {
			this.copiePdf.addPage(new PDPageID(PDRectangle.A4, GestionnaireIds.nextId(0)));
		}
		this.copiePdf.generatePDF(config);
		
		this.copiePdf.save(this.saveLocation + "ID/CopieID.pdf");
	}
	
	@Test
	@DisplayName("Teste la tenue des IDs à travers les copies")
	public void testGenerationIDMult() throws IdCopieOutOfBoundsException, IdTestOutOfBoundsException {
		int nbPages = 10;
		this.copiePdf = new FeuilleReponses(this.copie);
		for (int i = 0; i < nbPages; i++) {
			this.copiePdf.addPage(new PDPageID(PDRectangle.A4, GestionnaireIds.nextId(0)));
		}
		this.copiePdf.generatePDF(config);
		this.copiePdf.save(this.saveLocation + "ID/CopieIDMult1.pdf");
		
		//int nbP = this.copiePdf.getNumberOfPages();
		//this.mapTestCurrId.put(0, nbP);
		
		
		this.copiePdf = new FeuilleReponses(this.copie);
		for (int i = 0; i < nbPages/2; i++) {
			this.copiePdf.addPage(new PDPageID(PDRectangle.A4, GestionnaireIds.nextId(0)));
		}
		this.copiePdf.generatePDF(config);
		this.copiePdf.save(this.saveLocation + "ID/CopieIDMult2.pdf");
		
		//this.mapTestCurrId.put(0, nbP + this.copiePdf.getNumberOfPages());
		
		this.copiePdf = new FeuilleReponses(this.copie);
		for (int i = 0; i < nbPages; i++) {
			this.copiePdf.addPage(new PDPageID(PDRectangle.A4, GestionnaireIds.nextId(0)));
		}
		this.copiePdf.generatePDF(config);
		this.copiePdf.save(this.saveLocation + "ID/CopieIDMult3.pdf");
		
		// Fonctionnel
		// Décalage de 2 par début de copie
	}
}

