package org.brailleblaster.libembosser.drivers.utils;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

@FunctionalInterface	
public interface DocumentHandler {
	interface DocumentEvent {}
	interface OptionEvent {
		public Set<? extends Option> getOptions();
	}
	abstract class BaseOptionEvent<T extends Option> implements OptionEvent, DocumentEvent {
		private Set<T> options;
		protected BaseOptionEvent() {
			this.options = ImmutableSet.of();
		}
		protected BaseOptionEvent(Set<T> options) {
			this.options = ImmutableSet.copyOf(options);
		}
		@Override
		public Set<T> getOptions() {
			return options;
		}
	}
	class StartDocumentEvent extends BaseOptionEvent<DocumentOption> {
		public StartDocumentEvent() {
			super();
		}
		public StartDocumentEvent(Set<DocumentOption> options) {
			super(options);
		}
	}
	class StartVolumeEvent extends BaseOptionEvent<VolumeOption> {
		public StartVolumeEvent() {
			super();
		}
		public StartVolumeEvent(Set<VolumeOption> options) {
			super(options);
		}
	}
	class StartSectionEvent extends BaseOptionEvent<SectionOption> {
		public StartSectionEvent() {
			super();
		}
		public StartSectionEvent(Set<SectionOption> options) {
			super(options);
		}
	}
	class StartPageEvent extends BaseOptionEvent<PageOption> {
		public StartPageEvent() {
			super();
		}
		public StartPageEvent(Set<PageOption> options) {
			super(options);
		}
	}
	class StartLineEvent extends BaseOptionEvent<RowOption> {
		public StartLineEvent() {
			super();
		}
		public StartLineEvent(Set<RowOption> options) {
			super(options);
		}
	}
	class BrailleEvent implements DocumentEvent {
		private String braille;
		public BrailleEvent(String braille) {
			this.braille =braille;
		}
		public String getBraille() {
			return braille;
		}
	}
	class EndLineEvent implements DocumentEvent {}
	class EndPageEvent implements DocumentEvent {}
	class EndSectionEvent implements DocumentEvent {}
	class EndVolumeEvent implements DocumentEvent {}
	class EndDocumentEvent implements DocumentEvent {}
	/**
	 * Interface which should be used by all option types.
	 * 
	 * @author Michael Whapples
	 *
	 */
	interface Option { }
	interface ValueOption<T> {
		public T getValue();
	}
	/** 
	 * Interface to identify options which can apply to the document.
	 * 
	 * @author Michael Whapples
	 *
	 */
	interface DocumentOption extends Option { }
	/**
	 * Interface to identify options which can apply to volumes.
	 * 
	 * @author Michael Whapples
	 *
	 */
	interface VolumeOption extends Option { }
	/**
	 * Interface to identify options which can apply to a section.
	 * 
	 * @author Michael Whapples
	 *
	 */
	interface SectionOption extends Option { }
	/**
	 * Interface to identify options which can apply to pages.
	 * 
	 * @author Michael Whapples
	 *
	 */
	interface PageOption extends Option { }
	/**
	 * Interface to identify options which can apply to rows.
	 * 
	 * @author Michael Whapples
	 *
	 */
	interface RowOption extends Option { }
	abstract class BaseOption {
		@Override
		public boolean equals(Object other) {
			if (other == null) {
				return false;
			}
			return getClass().equals(other.getClass());
		}
		@Override
		public int hashCode() {
			return getClass().hashCode();
		}
	}
	abstract class BaseValueOption<T> extends BaseOption implements ValueOption<T> {
		private final T value;
		public BaseValueOption(T value) {
			this.value = value;
		}
		public T getValue() {
			return value;
		}
	}
	final class CellsPerLine extends BaseValueOption<Integer> implements DocumentOption, VolumeOption, SectionOption, PageOption {
		public CellsPerLine(Integer value) {
			super(value);
		}
	}
	final class LinesPerPage extends BaseValueOption<Integer> implements DocumentOption, VolumeOption, SectionOption, PageOption {
		public LinesPerPage(Integer value) {
			super(value);
		}
	}
	final class RowGap extends BaseValueOption<Integer> implements DocumentOption, VolumeOption, SectionOption, RowOption {
		public RowGap(Integer value) {
			super(value);
		}
	}
	final class Duplex extends BaseValueOption<Boolean> implements DocumentOption, VolumeOption, SectionOption {
		public Duplex(Boolean value) {
			super(value);
		}
	}
	public void onEvent(DocumentEvent event);
}