package test.multiRDVSync;

import org.javabip.annotations.ComponentType;
import org.javabip.annotations.Port;
import org.javabip.annotations.Ports;
import org.javabip.annotations.Transition;
import org.javabip.annotations.Transitions;
import org.javabip.api.PortType;

@Ports({
	@Port(name = "toX", type = PortType.enforceable),
	@Port(name = "toY", type = PortType.enforceable),
	@Port(name = "backAll", type = PortType.enforceable),
})

@ComponentType(initial = "init", name = "org.javabip.executor.test.test1.monitor")
public class monitor {
	@Transitions({
		@Transition(name = "toX", source = "init", target = "X"),
	})
	public void toX() {	
		System.out.println("[M] toX ....");
	}
	
	@Transitions({
		@Transition(name = "toY", source = "X", target = "Y"),
	})
	public void toY() {	
		System.out.println("[M] toY ....");
	}
	
	@Transitions({
		@Transition(name = "backAll", source = "Y", target = "init"),
	})
	public void backAll() {	
		System.out.println("[M] BackAll ....");
	}
}
