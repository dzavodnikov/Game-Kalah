const SYNC_MS = 2_000;

function StartNewGame(props) {
  const playersList = () => {
    return (
      <div
        className="modal fade"
        id="startNewGame"
        tabIndex="-1"
        aria-labelledby="Select Player for new Game"
        aria-hidden="true"
      >
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h1 className="modal-title fs-5">Select Player for new Game</h1>
            </div>
            <div className="modal-body">
              <div className="d-grid gap-2">
                {props.players.map((name, idx) => (
                  <button
                    key={idx + 1}
                    type="button"
                    className="btn btn-secondary"
                    data-bs-dismiss="modal"
                    onClick={() => rest(props.accessToken, "POST", `/v1/board?secondPlayerName=${name}`)}
                  >
                    {name}
                  </button>
                ))}
              </div>
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">
                Close
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  };
  return (
    <React.Fragment>
      <button
        type="button"
        className={`btn btn-secondary ${props.players ? "" : "disabled"}`}
        data-bs-toggle="modal"
        data-bs-target="#startNewGame"
      >
        Start new game
      </button>
      {props.players != null ? playersList() : null}
    </React.Fragment>
  );
}

class Game extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      players: null,

      boards: null,
      boardIdx: null,
    };
  }

  componentDidMount() {
    this.playerTimeId = setInterval(() => {
      rest(this.props.accessToken, "GET", "/v1/security/players")
        .then((r) => r.json())
        .then((players) => players.filter((name) => name !== this.props.playerName))
        .then((players) => this.setState({ players: players }));
    }, SYNC_MS);

    this.boardsTimeId = setInterval(() => {
      rest(this.props.accessToken, "GET", "/v1/board/list")
        .then((r) => r.json())
        .then((boards) => {
          const update = { boards: boards };
          // Initial load select last game.
          if (this.state.boardIdx === null) {
            update["boardIdx"] = boards.length ? boards.length - 1 : 0;
          }
          this.setState(update);
        });
    }, SYNC_MS);
  }

  componentWillUnmount() {
    clearInterval(this.playerTimeId);
    clearInterval(this.boardsTimeId);
  }

  handleBoardIdx = (idx) => this.setState({ boardIdx: idx });

  render() {
    return (
      <div className="container">
        <div className="row justify-content-md-center">
          <div className="container mb-3">
            <StartNewGame accessToken={this.props.accessToken} players={this.state.players} />
            <SelectBoard
              boardIdx={this.state.boardIdx}
              boardsLen={this.state.boards ? this.state.boards.length : null}
              onClick={this.handleBoardIdx}
            />
          </div>
          {this.state.boards != null && this.state.boards.length > 0 ? (
            <Board
              accessToken={this.props.accessToken}
              playerName={this.props.playerName}
              board={this.state.boards[this.state.boardIdx]}
            />
          ) : this.state.boards == null ? (
            <h3>Loading...</h3>
          ) : (
            <h3>Empty</h3>
          )}
        </div>
      </div>
    );
  }
}
