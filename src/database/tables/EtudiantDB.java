package database.tables;

import java.io.Serializable;

public class EtudiantDB implements Serializable {

	private static final long serialVersionUID = -5253583090842728190L;

	private int idEtudiant;

	public EtudiantDB(int idEtudiant) {
		this.idEtudiant = idEtudiant;
	}

	public int getIdEtudiant() {
		return this.idEtudiant;
	}

	public void setIdEtudiant(int idEtudiant) {
		this.idEtudiant = idEtudiant;
	}
}
