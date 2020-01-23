package checker;

public class PaireNoteCoef {

	private double note;
	private double coef;
	
	public PaireNoteCoef(double note, double coef)
	{
		this.coef = coef;
		this.note = note;
	}
	
	public double noteCoef()
	{
		return note*coef;
	}

	public double getNote() {
		return note;
	}

	public void setNote(double note) {
		this.note = note;
	}

	public double getCoef() {
		return coef;
	}

	public void setCoef(double coef) {
		this.coef = coef;
	}
	
}
