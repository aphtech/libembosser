package org.brailleblaster.libembosser.drivers.utils.document.events;

import java.util.Set;

public class StartDocumentEvent extends BaseOptionEvent<DocumentOption> {
	public StartDocumentEvent() {
		super();
	}
	public StartDocumentEvent(Set<DocumentOption> options) {
		super(options);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		StartDocumentEvent other = (StartDocumentEvent)obj;
		if (optionsEquals(other)) {
			return false;
		}
		return true;
	}
	@Override
	public int hashCode() {
		return 1;
	}
}