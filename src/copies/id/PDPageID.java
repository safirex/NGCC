package copies.id;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import copies.Copie;

public class PDPageID extends PDPage {
	// Agit comme un pdPage classique mais stocke l'id et la copie d'appartenance
	// Le seul constructeur fait est avec format, a voir s'il en faut plus
	// L'id n'est pas settable et seulement passable à l'instanciation, ca evite que tout se melange
	private int ID;
	private Copie copie;
	
	public PDPageID(PDRectangle format, int ID) {
		super(format);
		this.ID = ID;
	}
	
	public PDPageID(PDRectangle format, int ID, Copie copie) {
		super(format);
		this.ID = ID;
		this.copie = copie;
	}
	
	public Copie getCopie() {
		return copie;
	}
	
	public void setCopie(Copie copie) {
		this.copie = copie;
	}
	
	public int getId() {
		return ID;
	}
}
