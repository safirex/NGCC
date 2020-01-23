package ocr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import copies.GestionnaireCopies;
import copies.exceptions.IdCopieOutOfBoundsException;
import copies.exceptions.IdTestOutOfBoundsException;
import generation.IncorrectCopieTypeException;

class TestGestionnaireCopies {

	// TODO: faire les tests

	private final String sourceFile = "./config/source.txt";
	private GestionnaireCopies gc;

	@BeforeEach
	public void setup() throws IncorrectCopieTypeException, IdCopieOutOfBoundsException, IdTestOutOfBoundsException {
		this.gc = new GestionnaireCopies();
		
	}

	@After
	public void clean() {

	}

	@Test
	public void testInitializeOmr_correctSourcePath() throws IncorrectCopieTypeException, IdCopieOutOfBoundsException, IdTestOutOfBoundsException
	{
		this.gc.initializeOmr(sourceFile);
		
		assertNotNull(this.gc.getSujet());
		assertNotNull(this.gc.getListeCopies());
		assertNotNull(this.gc.getListeCopiesPdf());
		
	}
}
