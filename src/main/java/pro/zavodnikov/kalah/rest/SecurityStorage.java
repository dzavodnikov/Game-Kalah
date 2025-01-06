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

import org.springframework.stereotype.Controller;

import pro.zavodnikov.kalah.player.Player;

/**
 * Security storage.
 *
 * @author Dmitry Zavodnikov
 */
@Controller
public interface SecurityStorage {

    /**
     * @return sorted players names.
     */
    List<String> getPlayersNames();

    /**
     * @param name of player.
     * @return player with defined name.
     */
    Player findPlayerByName(String name);

    /**
     * Create new access token for provided credentials. If player not exists it
     * will be created; otherwise provided
     * password will be validated.
     *
     * @param name of player;
     * @param pass of player;
     * @return access token.
     */
    String createNewAccessToken(String name, String pass);

    /**
     * Validate if provided token exists.
     *
     * @param token that needs to be validated;
     * @return player that generate provided token.
     */
    Player validateAccessToken(String token);
}
