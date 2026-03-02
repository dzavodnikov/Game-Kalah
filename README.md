# Game Kalah

This program allowed to play in [Kalah](https://en.wikipedia.org/wiki/Kalah) game for two players in a web-browser.

## Rules

### Board Setup

Each of the two players has his six pits in front of him. To the right of the six pits, each player has a big pit.
At the start of the game, there are six stones in each of the six round pits.

### Game Play

The player who begins with the first move picks up all the stones in any of his own six pits, and sows the stones on
to the right, one in each of the following pits, including his own big pit. No stones are put in the opponents' big
pit. If the player's last stone lands in his own big pit, he gets another turn. This can be repeated several times
before it's the other player's turn.

### Capturing Stones

During the game the pits are emptied on both sides. Always when the last stone lands in an own empty pit, the player
captures his own stone and all stones in the opposite pit (the other player's pit) and puts them in his own big pit.

### The Game Ends

The game is over as soon as one of the sides runs out of stones. The player who still has stones in his pits keeps
them and puts them in his big pit. The winner of the game is the player who has the most stones in his big pit.

## Run Application

To run the application execute:

```sh
$ mvn spring-boot:run
```

After that [web UI](http://localhost:8080/) and [Swagger UI](http://localhost:8080/swagger-ui.html) will be available.

## How to play

Run the application and go to [web UI](http://localhost:8080/). On that page you can choose player name and password to
keep that name during all application session (application have no database and do not save the state). After login you
can select user (or Computer) to play.

You can open the UI in two (or more) browser tabs/windows and every tab/window will possible to play with own player.
If you are refresh the page you will need to login again. Use your previous name/password to continue with your games.

You can run multiple gams at the same time. Just select the board using proper buttons.

## Solution restrictions

Current implementation have no database and save users and games into the memory -- restarting the application will
clean all player/games.

Also you are can not run application on multiple instances because every instance will have own memory and own saved
users/games.

## License

Distributed under MIT License.
