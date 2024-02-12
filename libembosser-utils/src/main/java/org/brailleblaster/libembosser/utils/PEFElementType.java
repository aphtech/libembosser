/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.w3c.dom.Element;

import com.google.common.collect.ImmutableList;

public enum PEFElementType {
	PEF(PEFNamespaceContext.PEF_NAMESPACE, "pef", ImmutableList.of(e -> "2008-1".equals(e.getAttribute("version")))),
	HEAD(PEFNamespaceContext.PEF_NAMESPACE, "head"),
	META(PEFNamespaceContext.PEF_NAMESPACE, "meta"),
	DC_FORMAT(PEFNamespaceContext.DC_NAMESPACE, "format", ImmutableList.of(e -> "application/x-pef+xml".equals(e.getTextContent()))),
	DC_IDENTIFIER(PEFNamespaceContext.DC_NAMESPACE, "identifier"),
	BODY(PEFNamespaceContext.PEF_NAMESPACE, "body"),
	GRAPHIC(PEFNamespaceContext.TG_NAMESPACE, "graphic"),
	VOLUME(PEFNamespaceContext.PEF_NAMESPACE, "volume"),
	SECTION(PEFNamespaceContext.PEF_NAMESPACE, "section"),
	PAGE(PEFNamespaceContext.PEF_NAMESPACE, "page"),
	ROW(PEFNamespaceContext.PEF_NAMESPACE, "row");
	private final String ns;
	private final String elemName;
	private final List<Predicate<Element>> additionalChecks;
	PEFElementType(String ns, String elemName) {
		this(ns, elemName, ImmutableList.of());
	}
	PEFElementType(String ns, String elemName, List<Predicate<Element>> additionalChecks) {
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
			&& t.additionalChecks.stream().allMatch(c -> c.test(element))
		).findFirst();
	}
}
