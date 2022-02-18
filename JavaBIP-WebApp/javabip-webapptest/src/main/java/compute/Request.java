package compute;

public class Request {
	private int id;
	private int randomNumber;
	private int counter;
	private int limit;
	private String server;
	
	Request (int id, int randomNumber, int counter, int limit, String server) {
		this.setId(id);
		this.setRandomNumber(randomNumber);
		this.setCounter(counter);
		this.setLimit(limit);
		this.setServer(server);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRandomNumber() {
		return randomNumber;
	}

	public void setRandomNumber(int randomNumber) {
		this.randomNumber = randomNumber;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}
}
