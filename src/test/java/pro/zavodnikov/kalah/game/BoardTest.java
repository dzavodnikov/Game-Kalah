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
package pro.zavodnikov.kalah.game;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import pro.zavodnikov.kalah.player.ComputerPlayer;
import pro.zavodnikov.kalah.player.Player;
import pro.zavodnikov.kalah.player.RandomPlayer;

/**
 * Tests for {@link Board}.
 *
 * @author Dmitry Zavodnikov
 */
class BoardTest {

    @Test
    void testCreate() {
        final var rand = new Random(1L);
        final var player1 = new RandomPlayer("Player 1", rand);
        final var player2 = new RandomPlayer("Player 2", rand);
        final var player3 = new RandomPlayer("Player 3", rand);

        assertThrows(IllegalArgumentException.class, () -> new Board(player1, null, player1));
        assertThrows(IllegalArgumentException.class, () -> new Board(player1, player1, player1));
        assertThrows(IllegalArgumentException.class, () -> new Board(player1, player1, player3));
        final var board = new Board(player1, player2, player1);
        assertEquals(player1, board.getActivePlayer());
        assertEquals(0, board.getTurnNum());
    }

    private void assertPlayerRegularPits(final Board board, final Player player, int... expectedRegularPits) {
        assertArrayEquals(expectedRegularPits, board.getRegularPits().get(player));
    }

    private void assertPlayerBigPit(final Board board, final Player player, int expectedBigPit) {
        assertEquals(expectedBigPit, board.getBigPits().get(player));
    }

    @Test
    void testInitThenActivePlayerAndGetPits() {
        final var rand = new Random(1L);
        final var player1 = new RandomPlayer("Player 1", rand);
        final var player2 = new RandomPlayer("Player 2", rand);
        final var board = new Board(player1, player2, player1);
        board.init(new int[] { 1, 2 }, 3, new int[] { 4, 5 }, 6);
        assertEquals(player1, board.getActivePlayer());

        final var regPits = board.getRegularPits();
        assertEquals(2, regPits.keySet().size());
        assertTrue(regPits.keySet().contains(player1));
        assertTrue(regPits.keySet().contains(player2));

        assertPlayerRegularPits(board, player1, 1, 2);
        assertPlayerRegularPits(board, player2, 4, 5);

        final var bigPits = board.getBigPits();
        assertEquals(regPits.keySet(), bigPits.keySet());
        assertPlayerBigPit(board, player1, 3);
        assertPlayerBigPit(board, player2, 6);
    }

    @Test
    void testIsGameOverAndWinner() {
        final var rand = new Random(1L);
        final var player1 = new RandomPlayer("Player 1", rand);
        final var player2 = new RandomPlayer("Player 2", rand);
        final var board = new Board(player1, player2, player1);
        assertTrue(board.isGameOver());
        assertNull(board.getWinner());

        board.init(new int[] { 1, 2 }, 3, new int[] { 4, 5 }, 6);
        assertFalse(board.isGameOver());
        assertNull(board.getWinner());

        board.init(new int[] { 0, 0 }, 1, new int[] { 0, 0 }, 0);
        assertTrue(board.isGameOver());
        assertEquals(player1, board.getWinner());
    }

    @Test
    void testTurnEndsOnNonEmptyRegularPit() {
        final var rand = new Random(1L);
        final var player1 = new RandomPlayer("Player 1", rand);
        final var player2 = new RandomPlayer("Player 2", rand);
        final var board = new Board(player1, player2, player1);
        board.init(new int[] { 1, 2 }, 3, new int[] { 4, 5 }, 6);
        assertEquals(player1, board.getActivePlayer());
        assertEquals(0, board.getTurnNum());

        board.turn(0);
        assertEquals(player2, board.getActivePlayer());
        assertEquals(1, board.getTurnNum());
        assertPlayerRegularPits(board, player1, 0, 3);
        assertPlayerRegularPits(board, player2, 4, 5);
        assertPlayerBigPit(board, player1, 3);
        assertPlayerBigPit(board, player2, 6);
        assertFalse(board.isGameOver());
        assertNull(board.getWinner());

        board.turn(0);
        assertEquals(player1, board.getActivePlayer());
        assertEquals(2, board.getTurnNum());
        assertPlayerRegularPits(board, player1, 1, 4);
        assertPlayerRegularPits(board, player2, 0, 6);
        assertPlayerBigPit(board, player1, 3);
        assertPlayerBigPit(board, player2, 7);
        assertFalse(board.isGameOver());
        assertNull(board.getWinner());

        board.turn(1);
        assertEquals(player2, board.getActivePlayer());
        assertEquals(3, board.getTurnNum());
        assertPlayerRegularPits(board, player1, 2, 0);
        assertPlayerRegularPits(board, player2, 1, 7);
        assertPlayerBigPit(board, player1, 4);
        assertPlayerBigPit(board, player2, 7);
        assertFalse(board.isGameOver());
        assertNull(board.getWinner());
    }

    @Test
    void testTurnEndsOnEmptyRegularPit() {
        final var rand = new Random(1L);
        final var player1 = new RandomPlayer("Player 1", rand);
        final var player2 = new RandomPlayer("Player 2", rand);
        final var board = new Board(player1, player2, player1);
        board.init(new int[] { 5, 1, 0 }, 2, new int[] { 3, 4, 6 }, 7);
        assertEquals(player1, board.getActivePlayer());
        assertEquals(0, board.getTurnNum());

        board.turn(1);
        assertEquals(player2, board.getActivePlayer());
        assertEquals(1, board.getTurnNum());
        assertPlayerRegularPits(board, player1, 5, 0, 0); // Collect stones from opposite pit.
        assertPlayerRegularPits(board, player2, 0, 4, 6);
        assertPlayerBigPit(board, player1, 6);
        assertPlayerBigPit(board, player2, 7);
        assertFalse(board.isGameOver());
        assertNull(board.getWinner());
    }

    @Test
    void testTurnEndsOnEmptyOppositeRegularPit() {
        final var rand = new Random(1L);
        final var player1 = new RandomPlayer("Player 1", rand);
        final var player2 = new RandomPlayer("Player 2", rand);
        final var board = new Board(player1, player2, player1);
        board.init(new int[] { 5, 1, 2 }, 3, new int[] { 4, 0, 6 }, 7);

        assertEquals(player1, board.getActivePlayer());
        assertEquals(0, board.getTurnNum());

        board.turn(0);

        assertEquals(player2, board.getActivePlayer());
        assertEquals(1, board.getTurnNum());
        assertFalse(board.isGameOver());
        assertNull(board.getWinner());

        assertPlayerRegularPits(board, player1, 0, 2, 3);
        assertPlayerRegularPits(board, player2, 5, 1, 6);
        assertPlayerBigPit(board, player1, 4);
        assertPlayerBigPit(board, player2, 7);
    }

    @Test
    void testTurnGameOverOnEmptyRegularPit() {
        final var rand = new Random(1L);
        final var player1 = new RandomPlayer("Player 1", rand);
        final var player2 = new RandomPlayer("Player 2", rand);
        final var board = new Board(player1, player2, player1);
        board.init(new int[] { 0, 1, 0 }, 2, new int[] { 3, 4, 6 }, 7);
        assertEquals(player1, board.getActivePlayer());
        assertEquals(0, board.getTurnNum());

        board.turn(1);

        assertNull(board.getActivePlayer());
        assertEquals(1, board.getTurnNum());
        assertTrue(board.isGameOver());
        assertEquals(player2, board.getWinner());

        assertPlayerRegularPits(board, player1, 0, 0, 0);
        assertPlayerRegularPits(board, player2, 0, 0, 0);
        assertPlayerBigPit(board, player1, 6);
        assertPlayerBigPit(board, player2, 17);
    }

    @Test
    void testTurnEndsOnBigPit() {
        final var rand = new Random(1L);
        final var player1 = new RandomPlayer("Player 1", rand);
        final var player2 = new RandomPlayer("Player 2", rand);
        final var board = new Board(player1, player2, player1);
        board.init(new int[] { 2, 1 }, 3, new int[] { 4, 5 }, 6);

        assertEquals(player1, board.getActivePlayer());
        assertEquals(0, board.getTurnNum());

        board.turn(0);

        assertEquals(player1, board.getActivePlayer()); // Repeat the turn.
        assertEquals(1, board.getTurnNum());
        assertFalse(board.isGameOver());
        assertNull(board.getWinner());

        assertPlayerRegularPits(board, player1, 0, 2);
        assertPlayerRegularPits(board, player2, 4, 5);
        assertPlayerBigPit(board, player1, 4);
        assertPlayerBigPit(board, player2, 6);
    }

    @Test
    void testTurnOnEmptyPit() {
        final var rand = new Random(1L);
        final var player1 = new RandomPlayer("Player 1", rand);
        final var player2 = new RandomPlayer("Player 2", rand);
        final var board = new Board(player1, player2, player1);
        board.init(new int[] { 0, 1 }, 2, new int[] { 3, 4 }, 5);

        assertEquals(player1, board.getActivePlayer());
        assertEquals(0, board.getTurnNum());

        assertThrows(IllegalArgumentException.class, () -> board.turn(0));
    }

    @Test
    void testLastTurn() {
        final var rand = new Random(1L);
        final var player1 = new RandomPlayer("Player 1", rand);
        final var player2 = new RandomPlayer("Player 2", rand);
        final var board = new Board(player1, player2, player1);
        board.init(new int[] { 0, 2 }, 2, new int[] { 3, 4 }, 5);

        assertEquals(player1, board.getActivePlayer());
        assertEquals(0, board.getTurnNum());

        board.turn(1);

        assertNull(board.getActivePlayer()); // No current player.
        assertEquals(1, board.getTurnNum());
        assertTrue(board.isGameOver());
        assertEquals(player2, board.getWinner());

        assertPlayerRegularPits(board, player1, 0, 0);
        assertPlayerRegularPits(board, player2, 0, 0);
        assertPlayerBigPit(board, player1, 3);
        assertPlayerBigPit(board, player2, 13);
    }

    @Test
    void testLastTurnNoWinner() {
        final var rand = new Random(1L);
        final var player1 = new RandomPlayer("Player 1", rand);
        final var player2 = new RandomPlayer("Player 2", rand);
        final var board = new Board(player1, player2, player1);
        board.init(new int[] { 0, 1 }, 2, new int[] { 1, 1 }, 1);

        assertEquals(player1, board.getActivePlayer());
        assertEquals(0, board.getTurnNum());

        board.turn(1);

        assertNull(board.getActivePlayer()); // No current player.
        assertEquals(1, board.getTurnNum());
        assertTrue(board.isGameOver());
        assertNull(board.getWinner());

        assertPlayerRegularPits(board, player1, 0, 0);
        assertPlayerRegularPits(board, player2, 0, 0);
        assertPlayerBigPit(board, player1, 3);
        assertPlayerBigPit(board, player2, 3);
    }

    @Test
    void testGame() {
        final Random rand = new Random(0L); // Generate same values in all runs.
        final var player1 = new RandomPlayer("Player 1", rand);
        final var player2 = new RandomPlayer("Player 2", rand);
        final var board = new Board(player1, player2, player1);
        board.init();

        while (!board.isGameOver()) {
            // System.out.println(String.format("Turn %d:", board.getTurnNum()));
            // System.out.print(board);

            final var player = (ComputerPlayer) board.getActivePlayer();
            final var pitNum = player.getNextTurnPitNum(board);

            // System.out.println(String.format("Player '%s' take stone(s) from pit #%d",
            // player, pitNum));

            board.turn(pitNum);

            // System.out.println(b);
        }
        // System.out.println(
        // board.getWinner() != null ? String.format("Winner is '%s'",
        // board.getWinner()) : "Draw in the game");

        assertNull(board.getActivePlayer());
        assertEquals(player2, board.getWinner());
        assertEquals(46, board.getTurnNum());

        assertPlayerRegularPits(board, player1, 0, 0, 0, 0, 0, 0);
        assertPlayerRegularPits(board, player2, 0, 0, 0, 0, 0, 0);
        assertPlayerBigPit(board, player1, 27);
        assertPlayerBigPit(board, player2, 45);
    }
}
