/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.utils.xml;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.w3c.dom.Node;

public class UnknownNodeFlatMapper implements Function<Node, Stream<Node>> {
	private final Predicate<Node> recognised;
	public UnknownNodeFlatMapper(Predicate<Node> recognised) {
		this.recognised = recognised;
	}
	@Override
	public Stream<Node> apply(Node node) {
		return recognised.test(node)? Stream.of(node) : NodeListUtils.asStream(node.getChildNodes()).flatMap(this);
	}
}
