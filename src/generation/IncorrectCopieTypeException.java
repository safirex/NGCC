package generation;

public class IncorrectCopieTypeException extends Exception {
	private static final long serialVersionUID = 1L;

	public IncorrectCopieTypeException(String errorMessage) {
		super(errorMessage);
	}
}
