/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.utils.xml;

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class NodeUtils {
	public static Stream<Node> findMatchingDescendants(Element e, Predicate<Node> filter) {
		return NodeListUtils.asStream(e.getChildNodes()).flatMap(new UnknownNodeFlatMapper(filter));
	}
}
