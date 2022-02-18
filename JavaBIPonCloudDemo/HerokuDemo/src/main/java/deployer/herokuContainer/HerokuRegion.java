package deployer.herokuContainer;

import org.javabip.annotations.ComponentType;
import org.javabip.annotations.Port;
import org.javabip.annotations.Ports;
import org.javabip.annotations.Transition;
import org.javabip.annotations.Transitions;
import org.javabip.api.PortType;

@Ports({

	@Port(name = "toUS", type = PortType.enforceable),
	@Port(name = "setAddonsForUS", type = PortType.enforceable),
	
	@Port(name = "toEU", type = PortType.enforceable),
	@Port(name = "setAddonsForEU", type = PortType.enforceable),

	@Port(name = "USreset", type = PortType.enforceable),
	@Port(name = "EUreset", type = PortType.enforceable),
	
//	@Port(name = "toOregon", type = PortType.enforceable),
	
	

})

@ComponentType(initial = "Init", name = "deployer.HerokuRegion")
public class HerokuRegion {
	
//	String setRegion = "";
	
	@Transitions({
		@Transition(name = "toUS", source = "Init", target = "US"),
	})
	public void toUS() {	
		System.out.println("[HerokuRegion] toUS triggered .... (Init --> US)");
	}
	@Transitions({
		@Transition(name = "setAddonsForUS", source = "US", target = "USAddonsSet"),
	})
	public void setAddonsForUS() {	
		System.out.println("[HerokuRegion] setAddonsForUS triggered .... (US --> USAddonsSet)");
	}
	
	
	
	@Transitions({
		@Transition(name = "toEU", source = "Init", target = "EU"),
	})
	public void toEU() {	
		System.out.println("[HerokuRegion] toEU triggered .... (Init --> EU)");
	}
	@Transitions({
		@Transition(name = "setAddonsForEU", source = "EU", target = "EUAddonsSet"),
	})
	public void setAddonsForEU() {	
		System.out.println("[HerokuRegion] setAddonsForEU triggered .... (EU --> EUAddonsSet)");
	}
	
	
//	@Transitions({
//		@Transition(name = "toOregon", source = "Init", target = "Oregon"),
//	})
//	public void toOregon() {	
//		System.out.println("[HerokuRegion] toOregon triggered .... (Init --> Oregon)");
////		System.out.println("[HerokuRegion] Init --> Oregon");
//	}
	
	
	
	
	
	@Transitions({
		@Transition(name = "USreset", source = "USAddonsSet", target = "Init"),
	})
	public void USreset() {	
		System.out.println("[HerokuRegion] USreset triggered .... (USAddonsSet --> Init)");
//		System.out.println("[HerokuRegion] US --> Init");
	}
	
	@Transitions({
		@Transition(name = "EUreset", source = "EUAddonsSet", target = "Init"),
	})
	public void EUreset() {	
		System.out.println("[HerokuRegion] EUreset triggered .... (EUAddonsSet --> Init)");
//		System.out.println("[HerokuRegion] EU --> Init");
	}
	
//	@Transitions({
//		@Transition(name = "Oregonreset", source = "Oregon", target = "Init"),
//	})
//	public void Oregonreset() {	
//		System.out.println("[HerokuRegion] Oregonreset triggered ....");
//		System.out.println("[HerokuRegion] Oregon --> Init");
//	}
	

}
