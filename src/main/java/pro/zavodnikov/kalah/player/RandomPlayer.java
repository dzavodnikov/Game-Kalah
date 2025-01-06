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

import java.util.ArrayList;
import java.util.Random;

import pro.zavodnikov.kalah.game.Board;

/**
 * Simple user that choose first non-empty pit for the next turn.
 *
 * @author Dmitry Zavodnikov
 */
public class RandomPlayer extends AbstractPlayer implements ComputerPlayer {

    private final Random random;

    /**
     * Create player that make random turns.
     *
     * @param name   of player;
     * @param random generator for turns.
     */
    public RandomPlayer(final String name, final Random random) {
        super(name);

        this.random = random;
    }

    @Override
    public int getNextTurnPitNum(final Board board) {
        final var regPits = board.getRegularPits().get(board.getActivePlayer());
        final var nonemptyRegPits = new ArrayList<Integer>();
        for (var i = 0; i < regPits.length; ++i) {
            if (regPits[i] > 0) {
                nonemptyRegPits.add(i);
            }
        }
        if (nonemptyRegPits.isEmpty()) {
            throw new IllegalArgumentException("Next turn can not be performed");
        }
        final int pos = this.random.nextInt(nonemptyRegPits.size());
        return nonemptyRegPits.get(pos);
    }

}
