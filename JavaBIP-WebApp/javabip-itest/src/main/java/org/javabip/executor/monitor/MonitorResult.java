package org.javabip.executor.monitor;

public class MonitorResult {

	private String httpResponse;
	
	public MonitorResult(String httpResponse) {
		this.httpResponse = httpResponse;
	}
	
	public MonitorResult() {
		this.httpResponse = new String();
	}
	
	public String getHttpResponse() {
		return httpResponse;
	}

	public void setHttpResponse(String httpResponse) {
		this.httpResponse = httpResponse;
	}
	
	public void printHttpResponse() {
		System.out.println(" HTTP Response: " + httpResponse);
	}

}
