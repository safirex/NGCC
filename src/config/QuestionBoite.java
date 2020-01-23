package config;

public class QuestionBoite extends Question {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	// Type de question differente du normal, c'est une question avec une boite de
	// texte où l'étudiant saisira du texte
	private int nbligne; // nb de lignes dans la boite

	public QuestionBoite(String t, boolean b, int n, boolean f, int nb) {
		super(t, b, n, f);
		nbligne = nb;
	}

	public int getNbligne() {
		return nbligne;
	}

	public void setNbligne(int nbligne) {
		this.nbligne = nbligne;
	}

	//N= "-" ,P= 1point,TQP= 0.75point,DP= 0.5point,DDP= 0.25point,Z= 0point 
    public boolean verifCaseQuestionO() {
        int compteur = 0;
        for(int i=1;i<this.getReponses().size();i++) {
            if(this.getReponses().get(i).isJuste()) {
                compteur=compteur+1;
            }
        }

        if(compteur>1) {
            return false;
        }
        else
            return true;
    }
    public String caseCoche() {
	String tab[]={"N","P","TQP","DP","DDP","0P"};

	
	for(int i=0;i<this.getReponses().size();i++) {
	if(this.getReponses().get(i).isJuste()==true) {
		return tab[i];
	}

}
	return "";
}
public boolean caseNcoche() {
	return this.getReponses().get(0).isJuste();
  
}
}