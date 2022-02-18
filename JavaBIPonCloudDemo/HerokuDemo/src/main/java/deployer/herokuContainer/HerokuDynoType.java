package deployer.herokuContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.javabip.annotations.ComponentType;
import org.javabip.annotations.Data;
import org.javabip.annotations.Port;
import org.javabip.annotations.Ports;
import org.javabip.annotations.Transition;
import org.javabip.annotations.Transitions;
import org.javabip.api.PortType;

import deployer.BIPDeployer;
import deployer.Response;

@Ports({
	// Plan
	@Port(name = "sub1", type = PortType.enforceable),
//	@Port(name = "sub2", type = PortType.spontaneous),
//	@Port(name = "sub3", type = PortType.spontaneous),
//	@Port(name = "sub4", type = PortType.spontaneous),
//	@Port(name = "sub5", type = PortType.spontaneous),
//	@Port(name = "sub6", type = PortType.spontaneous),

	// Upgrade from 1
//	@Port(name = "up1t2", type = PortType.spontaneous),
//	@Port(name = "up1t3", type = PortType.spontaneous),
//	@Port(name = "up1t4", type = PortType.spontaneous),
//	@Port(name = "up1t5", type = PortType.spontaneous),
//	@Port(name = "up1t6", type = PortType.spontaneous),
	
	// Upgrade from 2
//	@Port(name = "up2t3", type = PortType.spontaneous),
//	@Port(name = "up2t4", type = PortType.spontaneous),
//	@Port(name = "up2t5", type = PortType.spontaneous),
//	@Port(name = "up2t6", type = PortType.spontaneous),
	
	// Upgrade from 3
//	@Port(name = "up3t4", type = PortType.spontaneous),
//	@Port(name = "up3t5", type = PortType.spontaneous),
//	@Port(name = "up3t6", type = PortType.spontaneous),

	//Upgrade from 4
//	@Port(name = "up4t5", type = PortType.spontaneous),
//	@Port(name = "up4t6", type = PortType.spontaneous),

	// Upgrade from 5
//	@Port(name = "up5t6", type = PortType.spontaneous),

	// Down-grade from 6
//	@Port(name = "down6t1", type = PortType.spontaneous),
//	@Port(name = "down6t2", type = PortType.spontaneous),
//	@Port(name = "down6t3", type = PortType.spontaneous),
//	@Port(name = "down6t4", type = PortType.spontaneous),
//	@Port(name = "down6t5", type = PortType.spontaneous),
	
	// Down-grade from 5
//	@Port(name = "down5t1", type = PortType.spontaneous),
//	@Port(name = "down5t2", type = PortType.spontaneous),
//	@Port(name = "down5t3", type = PortType.spontaneous),
//	@Port(name = "down5t4", type = PortType.spontaneous),

	// Down-grade from 4
//	@Port(name = "down4t1", type = PortType.spontaneous),
//	@Port(name = "down4t2", type = PortType.spontaneous),
//	@Port(name = "down4t3", type = PortType.spontaneous),
	
	// Down-grade from 3
//	@Port(name = "down3t1", type = PortType.spontaneous),
//	@Port(name = "down3t2", type = PortType.spontaneous),
	
	// Down-grade from 2
//	@Port(name = "down2t1", type = PortType.spontaneous),

	// reset
	@Port(name = "reset1", type = PortType.enforceable),
//	@Port(name = "reset2", type = PortType.enforceable),
//	@Port(name = "reset3", type = PortType.enforceable),
//	@Port(name = "reset4", type = PortType.enforceable),
//	@Port(name = "reset5", type = PortType.enforceable),
//	@Port(name = "reset6", type = PortType.enforceable),

	//Data
	@Port(name = "sendDynoResponse", type = PortType.enforceable),
})

@ComponentType(initial = "Init", name = "deployer.HerokuDynoType")
public class HerokuDynoType {
	
	//
	Response dynoCreatingResponse;
	@Transitions({
		@Transition(name = "sub1", source = "Init", target = "Free"),
	})
	public void sub1(@Data(name = "DynoOptions") ArrayList<String> DynoOptions) throws IOException {	
		System.out.println("[HerokuDynoType] sub1 Free triggered .... (Init --> Free)");
		URL obj = new URL("https://api.heroku.com/apps");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		//add request header
		con.setRequestProperty("Accept", "application/vnd.heroku+json; version=3");
		con.setRequestProperty("Authorization", "Bearer " + BIPDeployer.authorizationToken);
		con.setRequestProperty("Content-Type", "application/json");
		con.setDoOutput(true);
		
		if (!DynoOptions.get(0).isEmpty()) {
			DynoOptions.set(0, "\"name\": \"" + DynoOptions.get(0) +"\"");
		} 
		if (!DynoOptions.get(1).isEmpty()) { 
			DynoOptions.set(1, "  \"region\": \"" + DynoOptions.get(1) + "\"");
		} 
		if (!DynoOptions.get(2).isEmpty()) { 
			DynoOptions.set(2, "  \"stack\": \"" + DynoOptions.get(2) + "\"");
		}
		String jsonInputString = "{";
		boolean first = false;
		for (int i = 0; i < DynoOptions.size(); i++) {
			if (!DynoOptions.get(i).isEmpty() && !first) {
				jsonInputString = jsonInputString + DynoOptions.get(i);
				first = true;
			} else {
				if (!DynoOptions.get(i).isEmpty()) {
				jsonInputString = jsonInputString + "," + DynoOptions.get(i);
				}
			}
		}
		jsonInputString = jsonInputString + "}";
		
		System.err.println(jsonInputString);
		try(OutputStream os = con.getOutputStream()) {
		    byte[] input = jsonInputString.getBytes("utf-8");
		    os.write(input, 0, input.length);           
		}
		//
		int responseCode = con.getResponseCode();
//		System.out.println("\nSending 'GET' request to URL : " );
//		System.out.println("Response Code : " + responseCode);
		//
		String inputLine = null;
		StringBuffer jSONContent = new StringBuffer();
		BufferedReader in;
		if (responseCode < 300) {
			 in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			//
			while ((inputLine = in.readLine()) != null) {
				jSONContent.append(inputLine);
			}
		} else {
			 in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			//
			while ((inputLine = in.readLine()) != null) {
				jSONContent.append(inputLine);
			}
		}
		in.close();
		//
//		System.out.println(jSONContent.toString());
		dynoCreatingResponse = new Response(responseCode, jSONContent.toString());
	}
	
	@Transitions({
		@Transition(name = "sendDynoResponse", source = "Free", target = "Free"),
	})
	public void sendDynoResponse() throws IOException {	
		System.out.println("[HerokuDynoType] sendDynoResponse Free triggered .... (Init --> Free)");
	}
	// Adding more options for deploying
	@Data(name = "DynoResponseData")
	public Response DynoResponseData() {
		return dynoCreatingResponse;
	}
	
	
	
//	@Transitions({
//		@Transition(name = "sub2", source = "Init", target = "Hobby"),
//	})
//	public void sub2() {	
//		System.out.println("[HerokuDynoType] sub2 Hobby triggered .... (Init --> Hobby)");
////		System.out.println("[HerokuDynoType] Init --> Hobby");
//	}
	
//	@Transitions({
//		@Transition(name = "sub3", source = "Init", target = "Standard1X"),
//	})
//	public void sub3() {	
//		System.out.println("[HerokuDynoType] sub Standard1X triggered ....");
//		System.out.println("[HerokuDynoType] SwitchReady --> ServerReady");
//	}
//	
//	@Transitions({
//		@Transition(name = "sub4", source = "Init", target = "Standard2X"),
//	})
//	public void sub4() {	
//		System.out.println("[HerokuDynoType] sub Standard2X triggered ....");
//		System.out.println("[HerokuDynoType] SwitchReady --> ServerReady");
//	}
//	
//	@Transitions({
//		@Transition(name = "sub5", source = "Init", target = "PerformanceM"),
//	})
//	public void sub5() {	
//		System.out.println("[HerokuDynoType] sub PerformanceM triggered ....");
//		System.out.println("[HerokuDynoType] SwitchReady --> ServerReady");
//	}
//	
//	@Transitions({
//		@Transition(name = "sub6", source = "Init", target = "PerformanceL"),
//	})
//	public void sub6() {	
//		System.out.println("[HerokuDynoType] sub PerformanceL triggered ....");
//		System.out.println("[HerokuDynoType] SwitchReady --> ServerReady");
//	}
//	
//	
//	
//// 1 up	Free Hobby Standard1X Standard2X PerformanceM PerformanceL
//	@Transitions({
//		@Transition(name = "up1t2", source = "Free", target = "Hobby"),
//	})
//	public void up1t2() {	
//		System.out.println("[HerokuDynoType] ub1t2 triggered ....");
//		System.out.println("[HerokuDynoType] Free --> Hobby");
//	}
//	
//	@Transitions({
//		@Transition(name = "up1t3", source = "Free", target = "Standard1X"),
//	})
//	public void up1t3() {	
//		System.out.println("[HerokuDynoType] up1t3 triggered ....");
//		System.out.println("[HerokuDynoType] Free --> Standard1X");
//	}
//	
//	@Transitions({
//		@Transition(name = "up1t4", source = "Free", target = "Standard2X"),
//	})
//	public void up1t4() {	
//		System.out.println("[HerokuDynoType] ub1t4 triggered ....");
//		System.out.println("[HerokuDynoType] Free --> Standard2X");
//	}
//	
//	@Transitions({
//		@Transition(name = "up1t5", source = "Free", target = "PerformanceM"),
//	})
//	public void up1t5() {	
//		System.out.println("[HerokuDynoType] up1t5 triggered ....");
//		System.out.println("[HerokuDynoType] Free --> PerformanceM");
//	}
//	
//	@Transitions({
//		@Transition(name = "up1t6", source = "Free", target = "PerformanceL"),
//	})
//	public void up1t6() {	
//		System.out.println("[HerokuDynoType] up1t6 triggered ....");
//		System.out.println("[HerokuDynoType] Free --> PerformanceL");
//	}
//	
//	
////	2 up Free Hobby Standard1X Standard2X PerformanceM PerformanceL
//	@Transitions({
//		@Transition(name = "up2t3", source = "Hobby", target = "Standard1X"),
//	})
//	public void up2t3() {	
//		System.out.println("[HerokuDynoType] up2t3 triggered ....");
//		System.out.println("[HerokuDynoType] Hobby --> Standard1X");
//	}
//	
//	@Transitions({
//		@Transition(name = "up2t4", source = "Hobby", target = "Standard2X"),
//	})
//	public void up2t4() {	
//		System.out.println("[HerokuDynoType] up2t4 triggered ....");
//		System.out.println("[HerokuDynoType] Hobby --> Standard2X");
//	}
//	
//	@Transitions({
//		@Transition(name = "up2t5", source = "Hobby", target = "PerformanceM"),
//	})
//	public void up2t5() {	
//		System.out.println("[HerokuDynoType] up2t5 triggered ....");
//		System.out.println("[HerokuDynoType] Hobby --> PerformanceM");
//	}
//	
//	@Transitions({
//		@Transition(name = "up2t6", source = "Hobby", target = "PerformanceL"),
//	})
//	public void up2t6() {	
//		System.out.println("[HerokuDynoType] up2t6 triggered ....");
//		System.out.println("[HerokuDynoType] Hobby --> PerformanceL");
//	}
//	
//	
////	3 up Free Hobby Standard1X Standard2X PerformanceM PerformanceL
//	@Transitions({
//		@Transition(name = "up3t4", source = "Standard1X", target = "Standard2X"),
//	})
//	public void up3t4() {	
//		System.out.println("[HerokuDynoType] up3t4 triggered ....");
//		System.out.println("[HerokuDynoType] Standard1X --> Standard2X");
//	}
//	
//	@Transitions({
//		@Transition(name = "up3t5", source = "Standard1X", target = "PerformanceM"),
//	})
//	public void up3t5() {	
//		System.out.println("[HerokuDynoType] up3t5 triggered ....");
//		System.out.println("[HerokuDynoType] Standard1X --> PerformanceM");
//	}
//	
//	@Transitions({
//		@Transition(name = "up3t6", source = "Standard1X", target = "PerformanceL"),
//	})
//	public void up3t6() {	
//		System.out.println("[HerokuDynoType] up3t6 triggered ....");
//		System.out.println("[HerokuDynoType] Standard1X --> PerformanceL");
//	}
//	
//	
////	4 up Free Hobby Standard1X Standard2X PerformanceM PerformanceL
//	@Transitions({
//		@Transition(name = "up4t5", source = "Standard2X", target = "PerformanceM"),
//	})
//	public void up4t5() {	
//		System.out.println("[HerokuDynoType] up4t5 triggered ....");
//		System.out.println("[HerokuDynoType] Standard2X --> PerformanceM");
//	}
//	
//	@Transitions({
//		@Transition(name = "up4t6", source = "Standard2X", target = "PerformanceL"),
//	})
//	public void up4t6() {	
//		System.out.println("[HerokuDynoType] up4t6 triggered ....");
//		System.out.println("[HerokuDynoType] Standard2X --> PerformanceL");
//	}
//	
//	
////	5 up Free Hobby Standard1X Standard2X PerformanceM PerformanceL
//	@Transitions({
//		@Transition(name = "up5t6", source = "PerformanceM", target = "PerformanceL"),
//	})
//	public void up5t6() {	
//		System.out.println("[HerokuDynoType] up5t6 triggered ....");
//		System.out.println("[HerokuDynoType] PerformanceM --> PerformanceL");
//	}
//	
//	
//
//	
//// 6 down	tree Hobby Standard1X Standard2X PerformanceM PerformanceL
//	@Transitions({
//		@Transition(name = "down6t1", source = "PerformanceL", target = "Free"),
//	})
//	public void down6t1() {	
//		System.out.println("[HerokuDynoType] down6t1 triggered ....");
//		System.out.println("[HerokuDynoType] PerformanceL --> Free");
//	}
//	
//	@Transitions({
//		@Transition(name = "down6t2", source = "PerformanceL", target = "Hobby"),
//	})
//	public void down6t2() {	
//		System.out.println("[HerokuDynoType] down6t2 triggered ....");
//		System.out.println("[HerokuDynoType] PerformanceL --> Hobby");
//	}
//	
//	@Transitions({
//		@Transition(name = "down6t3", source = "PerformanceL", target = "Standard1X"),
//	})
//	public void down6t3() {	
//		System.out.println("[HerokuDynoType] down6t3 triggered ....");
//		System.out.println("[HerokuDynoType] PerformanceL --> Standard1X");
//	}
//	
//	@Transitions({
//		@Transition(name = "down6t4", source = "PerformanceL", target = "Standard2X"),
//	})
//	public void down6t4() {	
//		System.out.println("[HerokuDynoType] down6t4 triggered ....");
//		System.out.println("[HerokuDynoType] PerformanceL --> Standard2X");
//	}
//	
//	@Transitions({
//		@Transition(name = "down6t5", source = "PerformanceL", target = "PerformanceM"),
//	})
//	public void down6t5() {	
//		System.out.println("[HerokuDynoType] down6t5 triggered ....");
//		System.out.println("[HerokuDynoType] PerformanceL --> PerformanceM");
//	}
//
//	
//// 5 down	tree Hobby Standard1X Standard2X PerformanceM PerformanceL
//	@Transitions({
//		@Transition(name = "down5t1", source = "PerformanceM", target = "Free"),
//	})
//	public void down5t1() {	
//		System.out.println("[HerokuDynoType] down5t1 triggered ....");
//		System.out.println("[HerokuDynoType] PerformanceM --> Free");
//	}
//	
//	@Transitions({
//		@Transition(name = "down5t2", source = "PerformanceM", target = "Hobby"),
//	})
//	public void down5t2() {	
//		System.out.println("[HerokuDynoType] down5t2 triggered ....");
//		System.out.println("[HerokuDynoType] PerformanceM --> Hobby");
//	}
//	
//	@Transitions({
//		@Transition(name = "down5t3", source = "PerformanceM", target = "Standard1X"),
//	})
//	public void down5t3() {	
//		System.out.println("[HerokuDynoType] down5t3 triggered ....");
//		System.out.println("[HerokuDynoType] PerformanceM --> Standard1X");
//	}
//	
//	@Transitions({
//		@Transition(name = "down5t4", source = "PerformanceM", target = "Standard2X"),
//	})
//	public void down5t4() {	
//		System.out.println("[HerokuDynoType] down5t4 triggered ....");
//		System.out.println("[HerokuDynoType] PerformanceM --> Standard2X");
//	}
//	
//
//// 4 down	tree Hobby Standard1X Standard2X PerformanceM PerformanceL
//	@Transitions({
//		@Transition(name = "down4t1", source = "Standard2X", target = "Free"),
//	})
//	public void down4t1() {	
//		System.out.println("[HerokuDynoType] down4t1 triggered ....");
//		System.out.println("[HerokuDynoType] Standard2X --> Free");
//	}
//	
//	@Transitions({
//		@Transition(name = "down4t2", source = "Standard2X", target = "Hobby"),
//	})
//	public void down4t2() {	
//		System.out.println("[HerokuDynoType] down4t2 triggered ....");
//		System.out.println("[HerokuDynoType] Standard2X --> Hobby");
//	}
//	
//	@Transitions({
//		@Transition(name = "down4t3", source = "Standard2X", target = "Standard1X"),
//	})
//	public void down4t3() {	
//		System.out.println("[HerokuDynoType] down4t3 triggered ....");
//		System.out.println("[HerokuDynoType] Standard2X --> Standard1X");
//	}
//	
//
//// 3 down	tree Hobby Standard1X Standard2X PerformanceM PerformanceL
//	@Transitions({
//		@Transition(name = "down3t1", source = "Standard1X", target = "Free"),
//	})
//	public void down3t1() {	
//		System.out.println("[HerokuDynoType] down3t1 triggered ....");
//		System.out.println("[HerokuDynoType] Standard1X --> Free");
//	}
//	
//	@Transitions({
//		@Transition(name = "down3t2", source = "Standard1X", target = "Hobby"),
//	})
//	public void down3t2() {	
//		System.out.println("[HerokuDynoType] down3t2 triggered ....");
//		System.out.println("[HerokuDynoType] Standard1X --> Hobby");
//	}
//	
//	
//		
//// 2 down	tree Hobby Standard1X Standard2X PerformanceM PerformanceL
//	@Transitions({
//		@Transition(name = "down2t1", source = "Hobby", target = "Free"),
//	})
//	public void down2t1() {	
//		System.out.println("[HerokuDynoType] down2t1 triggered ....");
//		System.out.println("[HerokuDynoType] Hobby --> Free");
//	}
//	
//	
// reset	Free Hobby Standard1X Standard2X PerformanceM PerformanceL
	@Transitions({
		@Transition(name = "reset1", source = "Free", target = "Init"),
	})
	public void reset1() {	
		System.out.println("[HerokuDynoType] reset1 triggered .... (Free --> Init)");
	}
	
//	@Transitions({
//		@Transition(name = "reset2", source = "Hobby", target = "Init"),
//	})
//	public void reset2() {	
//		System.out.println("[HerokuDynoType] reset2 triggered ....");
//		System.out.println("[HerokuDynoType] Hobby --> Init");
//	}
	
//	@Transitions({
//		@Transition(name = "reset3", source = "Standard1X", target = "Init"),
//	})
//	public void reset3() {	
//		System.out.println("[HerokuDynoType] reset3 triggered ....");
//		System.out.println("[HerokuDynoType] Standard1X --> Init");
//	}
//	
//	@Transitions({
//		@Transition(name = "reset4", source = "Standard2X", target = "Init"),
//	})
//	public void reset4() {	
//		System.out.println("[HerokuDynoType] reset4 triggered ....");
//		System.out.println("[HerokuDynoType] Standard2X --> Init");
//	}
//	
//	@Transitions({
//		@Transition(name = "reset5", source = "PerformanceM", target = "Init"),
//	})
//	public void reset5() {	
//		System.out.println("[HerokuDynoType] reset5 triggered ....");
//		System.out.println("[HerokuDynoType] PerformanceM --> Init");
//	}
//	
//	@Transitions({
//		@Transition(name = "reset6", source = "PerformanceL", target = "Init"),
//	})
//	public void reset6() {	
//		System.out.println("[HerokuDynoType] reset6 triggered ....");
//		System.out.println("[HerokuDynoType] PerformanceL --> Init");
//	}
		

}
