/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2023 American Printing House for the Blind
 */

package org.brailleblaster.libembosser.drivers.utils

import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream

class BrailleFilterInputStream(arg0: InputStream?) : FilterInputStream(arg0) {
    private fun translate(b: Byte): Byte {
        return if (0xff and b.toInt() in 0x60..0x7f) {
            (b - 0x20).toByte()
        } else {
            b
        }
    }

    @Throws(IOException::class)
    override fun read(): Int {
        var result = super.read()
        if (result in 0..255) {
            result = java.lang.Byte.toUnsignedInt(translate(result.toByte()))
        }
        return result
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray): Int {
        val n = super.read(b)
        for (i in 0 until n) {
            b[i] = translate(b[i])
        }
        return n
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val n = super.read(b, off, len)
        for (i in 0 until n) {
            b[i] = translate(b[i])
        }
        return n
    }
}