package compute;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "request")
public class Request {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "id")
	private int id;
	@Column(name = "randomNumber")
	private int randomNumber;
	@Column(name = "server")
	private String server;
	@Column(name = "counter")
	private int counter;
	@Column(name = "requestLimit")
	private int requestLimit;

	public Request() {
		// TODO Auto-generated constructor stub
	}

	Request(int randomNumber, String server, int counter, int requestLimit) {
		this.setRandomNumber(randomNumber);
		this.setServer(server);
		this.setCounter(counter);
		this.setRequestLimit(requestLimit);
	}

	Request(int id, int randomNumber, String server, int counter, int limit) {
		this.setId(id);
		this.setRandomNumber(randomNumber);
		this.setServer(server);
		this.setCounter(counter);
		this.setRequestLimit(requestLimit);
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

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public int getRequestLimit() {
		return requestLimit;
	}

	public void setRequestLimit(int requestLimit) {
		this.requestLimit = requestLimit;
	}
}
