package generation.factories;

import copies.Copie;
import generation.IncorrectCopieTypeException;
import generation.pdf.CopiePdfImpl;
import generation.pdf.FeuilleReponses;
import generation.pdf.Fusion;
import generation.pdf.Sujet;

public class BlankFactory extends CopieFactory {

	public BlankFactory() {
		super();
	}

	@Override
	public CopiePdfImpl creerCopie(String type, Copie c) throws IncorrectCopieTypeException {
		switch (type.toLowerCase()) {
		case "sujet":
			return new Sujet(c);
		case "fusion":
			return new Fusion(c);
		case "reponses":
			return new FeuilleReponses(c);
		case "detachable":
			System.out.println("DETACHABLE"); // TODO: to complete
		default:
			throw new IncorrectCopieTypeException("Le type de copie donne est invalide.");
		}
	}
}
