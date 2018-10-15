package org.brailleblaster.libembosser.drivers.generic;

import java.io.IOException;

import org.brailleblaster.libembosser.drivers.utils.BaseTextEmbosser.DocumentHandler;

import com.google.common.io.FileBackedOutputStream;

public class GenericTextDocumentHandler implements DocumentHandler {
	private int pageNum = 0;
	private FileBackedOutputStream output;
	private final int fileThreshold;

	public GenericTextDocumentHandler() {
		fileThreshold = 1000000;
		output = new FileBackedOutputStream(fileThreshold);
	}

	@Override
	public void setTopMargin(int lines) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLeftMargin(int cells) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLinesPerPage(int lines) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCellsPerLine(int cells) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startDocument() {
		pageNum = 0;
		try {
			output.reset();
		} catch (IOException e) {
			output = new FileBackedOutputStream(fileThreshold);
		}
	}
	
	@Override
	public void endDocument() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void startVolume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endVolume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startPage() {
		// When at the start of the document we do not insert a form feed.
		// Assume embosser is already on a new page due to starting a new job.
		if (pageNum > 0) {
			
		}
		pageNum++;
	}

	@Override
	public void endPage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startLine() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endLine() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeBraille(String Braille) {
		// TODO Auto-generated method stub
		
	}

}
