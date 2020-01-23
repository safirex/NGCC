package copies;

import java.util.ArrayList;

import config.Reponse;
public class Page {
	
	private int id;
	private ArrayList<Reponse> listeReps ;
	
	public Page(int idCopie) {
		this.setId(idCopie);
		setListeReps(new ArrayList<Reponse>());		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Reponse> getListeReps() {
		return listeReps;
	}

	public void setListeReps(ArrayList<Reponse> listeReps) {
		this.listeReps = listeReps;
	}
}
