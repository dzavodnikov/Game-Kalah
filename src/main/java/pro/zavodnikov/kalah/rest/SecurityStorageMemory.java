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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.PostConstruct;
import pro.zavodnikov.kalah.player.ConsolePlayer;
import pro.zavodnikov.kalah.player.Player;
import pro.zavodnikov.kalah.player.RandomPlayer;

/**
 * Save data in memory.
 *
 * @author Dmitry Zavodnikov
 */
@Controller
public class SecurityStorageMemory implements SecurityStorage {

    private final Map<String, ConsolePlayer> tokens = new HashMap<>();
    private final Map<String, Player> players = new HashMap<>();

    @Autowired
    private Supplier<Random> random;

    enum ComputerPlayerName {
        RANDOM;
    }

    @PostConstruct
    public void initComputerPlayers() {
        final var computerPlayer = new RandomPlayer("Computer", this.random.get());
        this.players.put(computerPlayer.getName(), computerPlayer);
    }

    @Override
    public List<String> getPlayersNames() {
        final var names = new ArrayList<>(this.players.keySet());
        Collections.sort(names);
        return names;
    }

    @Override
    public Player findPlayerByName(final String name) {
        final var player = this.players.get(name);
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Player with name '%s' is not found", name));
        }
        return player;
    }

    private ConsolePlayer createNewConsolePlayer(final String name, final String pass) {
        if (this.players.containsKey(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Player with name '%s' already exists", name));
        }
        try {
            final var player = new ConsolePlayer(name, pass);
            this.players.put(player.getName(), player);
            return player;
        } catch (NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private String createNewAccessToken(final ConsolePlayer player) {
        final var token = UUID.randomUUID().toString();
        this.tokens.put(token, player);
        return token;
    }

    @Override
    public String createNewAccessToken(final String name, final String pass) {
        if (this.players.containsKey(name)) {
            final var player = this.players.get(name);
            if (player instanceof ConsolePlayer) {
                final var consolePlayer = (ConsolePlayer) player;
                if (!consolePlayer.isPasswordValid(pass)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Password is not correct");
                }
                return createNewAccessToken(consolePlayer);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Player '%s' used for computer player", name));
            }
        } else {
            final var consolePlayer = createNewConsolePlayer(name, pass);
            return createNewAccessToken(consolePlayer);
        }
    }

    @Override
    public ConsolePlayer validateAccessToken(final String token) {
        final var player = this.tokens.get(token);
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access token is incorrect");
        }
        return player;
    }
}
