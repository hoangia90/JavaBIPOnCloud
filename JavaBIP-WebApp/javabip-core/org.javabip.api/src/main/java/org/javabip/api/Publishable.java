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
 * Date: 27.01.14
 */

package org.javabip.api;

/**
 * It specifies that a BIP entity can publish itself using framework mechanisms and make it visible to other BIP
 * entities.
 */
public interface Publishable extends Identifiable {

	/**
	 * It publishes a BIP entity in such a manner that it can be found using the framework mechanisms.
	 */
	void publish();

	/**
	 * It unpublishes a BIP entity so that is no longer possible to find using the framework mechanisms.
	 */
	void unpublish();

}
