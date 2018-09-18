package org.brailleblaster.libembosser.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class PropertyCharMapper implements CharMapperFunction {

	private final Map<String, String> map;
	public PropertyCharMapper(String key) {
		Map<String, String> tmpMap;
		try (InputStream in = getClass().getResourceAsStream(String.format("/org/brailleblaster/libembosser/brailleMappings/%s.properties", key))) {
			Properties properties = new Properties();
			properties.load(in);
			tmpMap = Maps.fromProperties(properties);
		} catch (IOException e) {
			// Log the problem and just provide an empty map
			tmpMap = ImmutableMap.of();
		}
		map = tmpMap;
	}
	@Override
	public char applyAsChar(char operand) {
		String input = Character.toString(operand);
		return map.getOrDefault(input, input).charAt(0);
	}
}
