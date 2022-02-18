/*
 * Copyright 2012-2016 École polytechnique fédérale de Lausanne (EPFL), Switzerland
 * Copyright 2012-2016 Crossing-Tech SA, Switzerland
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Simon Bliudze, Anastasia Mavridou, Radoslaw Szymanek and Alina Zolotukhina
 */

package org.javabip.executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.RoutePolicy;
import org.javabip.spec.ComponentA;
import org.javabip.spec.ComponentB;
import org.javabip.spec.ComponentC;
import org.javabip.spec.Consumer;
import org.javabip.spec.Feeder;
import org.javabip.spec.InitialServer;
import org.javabip.spec.Master;
import org.javabip.spec.MemoryMonitor;
import org.javabip.spec.PSSComponent;
import org.javabip.spec.Peer;
import org.javabip.spec.RouteOnOffMonitor;
import org.javabip.spec.Server;
import org.javabip.spec.Slave;
import org.javabip.spec.SwitchableRouteDataTransfers;
import org.javabip.spec.SwitchableRouteErrorException;
import org.javabip.spec.Tracker;
import org.javabip.spec.TwoDataProvider1;
import org.javabip.spec.TwoDataProvider2;
import org.javabip.spec.TwoDataTaker;
import org.javabip.spec.diningphilosophers.DiningPhilosophersGlueBuilder;
import org.javabip.spec.diningphilosophers.Fork;
import org.javabip.spec.diningphilosophers.Philosophers;
import org.javabip.spec.hanoi.HanoiGlueBuilder;
import org.javabip.spec.hanoi.HanoiMonitor;
import org.javabip.spec.hanoi.HanoiOptimalMonitor;
import org.javabip.spec.hanoi.LeftHanoiPeg;
import org.javabip.spec.hanoi.MiddleHanoiPeg;
import org.javabip.spec.hanoi.RightHanoiPeg;
import org.javabip.api.BIPActor;
import org.javabip.api.BIPEngine;
import org.javabip.api.BIPGlue;
import org.javabip.engine.factory.EngineFactory;
import org.javabip.exceptions.BIPException;
import org.javabip.glue.GlueBuilder;
import org.javabip.glue.TwoSynchronGlueBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import akka.actor.ActorSystem;

public class AkkaExecutorTests {

	ActorSystem system;
	EngineFactory engineFactory;

	@Before
	public void initialize() {

		system = ActorSystem.create("MySystem");
		engineFactory = new EngineFactory(system);

	}

	@After
	public void cleanup() {

//		system.shutdown();

	}

	/**
	 * A simple test verifying the creation of the executor actor by the engine.
	 */
	@Test
	public void akkaExecutorSimpleTest() {

		// Create BIP engine.
		BIPEngine engine = engineFactory.create("myEngine", new EmptyGlue());

		// Create BIP Spec.
		HanoiMonitor hanoiMonitor = new HanoiMonitor(3);

		BIPActor actor = engine.register(hanoiMonitor, "hanoiMonitor", false);
		engine.start();
		assertNotNull("Actor Typed actor is not created properly", actor);

		engine.stop();
		engineFactory.destroy(engine);

	}

	/**
	 * A test creating three Switchable routes with a monitor, exchanging data of the routes memory usage.
	 * 
	 * @throws BIPException
	 */
	@Test
	public void routesWithDataTest() throws BIPException {

		BIPGlue bipGlue = new TwoSynchronGlueBuilder() {
			@Override
			public void configure() {

				synchron(SwitchableRouteDataTransfers.class, "on").to(MemoryMonitor.class, "add");
				synchron(SwitchableRouteDataTransfers.class, "finished").to(MemoryMonitor.class, "rm");

				port(SwitchableRouteDataTransfers.class, "off").acceptsNothing();
				port(SwitchableRouteDataTransfers.class, "off").requiresNothing();

				data(SwitchableRouteDataTransfers.class, "deltaMemoryOnTransition").to(MemoryMonitor.class,
						"memoryUsage");

			}

		}.build();

		BIPEngine engine = engineFactory.create("myEngine", bipGlue);

		CamelContext camelContext = new DefaultCamelContext();
		camelContext.setAutoStartup(false);

		SwitchableRouteDataTransfers route1 = new SwitchableRouteDataTransfers("1", camelContext);
		SwitchableRouteDataTransfers route2 = new SwitchableRouteDataTransfers("2", camelContext);
		SwitchableRouteDataTransfers route3 = new SwitchableRouteDataTransfers("3", camelContext);

		BIPActor actor1 = engine.register(route1, "1", true);
		BIPActor actor2 = engine.register(route2, "2", true);
		BIPActor actor3 = engine.register(route3, "3", true);

		final RoutePolicy routePolicy1 = createRoutePolicy(actor1);
		final RoutePolicy routePolicy2 = createRoutePolicy(actor2);
		final RoutePolicy routePolicy3 = createRoutePolicy(actor3);

		RouteBuilder builder = new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("file:inputfolder1?delete=true").routeId("1").routePolicy(routePolicy1).to("file:outputfolder1");
				from("file:inputfolder2?delete=true").routeId("2").routePolicy(routePolicy2).to("file:outputfolder2");
				from("file:inputfolder3?delete=true").routeId("3").routePolicy(routePolicy3).to("file:outputfolder3");
			}

		};

		try {
			camelContext.addRoutes(builder);
			camelContext.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		MemoryMonitor routeOnOffMonitor = new MemoryMonitor(200);
		@SuppressWarnings("unused")
		final BIPActor executorM = engine.register(routeOnOffMonitor, "monitor", true);

		engine.start();

		engine.execute();

		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		engineFactory.destroy(engine);

		assertTrue("Routes have not made any transitions", route1.noOfEnforcedTransitions
				+ route2.noOfEnforcedTransitions + route3.noOfEnforcedTransitions > 0);

	}

	/**
	 * Creates five hanoi pegs with three disks each. The disks are moved around randomly. The pegs use data transfer.
	 * 
	 */
	@SuppressWarnings("unused")
	@Test
	public void randomLargerHannoiWithDataTest() {

		int size = 3;

		BIPGlue bipGlue4Hanoi = new org.javabip.spec.hanoi.HanoiRandomGlueBuilder().build();

		BIPEngine engine = engineFactory.create("myEngine", bipGlue4Hanoi);

		org.javabip.spec.hanoi.HanoiPegWithData leftHanoiPeg = new org.javabip.spec.hanoi.HanoiPegWithData(size, false);
		BIPActor actor1 = engine.register(leftHanoiPeg, "LeftHanoiPeg", false);

		org.javabip.spec.hanoi.HanoiPegWithData middleHanoiPeg = new org.javabip.spec.hanoi.HanoiPegWithData(size, true);
		BIPActor actor2 = engine.register(middleHanoiPeg, "MiddleHanoiPeg", false);

		org.javabip.spec.hanoi.HanoiPegWithData rightHanoiPeg = new org.javabip.spec.hanoi.HanoiPegWithData(size, true);
		BIPActor actor3 = engine.register(rightHanoiPeg, "RightHanoiPeg", false);

		org.javabip.spec.hanoi.HanoiPegWithData rightMiddleHanoiPeg = new org.javabip.spec.hanoi.HanoiPegWithData(size, true);
		BIPActor actor4 = engine.register(rightMiddleHanoiPeg, "RightMiddleHanoiPeg", false);

		org.javabip.spec.hanoi.HanoiPegWithData leftMiddleHanoiPeg = new org.javabip.spec.hanoi.HanoiPegWithData(size, false);
		BIPActor actor5 = engine.register(leftMiddleHanoiPeg, "LeftMiddleHanoiPeg", false);

		engine.start();

		engine.execute();

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		engineFactory.destroy(engine);

		int noOfAllTransitions = leftHanoiPeg.noOfTransitions + rightHanoiPeg.noOfTransitions
				+ leftMiddleHanoiPeg.noOfTransitions + rightMiddleHanoiPeg.noOfTransitions
				+ middleHanoiPeg.noOfTransitions;

		assertTrue("Hanoi tower have seen progress of executing transitions", noOfAllTransitions > 0);

	}

	/**
	 * Creates three hanoi pegs with three disks each. The disks are moved around randomly. The pegs use data transfer.
	 * 
	 */
	@Test
	@SuppressWarnings("unused")
	public void randomHannoiWithDataTest() {

		int size = 3;

		BIPGlue bipGlue4Hanoi = new org.javabip.spec.hanoi.HanoiRandomGlueBuilder().build();

		BIPEngine engine = engineFactory.create("myEngine", bipGlue4Hanoi);

		org.javabip.spec.hanoi.HanoiPegWithData leftHanoiPeg = new org.javabip.spec.hanoi.HanoiPegWithData(size, false);
		BIPActor actor1 = engine.register(leftHanoiPeg, "LeftHanoiPeg", false);

		org.javabip.spec.hanoi.HanoiPegWithData middleHanoiPeg = new org.javabip.spec.hanoi.HanoiPegWithData(size, true);
		BIPActor actor2 = engine.register(middleHanoiPeg, "MiddleHanoiPeg", false);

		org.javabip.spec.hanoi.HanoiPegWithData rightHanoiPeg = new org.javabip.spec.hanoi.HanoiPegWithData(size, true);
		BIPActor actor3 = engine.register(rightHanoiPeg, "RightHanoiPeg", false);

		engine.start();

		engine.execute();

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		engineFactory.destroy(engine);

		int noOfAllTransitions = leftHanoiPeg.noOfTransitions + rightHanoiPeg.noOfTransitions
				+ middleHanoiPeg.noOfTransitions;
		System.out.println("Number of component transitions: " + noOfAllTransitions);
		assertTrue("Hanoi tower have seen progress of executing transitions", noOfAllTransitions > 0);

	}

	/**
	 * Creates three hanoi pegs with 3 disks. Uses an optimal monitor for disk placement.
	 */
	@Test
	@SuppressWarnings("unused")
	public void hannoiWithDataTestSize3() {

		BIPGlue bipGlue4Hanoi = new org.javabip.spec.hanoi.HanoiOptimalGlueBuilder().build();

		BIPEngine engine = engineFactory.create("myEngine", bipGlue4Hanoi);

		int size = 3;

		HanoiOptimalMonitor hanoiMonitor = new HanoiOptimalMonitor(size);
		BIPActor actor1 = engine.register(hanoiMonitor, "hanoiMonitor", false);

		org.javabip.spec.hanoi.LeftHanoiPegWithData leftHanoiPeg = new org.javabip.spec.hanoi.LeftHanoiPegWithData(size);
		BIPActor actor2 = engine.register(leftHanoiPeg, "LeftHanoiPeg", false);

		org.javabip.spec.hanoi.MiddleHanoiPegWithData middleHanoiPeg = new org.javabip.spec.hanoi.MiddleHanoiPegWithData(size);
		BIPActor actor3 = engine.register(middleHanoiPeg, "MiddleHanoiPeg", false);

		org.javabip.spec.hanoi.RightHanoiPegWithData rightHanoiPeg = new org.javabip.spec.hanoi.RightHanoiPegWithData(size);
		BIPActor actor4 = engine.register(rightHanoiPeg, "RightHanoiPeg", false);

		engine.start();

		engine.execute();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Finished test, number of transitions " + hanoiMonitor.getNumberOfMoves());
		System.out.flush();

		engine.stop();
		engineFactory.destroy(engine);

		assertEquals("Hanoi tower has not reached its final state ", (int) Math.pow(2, size) - 1,
				hanoiMonitor.getNumberOfMoves());

	}

	/**
	 * Creates three hanoi pegs with 8 disks. Uses an optimal monitor for disk placement.
	 */
	@Test
	@SuppressWarnings("unused")
	public void hannoiWithDataTestSize8() {

		BIPGlue bipGlue4Hanoi = new org.javabip.spec.hanoi.HanoiOptimalGlueBuilder().build();

		BIPEngine engine = engineFactory.create("myEngine", bipGlue4Hanoi);

		int size = 8;

		HanoiOptimalMonitor hanoiMonitor = new HanoiOptimalMonitor(size);
		BIPActor actor1 = engine.register(hanoiMonitor, "hanoiMonitor", false);

		org.javabip.spec.hanoi.LeftHanoiPegWithData leftHanoiPeg = new org.javabip.spec.hanoi.LeftHanoiPegWithData(size);
		BIPActor actor2 = engine.register(leftHanoiPeg, "LeftHanoiPeg", false);

		org.javabip.spec.hanoi.MiddleHanoiPegWithData middleHanoiPeg = new org.javabip.spec.hanoi.MiddleHanoiPegWithData(size);
		BIPActor actor3 = engine.register(middleHanoiPeg, "MiddleHanoiPeg", false);

		org.javabip.spec.hanoi.RightHanoiPegWithData rightHanoiPeg = new org.javabip.spec.hanoi.RightHanoiPegWithData(size);
		BIPActor actor4 = engine.register(rightHanoiPeg, "RightHanoiPeg", false);

		engine.start();

		engine.execute();

		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Finished test, number of transitions " + hanoiMonitor.getNumberOfMoves());
		System.out.flush();

		engine.stop();
		engineFactory.destroy(engine);

		assertEquals("Hanoi tower has not reached its final state ", (int) Math.pow(2, size) - 1,
				hanoiMonitor.getNumberOfMoves());

	}

	/**
	 * Creates three hanoi pegs with 3 disks. Uses some monitor for disk placement. No data transfer happening, but
	 * plenty of interactions and more ports.
	 */
	@Test
	@SuppressWarnings("unused")
	public void hannoiNoDataSize3Test() {

		int size = 3;

		BIPGlue bipGlue4Hanoi = new HanoiGlueBuilder(size).build();

		BIPEngine engine = engineFactory.create("myEngine", bipGlue4Hanoi);

		HanoiMonitor hanoiMonitor = new HanoiMonitor(size);
		BIPActor actor1 = engine.register(hanoiMonitor, "hanoiMonitor", false);

		LeftHanoiPeg leftHanoiPeg = new LeftHanoiPeg(size);
		BIPActor actor2 = engine.register(leftHanoiPeg, "leftHanoi", false);

		MiddleHanoiPeg middleHanoiPeg = new MiddleHanoiPeg(size);
		BIPActor actor3 = engine.register(middleHanoiPeg, "middleHanoi", false);

		RightHanoiPeg rightHanoiPeg = new RightHanoiPeg(size);
		BIPActor actor4 = engine.register(rightHanoiPeg, "rightHanoi", false);

		engine.start();

		engine.execute();

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Finished test, number of transitions " + hanoiMonitor.getNumberOfMoves());
		System.out.flush();

		engine.stop();
		engineFactory.destroy(engine);

		assertEquals("Hanoi tower has not reached its final state ", (int) Math.pow(2, size) - 1,
				hanoiMonitor.getNumberOfMoves());

	}

	/**
	 * Creates three hanoi pegs with 8 disks. Uses some monitor for disk placement. No data transfer happening.
	 */
	@Test
	@SuppressWarnings("unused")
	public void hannoiNoDataSize8Test() {

		int size = 8;

		BIPGlue bipGlue4Hanoi = new HanoiGlueBuilder(size).build();

		BIPEngine engine = engineFactory.create("myEngine", bipGlue4Hanoi);

		HanoiMonitor hanoiMonitor = new HanoiMonitor(size);
		BIPActor actor1 = engine.register(hanoiMonitor, "hanoiMonitor", false);

		LeftHanoiPeg leftHanoiPeg = new LeftHanoiPeg(size);
		BIPActor actor2 = engine.register(leftHanoiPeg, "leftHanoi", false);

		MiddleHanoiPeg middleHanoiPeg = new MiddleHanoiPeg(size);
		BIPActor actor3 = engine.register(middleHanoiPeg, "middleHanoi", false);

		RightHanoiPeg rightHanoiPeg = new RightHanoiPeg(size);
		BIPActor actor4 = engine.register(rightHanoiPeg, "rightHanoi", false);

		engine.start();

		engine.execute();

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		engineFactory.destroy(engine);

		assertEquals("Hanoi tower has not reached its final state ", (int) Math.pow(2, size) - 1,
				hanoiMonitor.getNumberOfMoves());

	}

	// TODO, IMPROVEMENT When BIP protocol is optimized and wait no longer is used then enable this test.
	// Currently, this test will fail as spontaneous wait on one component blocks BIP engine cycle.
	@Test
	@Ignore
	public void testNoSpontaneousWaitBlockEverything() throws BIPException {

		BIPGlue bipGlue = new GlueBuilder() {
			@Override
			public void configure() {

				port(PSSComponent.class, "p").requires(ComponentB.class, "b");

				port(PSSComponent.class, "p").accepts(ComponentB.class, "b");

				port(ComponentB.class, "a").requiresNothing();
				port(ComponentB.class, "b").requires(PSSComponent.class, "p");

				// Just to keep the DataCoordinator happy to have something even if this BIP component is not
				// instantiated.
				data(ComponentB.class, "memoryY").to(Consumer.class, "memoryUsage");

			}

		}.build();

		BIPEngine engine = engineFactory.create("myEngine", bipGlue);

		PSSComponent pssComponent = new PSSComponent(true);
		@SuppressWarnings("unused")
		BIPActor pssExecutor = engine.register(pssComponent, "pssCompE", true);

		ComponentB bComponent = new ComponentB();
		@SuppressWarnings("unused")
		BIPActor bExecutor = engine.register(bComponent, "bCompE", true);

		Consumer cComponent = new Consumer(100);
		@SuppressWarnings("unused")
		BIPActor cExecutor = engine.register(cComponent, "cCompE", true);

		engine.start();

		engine.execute();

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		engineFactory.destroy(engine);

		assertEquals("Spontaneous wait on one component has blocked all the components", bComponent.counterA > 0, true);

	}

	@Ignore
	@Test
	public void serversTest() {

		// TODO, PLEASE use BIP glue builders and not XML file.
		// TODO, BUG? BTW, BIP glue is not using class InitialServer only Server.
		BIPGlue bipGlue = createGlue("src/test/resources/bipGlueServers.xml");

		BIPEngine engine = engineFactory.create("myEngine", bipGlue);

		// TODO, Why both Classes use the same specType org.javabip.spec.Server? Would not this cause huge problems on
		// engine side?
		// or at least the second attempt to define type of the spec will be ignored? and thus all the components will
		// actually have
		// the same behavior associated?
		InitialServer server1 = new InitialServer(1);
		Server server2 = new Server(2);
		Server server3 = new Server(3);

		@SuppressWarnings("unused")
		final BIPActor executor1 = engine.register(server1, "1", true);
		@SuppressWarnings("unused")
		final BIPActor executor2 = engine.register(server2, "2", true);
		@SuppressWarnings("unused")
		final BIPActor executor3 = engine.register(server3, "3", true);

		engine.start();
		engine.execute();

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		engineFactory.destroy(engine);

	}

	/**
	 * Test with two trackers and four peers.
	 */
	@Test
	@SuppressWarnings("unused")
	public void trackerPeerTest() {

		// TODO, PLEASE use BIP glue builders and not XML file.
		BIPGlue bipGlue = createGlue("src/test/resources/trackerPeerGlue.xml");

		BIPEngine engine = engineFactory.create("myEngine", bipGlue);

		Tracker tracker1 = new Tracker(1);
		Peer peer1a = new Peer(11);
		Peer peer1b = new Peer(12);
		Tracker tracker2 = new Tracker(2);
		Peer peer2a = new Peer(21);
		Peer peer2b = new Peer(22);

		final BIPActor executor1 = engine.register(tracker1, "1", true);
		final BIPActor executor1a = engine.register(peer1a, "11", true);
		final BIPActor executor1b = engine.register(peer1b, "12", true);
		final BIPActor executor2 = engine.register(tracker2, "2", true);
		final BIPActor executor2a = engine.register(peer2a, "21", true);
		final BIPActor executor2b = engine.register(peer2b, "22", true);

		engine.start();

		engine.execute();

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		engineFactory.destroy(engine);

		assertTrue("Tracker 1 has not made any transitions", tracker1.noOfTransitions > 0);
		assertTrue("Tracker 2 has not made any transitions", tracker2.noOfTransitions > 0);

		assertTrue("Peer 1a has not made any transitions", peer1a.noOfTransitions > 0);
		assertTrue("Peer 2a has not made any transitions", peer2a.noOfTransitions > 0);
		assertTrue("Peer 1b has not made any transitions", peer1b.noOfTransitions > 0);
		assertTrue("Peer 2b has not made any transitions", peer2b.noOfTransitions > 0);

	}

	@Test
	@SuppressWarnings("unused")
	public void feederConsumerTest() throws BIPException {

		BIPGlue bipGlue = createGlue("src/test/resources/bipGlueFeederConsumer.xml");

		BIPEngine engine = engineFactory.create("myEngine", bipGlue);

		Feeder feeder = new Feeder();
		BIPActor actor1 = engine.register(feeder, "feeder", true);

		Consumer consumer = new Consumer(350);
		BIPActor actor2 = engine.register(consumer, "consumer", true);

		engine.start();

		engine.execute();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		engineFactory.destroy(engine);

		assertTrue("Feeder has not made any transitions", feeder.noOfTransitions > 0);
		assertTrue("Consumer has not made any transitions", consumer.noOfTransitions > 0);

	}

	@Test
	@SuppressWarnings("unused")
	public void dataAvailabilityTest() throws BIPException {

		BIPGlue bipGlue = createGlue("src/test/resources/bipGlueDataAvailability.xml");

		BIPEngine engine = engineFactory.create("myEngine", bipGlue);

		ComponentA componentA = new ComponentA(250);
		ComponentB componentB = new ComponentB();
		ComponentC componentC = new ComponentC();

		BIPActor executorA = engine.register(componentA, "compA", true);
		BIPActor executorB = engine.register(componentB, "compB", true);
		BIPActor executorC = engine.register(componentC, "compC", true);

		engine.start();

		engine.execute();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		engineFactory.destroy(engine);

		assertTrue("CompA has not made any transitions", componentA.noOfTransitions > 0);
		assertTrue("CompB has not made any transitions", componentB.noOfTransitions > 0);
		assertTrue("CompC has not made any transitions", componentC.noOfTransitions > 0);

	}

	@Test
	@SuppressWarnings("unused")
	/*
	 * * The test-case is not finished due to a limitation of the data approach. The problem is in the guard in Master
	 * in compute-work-work interaction. The Master needs two different data, representing Slave id, each of the data
	 * can be provided by any slave. The DataCoordinator queries all Slave components, and outputs false for the
	 * interaction with the same component (M-S1-S1). After this false the interaction (M-S1-S2) will be also disabled
	 * since S1 is disabled.
	 * 
	 * We cannot disable the possibility of the same component providing different data during one interaction, as it is
	 * a valid usage. However we cannot distinguish in the DataCoordinator the case of M-S from the case of M-S-S, since
	 * this is information from the Glue, which is analyzed elsewhere.
	 */
	@Ignore
	public void masterSlaveTest() throws BIPException {

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

		BIPEngine engine = engineFactory.create("myEngine", bipGlue);

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
		engineFactory.destroy(engine);

		// assertTrue("Master 1 has not made any transitions", master1.noOfTransitions > 2);
		// assertTrue("Slave A has not made any transitions", slaveA.noOfTransitions > 0);
		// assertTrue("Slave B has not made any transitions", slaveB.noOfTransitions > 0);

	}

	@SuppressWarnings("unused")
	@Test
	public void philosopherwithDataTest() {

		BIPGlue bipGlue4Philosophers = new DiningPhilosophersGlueBuilder().build();

		BIPEngine engine = engineFactory.create("myEngine", bipGlue4Philosophers);

		Fork f1 = new Fork(1);
		Fork f2 = new Fork(2);
		Fork f3 = new Fork(3);

		Philosophers p1 = new Philosophers(1, 2, true);
		Philosophers p2 = new Philosophers(2, 3, true);
		Philosophers p3 = new Philosophers(3, 1, true);

		BIPActor f1E = engine.register(f1, "f1E", true);
		BIPActor f2E = engine.register(f2, "f2E", true);
		BIPActor f3E = engine.register(f3, "f3E", true);

		BIPActor p1E = engine.register(p1, "p1E", true);
		BIPActor p2E = engine.register(p2, "p2E", true);
		BIPActor p3E = engine.register(p3, "p3E", true);

		engine.start();

		engine.execute();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int noOfTimesUsed = f1.noOfTimesUsed() + f2.noOfTimesUsed() + f3.noOfTimesUsed();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int noOfTimesUsed2 = f1.noOfTimesUsed() + f2.noOfTimesUsed() + f3.noOfTimesUsed();

		engine.stop();
		engineFactory.destroy(engine);

		assertEquals("BIP engine could not progress the system.", true, noOfTimesUsed2 > noOfTimesUsed);

	}

	// TODO, This test is hitting the limitation of BIP engine concerning multiple transfers.
	// TODO, Explain exactly what is the limitation.
	@Test
	@Ignore
	public void twoDataTest() throws BIPException {

		BIPGlue bipGlue = createGlue("src/test/resources/bipGlueTwoData.xml");

		BIPEngine engine = engineFactory.create("myEngine", bipGlue);

		TwoDataTaker componentA = new TwoDataTaker(100);
		TwoDataProvider1 componentB = new TwoDataProvider1();
		TwoDataProvider2 componentC = new TwoDataProvider2();

		@SuppressWarnings("unused")
		final BIPActor executorA = engine.register(componentA, "compA", true);
		@SuppressWarnings("unused")
		final BIPActor executorB = engine.register(componentB, "compB", true);
		@SuppressWarnings("unused")
		final BIPActor executorC = engine.register(componentC, "compC", true);

		engine.start();

		engine.execute();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		engineFactory.destroy(engine);

		assertTrue("CompA has not made any transitions", componentA.noOfTransitions > 0);
		assertTrue("CompB has not made any transitions", componentB.noOfTransitions > 0);
		assertTrue("CompC has not made any transitions", componentC.noOfTransitions > 0);

	}

	@Test
	public void switchableRouteWithErrors() throws BIPException {

		BIPGlue bipGlue = new TwoSynchronGlueBuilder() {
			@Override
			public void configure() {

				synchron(SwitchableRouteErrorException.class, "on").to(RouteOnOffMonitor.class, "add");
				synchron(SwitchableRouteErrorException.class, "finished").to(RouteOnOffMonitor.class, "rm");

				port(SwitchableRouteErrorException.class, "off").acceptsNothing();
				port(SwitchableRouteErrorException.class, "off").requiresNothing();

			}

		}.build();

		BIPEngine engine = engineFactory.create("myEngine", bipGlue);

		CamelContext camelContext = new DefaultCamelContext();
		camelContext.setAutoStartup(false);

		SwitchableRouteErrorException route1 = new SwitchableRouteErrorException("1", camelContext);
		SwitchableRouteErrorException route2 = new SwitchableRouteErrorException("2", camelContext);
		SwitchableRouteErrorException route3 = new SwitchableRouteErrorException("3", camelContext);

		BIPActor actor1 = engine.register(route1, "1", true);
		BIPActor actor2 = engine.register(route2, "2", true);
		BIPActor actor3 = engine.register(route3, "3", true);

		final RoutePolicy routePolicy1 = createRoutePolicy(actor1);
		final RoutePolicy routePolicy2 = createRoutePolicy(actor2);
		final RoutePolicy routePolicy3 = createRoutePolicy(actor3);

		RouteBuilder builder = new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("file:inputfolder1?delete=true").routeId("1").routePolicy(routePolicy1).to("file:outputfolder1");
				from("file:inputfolder2?delete=true").routeId("2").routePolicy(routePolicy2).to("file:outputfolder2");
				from("file:inputfolder3?delete=true").routeId("3").routePolicy(routePolicy3).to("file:outputfolder3");
			}

		};

		try {
			camelContext.addRoutes(builder);
			camelContext.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		RouteOnOffMonitor routeOnOffMonitor = new RouteOnOffMonitor(2);
		@SuppressWarnings("unused")
		final BIPActor executorM = engine.register(routeOnOffMonitor, "monitor", true);

		engine.start();

		engine.execute();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		actor1.inform("error");

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		actor1.inform("functional");

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		actor2.inform("error");
		actor3.inform("error");

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		actor3.inform("functional");
		actor2.inform("functional");

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		engineFactory.destroy(engine);

		assertTrue("Routes have not made any transitions", route1.noOfEnforcedTransitions
				+ route2.noOfEnforcedTransitions + route3.noOfEnforcedTransitions > 0);

		assertTrue("Each route has had one error", route1.noOfErrors == 1 && route2.noOfErrors == 1
				&& route3.noOfErrors == 1);

	}

	private BIPGlue createGlue(String bipGlueFilename) {
		BIPGlue bipGlue = null;

		InputStream inputStream;
		try {
			inputStream = new FileInputStream(bipGlueFilename);

			bipGlue = GlueBuilder.fromXML(inputStream);

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		return bipGlue;
	}

	private RoutePolicy createRoutePolicy(final BIPActor actor1) {

		return new RoutePolicy() {

			public void onInit(Route route) {
			}

			public void onExchangeDone(Route route, Exchange exchange) {

				actor1.inform("end");
			}

			public void onExchangeBegin(Route route, Exchange exchange) {
			}

			@Override
			public void onRemove(Route arg0) {
			}

			@Override
			public void onResume(Route arg0) {
			}

			@Override
			public void onStart(Route arg0) {
			}

			@Override
			public void onStop(Route arg0) {
			}

			@Override
			public void onSuspend(Route arg0) {
			}
		};

	}

}
