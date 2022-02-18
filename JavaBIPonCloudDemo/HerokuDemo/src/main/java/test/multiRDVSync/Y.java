package test.multiRDVSync;

import org.javabip.annotations.ComponentType;
import org.javabip.annotations.Port;
import org.javabip.annotations.Ports;
import org.javabip.annotations.Transition;
import org.javabip.annotations.Transitions;
import org.javabip.api.PortType;

@Ports({
	@Port(name = "Y", type = PortType.enforceable),
	@Port(name = "back", type = PortType.enforceable),
})

@ComponentType(initial = "init", name = "org.javabip.executor.test.test1.Y")
public class Y {
	@Transitions({
		@Transition(name = "Y", source = "init", target = "init1"),
	})
	public void y() {	
		System.out.println("Y ....");
	}
	
	@Transitions({
		@Transition(name = "back", source = "init1", target = "init"),
	})
	public void back() {	
		System.out.println("Y Back....");
	}
}
