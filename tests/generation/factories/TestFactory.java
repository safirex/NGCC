package generation.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import copies.Copie;
import copies.GestionnaireCopies;
import copies.exceptions.IdCopieOutOfBoundsException;
import copies.exceptions.IdTestOutOfBoundsException;
import generation.IncorrectCopieTypeException;
import generation.pdf.FeuilleReponses;
import generation.pdf.FeuilleReponsesCorrige;
import generation.pdf.Fusion;
import generation.pdf.FusionCorrige;
import generation.pdf.Sujet;

public class TestFactory {

	private final String source = "./config/source.txt";
	private GestionnaireCopies gestionnaire;
	private Copie copie;

	@Before
	public void setUp() throws IncorrectCopieTypeException, IdCopieOutOfBoundsException, IdTestOutOfBoundsException {
		this.gestionnaire = new GestionnaireCopies();
		this.gestionnaire.initializeOmr(this.source);
		this.copie = this.gestionnaire.getSujet();
	}

	@After
	public void clear() {
		// dthis.gestionnaire = null;
		this.copie = null;
	}

	@Test
	@DisplayName("Test la création d'une fabrique depuis FactoryProducer")
	public void testFactoryProducer() throws IncorrectCopieTypeException {

		CopieFactory corrigeFactoryA = FactoryProducer.getFactory("corrige");
		CopieFactory corrigeFactoryB = FactoryProducer.getFactory("CORRIGE");
		CopieFactory corrigeFactoryC = FactoryProducer.getFactory("corrige ");
		CopieFactory blankFactoryA = FactoryProducer.getFactory("pas corrige !");
		CopieFactory blankFactoryB = FactoryProducer.getFactory("Sujet");
		CopieFactory blankFactoryC = FactoryProducer.getFactory("corrige");

		assertTrue(corrigeFactoryA instanceof CorrigeFactory);
		assertTrue(corrigeFactoryB instanceof CorrigeFactory);
		assertFalse(corrigeFactoryC instanceof CorrigeFactory);
		assertTrue(blankFactoryA instanceof BlankFactory);
		assertTrue(blankFactoryB instanceof BlankFactory);
		assertFalse(blankFactoryC instanceof BlankFactory);

	}

	@Test
	@DisplayName("Test la génération de sujets, feuilles de réponses et sujets fusions.")
	public void testBlankFactory() throws IncorrectCopieTypeException {
		CopieFactory factory = new BlankFactory();
		assertEquals(new Sujet(this.copie).getClass(), factory.creerCopie("sujet", this.copie).getClass());
		assertEquals(new Fusion(this.copie).getClass(), factory.creerCopie("fusion", this.copie).getClass());
		assertEquals(new FeuilleReponses(this.copie).getClass(), factory.creerCopie("reponses", this.copie).getClass());
		// assertEquals(, factory.creerCopie("detachable", this.copie));
		assertThrows(IncorrectCopieTypeException.class, () -> {
			factory.creerCopie("bad type", this.copie);
		});
	}

	@Test
	@DisplayName("Test la génération de feuilles de réponses corrigées et fusions corrigés.")
	public void testCorrigeFactory() throws IncorrectCopieTypeException {
		CopieFactory factory = new CorrigeFactory();
		assertEquals(new FusionCorrige(this.copie).getClass(), factory.creerCopie("fusion", this.copie).getClass());
		assertEquals(new FeuilleReponsesCorrige(this.copie).getClass(),
				factory.creerCopie("reponses", this.copie).getClass());
		// assertEquals(, factory.creerCopie("detachable", this.copie));
		assertThrows(IncorrectCopieTypeException.class, () -> {
			factory.creerCopie("bad type", this.copie);
		});
	}

}
