/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */
 
package org.brailleblaster.libembosser.utils;

import java.util.stream.IntStream;

public enum BrailleMapper {
	UNICODE_TO_ASCII(new PropertyCharMapper("unicodeToAscii")),
	UNICODE_TO_ASCII_FAST(new BlockCharMapper.Builder().add('`', "@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_").add('\u2800', " A1B'K2L@CIF/MSP\"E3H9O6R^DJG>NTQ,*5<-U8V.%[$+X!&;:4\\0Z7(_?W]#Y)=").build()),
	ASCII_TO_UNICODE(new PropertyCharMapper("asciiToUnicode")),
	ASCII_TO_UNICODE_FAST(new BlockCharMapper.Builder().add(' ', "\u2800\u282e\u2810\u283c\u282b\u2829\u282f\u2804\u2837\u283e\u2821\u282c\u2820\u2824\u2828\u280c\u2834\u2802\u2806\u2812\u2832\u2822\u2816\u2836\u2826\u2814\u2831\u2830\u2823\u283f\u281c\u2839\u2808\u2801\u2803\u2809\u2819\u2811\u280b\u281b\u2813\u280a\u281a\u2805\u2807\u280d\u281d\u2815\u280f\u281f\u2817\u280e\u281e\u2825\u2827\u283a\u282d\u283d\u2835\u282a\u2833\u283b\u2818\u2838\u2808\u2801\u2803\u2809\u2819\u2811\u280b\u281b\u2813\u280a\u281a\u2805\u2807\u280d\u281d\u2815\u280f\u281f\u2817\u280e\u281e\u2825\u2827\u283a\u282d\u283d\u2835\u282a\u2833\u283b\u2818\u2838").add('\u00a0', "\u2800").build());
	private final CharMapperFunction mapper;
	BrailleMapper(CharMapperFunction mapper) {
		this.mapper = mapper;
	}
	public String map(String inputText) {
		char[] chars = inputText.toCharArray();
		IntStream.range(0, chars.length).forEach(i -> chars[i] = mapper.applyAsChar(chars[i]));
		return String.valueOf(chars);
	}
	public char map(char ch) {
		return mapper.applyAsChar(ch);
	}
}
