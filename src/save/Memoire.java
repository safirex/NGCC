package save;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Based on http://www.jmdoudoux.fr/java/dej/chap-serialisation.htm
 * 
 * @author Mireille Blay
 *
 */

public class Memoire {

	static Logger monLog = Logger.getLogger(Memoire.class.getName());
	
	private Memoire() {
		super();
	}

	public static void save(Object o, String fileName) {

		try (FileOutputStream fichier = new FileOutputStream(fileName);
			 ObjectOutputStream oos = new ObjectOutputStream(fichier);) {
			oos.writeObject(o);
			oos.flush();
		} catch (java.io.IOException e) {
			monLog.log(Level.SEVERE, e.getMessage(), e);
		}
	}


	public static Object read(String fileName) {
		
		Object o = null;
		try (FileInputStream fichier = new FileInputStream(fileName);
			 ObjectInputStream ois = new ObjectInputStream(fichier);) {
			o = ois.readObject();
		} catch ( java.io.FileNotFoundException e) {
			monLog.log(Level.SEVERE, "Pas de precedentes sauvegardes",e);
		} catch (final java.io.IOException e) {
			monLog.log(Level.SEVERE, "Pbme de fichier",e);
		} catch (final ClassNotFoundException e) {
			monLog.log(Level.SEVERE, "Pbme de sauvegarde, classe inattendue",e);
		}
		return o;
	}
}