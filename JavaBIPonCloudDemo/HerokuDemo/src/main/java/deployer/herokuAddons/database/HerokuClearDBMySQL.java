package deployer.herokuAddons.database;

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
//	@Port(name = "sub2", type = PortType.spontaneous),
//	@Port(name = "sub3", type = PortType.spontaneous),
//	@Port(name = "sub4", type = PortType.spontaneous),
//	@Port(name = "up1t2", type = PortType.spontaneous),
//	@Port(name = "up1t3", type = PortType.spontaneous),
//	@Port(name = "up1t4", type = PortType.spontaneous),
//	@Port(name = "up2t3", type = PortType.spontaneous),
//	@Port(name = "up2t4", type = PortType.spontaneous),
//	@Port(name = "up3t4", type = PortType.spontaneous),
	@Port(name = "reset1", type = PortType.enforceable),
//	@Port(name = "reset2", type = PortType.enforceable),
//	@Port(name = "reset3", type = PortType.enforceable),
//	@Port(name = "reset4", type = PortType.enforceable),
})

@ComponentType(initial = "Init", name = "deployer.database.HerokuClearDBMySQL")
public class HerokuClearDBMySQL {
	
	String name = "ClearDBMySQL";
	
	AddOnAvailability availability;
	
	@Transitions({
		@Transition(name = "init", source = "Init", target = "Off"),
	})
	public void init(@Data(name = "availability") AddOnAvailability availability) {	
		this.availability = availability;
		System.out.println("[Addon-HerokuClearDBMySQL] init triggered .... (init --> Off)");
		this.availability.setAvailability(false);
	}
	
	@Transitions({
		@Transition(name = "on", source = "On", target = "On"),
		@Transition(name = "on", source = "Off", target = "On"),
	})
	public void on() {	
		System.out.println("[Addon-HerokuClearDBMySQL] on triggered .... (Off --> On)/(On --> On)");
//		System.out.println("[Addon-HerokuClearDBMySQL] Off --> On");
		this.availability.setAvailability(true);
	}
	
	@Transitions({
		@Transition(name = "off", source = "Off", target = "Off"),
		@Transition(name = "off", source = "On", target = "Off"),
	})
	public void off() {	
		System.out.println("[Addon-HerokuClearDBMySQL] off triggered .... (On --> Off)/(Off --> Off)");
//		System.out.println("[Addon-HerokuClearDBMySQL] On --> Off");
		this.availability.setAvailability(false);
	}
	
	
	@Transitions({
		@Transition(name = "sub1", source = "On", target = "Ignite"),
	})
	public void sub1() {	
		System.out.println("[Addon-HerokuClearDBMySQL] sub1 triggered .... (On --> Ignite)");
	}
	
//	@Transitions({
//		@Transition(name = "sub2", source = "On", target = "Punch"),
//	})
//	public void sub2() {	
//		System.out.println("[Addon-HerokuClearDBMySQL] sub2 triggered .... (On --> Punch)");
//	}
//	
//	@Transitions({
//		@Transition(name = "sub3", source = "On", target = "Drift"),
//	})
//	public void sub3() {	
//		System.out.println("[Addon-HerokuClearDBMySQL] sub3 triggered .... (On --> Drift)");
//	}
//	
//	@Transitions({
//		@Transition(name = "sub4", source = "On", target = "Scream"),
//	})
//	public void sub4() {	
//		System.out.println("[Addon-HerokuClearDBMySQL] sub4 triggered .... (On --> Scream)");
//	}
	
	
//	// up 1 Ignite Punch Drift Scream
//	@Transitions({
//		@Transition(name = "up1t2", source = "Ignite", target = "Punch"),
//	})
//	public void up1t2() {	
//		System.out.println("[Addon-HerokuClearDBMySQL] up1t2 triggered .... (Ignite --> Punch)");
//	}
//	
//	@Transitions({
//		@Transition(name = "up1t3", source = "Ignite", target = "Drift"),
//	})
//	public void up1t3() {	
//		System.out.println("[Addon-HerokuClearDBMySQL] up1t3 triggered .... (Ignite --> Drift)");
//	}
//	
//	@Transitions({
//		@Transition(name = "up1t4", source = "Ignite", target = "Scream"),
//	})
//	public void up1t4() {	
//		System.out.println("[Addon-HerokuClearDBMySQL] up1t4 triggered .... (Ignite --> Scream)");
//	}
//	
//	// up 2 Ignite Punch Drift Scream
//	@Transitions({
//		@Transition(name = "up2t3", source = "Punch", target = "Drift"),
//	})
//	public void up2t3() {	
//		System.out.println("[Addon-HerokuClearDBMySQL] up2t3 triggered .... (Punch --> Drift)");
//	}
//	
//	@Transitions({
//		@Transition(name = "up2t4", source = "Punch", target = "Scream"),
//	})
//	public void up2t4() {	
//		System.out.println("[Addon-HerokuClearDBMySQL] up2t4 triggered .... (Punch --> Scream)");
//	}
//	
//	// up 3 Ignite Punch Drift Scream
//	@Transitions({
//		@Transition(name = "up3t4", source = "Drift", target = "Scream"),
//	})
//	public void up3t4() {	
//		System.out.println("[Addon-HerokuClearDBMySQL] up3t4 triggered .... (Drift --> Scream)");
//	}
	

	
	
//	// off Ignite Punch Drift Scream
	@Transitions({
		@Transition(name = "reset1", source = "Ignite", target = "Off"),
	})
	public void reset1() {	
		System.out.println("[Addon-HerokuClearDBMySQL] off1 triggered .... (Ignite --> Off)");
		this.availability.setAvailability(false);
	}
	
//	@Transitions({
//		@Transition(name = "reset2", source = "Punch", target = "Off"),
//	})
//	public void reset2() {	
//		System.out.println("[Addon-HerokuClearDBMySQL] reset2 triggered .... (Punch --> Off)");
//		this.availability.setAvailability(false);
//	}
//	
//	@Transitions({
//		@Transition(name = "reset3", source = "Drift", target = "Off"),
//	})
//	public void reset3() {	
//		System.out.println("[Addon-HerokuClearDBMySQL] reset3 triggered .... (Drift --> Off)");
//		this.availability.setAvailability(false);
//	}
//	
//	@Transitions({
//		@Transition(name = "reset4", source = "Scream", target = "Off"),
//	})
//	public void reset4() {	
//		System.out.println("[Addon-HerokuClearDBMySQL] reset4 triggered .... (Scream --> Off)");
//		this.availability.setAvailability(false);
//	}

}
