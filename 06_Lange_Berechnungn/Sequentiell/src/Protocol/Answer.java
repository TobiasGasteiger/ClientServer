package Protocol;

public class Answer extends Message{

	private static final long serialVersionUID = 1673198899993802327L;

	//Values of status: ok, error, warning
	private String status = "";
	private String message = "";


	public Answer(String status, String message){
		setStatus(status);
		setMessage(message);
	}
	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;

	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
		
	}
}