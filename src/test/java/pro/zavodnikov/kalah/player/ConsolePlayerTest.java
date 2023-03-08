/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2025 Dmitry Zavodnikov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pro.zavodnikov.kalah.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ConsolePlayer}.
 *
 * @author Dmitry Zavodnikov
 */
class ConsolePlayerTest {

    @Test
    void testName() throws NoSuchAlgorithmException {
        final var player = new ConsolePlayer("test", "pass");
        assertEquals("test", player.getName());
    }

    @Test
    void testEquals() throws NoSuchAlgorithmException {
        final var player1 = new ConsolePlayer("test", "pass");
        final var player2 = new ConsolePlayer("test", "pass");
        assertFalse(player1 == player2); // Objects are different.
        assertTrue(player1.equals(player2));
    }

    @Test
    void testIsPasswordValid() throws NoSuchAlgorithmException {
        final var player = new ConsolePlayer("test", "pass");
        assertTrue(player.isPasswordValid("pass"));
        assertFalse(player.isPasswordValid("password"));
    }
}
