package monitor;


import java.util.ArrayList;
import java.util.List;

//import org.apache.commons.validator.UrlValidator;
import org.javabip.annotations.ComponentType;
import org.javabip.annotations.Data;
import org.javabip.annotations.Guard;
//import org.javabip.annotations.Data;
//import org.javabip.annotations.Guard;
import org.javabip.annotations.Port;
import org.javabip.annotations.Ports;
import org.javabip.annotations.Transition;
import org.javabip.annotations.Transitions;
import org.javabip.api.PortType;

//States: SwitchInit, SwitchReady, ServerSwitched
//Transition: addServer, removeAllServers, chooseServer, switchServer, switchConfirm
@Ports({
//	@Port(name = "stopServer", type = PortType.enforceable),
//	@Port(name = "addServer", type = PortType.enforceable),
//	@Port(name = "addServerLocal", type = PortType.enforceable),
//	@Port(name = "switchServer", type = PortType.enforceable),
//	@Port(name = "test", type = PortType.enforceable),
	
	
	@Port(name = "addServer", type = PortType.spontaneous),
	@Port(name = "removeAllServers", type = PortType.spontaneous),
	@Port(name = "chooseServer", type = PortType.spontaneous),
	@Port(name = "switchServer", type = PortType.enforceable),
	@Port(name = "switchConfirm", type = PortType.enforceable),
	@Port(name = "resetSwitch", type = PortType.spontaneous),
})
@ComponentType(initial = "SwitchInit", name = "org.javabip.executor.monitor.Switch")
public class Switch {
	
	public static List<String> serverslist = new ArrayList<String>();
	static String currentServer = "";
	static String previousServer = "";
	int serverIndex = 0;
	
//	String localServerAddress = "http://localhost:8080/javabip-itest/req";
//	String localLimitCheckAddress = "http://localhost:8080/javabip-itest/req?req=checklimit";
	
	
	@Transitions({
		@Transition(name = "addServer", source = "SwitchInit", target = "SwitchReady"),
		@Transition(name = "addServer", source = "SwitchReady", target = "SwitchReady"),
	})
	public void addServer(@Data(name = "adress") MonitorResult address) {
		// Validate the URL
//		String[] schemes = {"http","https"}; // DEFAULT schemes = "http", "https", "ftp"
//		UrlValidator urlValidator = new UrlValidator(schemes);
//		if (urlValidator.isValid(address)) {
//		   System.out.println("URL is valid");
//		} else {
//		   System.out.println("URL is invalid");
//		}
		// Add into the list
		System.out.println("[SWITCH] Adding a server....");
		serverslist.add(address.getHttpResponse());
		System.out.println("[SWITCH] Added the server : " + address.getHttpResponse() + " ("+ serverslist.size() +" servers are added)");
		System.out.println("[SWITCH] SwitchInit/SwitchReady --> SwitchReady");
		address.setHttpResponse("");
	}
	
	
	@Transitions({
		@Transition(name = "removeAllServers", source = "SwitchReady", target = "SwitchInit"),
	})
	public void removeAllServers(@Data(name = "flag") MonitorResult flag) {
		System.out.println("[SWITCH] Remove all servers....");
		serverslist.clear();
		currentServer = "";
		serverIndex = 0;
		System.out.println("[SWITCH] All servers are removed");
		System.out.println("[SWITCH] SwitchReady --> SwitchInit");
		flag.setHttpResponse("");
	}
	

	@Transitions({
		@Transition(name = "chooseServer", source = "SwitchReady", target = "SwitchReady"),
	})
	public void switchServer(@Data(name = "serverAddress") MonitorResult serverAddress) {	
		System.out.println("[SWITCH] Choosing a server....");
		if (serverslist.isEmpty()) {
			System.out.println("[SWITCH] No server registered");
		}
		else
		{
					System.out.println("[SWITCH] Choose server (" + serverAddress.getHttpResponse() + ")");
					serverIndex = serverslist.indexOf(serverAddress.getHttpResponse());
					previousServer = currentServer;
					currentServer = serverslist.get(serverIndex).toString();
		}
		System.out.println("[SWITCH] Server chose");
		System.out.println("[SWITCH] SwitchReady --> SwitchReady");
		serverAddress.setHttpResponse("");
	}
	
	
//	@Transitions({
//		@Transition(name = "switchConfirm", source = "SwitchReady", target = "ServerReady", guard = "isServerSet"),
//	})
//	public void switchConfirm(@Data(name = "flag") MonitorResult flag) {	
//		System.out.println("[SWITCH] Confirm ....");
//		System.out.println("[SWITCH] SwitchReady --> ServerReady");
//		flag.setHttpResponse("");
//	}
	@Transitions({
		@Transition(name = "switchConfirm", source = "SwitchReady", target = "ServerReady", guard = "isServerSet"),
	})
	public void switchConfirm() {	
		System.out.println("[SWITCH] Confirm ....");
		System.out.println("[SWITCH] SwitchReady --> ServerReady");
	}
	
	@Guard(name = "isServerSet")
	public boolean isServerSet() {
		return currentServer != "";
	}
	
	
	@Transitions({
//		@Transition(name = "switchServer", source = "SwitchReady", target = "ServerReady"),
		@Transition(name = "switchServer", source = "ServerReady", target = "ServerReady"),
//		@Transition(name = "switchServer", source = "SwitchReady", target = "ServerReady"),
	})
	public void switchServer() {	
		System.out.println("[SWITCH] Switching to another server....");
		if (serverslist.isEmpty()) {
			System.out.println("[SWITCH] No server registered");
		}
		else
		{
				if ( serverslist.size() > serverIndex + 1 ) {
					serverIndex = serverIndex + 1;
					previousServer = currentServer;
					currentServer = serverslist.get(serverIndex).toString();
//					System.out.println("[SWITCH] Switching to another server (" + serverslist.get(serverIndex).toString() + ")");
					System.out.println("[SWITCH] Switching to another server (" + currentServer + ")");
				} else {
					serverIndex = 0;
					previousServer = currentServer;
					currentServer = serverslist.get(serverIndex).toString();
//					System.out.println("[SWITCH] Switching back to 1st server (" + serverslist.get(serverIndex).toString() + ")");
					System.out.println("[SWITCH] Switching back to 1st server (" + currentServer + ")");
				}
		}
		System.out.println("[SWITCH] Server switched");
		System.out.println("[SWITCH] ServerReady --> ServerReady");
	}
	
	@Transitions({
		@Transition(name = "resetSwitch", source = "ServerReady", target = "SwitchInit"),
	})
	public void resetSwitch () {
		System.out.println("[SWITCH] Reset the Switch (ServerReady --> SwitchInit)");
		serverslist.clear();
		currentServer = "";
		serverIndex = 0;
		System.out.println("[SWITCH] All servers are removed");
	}
	
	
//	@Transitions({
//		@Transition(name = "stopServer", source = "ServerReady", target = "SwitchReady"),
//	})
//	public void switchConfirm () {
//		System.out.println("[SWITCH] sending stop message!");
//		System.out.println("[SWITCH] ServerReady --> SwitchReady");
//	}
}
