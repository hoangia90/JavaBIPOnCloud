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

@ComponentType(initial = "init", name = "org.javabip.executor.test.test1.loop5")
public class loop5 {
	@Transitions({
		@Transition(name = "loop", source = "init", target = "init"),
	})
	public void switchConfirm() {	
		System.out.println("loop 5 ....");
	}
}
