package database.tables;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class PageDB implements Serializable {

	private static final long serialVersionUID = -5253583090842728190L;

	private int idPage;
	private int idCopie;
	private BufferedImage image;

	public PageDB(int idPage, int idCopie) {
		this.idPage = idPage;
		this.idCopie = idCopie;
	}

	public int getIdPage() {
		return this.idPage;
	}

	public void setIdPage(int idPage) {
		this.idPage = idPage;
	}

	public int getIdCopie() {
		return this.idCopie;
	}

	public void setIdCopie(int idCopie) {
		this.idCopie = idCopie;
	}

	public BufferedImage getImage() {
		return this.image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

}