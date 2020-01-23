package copies.exceptions;

public class IdCopieOutOfBoundsException extends Exception{

	private static final long serialVersionUID = 1L;

	public IdCopieOutOfBoundsException(String errorMessage) {
		super(errorMessage);
	}
}
