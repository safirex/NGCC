package ocr;

import java.util.HashMap;

public class Id {

	static final HashMap<Integer,Integer> historique= new HashMap<Integer,Integer>(); // pour tout test, la premiere id de copie libre
	
	int idPage ;
	int idTest ;
	
	private final int tailleIdPage=18;
	private final int tailleIdTest=6;
	
	


	public Id(int idTest) {
		this.idTest = idTest;
		
		if (historique.containsKey(idTest)) {
			idPage=historique.get(idTest);	
			historique.put(idTest, idPage+1);
		}	
		else {
			historique.put(idTest, 0);
			idPage = historique.get(idTest);
			historique.put(idTest, idPage+1);
		}
	}
	
	
	
	public String getBinIdPage() {

		String bin= Integer.toBinaryString(idPage);
		for (int i = bin.length(); i <tailleIdPage; i++) {
			bin="0"+bin;
		}
		return bin;
	}
	
	public static HashMap<Integer, Integer> getHistorique() {
		return historique;
	}



	public int getIdPage() {
		return idPage;
	}



	public int getIdTest() {
		return idTest;
	}



	public String getBinIdTest() {

		String bin= Integer.toBinaryString(idTest);
		for (int i = bin.length(); i <tailleIdTest; i++) {
			bin="0"+bin;
		}
		return bin;
	}

	public String getBinCode() {
		return getBinCode()+getBinIdPage();
	}
	
	
	
	
	public int getTailleIdPage() {
		return tailleIdPage;
	}

	public int getTailleIdTest() {
		return tailleIdTest;
	}

}
