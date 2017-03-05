package Protocol;

public class Request extends Message{

	private static final long serialVersionUID = 4952049042016667859L;
	private String message = "";
	
	public Request(String message)
	{
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;

	}
}
