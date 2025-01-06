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

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * Tests for {@link SecurityController}.
 *
 * @author Dmitry Zavodnikov
 */
public class SecurityControllerTest extends AbstractControllerTest {

    private void checkStatus(final String playerName, final String password, final ResultMatcher status)
            throws Exception {
        this.mvc.perform(get("/v1/security/access_token").param("name", playerName).header("password", password)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status);
    }

    @Test
    void testAccessToken() throws Exception {
        final var playerName = "Player";
        final var playerCorrectPass = "CorrectPassword";
        final var playerWrongPass = "WrongPassword";

        checkStatus(playerName, playerCorrectPass, status().isOk()); // Create user.
        checkStatus(playerName, playerCorrectPass, status().isOk()); // Same user, same password.
        checkStatus(playerName, playerWrongPass, status().isForbidden()); // Same user, another password.
    }

    @Test
    void testAccessTokenForComputer() throws Exception {
        checkStatus("Computer", "pass", status().isBadRequest());
    }

    @Test
    void testGetPlayers() throws Exception {
        final var playerName = "Gamer 1";
        final var token1 = accessToken(playerName, "pass");
        this.mvc.perform(
                get("/v1/security/players").header("access-token", token1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasItem(playerName)));
    }

    @Test
    void testGetPlayersNoHeader() throws Exception {
        this.mvc.perform(get("/v1/security/players").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPlayersWrongHeader() throws Exception {
        this.mvc.perform(get("/v1/security/players").header("access-token", "wrongToken")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }
}
