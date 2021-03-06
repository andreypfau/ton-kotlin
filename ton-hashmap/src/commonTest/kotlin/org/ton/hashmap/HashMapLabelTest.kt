package org.ton.hashmap

import org.ton.bitstring.BitString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HashMapLabelTest {
    @Test
    fun `test hml_same construction`() {
        assertNull(HashMapLabelSame.of(BitString.binary("0001")))
        assertNull(HashMapLabelSame.of(BitString.binary("1110")))
        assertNull(HashMapLabelSame.of(BitString.binary("1110111")))
        assertNull(HashMapLabelSame.of(BitString.binary("0001000")))

        assertEquals(HashMapLabelSame(true, 1), HashMapLabelSame.of(BitString.binary("1")))
        assertEquals(HashMapLabelSame(true, 2), HashMapLabelSame.of(BitString.binary("11")))
        assertEquals(HashMapLabelSame(true, 3), HashMapLabelSame.of(BitString.binary("111")))
        assertEquals(HashMapLabelSame(false, 1), HashMapLabelSame.of(BitString.binary("0")))
        assertEquals(HashMapLabelSame(false, 2), HashMapLabelSame.of(BitString.binary("00")))
        assertEquals(HashMapLabelSame(false, 3), HashMapLabelSame.of(BitString.binary("000")))
    }
}
