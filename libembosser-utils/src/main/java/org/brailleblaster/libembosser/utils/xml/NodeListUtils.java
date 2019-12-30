package org.brailleblaster.libembosser.utils.xml;

import java.util.stream.Stream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListUtils {
	public static Stream<Node> asStream(NodeList nodes) {
		Stream.Builder<Node> builder = Stream.builder();
		for (int i = 0; i < nodes.getLength(); i++) {
			builder.add(nodes.item(i));
		}
		return builder.build();
	}
}
