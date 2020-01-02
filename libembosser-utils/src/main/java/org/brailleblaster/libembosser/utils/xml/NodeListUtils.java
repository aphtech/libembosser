package org.brailleblaster.libembosser.utils.xml;

import java.util.List;
import java.util.stream.Stream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.ImmutableList;

/**
 * Utility methods for NodeList.
 * 
 * @author Michael Whapples
 *
 */
public class NodeListUtils {
	/**
	 * Get a NodeList as a stream.
	 * 
	 * @param nodes The NodeList.
	 * @return An immutable stream representation of the NodeList.
	 */
	public static Stream<Node> asStream(NodeList nodes) {
		Stream.Builder<Node> builder = Stream.builder();
		for (int i = 0; i < nodes.getLength(); i++) {
			builder.add(nodes.item(i));
		}
		return builder.build();
	}
	/**
	 * Get the NodeList as a List.
	 * 
	 * @param nodes The NodeList.
	 * @return An immutable list representation of the NodeList.
	 */
	public static List<Node> asList(NodeList nodes) {
		ImmutableList.Builder<Node> builder = ImmutableList.builderWithExpectedSize(nodes.getLength());
		for (int i = 0; i < nodes.getLength(); i++) {
			builder.add(nodes.item(i));
		}
		return builder.build();
	}
}
