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
package pro.zavodnikov.kalah.game.pit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.Test;

import pro.zavodnikov.kalah.player.Player;
import pro.zavodnikov.kalah.player.RandomPlayer;

/**
 * Tests for {@link Pit}.
 *
 * @author Dmitry Zavodnikov
 */
class PitTest {

    @Test
    void testAddStone() {
        final var np = new RegularPit(5);
        final var bp = new BigPit(2);

        assertEquals(5, np.getStones());
        assertEquals(2, bp.getStones());

        np.addOneStone();
        assertEquals(6, np.getStones());

        bp.addOneStone();
        assertEquals(3, bp.getStones());
    }

    @Test
    void testGetStones() {
        final var np = new RegularPit(5);

        assertEquals(5, np.getStones());
        assertEquals(5, np.pickupStones());

        assertEquals(0, np.getStones());
        assertEquals(0, np.pickupStones());

        assertEquals(0, np.getStones());
        assertEquals(0, np.pickupStones());
    }

    private void assertPits(final Pit startPit, final Player player, final Pit... expectedPits) {
        Pit currentPit = startPit.getNextPit(player);
        for (Pit p : expectedPits) {
            assertEquals(p, currentPit);
            currentPit = currentPit.getNextPit(player);
        }
    }

    @Test
    void testNextPit() {
        final var np1 = new RegularPit(0);
        final var np2 = new RegularPit(0);
        final var bp = new BigPit(0);

        final var r = new Random(1L);

        final var player1 = new RandomPlayer("Player 1", r);
        np1.setNextPit(player1, np2);
        np2.setNextPit(player1, bp);
        bp.setNextPit(player1, np1);

        final var player2 = new RandomPlayer("Player 2", r);
        np1.setNextPit(player2, np2);
        np2.setNextPit(player2, np1);

        assertPits(np1, player1, np2, bp, np1, np2, bp);
        assertPits(np2, player1, bp, np1, np2, bp, np1);

        assertPits(np1, player2, np2, np1, np2, np1);
        assertPits(np2, player2, np1, np2, np1, np2);
    }
}
