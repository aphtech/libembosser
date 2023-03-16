/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

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
