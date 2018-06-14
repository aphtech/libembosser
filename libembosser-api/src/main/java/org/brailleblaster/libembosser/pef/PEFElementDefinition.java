package org.brailleblaster.libembosser.pef;

enum PEFElementDefinition {
	PEF(PEFDocument.PEF_NAMESPACE, "pef"),
	HEAD(PEFDocument.PEF_NAMESPACE, "head"),
	META(PEFDocument.PEF_NAMESPACE, "meta"),
	BODY(PEFDocument.PEF_NAMESPACE, "body"),
	VOLUME(PEFDocument.PEF_NAMESPACE, "volume"),
	SECTION(PEFDocument.PEF_NAMESPACE, "section"),
	PAGE(PEFDocument.PEF_NAMESPACE, "page"),
	ROW(PEFDocument.PEF_NAMESPACE, "row");
	private String namespace;
	private String localName;
	PEFElementDefinition(String namespace, String localName) {
		this.namespace = namespace;
		this.localName = localName;
	}
}
