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
package org.javabip.spec;

import org.javabip.annotations.*;
import org.javabip.api.PortType;
import org.javabip.api.DataOut.AccessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ports({ @Port(name = "giveY", type = PortType.enforceable), @Port(name = "giveZ", type = PortType.enforceable),
		@Port(name = "returnY", type = PortType.enforceable), @Port(name = "returnZ", type = PortType.enforceable) })
@ComponentType(initial = "zero", name = "org.bip.spec.Feeder")
public class Feeder {
	Logger logger = LoggerFactory.getLogger(Feeder.class);

	private int memoryY = 200;
	private int memoryZ = 300;

	public int noOfTransitions = 0;

	@Transition(name = "giveY", source = "zero", target = "oneY")
	public void changeY() {
		logger.debug("Transition Y has been performed");
		noOfTransitions++;
	}

	@Transition(name = "returnY", source = "oneY", target = "zero")
	public void returnY() {
		logger.debug("Transition from oneY to zero has been performed");
		noOfTransitions++;
	}

	@Transition(name = "giveZ", source = "zero", target = "oneZ")
	public void changeZ() {
		logger.debug("Transition Z has been performed");
		noOfTransitions++;
	}

	@Transition(name = "returnZ", source = "oneZ", target = "zero")
	public void returnZ() {
		logger.debug("Transition from oneZ to zero has been performed");
		noOfTransitions++;
	}

	@Data(name = "memoryY", accessTypePort = AccessType.allowed, ports = { "giveY" })
	public int memoryY() {
		return memoryY;
	}

	@Data(name = "memoryZ", accessTypePort = AccessType.allowed, ports = { "giveZ" })
	public int memoryZ() {
		return memoryZ;
	}
}
