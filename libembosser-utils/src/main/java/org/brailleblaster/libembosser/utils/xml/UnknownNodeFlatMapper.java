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
