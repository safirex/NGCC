package csv;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestCSV {

	Map<String, String> map = new HashMap<String, String>();
	String length = "8";
	String format = "20";
	String path = "../export/result.csv";
	GenerateCSV csv = new GenerateCSV(map, length, format, path);

	@BeforeEach
	void setUp() {
		map = new HashMap<String, String>();
		length = "8";
		format = "20";
		path = "../export/result.csv";
	}

	@Test
	void testNotNull() {

		map.put("21705239", "17");
		csv = new GenerateCSV(map, length, format, path);

		assertFalse(csv == null);
	}

	@Test
	void testValid() {

		map.put("21435712", "9");
		map.put("21705239", "17");
		csv = new GenerateCSV(map, length, format, path);

		for (String etud : csv.etudiants.keySet()) {
			assertTrue(csv.isNumValid(etud));
		}

	}

	@Test
	void testNotValid() {

		map.put("21435", "9");
		map.put("1705239", "17");
		csv = new GenerateCSV(map, length, format, path);

		for (String etud : csv.etudiants.keySet()) {
			assertFalse(csv.isNumValid(etud));
		}

	}

	@Test
	void testGeneration() {

	}

	@Test
	void testIsMarkValid() {

		map.put("21435", "9.5");
		map.put("80216", "-5");
		csv = new GenerateCSV(map, length, format, path);

		boolean bo1 = true;
		boolean bo2 = true;
		double nb1 = Double.parseDouble(map.get("21435"));
		double nb2 = Double.parseDouble(map.get("80216"));

		if (!(nb1 >= 0 && nb1 <= csv.mType)) {

			bo1 = false;
		}

		if (!(nb2 >= 0 && nb2 <= csv.mType)) {

			bo2 = false;
		}

		assertEquals(csv.isMarkValid("9,5"), bo1);
		assertEquals(csv.isMarkValid("-5"), bo2);

	}

	@Test
	void testAlreadyExists() {

		map.put("21435", "10");
		map.put("21435", "10");
		
		
		csv = new GenerateCSV(map, length, format, path);
		
		assertTrue(csv.alreadyExists("21435"));
		assertFalse(csv.alreadyExists("21434"));

	}
	
	
}