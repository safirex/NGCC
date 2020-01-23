package copies.images;

import java.awt.image.BufferedImage;

public abstract class Img {

	BufferedImage img;
	String description;
	
	public Img(BufferedImage img) {
		
		this.img = img;
		
	}
	
	public abstract void applyOcrImg();
	
	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void sanitizeDesc()
	{
		this.description = this.description.replace(" ", "");
		this.description = this.description.replace("\n", "");
	}
	
	
}
