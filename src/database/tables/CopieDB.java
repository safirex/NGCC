package database.tables;

import java.io.Serializable;
import java.util.List;

import config.Question;
import config.Reponse;

public class CopieDB implements Serializable {

	private static final long serialVersionUID = -5253583090842728190L;

	// attributs definis avant la correction
	private int idCopie;
	private List<Question> questions;
	private int nbPages;
	// attributs definis apres la correction
	private int idEtudiant;
	private List<Reponse> reponsesCochees;
	private double note;

	public CopieDB(int idCopie, int nbPages, List<Question> questions) {
		this.idCopie = idCopie;
		this.nbPages = nbPages;
		this.questions = questions;
	}

	public int getIdCopie() {
		return this.idCopie;
	}

	public void setIdCopie(int idCopie) {
		this.idCopie = idCopie;
	}

	public List<Question> getQuestions() {
		return this.questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public int getNbPages() {
		return this.nbPages;
	}

	public void setNbPages(int nbPages) {
		this.nbPages = nbPages;
	}

	public int getIdEtudiant() {
		return this.idEtudiant;
	}

	public void setIdEtudiant(int idEtudiant) {
		this.idEtudiant = idEtudiant;
	}

	public List<Reponse> getReponsesCochees() {
		return this.reponsesCochees;
	}

	public void setReponsesCochees(List<Reponse> reponsesCochees) {
		this.reponsesCochees = reponsesCochees;
	}

	public double getNote() {
		return this.note;
	}

	public void setNote(double note) {
		this.note = note;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		CopieDB other = (CopieDB) obj;
		if (this.idCopie != other.idCopie) {
			return false;
		}
		if (this.idEtudiant != other.idEtudiant) {
			return false;
		}
		if (this.nbPages != other.nbPages) {
			return false;
		}
		if (Double.doubleToLongBits(this.note) != Double.doubleToLongBits(other.note)) {
			return false;
		}
		if (this.questions == null) {
			if (other.questions != null) {
				return false;
			}
		} else if (!this.questions.equals(other.questions)) {
			return false;
		}
		return true;
	}

}