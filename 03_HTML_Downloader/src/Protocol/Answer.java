package Protocol;

import java.io.File;

public class Answer extends Message{

	private static final long serialVersionUID = 1673198899993802327L;

	//Values of status: ok, error, warning
	private String status = "";
	private String message = "";
	private String html_content;

	public String getHtml_content() {
		return html_content;
	}

	public void setHtml_content(String html_content) {
		this.html_content = html_content;
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