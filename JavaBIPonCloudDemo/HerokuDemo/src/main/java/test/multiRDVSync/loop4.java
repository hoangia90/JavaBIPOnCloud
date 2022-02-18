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

@ComponentType(initial = "init", name = "org.javabip.executor.test.test1.loop4")
public class loop4 {
	@Transitions({
		@Transition(name = "loop", source = "init", target = "init4"),
	})
	public void switchConfirm() {	
		System.out.println("loop 4 ....");
	}
}
