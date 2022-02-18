package test.multiRDVSync;

import org.javabip.annotations.ComponentType;
import org.javabip.annotations.Port;
import org.javabip.annotations.Ports;
import org.javabip.annotations.Transition;
import org.javabip.annotations.Transitions;
import org.javabip.api.PortType;

@Ports({
	@Port(name = "X", type = PortType.enforceable),
	@Port(name = "back", type = PortType.enforceable),
})

@ComponentType(initial = "init", name = "org.javabip.executor.test.test1.X")
public class X {
	@Transitions({
		@Transition(name = "X", source = "init", target = "init1"),
	})
	public void x() {	
		System.out.println("X ....");
	}
	
	@Transitions({
		@Transition(name = "back", source = "init1", target = "init"),
	})
	public void back() {	
		System.out.println("X Back....");
	}
}
