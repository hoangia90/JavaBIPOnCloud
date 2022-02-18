package compute;

public class LimitCounter implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int counter = 0;

    public void increment() {
        counter++;
    }

    public void decrement() {
        counter--;
    }

    public void reset() {
        counter = 0;
    }

    public int getCounter() {

        return counter;
    }

}
