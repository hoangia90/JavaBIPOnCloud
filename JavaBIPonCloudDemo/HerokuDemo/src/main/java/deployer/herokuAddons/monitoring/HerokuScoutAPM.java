package deployer.herokuAddons.monitoring;

import org.javabip.annotations.ComponentType;
import org.javabip.annotations.Data;
import org.javabip.annotations.Port;
import org.javabip.annotations.Ports;
import org.javabip.annotations.Transition;
import org.javabip.annotations.Transitions;
import org.javabip.api.PortType;

import deployer.herokuAddons.AddOnAvailability;

@Ports({
	@Port(name = "init", type = PortType.spontaneous),
	@Port(name = "on", type = PortType.enforceable),
	@Port(name = "off", type = PortType.enforceable),
	@Port(name = "sub1", type = PortType.enforceable),
	@Port(name = "reset1", type = PortType.enforceable),
})

@ComponentType(initial = "init", name = "deployer.monitoring.HerokuScoutAPM")
public class HerokuScoutAPM {
	
	String name = "ScoutAPM";
	
	AddOnAvailability availability;
	
	@Transitions({
		@Transition(name = "init", source = "init", target = "Off"),
	})
	public void init(@Data(name = "availability") AddOnAvailability availability) {	
		this.availability = availability;
		System.out.println("[Addon-HerokuScoutAPM] init triggered .... (init --> Off)");
		this.availability.setAvailability(false);
	}
	
	@Transitions({
		@Transition(name = "on", source = "On", target = "On"),
		@Transition(name = "on", source = "Off", target = "On"),
	})
	public void on() {	
		System.out.println("[Addon-HerokuScoutAPM] on triggered .... (Off --> On)/(On --> On)");
		this.availability.setAvailability(true);
	}
	
	@Transitions({
		@Transition(name = "off", source = "Off", target = "Off"),
		@Transition(name = "off", source = "On", target = "Off"),
	})
	public void off() {	
		System.out.println("[Addon-HerokuScoutAPM] off triggered .... (Off --> On)/(Off --> Off)");
		this.availability.setAvailability(false);
	}
	
	
	@Transitions({
		@Transition(name = "sub1", source = "On", target = "Chair"),
	})
	public void sub1() {	
		System.out.println("[Addon-HerokuScoutAPM] sub1 triggered .... (On --> Chair)");
	}
	
	
	@Transitions({
		@Transition(name = "reset1", source = "Chair", target = "Off"),
	})
	public void reset1() {	
		System.out.println("[Addon-HerokuScoutAPM] reset1 triggered .... (Chair --> Off)");
		this.availability.setAvailability(false);
	}

}
