package org.brailleblaster.libembosser.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.w3c.dom.Element;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public enum PEFElementType {
	PEF(PEFNamespaceContext.PEF_NAMESPACE, "pef", ImmutableList.of(e -> "2008-1".equals(e.getAttribute("version")))),
	HEAD(PEFNamespaceContext.PEF_NAMESPACE, "head"),
	META(PEFNamespaceContext.PEF_NAMESPACE, "meta"),
	DC_FORMAT(PEFNamespaceContext.DC_NAMESPACE, "format", ImmutableList.of(e -> "application/x-pef+xml".equals(e.getTextContent()))),
	DC_IDENTIFIER(PEFNamespaceContext.DC_NAMESPACE, "identifier"),
	BODY(PEFNamespaceContext.PEF_NAMESPACE, "body"),
	VOLUME(PEFNamespaceContext.PEF_NAMESPACE, "volume"),
	SECTION(PEFNamespaceContext.PEF_NAMESPACE, "section"),
	PAGE(PEFNamespaceContext.PEF_NAMESPACE, "page"),
	ROW(PEFNamespaceContext.PEF_NAMESPACE, "row");
	private String ns;
	private String elemName;
	private List<Predicate<Element>> additionalChecks;
	private PEFElementType(String ns, String elemName) {
		this(ns, elemName, ImmutableList.of());
	}
	private PEFElementType(String ns, String elemName, List<Predicate<Element>> additionalChecks) {
		this.ns = ns;
		this.elemName = elemName;
		this.additionalChecks = additionalChecks;
	}
	public String getNamespaceUri() {
		return this.ns;
	}
	public String getElementName() {
		return this.elemName;
	}
	public static Optional<PEFElementType> findElementType(Element element) {
		return Arrays.stream(PEFElementType.values()).filter(t -> 
			t.ns.equals(element.getNamespaceURI())
			&& t.elemName.equals(element.getLocalName())
			&& Iterables.all(t.additionalChecks, c -> c.test(element))
		).findFirst();
	}
}
