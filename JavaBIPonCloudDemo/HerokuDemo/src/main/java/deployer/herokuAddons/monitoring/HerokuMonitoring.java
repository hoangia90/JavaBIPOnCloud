package deployer.herokuAddons.monitoring;

import org.javabip.annotations.ComponentType;
import org.javabip.annotations.Port;
import org.javabip.annotations.Ports;
import org.javabip.annotations.Transition;
import org.javabip.annotations.Transitions;
import org.javabip.api.PortType;

@Ports({
	@Port(name = "init", type = PortType.enforceable),
	@Port(name = "addScoutAPM", type = PortType.enforceable),
	@Port(name = "addNewRelicAPM", type = PortType.enforceable),
	@Port(name = "reset", type = PortType.enforceable),
})

@ComponentType(initial = "Init", name = "deployer.monitoring.Monitoring")
public class HerokuMonitoring {
	@Transitions({
		@Transition(name = "addScoutAPM", source = "Init", target = "MonitoringUsed"),
		@Transition(name = "addNewRelicAPM", source = "Init", target = "MonitoringUsed"),
	})
	public void addMonitoring () {	
		System.out.println("[AddonCategories-Monitoring] addMonitoring triggered .... (Init --> MonitoringUsed)");
	}
	
	@Transitions({
		@Transition(name = "reset", source = "MonitoringUsed", target = "Init"),
	})
	public void reset () {	
		System.out.println("[AddonCategories-Monitoring] reset triggered .... (MonitoringUsed --> Init)");
	}
		
}
