package org.brailleblaster.libembosser.utils;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import com.google.common.collect.ImmutableList;

public class PEFNamespaceContext implements NamespaceContext {
	public static final String DC_NAMESPACE = "http://purl.org/dc/elements/1.1/";
	public static final String PEF_NAMESPACE = "http://www.daisy.org/ns/2008/pef";
	@Override
	public String getNamespaceURI(String prefix) {
		checkArgument(prefix != null, "Prefix cannot be null");
		switch(prefix) {
		case XMLConstants.DEFAULT_NS_PREFIX:
		case "pef":
			return PEFNamespaceContext.PEF_NAMESPACE;
		case "dc":
			return PEFNamespaceContext.DC_NAMESPACE;
		case XMLConstants.XMLNS_ATTRIBUTE:
			return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
		case XMLConstants.XML_NS_PREFIX:
			return XMLConstants.XML_NS_URI;
		default:
			return XMLConstants.NULL_NS_URI;
		}
	}
	@Override
	public String getPrefix(String namespaceURI) {
		checkArgument(namespaceURI != null, "Namespace URI cannot be null");
		switch(namespaceURI) {
		case PEFNamespaceContext.PEF_NAMESPACE:
			return XMLConstants.DEFAULT_NS_PREFIX;
		case PEFNamespaceContext.DC_NAMESPACE:
			return "dc";
		case XMLConstants.XML_NS_PREFIX:
			return XMLConstants.XML_NS_URI;
		case XMLConstants.XMLNS_ATTRIBUTE_NS_URI:
			return XMLConstants.XMLNS_ATTRIBUTE;
		default:
			return null;
		}
	}
	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {
		checkArgument(namespaceURI != null, "Namespace URI cannot be null");
		ImmutableList<String> prefixes;
		switch(namespaceURI) {
		case PEFNamespaceContext.PEF_NAMESPACE:
			prefixes = ImmutableList.of(XMLConstants.DEFAULT_NS_PREFIX, "pef");
			break;
		case PEFNamespaceContext.DC_NAMESPACE:
			prefixes = ImmutableList.of("dc");
			break;
		case XMLConstants.XML_NS_URI:
			prefixes = ImmutableList.of(XMLConstants.XML_NS_PREFIX);
			break;
		case XMLConstants.XMLNS_ATTRIBUTE_NS_URI:
			prefixes = ImmutableList.of(XMLConstants.XMLNS_ATTRIBUTE);
			break;
		default:
			prefixes = ImmutableList.of();
		}
		return prefixes.iterator();
	}
}