package config;

import java.util.ArrayList;
import java.util.List;

public class Groupe {
	/**
	 *
	 */
	private ArrayList<Question> questions;
	private int nbQtChoisie;

	/*
	 *
	 * Groupes de selections aleatoires: Il est possible que certaines questions
	 * soient rassemblées dans un groupe de selections aleatoires. Le principe de ce
	 * type de groupe est de pouvoir selectionner n questions parmi les p questions
	 * du groupe (0<n<p).
	 *
	 */

	public Groupe(List<Question> q, int nb) {
		this.nbQtChoisie = nb;
		this.questions = (ArrayList<Question>) q;
	}

	public Groupe(int nbQtChoisie) {
		this(new ArrayList<Question>(), nbQtChoisie);
	}

	public Groupe() {
		// groupe sans n, c'est le groupe auquel toutes les questions sans groupe
		// aléatoire vont appartenir
		this(200);
	}

	public int getNbQtChoisie() {
		return this.nbQtChoisie;
	}

	public void setNbQtChoisie(int nbQtChoisie) {
		this.nbQtChoisie = nbQtChoisie;
	}

	public void addQuestions(Question q) {
		this.questions.add(q);
	}

	public ArrayList<Question> getQuestions() {
		return this.questions;
	}

	public void setQuestions(ArrayList<Question> questions) {
		this.questions = questions;
	}

}
