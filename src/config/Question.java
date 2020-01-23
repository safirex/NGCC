package config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -5197952764425658836L;
	private String titre; // intitulé de la question
	private int coeff = 1; // coefficient de la question, si -1 pas de coeff
	private boolean multiple = false; // boolean pour savoir si c'est une question choix multiple
	private boolean frozenanswer = false;
	private List<Reponse> reponses = new ArrayList<>(); // liste des reponses

	public Question(String t, boolean b, int n, boolean f) {
		this.titre = t;
		this.multiple = b;
		this.coeff = n;
		this.frozenanswer = f;
		this.reponses = new ArrayList<>();
	}

	public Question getEmptiedQuestion() {
		return new Question(this.titre, this.multiple, this.coeff, this.frozenanswer);
	}

	public boolean isFrozenanswer() {
		return this.frozenanswer;
	}

	public void setFrozenanswer(boolean frozenanswer) {
		this.frozenanswer = frozenanswer;
	}

	public List<Reponse> getReponses() {
		return this.reponses;
	}

	public void setReponses(List<Reponse> reponses) {
		this.reponses = reponses;
	}

	public int getCoeff() {
		return this.coeff;
	}

	public void setCoeff(int n) {
		this.coeff = n;
	}

	public String getTitre() {
		return this.titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public boolean isMultiple() {
		return this.multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	// rajout des reponses a la question
//prend en parametre un string qui commence par + ou -
//si le string commence pas par + ou - alors c'est la suite du titre
	public void addReponse(String ligne) {
		switch (ligne.substring(0, 1)) {
		case "+":
			// reponse correcte
			this.reponses.add(new Reponse(ligne.substring(2, ligne.length()), true));

			break;

		case "-":
			// reponse fausse
			this.reponses.add(new Reponse(ligne.substring(2, ligne.length()), false));
			break;

		default:
			// si pas + ou - alors c'est la suite a la ligne du titre
			this.titre = (this.titre + "\n" + ligne);
		}
	}

	public boolean auMoinsUneBonneRep() {
		int nbrepf = 0;
		int nbrepj = 0;
		for (Reponse p : this.reponses) {

			if (p.isJuste() == true) {
				nbrepj = nbrepj + 1;
			} else {
				nbrepf = nbrepf + 1;
			}

		}
		if ((nbrepf == 0) || (nbrepj == 0)) {
			return false;
		} else {
			return true;
		}
	}

	public boolean verifQuestionSimple() {
		if (this.multiple == false) {
			int nbrepj = 0;
			for (Reponse p : this.reponses) {

				if (p.isJuste() == true) {
					nbrepj = nbrepj + 1;
				}
				if (nbrepj > 1) {
					return false;
				}
			}

		}
		return true;
	}

	public boolean equalsIntitule(Question questionEtu) {
		return (questionEtu.getTitre().equals(titre));
	}

	public Question cloneEmpty() {
		Question temp = new Question(this.titre, this.multiple, this.coeff, this.frozenanswer);

		for (Reponse r : this.reponses) {
			temp.getReponses().add(r.cloneEmpty());
		}

		return temp;
	}

	public boolean equalsQuestion(List<Question> qCopie) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public Question clone() {
		Question temp = new Question(this.titre, this.multiple, this.coeff, this.frozenanswer);

		for (Reponse r : this.reponses) {
			temp.getReponses().add(r.clone());
		}

		return temp;
	}
	
	
	

}
