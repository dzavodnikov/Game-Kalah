class Page extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      accessToken: "",
      playerName: "",
    };
  }

  handlePlayerName = (name) => this.setState({ playerName: name });
  handleAccessToken = (token) => this.setState({ accessToken: token });

  render() {
    return !this.state.accessToken || !this.state.playerName ? (
      <Login
        accessToken={this.state.accessToken}
        playerName={this.state.playerName}
        onChange={this.handlePlayerName}
        onClick={this.handleAccessToken}
      />
    ) : (
      <Game accessToken={this.state.accessToken} playerName={this.state.playerName} />
    );
  }
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<Page />);
