package database;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.tables.CopieDB;
import database.tables.EtudiantDB;
import database.tables.PageDB;

public class DBManager {

	// TODO: voir idpage et numPage
	// TODO: voir connection + quand close ?

	// Voir : https://www.rgagnon.com/javadetails/java-0117.html

	private static DBManager INSTANCE = null;

	private Connection connection;

	// Table's creation queries
	private final String etudiantCreateQuery = "CREATE TABLE IF NOT EXISTS ETUDIANT (idetudiant INT PRIMARY KEY NOT NULL)";
	private final String copieCreateQuery = "CREATE TABLE IF NOT EXISTS COPIE (idcopie INT PRIMARY KEY NOT NULL, idetudiant INT, note REAL, copie BLOB, FOREIGN KEY(idetudiant) REFERENCES ETUDIANT(idetudiant))";
	private final String pageCreateQuery = "CREATE TABLE IF NOT EXISTS PAGE (idpage INT PRIMARY KEY NOT NULL, idcopie INT, page BLOB, FOREIGN KEY(idcopie) REFERENCES COPIE(idcopie))";

	/**
	 * Retourne l'unique instance de <code>DBManager</code> (Singleton design
	 * pattern)
	 *
	 * @param fname chemin vers la base de donnee
	 * @return L'instance de <code>DBManager</code>
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public final static DBManager getInstance(String fname) throws ClassNotFoundException, SQLException {
		// Singleton design pattern
		if (DBManager.INSTANCE == null) {

			synchronized (DBManager.class) {
				if (DBManager.INSTANCE == null) {
					DBManager.INSTANCE = new DBManager();
					DBManager.INSTANCE.connectToDB(fname);
				}
			}
		}
		return DBManager.INSTANCE;
	}

	private DBManager() throws ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
	}

	/**
	 * Initialise la connection a la base de donnee
	 *
	 * @param fname chemin vers la base de donnee
	 * @throws SQLException
	 */
	private void connectToDB(String fname) throws SQLException {
		this.connection = DriverManager.getConnection("jdbc:sqlite:" + fname);
		this.connection.setAutoCommit(false);
		System.out.println("Connection state : connected");
	}

	// TABLE CREATION METHOD

	/**
	 * Methode de creation de tables
	 *
	 * @throws SQLException
	 */
	public void createTables() throws SQLException {
		List<String> tablesQueries = Arrays.asList(this.etudiantCreateQuery, this.copieCreateQuery,
				this.pageCreateQuery);
		Statement stmt = this.connection.createStatement();
		try {
			for (String query : tablesQueries) {
				stmt.executeUpdate(query);
			}
			this.connection.commit();
			stmt.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		stmt.close();
	}

	// INSERTIONS METHODS

	/**
	 * <code>INSERT</code> query for <code>EtudiantDB</code> table
	 *
	 * @param etudiant etudiant qui sera stocke dans la base de donnee
	 */
	public void insertNewStudent(EtudiantDB etudiant) {
		// TODO: revoir PreapredStatement
		try {
			PreparedStatement pstmt = this.connection
					.prepareStatement("INSERT INTO ETUDIANT VALUES (" + etudiant.getIdEtudiant() + ")");
			pstmt.executeUpdate();
			this.connection.commit();
			pstmt.close();
			System.out.println("Student number " + etudiant.getIdEtudiant() + " successfully insered !");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <code>INSERT</code> query for <code>CopieDB</code> table
	 *
	 * @param copie copie qui sera stocke dans la base de donnee
	 */
	public void insertNewCopie(CopieDB copie) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(copie);
			// serialize the copy object into a byte array
			byte[] copyAsBytes = baos.toByteArray();
			PreparedStatement pstmt = this.connection
					.prepareStatement("INSERT INTO COPIE (idcopie, copie) VALUES (" + copie.getIdCopie() + ", ?)");
			ByteArrayInputStream bais = new ByteArrayInputStream(copyAsBytes);
			// bind our byte array to the copy column
			pstmt.setBinaryStream(1, bais, copyAsBytes.length);
			pstmt.executeUpdate();
			this.connection.commit();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <code>INSERT</code> query for <code>PageDB</code> table
	 *
	 * @param page page qui sera stocke dans la base de donnee
	 */
	public void insertNewPage(PageDB page) {
		// TODO: choisir si l'on stocke l'instance de page ou non (partie en
		// commentaire)
		try {
			/*-ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(page);
			// serialize the copy object into a byte array
			byte[] pageAsBytes = baos.toByteArray();*/
			PreparedStatement pstmt = this.connection.prepareStatement(
					"INSERT INTO PAGE (idpage, idcopie) VALUES (" + page.getIdPage() + ", " + page.getIdCopie() + ")");
			/*-ByteArrayInputStream bais = new ByteArrayInputStream(pageAsBytes);
			// bind our byte array to the copy column
			pstmt.setBinaryStream(1, bais, pageAsBytes.length);*/
			pstmt.executeUpdate();
			this.connection.commit();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// SELECTIONS/RETRIEVALS METHODS

	/**
	 * Recupere toutes les copies presentes dans la base de donnees
	 *
	 * @return les copies presentes dans la base de donnees
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public List<CopieDB> getAllCopies() throws SQLException, IOException, ClassNotFoundException {
		String query = "SELECT copie, idEtudiant FROM COPIE";
		CopieDB copie = null;
		List<CopieDB> copies = new ArrayList<CopieDB>();
		Statement stmt = this.connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			byte[] st = (byte[]) rs.getObject(1);
			ByteArrayInputStream baip = new ByteArrayInputStream(st);
			ObjectInputStream ois = new ObjectInputStream(baip);
			// re-create the object
			copie = (CopieDB) ois.readObject();
			// on associe l'idetudiant a la copie
			copie.setIdEtudiant(rs.getInt("idEtudiant"));
			copies.add(copie);
		}
		return copies;
	}

	/**
	 * Requete <code>SELECT</code> pour retrouver une copie a partir d'un id
	 *
	 * @param id id de la copie
	 * @return la copie correspondant a l'id
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public CopieDB getCopieWithCopieID(int id) throws SQLException, ClassNotFoundException, IOException {
		CopieDB copie = null;

		Statement stmt = this.connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT copie FROM COPIE WHERE idcopie = " + id);
		// get the next result set
		rs.next();
		// fetch the serialized object to a byte array
		byte[] st = (byte[]) rs.getObject(1);
		// or byte[] st = rs.getBytes(1);
		// or Blob aBlob = rs.getBlob(1);
		// byte[] st = aBlob.getBytes(0, (int) aBlob.length());
		ByteArrayInputStream baip = new ByteArrayInputStream(st);
		ObjectInputStream ois = new ObjectInputStream(baip);
		// re-create the object
		copie = (CopieDB) ois.readObject();
		stmt.close();
		rs.close();

		return copie;
	}

	/**
	 * Associe un numero d'etudiant a une note pour chaque etudiant present dans la
	 * base de donnees
	 *
	 * @return L'association numeroEtudiant/note
	 * @throws SQLException
	 */
	public Map<Integer, Double> getStudentsWithMarks() throws SQLException {
		// servira pr CSV
		// stockera les associations idetudiant-note
		Map<Integer, Double> result = new HashMap<>();
		int idEtudiant;
		// statements
		Statement stmtA;
		Statement stmtB;
		// resultsets
		ResultSet rsA;
		ResultSet rsB;
		// on recupere tous les idetudiants
		stmtA = this.connection.createStatement();
		rsA = stmtA.executeQuery("SELECT idetudiant FROM ETUDIANT");
		// pour chaque idetudiant
		while (rsA.next()) {
			idEtudiant = rsA.getInt("idetudiant");
			// on recupere la note associee
			stmtB = this.connection.createStatement();
			rsB = stmtB.executeQuery("SELECT note FROM COPIE WHERE idetudiant = " + idEtudiant);
			result.put(idEtudiant, rsB.getDouble("note"));
		}
		return result;
	}

	/**
	 * Recupere les copies n'ayant pas ete associees a un numero d'etudiant
	 *
	 * @return les id de copies n'ayant pas de numero d'etudiant associe
	 * @throws SQLException
	 */
	public List<Integer> getUnmatchedCopie() throws SQLException {
		List<Integer> copiesId = new ArrayList<>();
		Statement stmt = this.connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT idcopie FROM COPIE WHERE idetudiant IS NULL");
		while (rs.next()) {
			copiesId.add(rs.getInt("idcopie"));
		}
		return copiesId;
	}

	/**
	 * Recupere les pages de la copie manquantes
	 *
	 * @param copie Copie dont on souhaite verifier l'integrite
	 * @return les id de pages manquantes
	 * @throws SQLException
	 */
	public List<Integer> getMissingPagesForCopie(CopieDB copie) throws SQLException {
		// TODO: retrieve missing from pagesID list
		List<Integer> pagesId = new ArrayList<>();
		Statement stmt = this.connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT idpage FROM PAGE WHERE idcopie = " + copie.getIdCopie());
		while (rs.next()) {
			pagesId.add(rs.getInt("idpage"));
		}
		return pagesId;
	}

	// MODIFICATIONS METHODS

	/**
	 * Associe un etudiant a une copie
	 *
	 * @param idCopie    id de la copie que l'on souhaite associer a un etudiant
	 * @param idEtudiant id de l'etudiant pour lequel on souhaite associer une copie
	 * @throws SQLException
	 */
	public void associateStudentWithCopie(int idCopie, int idEtudiant) throws SQLException {
		String query = "UPDATE COPIE SET idetudiant = " + idEtudiant + " WHERE idcopie = " + idCopie + ";";
		Statement stmt = this.connection.createStatement();
		stmt.executeUpdate(query);
		this.connection.commit();
		stmt.close();
	}

	/**
	 * Associe une <code>BufferedImage</code> a une page
	 *
	 * @param idPage id de la page pour lequel on souhaite associer une image
	 * @param image  image que l'on souhaite associer a une page
	 * @throws SQLException
	 * @throws IOException
	 */
	public void associatePageWithImage(int idPage, BufferedImage image) throws SQLException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(image);
		// serialize the copy object into a byte array
		byte[] imgAsBytes = baos.toByteArray();
		PreparedStatement pstmt = this.connection
				.prepareStatement("UPDATE PAGE SET image = ? WHERE idpage = " + idPage + ";");
		ByteArrayInputStream bais = new ByteArrayInputStream(imgAsBytes);
		// bind our byte array to the copy column
		pstmt.setBinaryStream(1, bais, imgAsBytes.length);
		pstmt.executeUpdate();
		this.connection.commit();
		pstmt.close();
	}

	/**
	 * Remplace une copie par une autre copie
	 *
	 * @param copie la nouvelle copie
	 * @throws IOException
	 * @throws SQLException
	 */
	public void replaceCopieDB(CopieDB copie) throws IOException, SQLException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(copie);
		// serialize the copy object into a byte array
		byte[] imgAsBytes = baos.toByteArray();
		PreparedStatement pstmt = this.connection
				.prepareStatement("UPDATE COPIE SET copie = ? WHERE idcopie = " + copie.getIdCopie() + ";");
		ByteArrayInputStream bais = new ByteArrayInputStream(imgAsBytes);
		// bind our byte array to the copy column
		pstmt.setBinaryStream(1, bais, imgAsBytes.length);
		pstmt.executeUpdate();
		this.connection.commit();
		pstmt.close();
	}

	/**
	 * Associe une note a une copie
	 *
	 * @param idCopie id de la copie pour laquelle on associe une note
	 * @param mark    note donnee a la copie
	 * @throws SQLException
	 */
	public void markCopie(int idCopie, double mark) throws SQLException {
		String query = "UPDATE COPIE SET note = " + mark + " WHERE idcopie = " + idCopie + ";";
		Statement stmt = this.connection.createStatement();
		stmt.executeUpdate(query);
		this.connection.commit();
		stmt.close();
	}

	// OTHERS

	/**
	 * Deconnecte l'utilisateur a la base de donnees
	 */
	public void disconnect() {
		try {
			this.connection.close();
			System.out.println("Connection state : closed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
