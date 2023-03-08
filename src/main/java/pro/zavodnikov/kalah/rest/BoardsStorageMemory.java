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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import pro.zavodnikov.kalah.game.Board;
import pro.zavodnikov.kalah.player.ComputerPlayer;
import pro.zavodnikov.kalah.player.Player;
import pro.zavodnikov.kalah.player.RandomPlayer;

/**
 * Save data in memory.
 *
 * @author Dmitry Zavodnikov
 */
@Controller
public class BoardsStorageMemory implements BoardsStorage {

    private final Map<String, Board> boards = new LinkedHashMap<>();

    @Autowired
    private Supplier<Random> random;

    @Override
    public Board getGameBoard(final String boardId) {
        final var board = this.boards.get(boardId);
        if (board == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found");
        }
        return board;
    }

    @Override
    public List<Board> getPlayerBoards(final Player player) {
        return this.boards.values().stream().filter(b -> b.getPlayers().contains(player)).collect(Collectors.toList());
    }

    private void preparedAllComputerTurn(final Board board) {
        while (!board.isGameOver() && board.getActivePlayer() instanceof ComputerPlayer) {
            final var player = (ComputerPlayer) board.getActivePlayer();
            final var turn = player.getNextTurnPitNum(board);
            board.turn(turn);
        }
    }

    @Override
    public String createNewBoard(final Player firstPlayer, Player secondPlayer) {
        if (firstPlayer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }
        if (secondPlayer == null) {
            secondPlayer = new RandomPlayer("Computer", this.random.get());
        }
        final var statPlayer = this.random.get().nextInt(2) == 0 ? firstPlayer : secondPlayer;

        final var board = new Board(firstPlayer, secondPlayer, statPlayer);
        board.init();

        preparedAllComputerTurn(board);

        this.boards.put(board.getId(), board);
        return board.getId();
    }

    @Override
    public void nextTurn(final String boardId, final Player player, final int nextTurnPitNum) {
        final var board = getGameBoard(boardId);
        if (!Objects.equals(player, board.getActivePlayer())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player is not active");
        }
        board.turn(nextTurnPitNum);

        preparedAllComputerTurn(board);
    }
}
