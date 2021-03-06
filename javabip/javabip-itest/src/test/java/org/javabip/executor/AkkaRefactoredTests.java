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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.javabip.spec.ComponentAWithEnvData;
import org.javabip.spec.ComponentAWithEnvDataInterface;
import org.javabip.spec.ComponentB;
import org.javabip.spec.ComponentC;
import org.javabip.spec.MemoryMonitor;
import org.javabip.spec.ProperComponentAWithEnvData;
import org.javabip.spec.SwitchableRouteDataTransfers;
import org.javabip.spec.seal.SealableData;
import org.javabip.spec.seal.SealableDataReader;
import org.javabip.spec.seal.SealableDataWriter;
import org.javabip.api.BIPActor;
import org.javabip.api.BIPEngine;
import org.javabip.api.BIPGlue;
import org.javabip.api.OrchestratedExecutor;
import org.javabip.engine.factory.EngineFactory;
import org.javabip.exceptions.BIPException;
import org.javabip.executor.ExecutorHandler;
import org.javabip.executor.ExecutorKernel;
import org.javabip.executor.TunellingExecutorHandler;
import org.javabip.glue.GlueBuilder;
import org.javabip.glue.TwoSynchronGlueBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;

public class AkkaRefactoredTests {

	ActorSystem system;
	EngineFactory engineFactory;

	@Before
	public void initialize() {

		system = ActorSystem.create("MySystem");
		engineFactory = new EngineFactory(system);

	}

	@After
	public void cleanup() {

		system.shutdown();

	}

	@Test
	public void synchronGlueBuilderSwitchableRouteTest() {

		BIPGlue glue = new TwoSynchronGlueBuilder() {
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

		assertEquals("Incorrect number of accepts ", 5, glue.getAcceptConstraints().size());
		assertEquals("Incorrect number of requires ", 5, glue.getRequiresConstraints().size());
		assertEquals("Incorrect number of data wires ", 1, glue.getDataWires().size());

	}

	@SuppressWarnings("unused")
	@Test
	/**
	 * Testcase: there are three instances of sealable data, two writer instances and one reader instance. The data is sealed once a read is performed.
	 */
	public void akkaSealableDataTest() {

		BIPGlue bipGlue4SealableData = new TwoSynchronGlueBuilder() {

			@Override
			public void configure() {

				synchron(SealableData.class, "get").to(SealableDataReader.class, "read");
				synchron(SealableData.class, "set").to(SealableDataWriter.class, "write");

				data(SealableDataWriter.class, "value").to(SealableData.class, "input");
				data(SealableData.class, "value").to(SealableDataReader.class, "input");

			}
		}.build();

		BIPEngine engine = engineFactory.create("myEngine", bipGlue4SealableData);

		// Two not initialized and one initialized data
		SealableData<Integer> data1 = new SealableData<Integer>();
		BIPActor actor1 = engine.register(data1, "data1", true);

		SealableData<Integer> data2 = new SealableData<Integer>();
		BIPActor actor2 = engine.register(data2, "data2", true);

		SealableData<Integer> data3 = new SealableData<Integer>(5);
		BIPActor actor3 = engine.register(data3, "data3", true);

		// Two writers.
		SealableDataWriter<Integer> writer1 = new SealableDataWriter<Integer>(4);
		BIPActor actor4 = engine.register(writer1, "writer1", true);

		SealableDataWriter<Integer> writer2 = new SealableDataWriter<Integer>(3);
		BIPActor actor5 = engine.register(writer2, "writer2", true);

		// One reader.
		SealableDataReader<Integer> reader = new SealableDataReader<Integer>();
		BIPActor actor6 = engine.register(reader, "reader", true);

		engine.specifyGlue(bipGlue4SealableData);
		engine.start();

		engine.execute();

		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		engineFactory.destroy(engine);

		assertEquals("All SealableData are sealed", true, data1.isSealed() & data2.isSealed() & data3.isSealed());

	}

	@Test
	public void dataAvailabilityTestWithEnvDataSpontaneous() throws BIPException {

		BIPGlue bipGlue = createGlue("src/test/resources/bipGlueDataAvailability.xml");

		BIPEngine engine = engineFactory.create("myEngine", bipGlue);

		ComponentAWithEnvData componentA = new ComponentAWithEnvData(250);
		BIPActor actor1 = engine.register(componentA, "compA", true);

		ComponentB componentB = new ComponentB();
		@SuppressWarnings("unused")
		BIPActor actor2 = engine.register(componentB, "compB", true);

		ComponentC componentC = new ComponentC();
		@SuppressWarnings("unused")
		BIPActor actor3 = engine.register(componentC, "compC", true);

		engine.specifyGlue(bipGlue);
		engine.start();

		engine.execute();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Map<String, Object> data = new HashMap<String, Object>();

		data.put("memoryLimit", new Integer(500));

		actor1.inform("b", data);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		engineFactory.destroy(engine);

		assertEquals("New environment based memory limit is not set", componentA.memoryLimit, 500);

	}

	@Test
	public void simpleProxyTest() {

		ComponentAWithEnvData componentA = new ComponentAWithEnvData(250);

		ExecutorKernel executor = new ExecutorKernel(componentA, "compA", true);

		ComponentAWithEnvDataInterface proxy1 = (ComponentAWithEnvDataInterface) AkkaRefactoredTests.createSimpleProxy(
				system, OrchestratedExecutor.class.getClassLoader(), executor, componentA);

		proxy1.spontaneousOfA(500);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals("New environment based memory limit is not set", componentA.memoryLimit, 500);

	}

	@Test
	public void simpleTunnelingProxyTest() {

		BIPGlue bipGlue = createGlue("src/test/resources/bipGlueDataAvailability.xml");

		BIPEngine engine = engineFactory.create("myEngine", bipGlue);

		ComponentAWithEnvData componentA = new ComponentAWithEnvData(250);

		ComponentAWithEnvDataInterface proxy1 = (ComponentAWithEnvDataInterface) engine.register(componentA, "compA",
				true);

		ComponentB componentB = new ComponentB();
		@SuppressWarnings("unused")
		BIPActor actor2 = engine.register(componentB, "compB", true);

		ComponentC componentC = new ComponentC();
		@SuppressWarnings("unused")
		BIPActor actor3 = engine.register(componentC, "compC", true);

		engine.specifyGlue(bipGlue);
		engine.start();

		engine.execute();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		proxy1.spontaneousOfA(500);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		engineFactory.destroy(engine);

		assertEquals("New environment based memory limit is not set", componentA.memoryLimit, 500);

	}

	@Test
	public void bipProxyTest() throws BIPException {

		BIPGlue bipGlue = createGlue("src/test/resources/bipGlueDataAvailability.xml");

		BIPEngine engine = engineFactory.create("myEngine", bipGlue);

		ProperComponentAWithEnvData componentA = new ProperComponentAWithEnvData(250);

		ComponentAWithEnvDataInterface proxy1 = (ComponentAWithEnvDataInterface) engine.register(componentA, "compA",
				true);

		ComponentB componentB = new ComponentB();
		@SuppressWarnings("unused")
		BIPActor actor2 = engine.register(componentB, "compB", true);

		ComponentC componentC = new ComponentC();
		@SuppressWarnings("unused")
		BIPActor actor3 = engine.register(componentC, "compC", true);

		engine.specifyGlue(bipGlue);
		engine.start();

		engine.execute();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		proxy1.spontaneousOfA(500);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		engine.stop();
		engineFactory.destroy(engine);

		assertEquals("New environment based memory limit is not set", componentA.memoryLimit, 500);

	}

	// TODO, add a comment, what is this method for?
	public static Object createSimpleProxy(ActorSystem actorSystem, ClassLoader classLoader,
			OrchestratedExecutor executor, Object bipSpec) {

		final Object proxy = ExecutorHandler.newProxyInstance(classLoader, executor, bipSpec);

		@SuppressWarnings({ "serial", "unchecked" })
		Object executorActor = TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<Object>((Class<? super Object>) proxy.getClass(), new Creator<Object>() {
					public Object create() {
						return proxy;
					}
				}), executor.getId());

		return executorActor;

	}

	// TODO, add a comment, what is this method for?
	public static Object createTunellingProxy(ActorSystem actorSystem, ClassLoader classLoader,
			OrchestratedExecutor executor, Object bipSpec) {

		final Object proxy = TunellingExecutorHandler.newProxyInstance(classLoader, executor, bipSpec);

		@SuppressWarnings({ "serial", "unchecked" })
		Object executorActor = TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<Object>((Class<? super Object>) proxy.getClass(), new Creator<Object>() {
					public Object create() {
						return proxy;
					}
				}), executor.getId());

		return executorActor;

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

}
