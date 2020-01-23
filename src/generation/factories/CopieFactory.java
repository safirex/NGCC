package generation.factories;

import copies.Copie;
import generation.IncorrectCopieTypeException;
import generation.pdf.CopiePdfImpl;

public abstract class CopieFactory implements CopieAbstractFactory {

	public CopieFactory() {
	}

	@Override
	public CopiePdfImpl creerCopie(String type, Copie c) throws IncorrectCopieTypeException {
		CopiePdfImpl copiePdf = this.creerCopie(type, c);
		return copiePdf;
	}
}
