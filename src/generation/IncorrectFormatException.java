package generation;

public class IncorrectFormatException extends Exception {

	private static final long serialVersionUID = 1L;

	public IncorrectFormatException(String errorMessage) {
		super(errorMessage);
	}
}
