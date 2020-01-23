package copies.id;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.Question;
import copies.exceptions.IdCopieOutOfBoundsException;
import copies.exceptions.IdTestOutOfBoundsException;

public abstract class GestionnaireIds {
	
	// TestId and currentIdPage
	private static Map<Integer,Integer> mapTestCurrId = new HashMap<>();
	
	private static List<Integer> idList = new ArrayList<>();
	
	private static int ID;
	
	private final static int NB_BITS = 24;
	
	private final static int NB_BITS_COPIES = 18;
	public final static int NB_COPIES_MAX = (int)Math.pow(2, NB_BITS_COPIES) - 1;
	
	private final static int NB_BITS_TEST = NB_BITS-NB_BITS_COPIES;
	public final static int NB_TEST_MAX = (int)Math.pow(2, NB_BITS_TEST);
	
	
	//TODO: voir utilite bits test
	
	/*
	 * @Param
	 * 
	 * 
	 */
	public GestionnaireIds () {
		// TODO 
		//mapTestCurrId.put(0, 0);
		ID = 0;
	}
	
	public static int nextId(int numTest) throws IdCopieOutOfBoundsException, IdTestOutOfBoundsException
	{
		
//		if(isNextIdPossible(numTest))
//		{
//			mapTestCurrId.replace(numTest, mapTestCurrId.get(numTest)+1);
//			return (int)(mapTestCurrId.get(numTest)+Math.pow(2,NB_BITS_COPIES)*numTest);
//		}
//		return -1;
		
		//mapTestCurrId.replace(numTest, mapTestCurrId.get(numTest)+1);
		//return (int)(mapTestCurrId.get(numTest)+Math.pow(2,NB_BITS_COPIES)*numTest);
		int setId = ID;
		ID++;
		return setId;

	}

	private static boolean isNextIdPossible(int numTest) throws IdCopieOutOfBoundsException, IdTestOutOfBoundsException {
		
		if(isNumTestValid(numTest) && mapTestCurrId.containsKey(numTest))
		{
			if(isIdCopieValid(mapTestCurrId.get(numTest)+1))
			{
				return true;
			}else {
				return false;
				//throw new IdCopieOutOfBoundsException("");
			}
		}else {
			//throw new IdTestOutOfBoundsException("");
			return false;
		}
		
//		if(isIdCopieValid(mapTestCurrId.get(numTest)+1))
//		{
//			return true;
//		}else {
//			//return false;
//			throw new IdCopieOutOfBoundsException("");
//		}
		
		
	}
	
	private static boolean isIdCopieValid(int idCopie) {
		return idCopie >= 0 && idCopie <= NB_COPIES_MAX;
	}

	private static boolean isNumTestValid(int numTest) {
		
		return numTest >= 0 && numTest <= NB_TEST_MAX;
	}

	public static Map<Integer, Integer> getMapTestCurrId() {
		return mapTestCurrId;
	}


	public static void setMapTestCurrId(Map<Integer, Integer> mapTestCurrId) {
		GestionnaireIds.mapTestCurrId = mapTestCurrId;
	}
	
	
}
