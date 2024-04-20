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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import pro.zavodnikov.kalah.player.ConsolePlayer;

/**
 * Tests for {@link SecurityStorage}.
 *
 * @author Dmitry Zavodnikov
 */
@SpringBootTest
@ActiveProfiles(TestConfig.PROFILE)
class SecurityStorageTest {

    @Autowired
    private SecurityStorage security;

    @Test
    void testContextLoads() {
        assertThat(this.security).isNotNull();
    }

    @Test
    void testCreateGetFindPlayers() {
        final var nonExistingName = "Non-existing Security Test User";
        assertFalse(this.security.getPlayersNames().contains(nonExistingName));
        assertThrows(ResponseStatusException.class, () -> this.security.findPlayerByName(nonExistingName));

        final var playerName2 = "Security Test Player 2";
        this.security.createNewAccessToken(playerName2, "pass");
        assertTrue(this.security.getPlayersNames().contains(playerName2));
        final var player = this.security.findPlayerByName(playerName2);
        assertNotNull(player);

        this.security.createNewAccessToken(playerName2, "pass");
        final var samePlayer = this.security.findPlayerByName(playerName2);
        assertTrue(player == samePlayer); // Check that objects are the same.

        final var playerName1 = "Security Test Player 1";
        this.security.createNewAccessToken(playerName1, "pass");

        final var playerNamesList = this.security.getPlayersNames();
        assertTrue(playerNamesList.indexOf(playerName1) < playerNamesList.indexOf(playerName2));
    }

    @Test
    void testCreateAndValidateAccessToken() {
        final var token = this.security.createNewAccessToken("MyUser", "MyPass");
        final var player = this.security.validateAccessToken(token);
        assertTrue(player instanceof ConsolePlayer);
        assertTrue(((ConsolePlayer) player).isPasswordValid("MyPass"));
        assertThrows(ResponseStatusException.class, () -> this.security.createNewAccessToken("MyUser", "AnotherPass"));

        final var samePlayer = this.security.validateAccessToken(token);
        assertTrue(player == samePlayer); // Check that objects are the same.
        assertThrows(ResponseStatusException.class, () -> this.security.validateAccessToken("nonExistingToken"));
    }
}
