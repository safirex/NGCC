package copies;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import config.Question;
import copies.images.ImagesCopie;

public class Copie extends Page implements Serializable {

	private static final long serialVersionUID = 6811599293372549503L;
	private ImagesCopie base;
	private List<Question> questions;
	private String format;
	private List<Page> listePages;
	private double note;
	private int id;

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Copie() {
		super(-1);
		this.questions = new ArrayList<Question>();
		this.format = "";
		this.listePages = new ArrayList<Page>();
		this.note = 0;
		
		this.id = GestionnaireCopies.nextCopieId();
	}

	public Copie(BufferedImage img) {
		super(-1);
		this.base = new ImagesCopie(img);
		
		this.id = GestionnaireCopies.nextCopieId();
	}

	public Copie(String format) {
		super(-1);
		this.format = format;

		this.id = GestionnaireCopies.nextCopieId();
	}

	public Copie(List<Question> listeQuestions, String format) {
		super(-1);
		this.questions = listeQuestions;
		this.format = format;
		

		this.id = GestionnaireCopies.nextCopieId();
	}

	public ImagesCopie getBase() {
		return this.base;
	}

	public void setBase(ImagesCopie base) {
		this.base = base;
	}

	public Copie getEmptyCopy() {

		List<Question> temp = new ArrayList<Question>();

		for (Question q : this.questions) {
			temp.add(q.cloneEmpty());

		}

		return new Copie(temp, this.getFormat());
	}

	public List<Question> getQuestions() {
		return this.questions;
	}

	public double getNote() {
		return this.note;
	}

	public void setNote(double note) {
		this.note = note;
	}

	public int getId() {
		return id;
	}


}
