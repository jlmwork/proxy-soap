package prototypes.ws.proxy.soap.validation;

public class NotFoundSoapException extends SoapException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8258043481209571731L;

	public NotFoundSoapException(String message, Exception e) {
		super(message, e);
	}

	public NotFoundSoapException(String message) {
		super(message);
	}
}
