package config;

import java.awt.Point;
import java.io.Serializable;

public class Reponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 707904399082422632L;
	private String intitule;
	private boolean juste = false;
	private Point p;
	private int numPage;

	public String getIntitule() {
		return this.intitule;
	}

	public Reponse(String t, boolean b, int numPage) {
		this.intitule = t;
		this.juste = b;
		this.p = new Point();
		this.numPage = numPage;
	}

	public Reponse(String t, boolean b) {
		this(t, b, 0);
	}

	public void setIntitule(String intitule) {
		this.intitule = intitule;
	}

	public boolean isJuste() {
		return this.juste;
	}

	public void setJuste(boolean juste) {
		this.juste = juste;
	}

	public Point getP() {
		return this.p;
	}

	public void setP(Point p) {
		this.p = p;
	}

	public int getNumPage() {
		return this.numPage;
	}

	public void setNumPage(int numPage) {
		this.numPage = numPage;
	}

	public Reponse cloneEmpty() {
		return new Reponse(this.intitule, false, this.numPage);
	}

	public Reponse clone()
	{
		Reponse temp = new Reponse(this.intitule, false, this.numPage);
		temp.setP((Point)this.p.clone());
		return temp;
	}
	
}
