package database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import config.Question;
import database.tables.CopieDB;
import database.tables.EtudiantDB;
import database.tables.PageDB;
import jdk.jfr.Description;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDatabase {

	// TODO: add test for the replaceCopieDB method

	private DBManager dbManager;
	private List<EtudiantDB> etudiants = new ArrayList<>();
	private List<CopieDB> copies = new ArrayList<>();
	private List<PageDB> pages = new ArrayList<>();
	private Map<Integer, Double> notes = new HashMap<>();

	private final String db_fname = "./db/test.db";

	@Before
	public void setUp() throws ClassNotFoundException, SQLException {
		this.dbManager = DBManager.getInstance(this.db_fname);
	}

	@After
	public void clear() {
		// this.dbManager.disconnect();
		this.dbManager = null;
	}

	private void setStudMarkAssoc() {
		for (int i = 0; i < 10; i++) {
			this.notes.put(i, (double) (i + 1));
		}
	}

	private void createEtudiants(int n) {
		for (int i = 0; i < n; i++) {
			this.etudiants.add(new EtudiantDB(i));
		}
	}

	private void createCopies(int n) {
		int nbPages = 1;
		for (int i = 0; i < n; i++) {
			this.copies.add(new CopieDB(i + 10, nbPages, new ArrayList<Question>()));
		}
	}

	private void createPages(int n) {
		for (int i = 0; i < n; i++) {
			this.pages.add(new PageDB(i + 20, i + 10));
		}
	}

	@Test
	@Order(1)
	@Description("Test the creation of tables into the database.")
	public void testCreateTable() throws SQLException {
		this.dbManager.createTables();
	}

	@Test
	@Order(2)
	@Description("Test the insertion of students into the database.")
	public void testInsertIntoEtudiant() throws SQLException {
		this.createEtudiants(10);
		for (EtudiantDB etudiant : this.etudiants) {
			System.out.println("tentative with Student number " + etudiant.getIdEtudiant());
			this.dbManager.insertNewStudent(etudiant);
		}
	}

	@Test
	@Order(3)
	@Description("Test the insertion of copies into the database.")
	public void testInsertIntoCopie() throws SQLException {
		this.createCopies(10);
		for (CopieDB copie : this.copies) {
			System.out.println("tentative with Copy number " + copie.getIdCopie());
			this.dbManager.insertNewCopie(copie);
		}
	}

	@Test
	@Order(4)
	@Description("Test the insertion of pages into the database.")
	public void testInsertIntoPage() throws SQLException {
		this.createPages(10);
		for (PageDB page : this.pages) {
			System.out.println("tentative with page number " + page.getIdPage());
			this.dbManager.insertNewPage(page);
		}
	}

	@Test
	@Order(5)
	@Description("Test the selection of copies with a given copie's id.")
	public void testGetCopieWithIDCopie() throws ClassNotFoundException, SQLException, IOException {
		this.createCopies(10);
		for (CopieDB copie : this.copies) {
			System.out.println(copie);
			assertEquals(copie, this.dbManager.getCopieWithCopieID(copie.getIdCopie()));
		}
	}

	@Test
	@Order(6)
	@Description("Test the association of student with copie.")
	public void testStudentWithCopie() throws SQLException, ClassNotFoundException, IOException {
		for (int i = 0; i < 10; i++) {
			this.dbManager.associateStudentWithCopie(i + 10, i);
		}
		List<CopieDB> copies = this.dbManager.getAllCopies();
		int i = 0;
		for (CopieDB copie : copies) {
			System.out.println(copie);
			assertEquals(i++, copie.getIdEtudiant());
		}
	}

	@Ignore
	@Test
	@Order(7)
	@Description("Test the association of student with copie.")
	public void testAssociatePageWithImage() throws SQLException, IOException {
		for (int i = 0; i < 10; i++) {
			this.dbManager.associatePageWithImage(i + 20, new BufferedImage(100, 100, 1));
		}
		// TODO: add test part
	}

	@Test
	@Order(8)
	@Description("Test the association of a copie and a mark.")
	public void testMarkCopie() throws SQLException {
		for (int i = 0; i < 10; i++) {
			this.dbManager.markCopie(i + 10, i + 1);
		}
		// TODO: add test part
	}

	@Test
	@Order(9)
	@Description("Test the selection of student and their marks.")
	public void testGetStudentsWithMarks() throws SQLException {
		this.setStudMarkAssoc();
		assertEquals(this.notes, this.dbManager.getStudentsWithMarks());
	}

	@Test
	@Order(10)
	@Description("Test the selection of student without copie.")
	public void testGetUnmatchedCopie() throws SQLException {
		// TODO: complete test with different cases
		assertEquals(new ArrayList<Integer>(), this.dbManager.getUnmatchedCopie());
	}
}
