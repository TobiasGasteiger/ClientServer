package Protocol;


public class Answer extends Message{

	private static final long serialVersionUID = 1673198899993802327L;

	//Values of status: ok, error, warning, close
	private String status = "";
	private String message = "";
	private byte[] data;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Answer(String status, String message){
		setStatus(status);
		setMessage(message);
	}

	public String getMessage() {
		return message;
	}

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