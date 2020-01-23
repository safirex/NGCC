package generation.factories;

import copies.Copie;
import generation.IncorrectCopieTypeException;
import generation.pdf.CopiePdfImpl;

public interface CopieAbstractFactory {
	public abstract CopiePdfImpl creerCopie(String type, Copie c) throws IncorrectCopieTypeException;
}
