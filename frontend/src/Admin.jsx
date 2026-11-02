import { useEffect, useState } from "react";
import { movieApi, request } from "./api";
const resources = {
  movies: { label: "Movies", singular: "movie" },
  screens: { label: "Screens", singular: "screen" },
  showtimes: { label: "Showtimes", singular: "showtime" },
};
export default function Admin() {
  const [tab, setTab] = useState("movies"),
    [data, setData] = useState({ movies: [], screens: [], showtimes: [] }),
    [loading, setLoading] = useState(true),
    [error, setError] = useState(""),
    [success, setSuccess] = useState(""),
    [editing, setEditing] = useState(null),
    [busy, setBusy] = useState(false),
    [deleting, setDeleting] = useState(null),
    [version, setVersion] = useState(0);
  useEffect(() => {
    let active = true;
    Promise.allSettled([
      movieApi.movies(),
      movieApi.screens(),
      movieApi.showtimes(),
    ]).then((results) => {
      if (!active) return;
      const keys = ["movies", "screens", "showtimes"];
      setData(
        Object.fromEntries(
          results.map((r, i) => [
            keys[i],
            r.status === "fulfilled" ? r.value : [],
          ]),
        ),
      );
      setError(
        results
          .flatMap((r, i) =>
            r.status === "rejected" ? [`${keys[i]}: ${r.reason.message}`] : [],
          )
          .join(" · "),
      );
      setLoading(false);
    });
    return () => {
      active = false;
    };
  }, [version]);
  function reload() {
    setLoading(true);
    setVersion((v) => v + 1);
  }
  function changeTab(value) {
    setTab(value);
    setEditing(null);
    setDeleting(null);
    setSuccess("");
  }
  async function save(e) {
    e.preventDefault();
    setError("");
    setSuccess("");
    const fields = Object.fromEntries(new FormData(e.currentTarget));
    let body = fields;
    if (tab === "movies")
      body = {
        ...fields,
        title: fields.title.trim(),
        description: fields.description.trim(),
        duration: Number(fields.duration),
        genres: fields.genres
          .split(",")
          .map((g) => g.trim())
          .filter(Boolean),
      };
    if (tab === "screens")
      body = {
        ...fields,
        name: fields.name.trim(),
        rows: Number(fields.rows),
        seatsPerRow: Number(fields.seatsPerRow),
      };
    if (tab === "showtimes")
      body = {
        ...fields,
        startsAt:
          fields.startsAt.length === 16
            ? `${fields.startsAt}:00`
            : fields.startsAt,
        price: Number(fields.price),
      };
    if (
      tab === "movies" &&
      (!body.title || !body.description || !body.genres.length)
    ) {
      setError("Enter a title, description, and at least one genre.");
      return;
    }
    if (tab === "showtimes" && new Date(body.startsAt) <= new Date()) {
      setError("Choose a future showtime.");
      return;
    }
    setBusy(true);
    try {
      await request(
        "movie",
        `/api/${tab}${editing.id ? `/${encodeURIComponent(editing.id)}` : ""}`,
        { method: editing.id ? "PUT" : "POST", body },
      );
      setSuccess(`${resources[tab].singular} saved successfully.`);
      setEditing(null);
      reload();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }
  async function remove() {
    setBusy(true);
    setError("");
    setSuccess("");
    try {
      await request("movie", `/api/${tab}/${encodeURIComponent(deleting.id)}`, {
        method: "DELETE",
      });
      setSuccess(`${resources[tab].singular} deleted.`);
      setDeleting(null);
      reload();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }
  const name = (type, id) =>
    data[type].find((x) => x.id === id)?.title ||
    data[type].find((x) => x.id === id)?.name ||
    id;
  return (
    <>
      <div className="page-heading">
        <span className="eyebrow">BEHIND THE SCENES / ADMIN</span>
        <h1>
          Run the show<span className="accent">.</span>
        </h1>
        <p className="muted">
          Manage the films, spaces, and moments that bring people together.
        </p>
      </div>
      <div className="stats">
        {Object.entries(resources).map(([key, value]) => (
          <button
            key={key}
            onClick={() => changeTab(key)}
            className={tab === key ? "stat active" : "stat"}
          >
            <span>{value.label}</span>
            <strong>{data[key].length}</strong>
            <span>Manage {value.label.toLowerCase()} ↗</span>
          </button>
        ))}
      </div>
      <div className="section-heading">
        <div className="tabs">
          {Object.entries(resources).map(([key, value]) => (
            <button
              key={key}
              disabled={busy}
              className={tab === key ? "active" : "ghost"}
              onClick={() => changeTab(key)}
            >
              {value.label}
            </button>
          ))}
        </div>
        <button
          className="primary"
          disabled={busy || loading}
          onClick={() => {
            setEditing({});
            setDeleting(null);
            setSuccess("");
          }}
        >
          + Add {resources[tab].singular}
        </button>
      </div>
      {error && (
        <div className="alert" role="alert">
          {error}
          <button className="ghost" disabled={busy} onClick={reload}>
            Retry loading
          </button>
        </div>
      )}
      {success && (
        <div className="alert success" role="status">
          {success}
        </div>
      )}
      {deleting && (
        <div className="panel delete-confirm">
          <h3>Delete {deleting.title || deleting.name || "this showtime"}?</h3>
          <p>Existing reservations may reference this record.</p>
          <button className="danger" disabled={busy} onClick={remove}>
            {busy ? "Deleting…" : "Delete"}
          </button>
          <button disabled={busy} onClick={() => setDeleting(null)}>
            Keep it
          </button>
        </div>
      )}
      {editing && (
        <section className="panel admin-form">
          <div className="section-heading">
            <h2>
              {editing.id ? "Edit" : "Add"} {resources[tab].singular}
            </h2>
            <button
              className="ghost"
              disabled={busy}
              onClick={() => setEditing(null)}
            >
              Close ×
            </button>
          </div>
          <form onSubmit={save} key={`${tab}-${editing.id || "new"}`}>
            <div className="form-grid">
              {tab === "movies" && (
                <>
                  <label>
                    Title
                    <input
                      name="title"
                      defaultValue={editing.title}
                      required
                      maxLength={200}
                    />
                  </label>
                  <label>
                    Duration (minutes)
                    <input
                      name="duration"
                      type="number"
                      min="1"
                      max="1440"
                      defaultValue={editing.duration}
                      required
                    />
                  </label>
                  <label className="wide">
                    Description
                    <textarea
                      name="description"
                      defaultValue={editing.description}
                      required
                      rows="4"
                    />
                  </label>
                  <label>
                    Poster URL
                    <input
                      name="posterUrl"
                      type="url"
                      defaultValue={editing.posterUrl}
                      required
                      placeholder="https://…"
                    />
                  </label>
                  <label>
                    Genres (comma separated)
                    <input
                      name="genres"
                      defaultValue={editing.genres?.join(", ")}
                      required
                      placeholder="Drama, Adventure"
                    />
                  </label>
                </>
              )}
              {tab === "screens" && (
                <>
                  <label>
                    Screen name
                    <input
                      name="name"
                      defaultValue={editing.name}
                      required
                      pattern=".*\S.*"
                    />
                  </label>
                  <label>
                    Rows
                    <input
                      name="rows"
                      type="number"
                      min="1"
                      max="26"
                      defaultValue={editing.rows}
                      required
                      disabled={!!editing.id}
                    />
                    <input
                      type="hidden"
                      name="rows"
                      value={editing.rows || ""}
                      disabled={!editing.id}
                    />
                  </label>
                  <label>
                    Seats per row
                    <input
                      name="seatsPerRow"
                      type="number"
                      min="1"
                      max="100"
                      defaultValue={editing.seatPerRow}
                      required
                      disabled={!!editing.id}
                    />
                    <input
                      type="hidden"
                      name="seatsPerRow"
                      value={editing.seatPerRow || ""}
                      disabled={!editing.id}
                    />
                  </label>
                  {editing.id && (
                    <p className="fine-print wide">
                      The backend permits renaming screens but requires their
                      dimensions to stay unchanged.
                    </p>
                  )}
                </>
              )}
              {tab === "showtimes" && (
                <>
                  <label>
                    Movie
                    <select
                      name="movieId"
                      defaultValue={editing.movieId || ""}
                      required
                      disabled={!!editing.id}
                    >
                      <option value="" disabled>
                        Choose a movie
                      </option>
                      {data.movies.map((m) => (
                        <option value={m.id} key={m.id}>
                          {m.title}
                        </option>
                      ))}
                    </select>
                    {editing.id && (
                      <input
                        type="hidden"
                        name="movieId"
                        value={editing.movieId}
                      />
                    )}
                  </label>
                  <label>
                    Screen
                    <select
                      name="screenId"
                      defaultValue={editing.screenId || ""}
                      required
                      disabled={!!editing.id}
                    >
                      <option value="" disabled>
                        Choose a screen
                      </option>
                      {data.screens.map((s) => (
                        <option value={s.id} key={s.id}>
                          {s.name} · {s.rows} × {s.seatPerRow}
                        </option>
                      ))}
                    </select>
                    {editing.id && (
                      <input
                        type="hidden"
                        name="screenId"
                        value={editing.screenId}
                      />
                    )}
                  </label>
                  <label>
                    Starts at (cinema local time)
                    <input
                      name="startsAt"
                      type="datetime-local"
                      defaultValue={editing.startsAt?.slice(0, 16)}
                      required
                    />
                  </label>
                  <label>
                    Ticket price
                    <input
                      name="price"
                      type="number"
                      min="0.01"
                      step="0.01"
                      defaultValue={editing.price}
                      required
                    />
                  </label>
                  {editing.id && (
                    <p className="fine-print wide">
                      Movie and screen are fixed here because updating a
                      showtime does not regenerate backend seat inventory.
                      Create a new showtime to use a different screen.
                    </p>
                  )}
                </>
              )}
            </div>
            <button className="primary" disabled={busy}>
              {busy ? "Saving…" : "Save changes ↗"}
            </button>
          </form>
        </section>
      )}
      {loading ? (
        <div className="empty" role="status">
          <span className="spinner" />
          Loading dashboard…
        </div>
      ) : (
        <div className="panel table-wrap">
          <table>
            <thead>
              <tr>
                <th>
                  {tab === "movies"
                    ? "Movie"
                    : tab === "screens"
                      ? "Screen"
                      : "Movie / screen"}
                </th>
                <th>
                  {tab === "movies"
                    ? "Details"
                    : tab === "screens"
                      ? "Dimensions"
                      : "Showtime / price"}
                </th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {data[tab].map((item) => (
                <tr key={item.id}>
                  <td>
                    <strong>
                      {item.title || item.name || name("movies", item.movieId)}
                    </strong>
                    {tab === "showtimes" && (
                      <p className="muted">
                        {name("screens", item.screenId)} · {item.status}
                      </p>
                    )}
                  </td>
                  <td>
                    {tab === "movies"
                      ? `${item.duration} min · ${item.genres?.join(", ")}`
                      : tab === "screens"
                        ? `${item.rows} rows × ${item.seatPerRow} seats`
                        : `${new Date(item.startsAt).toLocaleString()} · ${Number(item.price).toFixed(2)}`}
                  </td>
                  <td>
                    <div className="table-actions">
                      <button
                        disabled={busy}
                        onClick={() => {
                          setEditing(item);
                          setDeleting(null);
                        }}
                      >
                        Edit
                      </button>
                      <button
                        className="ghost danger-text"
                        disabled={busy}
                        onClick={() => {
                          setDeleting(item);
                          setEditing(null);
                        }}
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {!data[tab].length && (
            <div className="empty">
              No {tab} available. Add the first one to get started.
            </div>
          )}
        </div>
      )}
      <p className="fine-print">
        An all-bookings admin view is unavailable: the Booking Service only
        exposes bookings by user ID.
      </p>
    </>
  );
}
