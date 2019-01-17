package org.brailleblaster.libembosser.drivers.utils;

import java.util.Arrays;
import java.util.stream.Stream;

public class ClassUtils {
	public static boolean isInstanceOf(Object obj, Stream<Class<?>> classes) {
		return classes.anyMatch(c -> c.isInstance(obj));
	}
	public static boolean isInstanceOf(Object obj, Class<?>... classes) {
		return isInstanceOf(obj, Arrays.stream(classes));
	}
}
