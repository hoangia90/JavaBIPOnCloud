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
 * Date: 15/07/2013
 */
package org.javabip.spec.hanoi;

import org.javabip.glue.GlueBuilder;

public class HanoiRandomGlueBuilder extends GlueBuilder {

	@Override
	public void configure() {

		String add = "pieceAdd";
		String remove = "pieceRemove";

		port(HanoiPegWithData.class, add).requires(HanoiPegWithData.class, remove);
		port(HanoiPegWithData.class, remove).requires(HanoiPegWithData.class, add);

		port(HanoiPegWithData.class, add).accepts(HanoiPegWithData.class, remove);
		port(HanoiPegWithData.class, remove).accepts(HanoiPegWithData.class, add);

		data(HanoiPegWithData.class, "disksize").to(HanoiPegWithData.class, "addedDisk");

	}
}
