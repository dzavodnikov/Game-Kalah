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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Implement player that plays over network.
 *
 * @author Dmitry Zavodnikov
 */
public class ConsolePlayer extends AbstractPlayer {

    private final MessageDigest digest;
    private final String passwordHash;

    /**
     * Register new player.
     *
     * @param name name of player;
     * @param pass password of player.
     * @throws NoSuchAlgorithmException
     */
    public ConsolePlayer(final String name, final String pass) throws NoSuchAlgorithmException {
        super(name);

        this.digest = MessageDigest.getInstance("SHA-256");
        this.passwordHash = new String(this.digest.digest(pass.getBytes()));
    }

    /**
     * Validate provided password and generate exception if it is not the same.
     *
     * @param password to validate.
     */
    public boolean isPasswordValid(final String password) {
        final var hash = new String(this.digest.digest(password.getBytes()));
        return this.passwordHash.equals(hash);
    }
}
