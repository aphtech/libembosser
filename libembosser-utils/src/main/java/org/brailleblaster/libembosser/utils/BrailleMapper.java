/*******************************************************************************
 * BrailleBlaster Braille Transcription Application
 *   
 * Copyright (C) 2012-2017
 * American Printing House for the Blind, Inc. www.aph.org
 * 1839 Frankfort Ave.
 * Louisville, KY 40206
 *
 * Portions copyright (C) 2010, 2012 
 * ViewPlus Technologies, Inc. www.viewplus.com
 *    and
 *    Abilitiessoft, Inc. www.abilitiessoft.com
 *
 * All rights reserved
 *   
 *    This file may contain code borrowed from files produced by various 
 *    Java development teams. These are gratefully acknowledged.
 *   
 * This file is free software; you can redistribute it and/or modify it
 *    under the terms of the Apache 2.0 License, as given at
 *    http://www.apache.org/licenses/
 *   
 *    This fi	le is distributed in the hope that it will be useful, but
 *    WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE
 *    See the Apache 2.0 License for more details.
 *   
 * You should have received a copy of the Apache 2.0 License along with 
 *    this program; see the file LICENSE.
 *    If not, see
 *    http://www.apache.org/licenses/
 *   
 *
 * 			
 *******************************************************************************/
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
