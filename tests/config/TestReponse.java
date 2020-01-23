package config;

import static org.junit.Assert.assertEquals;

import java.awt.Point;

import org.junit.jupiter.api.Test;

public class TestReponse {
	@Test
	void testReponse() {
	Reponse r=new Reponse("Je suis la r�ponse",true,2);
	 assertEquals("Je suis la r�ponse",r.getIntitule());
	 assertEquals(true,r.isJuste());
	 r.setIntitule("mon intitule");
	 r.setJuste(false);
	 r.getNumPage();
	 r.setNumPage(3);
	 
	 Reponse re=new Reponse("Je suis la r�ponse",true);
	 re.cloneEmpty();
	 Point point = new Point();
	 assertEquals(point, re.getP());
	 Point newPoint = new Point(1,1);
	 re.setP(new Point(1,1));
	 assertEquals(newPoint, re.getP());
	 }
	
}
