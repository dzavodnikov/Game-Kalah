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

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import pro.zavodnikov.kalah.game.Board;

/**
 * REST controller.
 *
 * @author Dmitry Zavodnikov
 */
@RestController
@RequestMapping(AbstractController.API_V1 + "/board")
@Tag(name = "Boards")
public class BoardsController extends AbstractController {

    @Autowired
    private SecurityStorage security;

    @Autowired
    private BoardsStorage games;

    @GetMapping("/list")
    @Operation(summary = "Return game boards of player with provided token")
    public List<Board> getBoard(@RequestHeader(value = ACCESS_TOKEN_HEADER) String token) {
        final var player = this.security.validateAccessToken(token);

        return this.games.getPlayerBoards(player);
    }

    @PostMapping("")
    @Operation(summary = "Initialize new game", description = "Create new game with existing player and return board name.")
    public String newGameWithUser(@RequestHeader(value = ACCESS_TOKEN_HEADER) String token,
            @RequestParam(value = "secondPlayerName") String secondPlayerName) throws NoSuchAlgorithmException {
        final var firstPlayer = this.security.validateAccessToken(token);

        final var secondPlayer = this.security.findPlayerByName(secondPlayerName);
        if (secondPlayerName == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not found");
        }

        return this.games.createNewBoard(firstPlayer, secondPlayer);
    }

    @PutMapping("/{boardId}/turn")
    @Operation(summary = "Make a new turn into the game board")
    public void nextTurn(@RequestHeader(value = ACCESS_TOKEN_HEADER) String token, @PathVariable() String boardId,
            final int nextTurnPitNum) {
        final var player = this.security.validateAccessToken(token);

        this.games.nextTurn(boardId, player, nextTurnPitNum);
    }
}
