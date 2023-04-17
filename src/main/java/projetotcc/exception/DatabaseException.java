package projetotcc.exception;

public class DatabaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DatabaseException(String msg) {
		super(msg);
	}
	
	public DatabaseException(Exception e) {
		super(e);
	}

}
