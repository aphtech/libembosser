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
