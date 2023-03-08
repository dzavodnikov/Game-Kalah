function rest(token, method, path) {
  return fetch(path, {
    method: method,
    headers: {
      "Content-Type": "application/json",
      "access-token": token,
    },
  }).then((r) => {
    if (r.status == 403) {
      location.reload();
    }
    return r;
  });
}
