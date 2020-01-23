package generation.factories;

import copies.Copie;
import generation.IncorrectCopieTypeException;
import generation.pdf.CopiePdfImpl;
import generation.pdf.FeuilleReponsesCorrige;
import generation.pdf.FusionCorrige;

public class CorrigeFactory extends CopieFactory {

	public CorrigeFactory() {
		super();
	}

	@Override
	public CopiePdfImpl creerCopie(String type, Copie c) throws IncorrectCopieTypeException {
		switch (type.toLowerCase()) {
		case "fusion":
			return new FusionCorrige(c);
		case "reponses":
			return new FeuilleReponsesCorrige(c);
		case "detachable":
			System.out.println("DETACHABLE"); // TODO: to complete
		default:
			throw new IncorrectCopieTypeException("Le type de copie donne est invalide.");
		}
	}
}
