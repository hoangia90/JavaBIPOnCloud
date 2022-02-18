package test;

import monitor.MonitorResult;
//import jdk.nashorn.internal.runtime.regexp.RegExpResult;

public class sample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String abc = new String();
		abc = "1";
		String def = abc;
		def = "2";
		System.out.println(abc);
		System.out.println(def);
		
		StringBuilder test = new StringBuilder("hello");
	    @SuppressWarnings("unused")
		StringBuilder myname = test;
	    test.replace(0, test.length(), "how are you?");
	    
	    MonitorResult re = new MonitorResult("1");
	    MonitorResult re2 = re;
	    re.setHttpResponse("2");
	    re.printHttpResponse();
	    re2.printHttpResponse();
	    
	}

}
