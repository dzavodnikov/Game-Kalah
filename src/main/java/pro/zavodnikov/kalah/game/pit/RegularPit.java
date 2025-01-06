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

/**
 * Pit that contain stones initially.
 *
 * @author Dmitry Zavodnikov
 */
public class RegularPit extends AbstractPit {

    private RegularPit oppositePit;

    public RegularPit(final int initSize) {
        super(initSize);
    }

    /**
     * @return number of stones from current pit and make the pit empty.
     */
    public int pickupStones() {
        final var stones = getStones();
        this.stones = 0;
        return stones;
    }

    /**
     * @return pit from opposite side.
     */
    public RegularPit getOppositePit() {
        return this.oppositePit;
    }

    /**
     * @param oppositePit pit from opposite side.
     */
    public void setOppositePit(final RegularPit oppositePit) {
        this.oppositePit = oppositePit;
    }

    @Override
    public String toString() {
        return String.format("[ %2d ]", getStones());
    }
}
