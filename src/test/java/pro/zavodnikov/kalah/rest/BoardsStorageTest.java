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
package pro.zavodnikov.kalah.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import pro.zavodnikov.kalah.game.Board;
import pro.zavodnikov.kalah.player.ConsolePlayer;
import pro.zavodnikov.kalah.player.Player;
import pro.zavodnikov.kalah.player.RandomPlayer;

/**
 * Tests for {@link BoardsStorage}.
 *
 * @author Dmitry Zavodnikov
 */
@SpringBootTest
@ActiveProfiles(TestConfig.PROFILE)
class BoardsStorageTest {

    @Autowired
    private BoardsStorage games;

    @Test
    void testContextLoads() {
        assertThat(this.games).isNotNull();
    }

    private void verifyPlayerBoards(final Player player, final String... boardNames) {
        final List<Board> boards = Arrays.stream(boardNames).map(name -> this.games.getGameBoard(name))
                .collect(Collectors.toList());
        assertEquals(boards, this.games.getPlayerBoards(player));
    }

    @Test
    void testPlayerBoards() throws NoSuchAlgorithmException {
        final var player1 = new ConsolePlayer("Board Player 1", "pass");
        final var player2 = new ConsolePlayer("Board Player 2", "pass");
        final var player3 = new ConsolePlayer("Board Player 3", "pass");
        final var boardName1 = this.games.createNewBoard(player3, player1);
        final var boardName2 = this.games.createNewBoard(player1, player2);
        final var boardName3 = this.games.createNewBoard(player2, player3);

        verifyPlayerBoards(player1, boardName1, boardName2);
        verifyPlayerBoards(player2, boardName2, boardName3);
        verifyPlayerBoards(player3, boardName1, boardName3);
    }

    @Test
    void testPlayTwoConsoleUsers() throws NoSuchAlgorithmException {
        final var firstConsolePlayer = new ConsolePlayer("Test Player 1", "pass");
        final var secondConsolePlayer = new ConsolePlayer("Test Player 2", "pass");
        final var gameId = this.games.createNewBoard(firstConsolePlayer, secondConsolePlayer);

        final var board = this.games.getGameBoard(gameId);
        final var playerEmulator = new RandomPlayer("Test Player Emulator", new Random(1L));
        while (!board.isGameOver()) {
            this.games.nextTurn(gameId, board.getActivePlayer(), playerEmulator.getNextTurnPitNum(board));
        }
        assertNotNull(board.getWinner());
    }

    @Test
    void testPlayWithComputer() throws NoSuchAlgorithmException {
        final var userPlayer = new ConsolePlayer("Test Gamer", "pass");
        final var gameId = this.games.createNewBoard(userPlayer, null);

        final var board = this.games.getGameBoard(gameId);
        final var playerEmulator = new RandomPlayer("Test Gamer Emulator", new Random(1L));
        while (!board.isGameOver()) {
            assertEquals(userPlayer, board.getActivePlayer());
            this.games.nextTurn(gameId, board.getActivePlayer(), playerEmulator.getNextTurnPitNum(board));
        }
        assertNotNull(board.getWinner());
    }
}
