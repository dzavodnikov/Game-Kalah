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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/**
 * Tests for {@link BoardsController}.
 *
 * @author Dmitry Zavodnikov
 */
class BoardsControllerTest extends AbstractControllerTest {

    private String startNewGame(final String firstPlayerToken, final String secondPlayerName) throws Exception {
        return this.mvc
                .perform(post("/v1/board").param("secondPlayerName", secondPlayerName)
                        .header("access-token", firstPlayerToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    private void validatePlayerBoards(final String playerToken, final String jsonPath, final Matcher<?> matcher)
            throws Exception {
        this.mvc.perform(
                get("/v1/board/list").header("access-token", playerToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath(jsonPath, matcher));
    }

    @Test
    void testStartNewGameAndGetPlayerBoards() throws Exception {
        final var playerName1 = "Player 1";
        final var playerName2 = "Player 2";
        final var playerName3 = "Player 3";

        final var token1 = accessToken(playerName1, "pass");
        final var token2 = accessToken(playerName2, "pass");
        final var token3 = accessToken(playerName3, "pass");

        validatePlayerBoards(token1, "$", hasSize(0));
        validatePlayerBoards(token2, "$", hasSize(0));
        validatePlayerBoards(token3, "$", hasSize(0));

        startNewGame(token1, playerName2);
        validatePlayerBoards(token1, "$", hasSize(1));
        validatePlayerBoards(token2, "$", hasSize(1));
        validatePlayerBoards(token3, "$", hasSize(0));

        startNewGame(token1, playerName2);
        validatePlayerBoards(token1, "$", hasSize(2));
        validatePlayerBoards(token2, "$", hasSize(2));
        validatePlayerBoards(token3, "$", hasSize(0));

        startNewGame(token1, playerName3);
        validatePlayerBoards(token1, "$", hasSize(3));
        validatePlayerBoards(token2, "$", hasSize(2));
        validatePlayerBoards(token3, "$", hasSize(1));
    }

    @Test
    void testStartNewGameNoAccessToken() throws Exception {
        final var playerName1 = "Player 1";
        final var playerName2 = "Player 2";

        accessToken(playerName1, "pass");
        accessToken(playerName2, "pass");

        this.mvc.perform(
                post("/v1/board").param("secondPlayerName", playerName2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testStartNewGameWrongAccessToken() throws Exception {
        final var playerName1 = "Player 1";
        final var playerName2 = "Player 2";

        accessToken(playerName1, "pass");
        accessToken(playerName2, "pass");

        this.mvc.perform(post("/v1/board").param("secondPlayerName", playerName2).header("access-token", "wrongToken")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    @Test
    void testStartNewGameWrongSecondPlayer() throws Exception {
        final var playerName1 = "Player 1";
        final var playerName2 = "Player 2";

        final var token1 = accessToken(playerName1, "pass");
        accessToken(playerName2, "pass");

        this.mvc.perform(post("/v1/board").param("secondPlayerName", "wrongPlayer").header("access-token", token1)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    void testStartNewGameMissedSecondPlayer() throws Exception {
        final var playerName1 = "Player 1";
        final var playerName2 = "Player 2";

        final var token1 = accessToken(playerName1, "pass");
        accessToken(playerName2, "pass");

        this.mvc.perform(post("/v1/board").header("access-token", token1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPlayerBoardsNoAccessToken() throws Exception {
        this.mvc.perform(get("/v1/board/list").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPlayerBoardsWrongAccessToken() throws Exception {
        this.mvc.perform(
                get("/v1/board/list").header("access-token", "wrongToken").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private void turn(final String playerToken, final String boardName, final int nextTurnPitNum) throws Exception {
        this.mvc.perform(put("/v1/board/{boardName}/turn", boardName).header("access-token", playerToken)
                .param("nextTurnPitNum", Integer.toString(nextTurnPitNum)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testTurn() throws Exception {
        final var playerName1 = "Gamer 1";
        final var playerName2 = "Gamer 2";

        final var token1 = accessToken(playerName1, "pass");
        final var token2 = accessToken(playerName2, "pass");

        final var boardName = startNewGame(token1, playerName2);

        validatePlayerBoards(token1, "$[0].players..name", is(Arrays.asList(playerName1, playerName2)));

        validatePlayerBoards(token1, "$[0].activePlayer.name", is(playerName2));
        validatePlayerBoards(token1, "$[0].turnNum", is(0));
        validatePlayerBoards(token1, "$[0].gameOver", is(false));
        validatePlayerBoards(token1, "$[0].winner", nullValue());
        validatePlayerBoards(token1, "$[0].regularPits.['Gamer 2'].[0:6]", is(Arrays.asList(6, 6, 6, 6, 6, 6)));
        validatePlayerBoards(token1, "$[0].regularPits.['Gamer 1'].[0:6]", is(Arrays.asList(6, 6, 6, 6, 6, 6)));
        validatePlayerBoards(token1, "$[0].bigPits.['Gamer 2']", is(0));
        validatePlayerBoards(token1, "$[0].bigPits.['Gamer 1']", is(0));

        turn(token2, boardName, 1);

        validatePlayerBoards(token1, "$[0].activePlayer.name", is(playerName1));
        validatePlayerBoards(token1, "$[0].turnNum", is(1));
        validatePlayerBoards(token1, "$[0].gameOver", is(false));
        validatePlayerBoards(token1, "$[0].winner", nullValue());
        validatePlayerBoards(token1, "$[0].regularPits.['Gamer 2'].[0:6]", is(Arrays.asList(6, 0, 7, 7, 7, 7)));
        validatePlayerBoards(token1, "$[0].regularPits.['Gamer 1'].[0:6]", is(Arrays.asList(7, 6, 6, 6, 6, 6)));
        validatePlayerBoards(token1, "$[0].bigPits.['Gamer 2']", is(1));
        validatePlayerBoards(token1, "$[0].bigPits.['Gamer 1']", is(0));
    }

    @Test
    void testTurnForNonActivePlayer() throws Exception {
        final var playerName1 = "Tester 1";
        final var playerName2 = "Tester 2";

        final var token1 = accessToken(playerName1, "pass");
        accessToken(playerName2, "pass");

        final var boardName = startNewGame(token1, playerName2);
        validatePlayerBoards(token1, "$[0].activePlayer.name", is(playerName2));

        this.mvc.perform(put("/v1/board/{boardName}/turn", boardName).header("access-token", token1)
                .param("nextTurnPitNum", Integer.toString(1)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testTurnNoAccessToken() throws Exception {
        final var playerName1 = "Tester 1";
        final var playerName2 = "Tester 2";

        final var token1 = accessToken(playerName1, "pass");
        accessToken(playerName2, "pass");

        final var boardName = startNewGame(token1, playerName2);
        validatePlayerBoards(token1, "$[0].activePlayer.name", is(playerName2));

        this.mvc.perform(put("/v1/board/{boardName}/turn", boardName).param("nextTurnPitNum", Integer.toString(1))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void testTurnWrongAccessToken() throws Exception {
        final var playerName1 = "Tester 1";
        final var playerName2 = "Tester 2";

        final var token1 = accessToken(playerName1, "pass");
        accessToken(playerName2, "pass");

        startNewGame(token1, playerName2);
        validatePlayerBoards(token1, "$[0].activePlayer.name", is(playerName2));

        this.mvc.perform(put("/v1/board/{boardName}/turn", "wrongBoard").header("access-token", "WrongToken")
                .param("nextTurnPitNum", Integer.toString(1)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testTurnWrongBoard() throws Exception {
        final var playerName1 = "Tester 1";
        final var playerName2 = "Tester 2";

        final var token1 = accessToken(playerName1, "pass");
        accessToken(playerName2, "pass");

        startNewGame(token1, playerName2);
        validatePlayerBoards(token1, "$[0].activePlayer.name", is(playerName2));

        this.mvc.perform(put("/v1/board/{boardName}/turn", "wrongBoard").header("access-token", token1)
                .param("nextTurnPitNum", Integer.toString(1)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
