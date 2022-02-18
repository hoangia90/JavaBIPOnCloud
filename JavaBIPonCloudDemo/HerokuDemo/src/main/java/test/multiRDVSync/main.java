package test.multiRDVSync;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javabip.api.BIPActor;
import org.javabip.api.BIPEngine;
import org.javabip.api.BIPGlue;
import org.javabip.engine.factory.EngineFactory;
import org.javabip.glue.GlueBuilder;
import akka.actor.ActorSystem;


@WebServlet(name = "TestRDV4", urlPatterns = "/testrdv4")
public class main extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ActorSystem system;
	EngineFactory engineFactory;
	
	BIPGlue bipGlue;
	BIPEngine engine;
	main m;
	
//	@Before
	public void initialize() {

		system = ActorSystem.create("MySystem");
		engineFactory = new EngineFactory(system);
	}
	
//	@Before
	@SuppressWarnings("unused")
	public void init() {
		
		m = new main();
		
		m.initialize();
		
		System.out.print("Hello!!!!");
		System.out.println(" Hello Java BIP - Server Example! ");
		
		BIPGlue bipGlue = new GlueBuilder() {
			@Override
			public void configure() {
				
				//2 RDV Sync
				
				port(loop1.class, "loop").requires(X.class, "X");
				port(loop2.class, "loop").requires(X.class, "X");
				port(loop3.class, "loop").requires(X.class, "X");
				
				port(loop1.class, "loop").requires(Y.class, "Y");
				port(loop2.class, "loop").requires(Y.class, "Y");
				port(loop3.class, "loop").requires(Y.class, "Y");
				
				port(loop1.class, "loop").accepts(X.class, "X", Y.class, "Y", loop2.class, "loop", loop3.class, "loop", monitor.class, "toX", monitor.class, "toY");
				port(loop2.class, "loop").accepts(X.class, "X", Y.class, "Y", loop1.class, "loop", loop3.class, "loop", monitor.class, "toX", monitor.class, "toY");
				port(loop3.class, "loop").accepts(X.class, "X", Y.class, "Y", loop1.class, "loop", loop2.class, "loop", monitor.class, "toX", monitor.class, "toY");
				
				
				port(X.class, "X").requires(monitor.class, "toX", loop1.class, "loop", loop2.class, "loop", loop3.class, "loop");
				port(X.class, "X").accepts(monitor.class, "toX", loop1.class, "loop", loop2.class, "loop", loop3.class, "loop");
				port(Y.class, "Y").requires(monitor.class, "toY", loop1.class, "loop", loop2.class, "loop", loop3.class, "loop");
				port(Y.class, "Y").accepts(monitor.class, "toY", loop1.class, "loop", loop2.class, "loop", loop3.class, "loop");
				
				
				port(monitor.class, "toX").requires(X.class, "X");
				port(monitor.class, "toX").accepts(X.class, "X", loop1.class, "loop", loop2.class, "loop", loop3.class, "loop");
				
				port(monitor.class, "toY").requires(Y.class, "Y");
				port(monitor.class, "toY").accepts(Y.class, "Y", loop1.class, "loop", loop2.class, "loop", loop3.class, "loop");
				
				
				
				port(X.class, "back").requires(monitor.class, "backAll", Y.class, "back");
				port(X.class, "back").accepts(monitor.class, "backAll", Y.class, "back");
				port(Y.class, "back").requires(monitor.class, "backAll", X.class, "back");
				port(Y.class, "back").accepts(monitor.class, "backAll", X.class, "back");
				
				
			}

		}.build();
		
		engine = m.engineFactory.create("myEngine", bipGlue);
	
		//Components
		monitor m = new monitor();
		X x = new X();
		Y y = new Y();
		loop1 loop1 = new loop1();
		loop2 loop2 = new loop2();
		loop3 loop3 = new loop3();
//		loop4 loop4 = new loop4();
//		loop5 loop5 = new loop5();
		//Actors
		BIPActor executorM = engine.register(m, "M", true);
		BIPActor executorX = engine.register(x, "X", true);
		BIPActor executorY = engine.register(y, "Y", true);
		BIPActor executor1 = engine.register(loop1, "loop1", true);
		BIPActor executor2 = engine.register(loop2, "loop2", true);
		BIPActor executor3 = engine.register(loop3, "loop3", true);
//		BIPActor executor4 = engine.register(loop4, "loop4", true);
//		BIPActor executor5 = engine.register(loop5, "loop5", true);

		engine.start();
		engine.execute();
	}

	// Use terminate method instead
//	@After
	public void cleanup() {
//		system.shutdown();
	}
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		int sec = 50;
		String reqContent = " ";
		reqContent = request.getParameter("req");
		if(reqContent == null) { reqContent = " "; };
		switch (reqContent) {
		// System requests
		case "restart":
			
			System.out.println("[Java BIP] The engine stopped!");
			m.engineFactory.destroy(engine);
			System.out.println("[Java BIP] The engine is detroyed!");
			init();
			try {
				TimeUnit.MILLISECONDS.sleep(sec);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			engine.stop();
//			m.engineFactory.destroy(engine);
			System.out.print("End!!!!");
			break;
		default:
			try {
				TimeUnit.MILLISECONDS.sleep(sec);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			engine.stop();
//			m.engineFactory.destroy(engine);
			System.out.print("End!!!!");
			break;
		}
		
	}

}
