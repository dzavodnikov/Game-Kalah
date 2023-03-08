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

import java.util.List;

import pro.zavodnikov.kalah.game.Board;
import pro.zavodnikov.kalah.player.Player;
import pro.zavodnikov.kalah.player.RandomPlayer;

/**
 * Games boards storage.
 *
 * @author Dmitry Zavodnikov
 */
public interface BoardsStorage {

    /**
     * @param boardName name of existing board;
     * @return board of game.
     */
    Board getGameBoard(String boardName);

    /**
     * @param player that plays in some boards;
     * @return list of user game boards sorted by adding order.
     */
    List<Board> getPlayerBoards(Player player);

    /**
     * Create new game.
     *
     * @param firstPlayer  first plater;
     * @param secondPlayer second player or <code>null</code> to use
     *                     {@link RandomPlayer};
     * @return ID of new created game board.
     */
    String createNewBoard(Player firstPlayer, Player secondPlayer);

    /**
     * Perform next turn into the game. Prepared all computer turns if another
     * player is a computer.
     *
     * @param boardId        ID of game board;
     * @param player         player who wants to make the turn;
     * @param nextTurnPitNum pit number for next turn.
     */
    void nextTurn(String boardId, Player player, int nextTurnPitNum);
}
