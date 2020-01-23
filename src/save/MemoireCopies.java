package save;

import java.io.Serializable;
import java.util.List;

import copies.Copie;

public class MemoireCopies implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7931154325058553242L;
	private Copie sujet;
	private List<Copie> listeCopies;
	
	public List<Copie> getListeCopies() {
		return listeCopies;
	}
	public void setListeCopies(List<Copie> listeCopies) {
		this.listeCopies = listeCopies;
	}
	public Copie getSujet() {
		return sujet;
	}
	public void setSujet(Copie sujet) {
		this.sujet = sujet;
	}
}
