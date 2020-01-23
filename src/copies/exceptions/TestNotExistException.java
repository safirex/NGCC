package copies.exceptions;

public class TestNotExistException extends Exception {

	private static final long serialVersionUID = 1L;

	public TestNotExistException(String errorMessage) {
		super(errorMessage);
	}
}
