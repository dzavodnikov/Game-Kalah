function RegularPit(props) {
  return (
    <div className="col">
      <div
        className="card pit"
        onClick={
          props.clickable
            ? () => rest(props.accessToken, "PUT", `/v1/board/${props.boardId}/turn?nextTurnPitNum=${props.pitNum}`)
            : undefined
        }
      >
        <div className={`card-body fs-2 ${props.clickable ? "clickable" : ""}`}>{props.value}</div>
      </div>
    </div>
  );
}

function Board(props) {
  const anotherPlayerName = Object.keys(props.board.regularPits).filter((n) => n !== props.playerName)[0];
  const playerNameActive = (playerName) => {
    const aPlayer = props.board.activePlayer;
    if (!aPlayer) {
      return playerName;
    }
    return `${aPlayer.name === playerName ? "➤ " : ""} ${playerName}`;
  };
  const getWinner = () => {
    if (props.board.gameOver) {
      return props.board.winner ? `Winner is ${props.board.winner.name}!` : "Draw in the game...";
    }
  };
  return (
    <div className="container text-center">
      <h3>{`Turn #${props.board.turnNum + 1}`}</h3>
      <div className="card holder mb-3">
        <h3>{playerNameActive(anotherPlayerName)}</h3>
        <div className="card-body">
          <div className="row">
            <div className="col col-md-2">
              <div className="card pit h-100">
                <div className="card-body fs-1">{props.board.bigPits[anotherPlayerName]}</div>
              </div>
            </div>
            <div className="col">
              <div className="row mb-3">
                {props.board.regularPits[anotherPlayerName]
                  .slice()
                  .reverse()
                  .map((value, idx) => (
                    <RegularPit key={idx} value={value} clickable={false} />
                  ))}
              </div>
              <div className="row">
                {props.board.regularPits[props.playerName].map((value, idx) => (
                  <RegularPit
                    key={idx}
                    pitNum={idx}
                    accessToken={props.accessToken}
                    boardId={props.board.id}
                    value={value}
                    clickable={value > 0 && props.board.activePlayer.name === props.playerName}
                  />
                ))}
              </div>
            </div>
            <div className="col col-md-2">
              <div className="card pit h-100">
                <div className="card-body fs-1">{props.board.bigPits[props.playerName]}</div>
              </div>
            </div>
          </div>
        </div>
        <h3>{playerNameActive(props.playerName)}</h3>
      </div>
      <h3>{getWinner()}</h3>
    </div>
  );
}

function SelectBoard(props) {
  return (
    <React.Fragment>
      &nbsp;&nbsp;
      <button
        type="button"
        className={`btn btn-secondary ${props.boardIdx > 0 ? "" : "disabled"}`}
        onClick={() => props.onClick(props.boardIdx - 1)}
      >
        🡄 Previous Board
      </button>
      &nbsp; &nbsp;
      <button
        type="button"
        className={`btn btn-secondary ${props.boardIdx < props.boardsLen - 1 ? "" : "disabled"}`}
        onClick={() => props.onClick(props.boardIdx + 1)}
      >
        Next Board 🡆
      </button>
    </React.Fragment>
  );
}
