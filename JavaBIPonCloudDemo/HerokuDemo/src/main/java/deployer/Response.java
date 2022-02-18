package deployer;

public class Response {
	int responseCode;
	String JSONContent;
	
	public Response(int responseCode, String jSONContent) {
		super();
		this.responseCode = responseCode;
		JSONContent = jSONContent;
	}
	
	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getJSONContent() {
		return JSONContent;
	}

	public void setJSONContent(String jSONContent) {
		JSONContent = jSONContent;
	}
	
}
