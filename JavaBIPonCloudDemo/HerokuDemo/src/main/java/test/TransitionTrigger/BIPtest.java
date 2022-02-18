package test.TransitionTrigger;



import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.javabip.api.BIPActor;
import org.javabip.api.BIPEngine;
import org.javabip.api.BIPGlue;
import org.javabip.engine.factory.EngineFactory;


import akka.actor.ActorSystem;
import monitor.MonitorResult;

import org.javabip.glue.TwoSynchronGlueBuilder;

public class BIPtest {
	
	ActorSystem system;
	EngineFactory engineFactory;

	
	//	@Before
	public void initialize() {

		system = ActorSystem.create("MySystem");
		engineFactory = new EngineFactory(system);
	}
	
	//Use terminate method instead
	//	@After
	public void cleanup() {
//		system.shutdown();
	}
	
//	private static BIPGlue createGlue(String bipGlueFilename) {
//		BIPGlue bipGlue = null;
//
//		InputStream inputStream;
//		try {
//			inputStream = new FileInputStream(bipGlueFilename);
//
//			bipGlue = GlueBuilder.fromXML(inputStream);
//
//		} catch (FileNotFoundException e) {
//
//			e.printStackTrace();
//		}
//		return bipGlue;
//	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.print("Hello!!!!");
		System.out.println(" Hello Java BIP - Server Example! ");
		
		BIPtest test = new BIPtest();
		test.initialize();
		// TODO Auto-generated method stub
//		BIPGlue bipGlue = new GlueBuilder() {
		BIPGlue bipGlue = new TwoSynchronGlueBuilder() {
			@Override
			public void configure() {
//				port(component1.class, "transition1").requires(component2.class, "selfloop");
//				port(component2.class, "selfloop").requires(component1.class, "transition1");
//				port(component1.class, "selfloop").requires(component2.class, "transition1");
				
//				port(component2.class, "selfloop").requires(component1.class, "transition2");
				
//				port(component1.class, "selfloop").requires(component2.class, "selfloop");
//				port(component2.class, "selfloop").requires(component1.class, "selfloop");
				
				port(component2.class, "transition1").requires(component1.class, "transition1");
				port(component1.class, "transition1").requires(component2.class, "transition1");
				
			}

		}.build();
		
//		BIPGlue bipGlue = createGlue("./src/main/java/org/javabip/executor/monitor/test.xml");

		BIPEngine engine = test.engineFactory.create("myEngine", bipGlue);
		
		
		component1 comp1 = new component1();
		component2 comp2 = new component2();

		BIPActor actor1 = engine.register(comp1, "test1", true);
		@SuppressWarnings("unused")
		BIPActor actor2 = engine.register(comp2, "test2", true);
		
		
		engine.start();
//		actor1.inform("selfloop");
		engine.execute();
		
//		@SuppressWarnings("unchecked")
		Map<String, Object> data = new HashMap<String, Object>();
		MonitorResult re = new MonitorResult("processing...");
		data.put("flag", re);
		actor1.inform("selfloop", data);

		try {
			TimeUnit.MILLISECONDS.sleep(55);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		engine.stop();
		test.engineFactory.destroy(engine);
		test.cleanup();
	}

}
