package org.javabip.executor.test;

import java.io.IOException;
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
import org.javabip.spec.Master;
import org.javabip.spec.Slave;



//import org.junit.After;
//import org.junit.Before;

import akka.actor.ActorSystem;


@WebServlet(name = "Test", urlPatterns = "/test")
public class webapptest extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ActorSystem system;
	EngineFactory engineFactory;
	

	public void initialize() {
		system = ActorSystem.create("MySystem");
		engineFactory = new EngineFactory(system);
	}

	public void cleanup() {
//		system.shutdown();
	}
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.print("Hello!!!!");
		System.out.println(" Hello Java BIP - Server Example! ");
		
		webapptest t = new webapptest();
		t.initialize();
		// TODO Auto-generated method stub
		BIPGlue bipGlue = new GlueBuilder() {
			@Override
			public void configure() {

				port(Master.class, "req").requires(Slave.class, "get");
				port(Master.class, "req").accepts(Slave.class, "get");

				port(Master.class, "compute").requires(Slave.class, "work", Slave.class, "work");
				port(Master.class, "compute").accepts(Slave.class, "work");

				port(Slave.class, "get").requires(Master.class, "req");
				port(Slave.class, "get").accepts(Master.class, "req");

				port(Slave.class, "work").requires(Master.class, "compute");
				port(Slave.class, "work").accepts(Master.class, "compute", Slave.class, "work");

				data(Slave.class, "ID").to(Master.class, "slaveID");
				data(Slave.class, "ID").to(Master.class, "slaveID1");
				data(Slave.class, "ID").to(Master.class, "slaveID2");

			}

		}.build();

		BIPEngine engine = t.engineFactory.create("myEngine", bipGlue);

		Master master1 = new Master("master1");
		Master master2 = new Master("master2");
		Slave slaveA = new Slave("slaveA");
		Slave slaveB = new Slave("slaveB");
		Slave slaveC = new Slave("slaveC");
		Slave slaveD = new Slave("slaveD");
		Slave slaveE = new Slave("slaveE");

		BIPActor executorM1 = engine.register(master1, "master1", true);
		BIPActor executorSA = engine.register(slaveA, "slaveA", true);
		BIPActor executorSB = engine.register(slaveB, "slaveB", true);

		engine.start();

		engine.execute();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		t.engineFactory.destroy(engine);
		t.cleanup();

		// assertTrue("Master 1 has not made any transitions", master1.noOfTransitions > 2);
		// assertTrue("Slave A has not made any transitions", slaveA.noOfTransitions > 0);
		// assertTrue("Slave B has not made any transitions", slaveB.noOfTransitions > 0);
		
		System.out.print("End!!!!");
	
	}

}
