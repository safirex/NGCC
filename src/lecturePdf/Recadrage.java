package lecturePdf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;


/**
 * Permet le recadrage d'une ou d'un groupe de BufferImage
 * @author chatin eudes, gross paul
 *
 */

public class Recadrage {
	String dir="";
	String filename="";
	BufferedImage img;

	public Recadrage(String directory,String file) {
		dir=directory;
		filename=file;
		File f =new File(dir+filename);
		try {
			img=ImageIO.read(f);
		} catch (IOException e) {
			//System.out.println(e);
		}
	}
	
	public Recadrage(BufferedImage image) {
		img=image;
	}
	public Recadrage(){
	}

	/**
	 * 
	 * @return un booleen indiquant si oui ou non l'image est droite
	 */
	public boolean estDroite1() {	//determine si l'image png/jpg du pdf est droite  (doit etre en noir et blanc)
		int count=0;
		boolean stop=false;
		for (int ty=0; ty<100 && !stop;ty++) {
			for (int tx=0;tx<img.getWidth();tx++) {

				Color tmp=new Color(img.getRGB(tx, ty));
				if (tmp.getGreen()<20) {
					count++;
					stop=true;
				}
			}
		}
		if (count>248 && count <496)	//hard coded parce que la taille de la bande du haut ne change pas
			return true;
		return false;
	}
	
	
	/*
	public boolean estDroite(){
		int count=0;
		boolean stop=false;
		for (int ty=0; ty<100 && !stop;ty++) {
			for (int tx=0;tx<img.getWidth();tx++) {
	
				Color tmp=new Color(img.getRGB(tx, ty));				
				if (tmp.getGreen()<20) {
					count++;
				}
			}
			if (count>img.getWidth()*0.1 && count <img.getWidth()*0.2)
				return true;
			count=0;
			if (ty>img.getHeight()*0.1)
				stop=true;
		}
		if (entreEnvers())
			this.img=Recadrage.rotate(img, 180);
		return false;
	}
	*/
	
	/**
	 * 
	 * @return un boolean indiquant si oui ou non l'image est à l'envers
	 */
	public boolean aLEnvers() {
		boolean stop2=false;
		int count=0;
		System.out.println(img.getWidth()/6);System.out.println(img.getWidth()/5);
		
		for (int ty2=img.getHeight()-100; ty2<img.getHeight() && !stop2;ty2++) {
			
			for (int tx2=0;tx2<img.getWidth();tx2++) {
				
				Color tmp=new Color(img.getRGB(tx2, ty2));
				if (tmp.getGreen()<20) {
					count++;
				}
			}
			System.out.println(count);
			if (count>img.getWidth()*0.1 ) {//&& count <img.getWidth()*0.2) {
				return true;
			}
			count=0;
			if (ty2>img.getHeight()*0.1)
				stop2=true;
		}
		return false;
	}
	
	/**
	 *  peu efficace, utiliser aLenvers() si fonctionnel
	 * @return un boolean indiquant si oui ou non l'image est à l'envers
	 */
	public boolean entreeEnvers() {		//compte pour 10 dern % de la page le nb de pixels noirs si > bas de la page alors retourner
		
		boolean stop2=false;
		int count=0;
		int largeur=img.getWidth();
		for (int ty2=(int)(img.getHeight()*0.9); ty2<img.getHeight() && !stop2;ty2++) {	
			
			for (int tx2=(int)(largeur*0.1);tx2<largeur*0.9;tx2++) {	
				
				Color tmp=new Color(img.getRGB(tx2, ty2));
				if (tmp.getGreen()<20) {
					count++;						//detecte si le nombre de pix noir present au milieu haut
				}									// de la copie correspond au carrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â© sinon swap
			}
			if (ty2>img.getHeight()*0.1)
				stop2=true;
		
		}
		if (count<largeur*0.2 && count >largeur*0.05) //min de 5% de largeur 
			return true;
			
		return false;
	}
	
	
	/*
	public boolean entreEnvers() {		//compte pour 10 dern % de la page le nb de pixels noirs si > bas de la page alors retourner
		//System.out.println("roar");
		boolean stop2=false;
		int countH=0;
		int countB=0;
		int largeur=img.getWidth();
		
		for (int ty=0;ty<(int)(img.getHeight()*0.05);ty++) {	
			for (int tx=(int)(largeur*0.1);tx<largeur*0.9;tx++) {	
				Color tmp=new Color(img.getRGB(tx, ty));
				if (tmp.getGreen()<20) {
					countH++;						//detecte si le nombre de pix noir present au milieu haut
				}									// de la copie correspond au carrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â© sinon swap
			}
		}
		for (int ty=(int)(img.getHeight()*0.95);ty<img.getHeight();ty++) {	
			for (int tx=(int)(largeur*0.1);tx<largeur*0.9;tx++) {	
				Color tmp=new Color(img.getRGB(tx, ty));
				if (tmp.getGreen()<20) {
					countB++;						//detecte si le nombre de pix noir present au milieu haut
				}									// de la copie correspond au carrÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â© sinon swap
			}
		}
		
		if (countB>countH)
			return true;
		return false;
		
	
	}*/

	public void setImage(BufferedImage entree){
		this.img=entree;
	}
	
	
	
	
	
	/**
	 * détermine si l'image est droite, dans le cas contraire, la remet à l'endroit
	 * @return	bufferedImage
	 * @throws IOException
	 */
	public BufferedImage automation() throws IOException {
		if (!this.estDroite1()) {
			int[][] points=RdB();
			double angle=getAngle(points);
			    
		    img=rotate(img,angle);
		    if (aLEnvers())
				this.img=Recadrage.rotate(img, 180);
		    //String nomImage="sortie";
			//File nomfichier = new File( nomImage + ".jpg");// ou jpg
			//ImageIO.write(img, "JPG", nomfichier);//ou JPG
			
		}	
		return img;
	}

	
	/**
	 * réalise automation() pour toutes les BufferedImages misent en argument
	 * @param list
	 * @return
	 */
	public ArrayList<BufferedImage> listAutomation(List<BufferedImage> list){
		ArrayList<BufferedImage> retour =new ArrayList<BufferedImage>();
		
		for (BufferedImage imag:list) {
			this.img=imag;
			try {
				this.automation();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			retour.add(this.img);
		}
		return retour;
	}
	
	
	
	
	
	/**
	 * recupere la position des 4 points de la feuille
	 * @return tableau indiquant le position de chaque point detecte, pour chaque 
	 * 		ligne [0]=x, [1]=y 
	 */
	public int[][] RdB() {								// cherche les 4 points noirs
		int[][] pixNoirs = new int[img.getWidth()*img.getHeight()][2];

		int i=0;
		int radius=8;									// 19= limite de detection de checkCircle
		for (int ty=0; ty<img.getHeight();ty++) {
			for (int tx=0;tx<img.getWidth();tx++) {

				Color tmp=new Color(img.getRGB(tx, ty));
				if (tmp.getGreen()<20) { 				//si le pixel  est noir
					if (checkCircle(tx,ty,radius) ) {	//verifie si un cercle de radius entoure le pixel
						pixNoirs[i][0]=tx;
						pixNoirs[i][1]=ty;
						i++;
					}
				}
			}
		}
		//System.out.println("fin");
		int tmp=1;
		int centreX[][]=new int [img.getWidth()*img.getHeight()][2];
		centreX[0]=pixNoirs[0];
		for (int l=0;l<img.getHeight()*img.getWidth() && (pixNoirs[l][1]!=0 || pixNoirs[l][0]!=0);l++) {
			if((pixNoirs[l][0]-centreX[tmp-1][0])<5 || pixNoirs[l][1]-centreX[tmp-1][1]<5 ){		//x-(x-1)>5 ou y-(y-1)>5
				centreX[tmp][0]=pixNoirs[l][0];			//efface le precedent roar2 si il ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©tait a 1 pixel de diff
				centreX[tmp][1]=pixNoirs[l][1];
			}
			else {
				tmp++;
				centreX[tmp]=pixNoirs[l];
			}
			
		}
		
		
		//boucle de determination des points noirs
		//System.out.println("roar2debut");
		int points[][]=new int [4][2];
		int centres[][]=new int [centreX.length][2]; int boucl=0; int lasti=0;
		int t=0;
		for (int l=1;l<=img.getHeight()*img.getWidth() && (centreX[l-1][1]!=0 || centreX[l-1][0]!=0);l++) {


			
			int diffx=centreX[l][0]-centreX[l-1][0];
			int diffy=centreX[l][1]-centreX[l-1][1];
			int diff=Math.abs(diffx)+Math.abs(diffy);
			if (diff>img.getWidth()*0.85)
			{
				points[t]=centreX[l];
				t++;
			}
			
			if (diffx<10 && diffy<10) {
				boucl++;
			}
			else {
				centres[lasti][0]=centreX[l-boucl/2][0];
				centres[lasti][1]=centreX[l-boucl/2][1];
				lasti++;boucl=0;
			}
		}
		
		
		
		
		for (int l=0;l<=centres.length && (centres[l][1]!=0 || centres[l][0]!=0);l++) {	
			boolean test=true;
			int maxPoint=0;
			for (int li=0;li<=points.length && (points[li][1]!=0 || points[li][0]!=0);li++) {
				int diffx=	Math.abs(centres[l][0]-points[li][0]);
				int diffy=	Math.abs(centres[l][1]-points[li][1]);
				boolean testx=		diffx>img.getWidth()*0.85 	|| diffx<img.getWidth()*0.2;	//diff   <0.1 ou >0.8 x la largeur de feuille
				boolean testy=		diffy>img.getHeight()*0.8 	|| diffy<img.getWidth()*0.2;
				boolean Repeat=	diffx+diffy>img.getWidth()*0.2;	 //si point deja prÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©sent

				if (!Repeat || (!testx || !testy) )	// si 0.2>diffx>0.8 ou "diffy" et  
				{
					test=false;
				}
				maxPoint=li;
			}
			
			if(test && maxPoint<2) {
				//System.out.println(lastRoar[l][0]+"   "+lastRoar[l][1]);
				points[maxPoint+1][0]=centres[l][0];
				points[maxPoint+1][1]=centres[l][1];
			}
		}
		return points;

	} 





	/**
	 * indique le pixel fait partie d un cercle de taille radius
	 * @param x	pos x du pixel de l image
	 * @param y pos y du pixel de l image
	 * @param radius du cercle a detecter
	 * @return true si c'est un cercle
	 */
	public boolean checkCircle(int x,int y,int radius) {
		double pi=Math.PI; 
		Color tmp;
		for (double k=-1;k<=1;k+=0.05) {							// de 0 a 2 pi
			int px=x+(int)Math.round(radius*Math.cos(k*pi));		//px = pos x du contour du supposee du cercle
			int py=y+(int)Math.round(radius*Math.sin(k*pi));		//diam calculee  +/- 42 pixels
			if(py<0)py=0;
			if(py>=img.getHeight())py=img.getHeight()-1;
			if(px<0)px=0;
			if (px>=img.getWidth())px=img.getWidth()-1;
			tmp=new Color(img.getRGB(px, py));
			if (tmp.getGreen()>20) {								//si pixel est blanc
				return false;
			}
		}
		return true; 
	}


	List<Point> usedPoints=new ArrayList<Point>();
	public double searchAngle() {
		int extremite[][]=new int[2][2];
		
		for (int x=0;x<img.getWidth();x++) {
			for (int y=0;y<img.getHeight()*0.2;y++) {		//20% de la hauteur de la feuille
				
				int count=0;
				Color tmp=new Color(img.getRGB(x, y));
				if (tmp.getGreen()<20) { 						//au premier pixel noir
					extremite[0]=new int[] {x,y};
					int[][] roar=getContour(new int[] {x,y});	// recupere les pixels alentours
					count++;								
					while(roar!=null) {
						extremite[1]=roar[0];					
						roar=getContour(new int[] {roar[0][0],roar[0][1]});	//récupere la ligne de pixels noirs 
						count++;
					}
				}
				if(count>248 && count <496) {
					return getAngle(extremite);
				}
			}
		}
		
		return 0;
	}
	
	/**
	 * donne la position de tous les pixels à droite collés (à 1 case) 
	 * @param pos
	 * @return tableau de tableau de 2 int donnant la position de tous les pixels noir aux alentours
	 */
	private int[][] getContour(int[] pos){
		
		int retour[][]= new int[8][2];
		int i=0;
		for (int xi=Math.max(pos[0],0);xi<pos[0]+1;xi++) {
			//System.out.println("ok2");
			for (int yi=Math.max(0, pos[1]-1);yi<pos[1]+1;yi++) {
				Color tmp=new Color(img.getRGB(xi, yi));

				if (tmp.getGreen()<20 &&
						!(xi==pos[0] && yi==pos[1])) { 	//tous pixels noir n'étant pas le pixel de départ
					
					retour[i][0]=xi;
					retour[i][1]=yi;
					i++;
				}
			}
		}
		
		
		return null;
	}
	
	
	
	
	
	/**
	 * determine l'inclinaison de la feuille a partir de 4 points de la feuille
	 * @param tab tableau [x][y] des positions des centres des 4 points de la feuille
	 * @return	Angle en degrees de l inclinaison actuelle de la feuille (image).<br>
	 * 0 si erreur d entree.
	 */
	public double getAngle(int[][] tab) {
		double[] res= new double[4];int resi=0;
		int cmpt=0;
		int angle=0;
		for (int i=0;i<tab.length;i++) {
			if (!(tab[i][0]==0) || !(tab[i][1]==0))
				cmpt++;
		}
		
		if (cmpt<=1) {
			 return 0; //RIP CODE
		}
		
		for (int i=1;i<tab.length && (!(tab[i][0]==0) || !(tab[i][1]==0));i++) {
			for(int c=1;i<tab.length && (tab[c][0]!=0 || tab[c][1]!=0);i++)
			{
				int diffx=tab[i-1][0]-tab[i][0];
				int diffy=tab[i-1][1]-tab[i][1];
				
				double yb=tab[i][1];
				double xb=tab[i][0];
				double ya=tab[i-1][1];
				double xa=tab[i-1][0];
				
				double pointy,pointx;
				pointy=yb; 							//pointy/x = coord du 3e point de triangle rectangle
				pointx=xa;				
				
				double dhypo=Math.sqrt(Math.pow(xb-xa,2)+Math.pow(yb-ya,2));	//(yb-ya)/(xb-xa)
				double dadj=Math.sqrt(Math.pow(xb-pointx, 2)+Math.pow(yb-pointy, 2));	//adjacent / rapport a xb,yb
				
				
				if (dhypo<img.getWidth() && dhypo!=0) {		//deux points selectionnees sont des diagonales
					double retour=Math.acos(dadj/dhypo)*(180/Math.PI);
					if (retour>90/2)
						retour=180-90-retour;
					
					if((xa<xb && ya<yb )||( xb<xa && yb<ya))				//point de droite plus haut que celui de gauche
						return -retour;
					else
						return retour;
				}
				/*else {			//deux points sont en diagonnale
					double retour=Math.acos(dadj/dhypo)*(180/Math.PI);		// ne marche pas 
					return (Math.abs(45-retour)/2);
					
				}*/
			
				
			}
			
			
		}		
		return 0;
	}



	//rotation de 
	/**
	 * <a href=" https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java">code pris ici</a>, 
	 * rotationne l'image de x degrees
	 * @param bimg image a retourner
	 * @param angle angle de rotation
	 * @return image retournee
	 */
	public static BufferedImage rotate(BufferedImage bimg, double angle) {		
	    int w = bimg.getWidth();    
	    int h = bimg.getHeight();

	    BufferedImage rotated = new BufferedImage(w, h, bimg.getType());  
	    Graphics2D graphic = rotated.createGraphics();
	    graphic.rotate(Math.toRadians(angle), w/2, h/2);
	    graphic.drawImage(bimg, null, 0, 0);
	    graphic.dispose();
	    return rotated;
	}
	
	/**
	 * sauvegarde l image actuelle dans le root du projet, fonction de test
	 * @param name
	 * @throws IOException
	 */
	public void save(String name) throws IOException {
		File outputfile = new File(name+".jpg");
		ImageIO.write(this.img, "jpg", outputfile);
	}
	
}
