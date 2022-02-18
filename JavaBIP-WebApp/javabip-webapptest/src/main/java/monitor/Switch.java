package monitor;


import java.util.ArrayList;
import java.util.List;


import org.javabip.annotations.ComponentType;
//import org.javabip.annotations.Data;
//import org.javabip.annotations.Guard;
import org.javabip.annotations.Port;
import org.javabip.annotations.Ports;
import org.javabip.annotations.Transition;
import org.javabip.annotations.Transitions;
import org.javabip.api.PortType;


@Ports({
	@Port(name = "end", type = PortType.spontaneous),
	@Port(name = "on", type = PortType.enforceable),
	@Port(name = "off", type = PortType.enforceable),
	@Port(name = "finished", type = PortType.enforceable)
})
@ComponentType(initial = "init", name = "monitor.switch")
public class Switch {
	List<String> serverslist = new ArrayList<String>();
	String currentServer = "";
	int serverIndex = 0;
	
	
	protected void initServer () {
		currentServer = "";
		serverIndex = 0;
	}
	
	@Transition(name = "addServer", source = "init", target = "init", guard = "")
	protected void addServer(String address) {
		if (serverslist.isEmpty() && currentServer == "") {
			currentServer = address;
		}
		serverslist.add(address);
		System.out.println("Added server : " + address);
		
	}	
	
	@Transitions({
		@Transition(name = "removeServers", source = "init", target = "init", guard = ""),
		@Transition(name = "removeServers", source = "switched", target = "init", guard = "")
	})
	protected void removeServers() {
		serverslist.clear();
		currentServer = "";
	}
	
	@Transitions({
		@Transition(name = "chooseServer", source = "init", target = "switched", guard = ""),
		@Transition(name = "chooseServer", source = "switched", target = "switched", guard = "")
	})
	protected void chooseServer() {			
		if ( serverslist.size() > serverIndex + 1 ) {
			serverIndex = serverIndex + 1;
			currentServer = serverslist.get(serverIndex).toString();
		} else {
			serverIndex = 0;
			currentServer = serverslist.get(serverIndex).toString();
		}
	}	
}
