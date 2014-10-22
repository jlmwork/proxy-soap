package prototypes.ws.proxy.soap.validation;

public class SoapException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8863295767211314294L;

	public SoapException(String message) {
		super(message);
	}

	public SoapException(String message, Exception e) {
		super(message, e);
	}
}
