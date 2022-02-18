package test.multiRDVSync;

import org.javabip.annotations.ComponentType;
import org.javabip.annotations.Port;
import org.javabip.annotations.Ports;
import org.javabip.annotations.Transition;
import org.javabip.annotations.Transitions;
import org.javabip.api.PortType;

@Ports({
	@Port(name = "loop", type = PortType.enforceable),
})

@ComponentType(initial = "init", name = "org.javabip.executor.test.test1.loop2")
public class loop2 {
	@Transitions({
		@Transition(name = "loop", source = "init", target = "init"),
	})
	public void loop() {	
		System.out.println("loop 2 ....");
	}
}
