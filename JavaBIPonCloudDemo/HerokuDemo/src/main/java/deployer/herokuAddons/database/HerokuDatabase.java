package deployer.herokuAddons.database;

import org.javabip.annotations.ComponentType;
import org.javabip.annotations.Port;
import org.javabip.annotations.Ports;
import org.javabip.annotations.Transition;
import org.javabip.annotations.Transitions;
import org.javabip.api.PortType;



@Ports({
	@Port(name = "init", type = PortType.enforceable),
	@Port(name = "addClearDBMySQL", type = PortType.enforceable),
	@Port(name = "addHerokuPostgres", type = PortType.enforceable),
	@Port(name = "reset", type = PortType.enforceable),
})

@ComponentType(initial = "Init", name = "deployer.database.Database")
public class HerokuDatabase {
	@Transitions({
		@Transition(name = "addClearDBMySQL", source = "Init", target = "DatabaseUsed"),
		@Transition(name = "addHerokuPostgres", source = "Init", target = "DatabaseUsed"),
	})
	public void addDatabase () {	
		System.out.println("[AddonCategories-Database] addDatabase triggered .... (Init --> DatabaseUsed)");
	}
	
	@Transitions({
		@Transition(name = "reset", source = "DatabaseUsed", target = "Init"),
	})
	public void reset () {	
		System.out.println("[AddonCategories-Database] reset triggered .... (DatabaseUsed --> Init)");
	}
		
}
