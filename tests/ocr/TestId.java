package ocr;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

public class TestId {

	@Test
	public void testCreator() {
		Id id = new Id(1);
		assertEquals(id.getIdTest(),1);
		assertEquals(id.getIdPage(),0);
		
		Id id2= new Id(1);
		assertEquals(id2.getIdTest(),1);
		assertEquals(id2.getIdPage(),1);
	}
	
	@Test
	public void testIdToBin() {
		Id id = new Id(1);
		Id id2 = new Id(1);
		assertEquals(id.getTailleIdPage(), id.getBinIdPage().length());
		assertEquals(id2.getTailleIdPage(), id2.getBinIdPage().length());
		
		assertEquals(id.getTailleIdTest(), id.getBinIdTest().length());
		assertEquals(id2.getTailleIdTest(), id2.getBinIdTest().length());
		
	}
	
}
