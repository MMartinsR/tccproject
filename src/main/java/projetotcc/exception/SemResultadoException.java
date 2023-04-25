package projetotcc.exception;

public class SemResultadoException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public SemResultadoException(String msg) {
		super(msg);
	}
	
	public SemResultadoException(Exception e) {
		super(e);
	}

}
