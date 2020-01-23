package copies.exceptions;

public class EmptyQuestionsException extends Exception {

	private static final long serialVersionUID = 1L;

	public EmptyQuestionsException(String errorMessage) {
		super(errorMessage);
	}
}
