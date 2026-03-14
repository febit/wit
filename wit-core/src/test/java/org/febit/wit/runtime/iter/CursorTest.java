package org.febit.wit.runtime.iter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CursorTest {

    @Test
    void test() {
        var cursor = new Cursor();
        assertEquals(-1, cursor.get());
        cursor.next();
        assertEquals(0, cursor.get());
        cursor.next();
        assertEquals(1, cursor.get());
        cursor.next();
        assertEquals(2, cursor.get());
        cursor.next();
        assertEquals(3, cursor.get());
    }

}
