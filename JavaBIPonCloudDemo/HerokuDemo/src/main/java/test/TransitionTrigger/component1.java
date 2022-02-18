package test.TransitionTrigger;

import org.javabip.annotations.ComponentType;
import org.javabip.annotations.Port;
import org.javabip.annotations.Ports;
import org.javabip.annotations.Transition;
import org.javabip.annotations.Transitions;
import org.javabip.api.PortType;

@Ports({  
	@Port(name = "selfloop", type = PortType.spontaneous),
	@Port(name = "transition1", type = PortType.enforceable),
	@Port(name = "transition2", type = PortType.enforceable),
	})
@ComponentType(initial = "0", name = "org.javabip.executor.test.TransitionTrigger.component1")
public class component1 {
	
	String componentName = "COMPONENT 1";
	
	
	@Transitions({ 
		@Transition(name = "selfloop", source = "0", target = "0")
	})
	public void fireSelfLoop() {
		System.out.println(componentName + ": selfloop is triggered!");
	}
	
	@Transitions({ 
		@Transition(name = "transition1", source = "0", target = "1")
	})
	public void fireTransition1() {
		System.out.println(componentName + ": transition 1 is triggered!");
	}
	
	@Transitions({ 
		@Transition(name = "transition2", source = "1", target = "0")
	})
	public void fireTransition2() {
		System.out.println(componentName + ": transition 2 is triggered!");
	}
	
}
