function login(name, pass) {
  return fetch(`/v1/security/access_token?name=${name}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      password: pass,
    },
  });
}

class Login extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      error: null,
      password: null,
    };
  }
  render() {
    return (
      <div className="container">
        <div className="row justify-content-md-center align-items-center vh-100">
          <div className="col-lg-4 align-middle">
            <div className="card holder">
              <div className="card-body">
                <h5 className="text-center mb-3">Login to Game Board</h5>
                <div className="form-floating mb-2">
                  <input
                    type="text"
                    className="form-control"
                    value={this.props.playerName}
                    onChange={(e) => this.props.onChange(e.target.value)}
                  />
                  <label htmlFor="name">Name</label>
                </div>

                <div className="form-floating mb-3">
                  <input
                    type="password"
                    className="form-control"
                    onChange={(e) => this.setState({ password: e.target.value })}
                  />
                  <label htmlFor="pass">Password</label>
                </div>
                {this.state.error ? (
                  <div className="alert alert-danger" role="alert">
                    {this.state.error}
                  </div>
                ) : (
                  <span></span>
                )}
                <button
                  type="button"
                  className={`btn btn-secondary float-end ${this.props.playerName ? "" : "disabled"}`}
                  onClick={() =>
                    login(this.props.playerName, this.state.password)
                      .then((r) => {
                        if (r.status == 403) {
                          throw Error(r.statusText ? r.statusText : "Wrong password");
                        }
                        return r;
                      })
                      .then((r) => r.text())
                      .then((token) => {
                        this.setState({ error: null });
                        this.props.onClick(token);
                      })
                      .catch((message) => {
                        this.setState({ error: message.message });
                      })
                  }
                >
                  Login
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
}
