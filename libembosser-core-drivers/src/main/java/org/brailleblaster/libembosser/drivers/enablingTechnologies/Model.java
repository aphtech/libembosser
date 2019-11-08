package org.brailleblaster.libembosser.drivers.enablingTechnologies;

import org.brailleblaster.libembosser.drivers.utils.TextEmbosserModel;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Bytes;

public enum Model implements TextEmbosserModel {
	BOOK_MAKER("libembosser.et.bookmaker", "BookMaker", RN, RNF, ImmutableList.of()),
	BRAILLE_EXPRESS("libembosser.et.braille_express", "Braille Express", RN, RNF, ImmutableList.of()),
	THOMAS("libembosser.et.thomas", "Thomas", RN, RNF, ImmutableList.of()),
	THOMAS_PRO("libembosser.et.thomas_pro", "Thomas Pro", RN, RNF, ImmutableList.of()),
	ET("libembosser.et.et", "ET", RN, RNF, ImmutableList.of()),
	JULIET_CLASSIC("libembosser.et.juliet_classic", "Juliet Classic", RN, RNF, ImmutableList.of()),
	JULIET_PRO("libembosser.et.juliet_pro", "Juliet Pro", RN, RNF, ImmutableList.of()),
	JULIET_PRO60("libembosser.et.juliet_pro60", "Juliet Pro60", RN, RNF, ImmutableList.of()),
	ROMEO_PRO50("libembosser.et.romeo_pro50", "Romeo Pro50", RN, RNF, ImmutableList.of()),
	ROMEO25("libembosser.et.romeo_25", "Romeo25", RN, RNF, ImmutableList.of()),
	TRIDENT("libembosser.et.trident", "Trident", RN, RNF, X1A),
	CYCLONE("libembosser.et.cyclone", "Cyclone", RN, RNF, X1A),
	PHOENIX_SILVER("libembosser.et.phoenix_silver", "Phoenix  silver", RN, RNF, X1A),
	PHOENIX_GOLD("libembosser.et.phoenix_gold", "Phoenix Gold", RN, RNF, X1A),
	ROMEO_ATTACHE("libembosser.et.romeo_attache", "Romeo Attach\u00e9", N, NF, ImmutableList.of()),
	ROMEO_ATTACHE_PRO("libembosser.et.romeo_attache_pro", "Romeo Attach\u00e9 Pro", N, NF, ImmutableList.of());
	private final ImmutableList<Byte> lineEnd;
	private final ImmutableList<Byte> pageEnd;
	private final ImmutableList<Byte> docEnd;
	private final String id;
	private final String name;
	private Model(String id, String name, ImmutableList<Byte> lineEnd, ImmutableList<Byte> pageEnd, ImmutableList<Byte> docEnd) {
		this.lineEnd = lineEnd;
		this.pageEnd = pageEnd;
		this.docEnd = docEnd;
		this.id = id;
		this.name = name;
	}
	@Override
	public byte[] getLineEnd() {
		return Bytes.toArray(lineEnd);
	}
	@Override
	public byte[] getPageEnd() {
		return Bytes.toArray(pageEnd);
	}
	@Override
	public byte[] getDocEnd() {
		return Bytes.toArray(docEnd);
	}
	@Override
	public String getId() {
		return id;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public String getManufacturer() {
		return "";
	}
}