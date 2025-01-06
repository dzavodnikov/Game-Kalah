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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import pro.zavodnikov.kalah.Entity;
import pro.zavodnikov.kalah.game.pit.BigPit;
import pro.zavodnikov.kalah.game.pit.Pit;
import pro.zavodnikov.kalah.game.pit.RegularPit;
import pro.zavodnikov.kalah.player.Player;

/**
 * Implements <a href="https://en.wikipedia.org/wiki/Kalah">Kalah</a> game
 * board.
 *
 * @author Dmitry Zavodnikov
 */
public class Board extends Entity {

    private final Player player1;
    private final Player player2;
    private Player activePlayer;

    private int turn = 0;

    private final Map<Player, RegularPit> firstPit = new HashMap<>();
    private final Map<Player, BigPit> bigPit = new HashMap<>();

    /**
     * Create game board.
     *
     * @param player1     first player;
     * @param player2     second player;
     * @param startPlayer player who will make first turn.
     */
    public Board(final Player player1, Player player2, final Player startPlayer) {

        if (player1 == null || player2 == null) {
            throw new IllegalArgumentException("Players can not be null");
        }
        if (Objects.equals(player1, player2)) {
            throw new IllegalArgumentException("Players should be different");
        }
        this.player1 = player1;
        this.player2 = player2;

        if (startPlayer != player1 && startPlayer != player2) {
            throw new IllegalArgumentException("Wrong active player");
        }
        this.activePlayer = startPlayer;
    }

    /**
     * @return players presented into the game board.
     */
    public Collection<Player> getPlayers() {
        return List.of(this.player1, this.player2);
    }

    /**
     * @return player that can make an action now or <code>null</code> if game
     *         ended.
     */
    public Player getActivePlayer() {
        return this.activePlayer;
    }

    /**
     * @return current turn number (starts with <code>0</cod>).
     */
    public int getTurnNum() {
        return this.turn;
    }

    private void nextPlayer() {
        if (this.activePlayer != null) {
            this.activePlayer = this.activePlayer == this.player1 ? this.player2 : this.player1;
        }
    }

    private boolean isEmptyPits(final Player player) {
        Pit curPit = this.firstPit.get(player);
        while (curPit instanceof RegularPit) {
            if (curPit.getStones() > 0) {
                return false;
            }
            curPit = curPit.getNextPit(player);
        }
        return true;
    }

    /**
     * @return player that win in the game rr <code>null</code> if game still going.
     */
    public boolean isGameOver() {
        if (isEmptyPits(this.player1)) {
            return true;
        }
        if (isEmptyPits(this.player2)) {
            return true;
        }
        return false;
    }

    /**
     * @return <code>null</code> if game is still going or player who wins
     *         otherwise.
     */
    public Player getWinner() {
        if (!isGameOver()) {
            return null;
        }

        final var bpPlayer1 = this.bigPit.get(this.player1);
        if (bpPlayer1 == null) {
            return null;
        }

        final var bpPlayer2 = this.bigPit.get(this.player2);
        if (bpPlayer2 == null) {
            return null;
        }

        final var s1 = bpPlayer1.getStones();
        final var s2 = bpPlayer2.getStones();
        return s1 > s2 ? this.player1 : s1 < s2 ? this.player2 : null;
    }

    private RegularPit addRegPits(final Player player, final int[] regPitSizes) {
        RegularPit curPit = null;
        for (var ps : regPitSizes) {
            final var newPit = new RegularPit(ps);
            if (curPit == null) {
                this.firstPit.put(player, newPit);
            } else {
                curPit.setNextPit(player, newPit);
            }
            curPit = newPit;
        }
        return curPit;
    }

    private void addBigPit(final Player player, final RegularPit regPit, final int bigPitSize) {
        final var newBigPit = new BigPit(bigPitSize);
        this.bigPit.put(player, newBigPit);
        regPit.setNextPit(player, newBigPit);
    }

    private void addNxtPit() {
        for (var player : this.firstPit.keySet()) {
            final var anotherPlayer = this.firstPit.keySet().stream().filter(p -> p != player).findFirst().get();

            Pit curPit = this.bigPit.get(player);
            Pit nxtPit = this.firstPit.get(anotherPlayer);
            while (nxtPit instanceof RegularPit) {
                curPit.setNextPit(player, nxtPit);
                curPit = nxtPit;
                nxtPit = curPit.getNextPit(anotherPlayer);
            }

            curPit.setNextPit(player, this.firstPit.get(player)); // Connect back to original line.
        }
    }

    private void addOppRegPit() {
        Pit curPit = this.firstPit.get(this.player1);
        final Stack<RegularPit> oppPits = new Stack<>();
        while (curPit instanceof RegularPit) {
            oppPits.add((RegularPit) curPit);
            curPit = curPit.getNextPit(this.player2);
        }

        curPit = this.firstPit.get(this.player1);
        while (curPit instanceof RegularPit) {
            final var curRegPit = (RegularPit) curPit;
            oppPits.peek().setOppositePit(curRegPit);
            curRegPit.setOppositePit(oppPits.pop());
            curPit = curRegPit.getNextPit(this.player1);
        }
    }

    /**
     * Initialize the game board with provided pit sizes.
     *
     * @param regPitSizesPlayer1 number of stones in every regular pit for first
     *                           player;
     * @param bigPitSizePlayer1  number of stones in big pit for first player;
     * @param regPitSizesPlayer2 number of stones in every regular pit for second
     *                           player;
     * @param bigPitSizePlayer2  number of stones in big pit for first second;
     */
    public void init(final int[] regPitSizesPlayer1, final int bigPitSizePlayer1, final int[] regPitSizesPlayer2,
            final int bigPitSizePlayer2) {
        if (regPitSizesPlayer1.length != regPitSizesPlayer2.length) {
            throw new IllegalArgumentException("Number of pits size for different players should be the same");
        }
        var lstRegPitPlayer1 = addRegPits(this.player1, regPitSizesPlayer1);
        var lstRegPitPlayer2 = addRegPits(this.player2, regPitSizesPlayer2);

        addBigPit(this.player1, lstRegPitPlayer1, bigPitSizePlayer1);
        addBigPit(this.player2, lstRegPitPlayer2, bigPitSizePlayer2);

        addNxtPit();

        addOppRegPit();
    }

    /**
     * Initialize the game board with six regular pits. Every regular pit have a six
     * stones. Big pits are empty.
     */
    public void init() {
        var arr = new int[6];
        Arrays.fill(arr, 6);
        init(arr, 0, arr, 0);
    }

    private List<RegularPit> getRegularPits(final Player player) {
        final var pits = new ArrayList<RegularPit>();
        Pit currentPit = this.firstPit.get(player);
        while (currentPit instanceof RegularPit) {
            pits.add((RegularPit) currentPit);
            currentPit = currentPit.getNextPit(player);
        }
        return pits;
    }

    /**
     * @return state of the game board for regular pits.
     */
    public Map<Player, int[]> getRegularPits() {
        final var result = new HashMap<Player, int[]>();
        for (var player : this.firstPit.keySet()) {
            result.put(player, getRegularPits(player).stream().mapToInt(p -> p.getStones()).toArray());
        }
        return result;
    }

    /**
     * @return state of the game board for big pits.
     */
    public Map<Player, Integer> getBigPits() {
        final var result = new HashMap<Player, Integer>();
        for (var player : this.firstPit.keySet()) {
            result.put(player, this.bigPit.get(player).getStones());
        }
        return result;
    }

    /**
     * @param idx index of regular pit;
     * @return pit by index; index starts with zero.
     */
    private RegularPit getRegularPit(final int idx) {
        return getRegularPits(getActivePlayer()).get(idx);
    }

    /**
     * Sow the stones between pits: every next pit get one stone.
     *
     * @param pitNum is a number of start pit;
     * @return last pit that got a stone.
     */
    private Pit sowStones(final int pitNum) {
        Pit currentPit = getRegularPit(pitNum);
        var stones = ((RegularPit) currentPit).pickupStones();
        if (stones <= 0) {
            throw new IllegalArgumentException("Pit have no stones");
        }
        while (stones > 0) {
            currentPit = currentPit.getNextPit(this.activePlayer);
            currentPit.addOneStone();
            --stones;
        }
        return currentPit;
    }

    private void collectPitsToBigPit(final Player player) {
        Pit curPit = this.firstPit.get(player);
        final var bigPit = this.bigPit.get(player);
        while (curPit instanceof RegularPit) {
            if (curPit.getStones() > 0) {
                final var regPit = (RegularPit) curPit;
                bigPit.addStones(regPit.pickupStones());
            }
            curPit = curPit.getNextPit(player);
        }
    }

    private void finishGame() {
        collectPitsToBigPit(isEmptyPits(this.player1) ? this.player2 : this.player1);
        this.activePlayer = null;
    }

    private void collectStones(final RegularPit currentPit) {
        var bigPit = this.bigPit.get(this.activePlayer);
        bigPit.addStones(currentPit.pickupStones());
        bigPit.addStones(currentPit.getOppositePit().pickupStones());
    }

    /**
     * Make a turn on the game board.
     *
     * @param pitNum number of pit for next turn.
     */
    public void turn(final int pitNum) {
        ++this.turn;

        final var finalPit = sowStones(pitNum);

        if (isGameOver()) {
            finishGame();
            return;
        }

        if (finalPit instanceof BigPit) {
            return; // Repeat the action.
        }

        if (finalPit instanceof RegularPit && getRegularPits(getActivePlayer()).contains(finalPit)
                && finalPit.getStones() == 1) {
            collectStones((RegularPit) finalPit);

            if (isGameOver()) {
                finishGame();
                return;
            }
        }

        nextPlayer();
    }

    private String playerString(final Player player, final boolean bigPitRight) {
        final var sb = new StringBuilder();

        final var bp = bigPit.get(player);
        if (bp != null) {
            if (bigPitRight) {
                sb.append("      ");
            } else {
                sb.append(bp);
            }
        }

        Pit curPit = this.firstPit.get(player);
        final List<RegularPit> pits = new ArrayList<>();
        while (curPit instanceof RegularPit) {
            pits.add((RegularPit) curPit);
            curPit = curPit.getNextPit(player);
        }
        if (!bigPitRight) {
            Collections.reverse(pits);
        }
        pits.forEach(p -> sb.append(p));

        if (bp != null) {
            if (bigPitRight) {
                sb.append(bp);
            } else {
                sb.append("      ");
            }
        }

        sb.append(" @ ");
        sb.append(player.getName());
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    @Override
    public String toString() {
        final var sb = new StringBuilder();
        sb.append(playerString(this.player2, false));
        sb.append(playerString(this.player1, true));
        return sb.toString();
    }
}
