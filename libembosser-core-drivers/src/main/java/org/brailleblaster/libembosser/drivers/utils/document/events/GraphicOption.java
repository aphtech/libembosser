package org.brailleblaster.libembosser.drivers.utils.document.events;

public interface GraphicOption extends Option {

	class Height extends BaseValueOption<Integer> implements GraphicOption {
		public Height(Integer height) {
			super(height);
		}
	}

	class Indent extends BaseValueOption<Integer> implements GraphicOption {
		public Indent(Integer indent) {
			super(indent);
		}
	}

	class ImageData extends BaseValueOption<java.awt.Image> implements GraphicOption {
		public ImageData(java.awt.Image image) {
			super(image);
		}
	}

	class Width extends BaseValueOption<Integer> implements GraphicOption {
		public Width(Integer width) {
			super(width);
		}
	} }