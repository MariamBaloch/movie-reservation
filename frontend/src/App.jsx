import { useEffect, useState } from "react";
import {
  BrowserRouter,
  Routes,
  Route,
  Link,
  NavLink,
  Navigate,
  useLocation,
  useNavigate,
  useParams,
} from "react-router-dom";
import { claims, savedToken, request, movieApi, bookingApi } from "./api";
import Admin from "./Admin";
import "./App.css";

const money = (value) =>
  Number(value ?? 0).toLocaleString(undefined, {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
const date = (value) =>
  value
    ? new Date(value).toLocaleString([], {
        dateStyle: "medium",
        timeStyle: "short",
      })
    : "Showtime unavailable";
const bookable = (show) =>
  show.status === "SCHEDULED" &&
  new Date(show.startsAt) > new Date() &&
  Number(show.price) > 0;
function useLoad(load, deps = []) {
  const [state, setState] = useState({ loading: true, data: null, error: "" });
  const [version, setVersion] = useState(0);
  useEffect(() => {
    let active = true;
    Promise.resolve()
      .then(() => {
        if (active) setState({ loading: true, data: null, error: "" });
        return load();
      })
      .then((data) => {
        if (active) setState({ loading: false, data, error: "" });
      })
      .catch((error) => {
        if (active)
          setState({ loading: false, data: null, error: error.message });
      });
    return () => {
      active = false;
    };
    // The caller supplies explicit resource dependencies.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...deps, version]);
  return { ...state, reload: () => setVersion((v) => v + 1) };
}
function Alert({ children, success = false }) {
  return children ? (
    <div
      role={success ? "status" : "alert"}
      className={`alert ${success ? "success" : ""}`}
    >
      {children}
    </div>
  ) : null;
}
function State({ state, children }) {
  if (state.loading)
    return (
      <div className="empty" role="status">
        <span className="spinner" />
        Loading your cinema experience…
      </div>
    );
  if (state.error)
    return (
      <div className="empty">
        <Alert>{state.error}</Alert>
        <button onClick={state.reload}>Try again</button>
      </div>
    );
  return children;
}
function Poster({ movie, className = "" }) {
  const [failed, setFailed] = useState(false);
  return movie.posterUrl && !failed ? (
    <img
      className={`poster ${className}`}
      src={movie.posterUrl}
      alt={`${movie.title} poster`}
      onError={() => setFailed(true)}
    />
  ) : (
    <div className={`poster fallback ${className}`}>
      <span>CB / CINEMA</span>
      <strong>{movie.title}</strong>
      <span>THE BIG SCREEN AWAITS</span>
    </div>
  );
}
function MovieCard({ movie }) {
  return (
    <Link className="movie-card" to={`/movies/${movie.id}`}>
      <div className="poster-wrap">
        <Poster movie={movie} />
        <span className="card-cta">Book tickets ↗</span>
      </div>
      <h3>{movie.title}</h3>
      <p>
        {(movie.genres || []).join(" · ")}
        <span>{movie.duration} min</span>
      </p>
    </Link>
  );
}
function Home() {
  const state = useLoad(movieApi.movies);
  const [query, setQuery] = useState("");
  const [genre, setGenre] = useState("All genres");
  const [featured, setFeatured] = useState(0);
  const movies = state.data || [];
  const hero = movies[featured % (movies.length || 1)];
  const genres = [
    "All genres",
    ...new Set(movies.flatMap((m) => m.genres || [])),
  ];
  const filtered = movies.filter(
    (m) =>
      m.title.toLowerCase().includes(query.toLowerCase()) &&
      (genre === "All genres" || m.genres?.includes(genre)),
  );
  return (
    <>
      <section className="hero">
        <div className="hero-art">
          {hero && <Poster key={hero.id} movie={hero} />}
        </div>
        <div className="hero-content">
          <span className="eyebrow">
            <i /> THE CINEMA IS CALLING
          </span>
          <h1>
            {hero?.title || (
              <>
                Big stories.
                <br />
                Bigger screens.
              </>
            )}
          </h1>
          <p>
            {hero?.description ||
              "Leave the everyday behind. Discover your next favorite film and find your perfect seat."}
          </p>
          {hero && (
            <div className="meta">
              {hero.duration} MIN <span>•</span> {hero.genres?.join(" / ")}
            </div>
          )}
          <Link
            className="button primary"
            to={hero ? `/movies/${hero.id}` : "/#now-showing"}
          >
            Find your seats <span>↗</span>
          </Link>
          <span className="hero-caption">
            A little escape. An unforgettable experience.
          </span>
        </div>
        {movies.length > 1 && (
          <div className="hero-pagination">
            {movies.slice(0, 5).map((m, i) => (
              <button
                key={m.id}
                aria-label={`Feature ${m.title}`}
                aria-pressed={featured === i}
                onClick={() => setFeatured(i)}
              >
                {String(i + 1).padStart(2, "0")}
              </button>
            ))}
          </div>
        )}
      </section>
      <section className="experience-strip">
        <span>YOUR NEXT GREAT NIGHT OUT</span>
        <span>
          Discover a film <b>01</b>
        </span>
        <span>
          Choose your seats <b>02</b>
        </span>
        <span>
          Feel every moment <b>03</b>
        </span>
      </section>
      <section id="now-showing" className="catalog">
        <div className="section-heading">
          <div>
            <span className="eyebrow">ON THE BIG SCREEN</span>
            <h2>
              Now showing<span className="accent">.</span>
            </h2>
          </div>
          <span className="muted">
            {movies.length} films. Endless possibilities.
          </span>
        </div>
        <div className="filters">
          <div className="genre-list">
            {genres.map((g) => (
              <button
                key={g}
                className={genre === g ? "active" : "ghost"}
                onClick={() => setGenre(g)}
              >
                {g}
              </button>
            ))}
          </div>
          <label className="search">
            <span>⌕</span>
            <input
              aria-label="Search movies by title"
              placeholder="Find a movie…"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
          </label>
        </div>
        <State state={state}>
          {filtered.length ? (
            <div className="movie-grid">
              {filtered.map((m) => (
                <MovieCard key={m.id} movie={m} />
              ))}
            </div>
          ) : (
            <div className="empty">
              <h3>
                {movies.length
                  ? "No films match your search"
                  : "The next great story is on its way"}
              </h3>
              <p>
                {movies.length
                  ? "Try another title or genre."
                  : "No movies have been published yet. Check back soon."}
              </p>
            </div>
          )}
        </State>
      </section>
      <section className="closing">
        <span className="eyebrow">MAKE TIME FOR THE MOVIES</span>
        <h2>
          Some moments deserve
          <br />a bigger screen.
        </h2>
        <p>Your story starts with a seat.</p>
      </section>
    </>
  );
}
function Auth({ register, user, onLogin }) {
  const location = useLocation(),
    navigate = useNavigate();
  const [error, setError] = useState(""),
    [busy, setBusy] = useState(false);
  const from = location.state?.from || "/";
  if (user)
    return (
      <Navigate
        to={user.role === "ADMIN" && from === "/" ? "/admin" : from}
        replace
      />
    );
  async function submit(e) {
    e.preventDefault();
    setError("");
    setBusy(true);
    const fields = Object.fromEntries(new FormData(e.currentTarget));
    fields.email = fields.email.trim();
    if (register) fields.name = fields.name.trim();
    try {
      const result = await request(
        "auth",
        `/auth/${register ? "register" : "login"}`,
        { method: "POST", body: fields, auth: false },
      );
      if (register)
        navigate("/login", {
          replace: true,
          state: {
            message:
              result.message ||
              "Account created. Sign in to book your first movie.",
            from,
          },
        });
      else {
        if (!claims(result.token))
          throw new Error(
            "The server returned an invalid or expired login token.",
          );
        onLogin(result.token);
        navigate(
          from === "/" && claims(result.token).role === "ADMIN"
            ? "/admin"
            : from,
          { replace: true, state: { message: result.message } },
        );
      }
    } catch (err) {
      setError(
        !register && err.status === 401
          ? "Email or password is incorrect. Please try again."
          : err.message,
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <section className="auth-layout">
      <div className="auth-story">
        <span className="eyebrow">WELCOME TO CINEBOOK</span>
        <h1>
          Your seat.
          <br />
          Your escape.
        </h1>
        <p>
          The lights go down.
          <br />A whole new world opens up.
        </p>
        <div className="film-mark">
          C<span>B</span>
        </div>
      </div>
      <div className="panel auth-form">
        <span className="eyebrow">THE NEXT SCENE STARTS HERE</span>
        <h2>{register ? "Join the audience." : "Welcome back."}</h2>
        <p className="muted">
          {register
            ? "Create an account for your next cinema night."
            : "Sign in and pick up where you left off."}
        </p>
        <Alert success>{location.state?.message}</Alert>
        <Alert>{error}</Alert>
        <form onSubmit={submit}>
          {register && (
            <label>
              Full name
              <input
                name="name"
                autoComplete="name"
                required
                maxLength={100}
                pattern=".*\S.*"
                placeholder="Your name"
              />
            </label>
          )}
          <label>
            Email address
            <input
              name="email"
              type="email"
              autoComplete="email"
              required
              placeholder="you@example.com"
            />
          </label>
          <label>
            Password
            <input
              name="password"
              type="password"
              autoComplete={register ? "new-password" : "current-password"}
              minLength={register ? 8 : 1}
              required
              placeholder={register ? "At least 8 characters" : "Your password"}
            />
          </label>
          <button className="primary" disabled={busy}>
            {busy
              ? "Please wait…"
              : register
                ? "Create account ↗"
                : "Sign in ↗"}
          </button>
        </form>
        <p>
          {register ? "Already have an account?" : "New to CineBook?"}{" "}
          <Link to={register ? "/login" : "/register"} state={{ from }}>
            {register ? "Sign in" : "Create an account"}
          </Link>
        </p>
      </div>
    </section>
  );
}
function Details() {
  const { id } = useParams(),
    navigate = useNavigate();
  const state = useLoad(
    () => Promise.all([movieApi.movie(id), movieApi.showtimes(id)]),
    [id],
  );
  const [selected, setSelected] = useState("");
  const [movie, shows] = state.data || [];
  return (
    <State state={state}>
      {movie && (
        <>
          <Link className="back" to="/">
            ← All movies
          </Link>
          <section className="details">
            <Poster movie={movie} />
            <div>
              <span className="eyebrow">NOW SHOWING</span>
              <h1>{movie.title}</h1>
              <div className="meta">
                {movie.duration} MIN <span>•</span> {movie.genres?.join(" / ")}
              </div>
              <p className="description">{movie.description}</p>
              <h2>Make it a movie night.</h2>
              <p className="muted">
                Choose a showtime to find your seats. Prices are per ticket.
              </p>
              <div className="showtimes">
                {shows.filter(bookable).map((s) => (
                  <button
                    key={s.id}
                    className={selected === s.id ? "active" : ""}
                    onClick={() => setSelected(s.id)}
                    aria-pressed={selected === s.id}
                  >
                    <strong>{date(s.startsAt)}</strong>
                    <span>{money(s.price)} / ticket</span>
                  </button>
                ))}
              </div>
              {!shows.some(bookable) && (
                <div className="empty">
                  No upcoming showtimes are available. Check back soon.
                </div>
              )}
              <button
                className="primary"
                disabled={!shows.some((s) => s.id === selected && bookable(s))}
                onClick={() => navigate(`/showtimes/${selected}/seats`)}
              >
                Book tickets ↗
              </button>
            </div>
          </section>
        </>
      )}
    </State>
  );
}
function Seats() {
  const { id } = useParams(),
    navigate = useNavigate();
  const state = useLoad(async () => {
    const show = await movieApi.showtime(id);
    const [movie, screen, seats] = await Promise.all([
      movieApi.movie(show.movieId),
      movieApi.screen(show.screenId),
      bookingApi.seats(id),
    ]);
    return { show, movie, screen, seats };
  }, [id]);
  const [selected, setSelected] = useState([]),
    [review, setReview] = useState(false),
    [busy, setBusy] = useState(false),
    [error, setError] = useState("");
  const data = state.data;
  async function confirm() {
    if (busy || !selected.length) return;
    setBusy(true);
    setError("");
    try {
      if (!bookable(data.show))
        throw new Error("This showtime is no longer available for booking.");
      const booking = await bookingApi.create({
        showtimeId: id,
        seatNumbers: selected,
      });
      if (booking?.status !== "CONFIRMED" || !booking.id)
        throw new Error(
          "The server did not confirm this booking. Check My Bookings before retrying.",
        );
      navigate("/booking/confirmation", {
        replace: true,
        state: { booking, movie: data.movie, show: data.show },
      });
    } catch (err) {
      setError(
        err.status === 409
          ? "Those seats or this showtime are no longer available. Availability has been refreshed. Please choose again."
          : err.message,
      );
      setReview(false);
      setSelected([]);
      state.reload();
    } finally {
      setBusy(false);
    }
  }
  function refresh() {
    setSelected([]);
    setReview(false);
    state.reload();
  }
  return (
    <>
      <div className="page-heading">
        <span className="eyebrow">YOUR NIGHT AT THE MOVIES</span>
        <h1>{review ? "One last look." : "Find your perfect seat."}</h1>
        <p className="muted">
          01 Showtime <span className="accent">→ 02 Seats → 03 Confirm</span>
        </p>
      </div>
      <Alert>{error}</Alert>
      <State state={state}>
        {data && (
          <div className="booking-layout">
            <section className="panel auditorium">
              <div className="section-heading">
                <div>
                  <h2>{data.movie.title}</h2>
                  <p className="muted">
                    {data.screen.name} · {date(data.show.startsAt)}
                  </p>
                </div>
                <button className="ghost" disabled={busy} onClick={refresh}>
                  Refresh seats
                </button>
              </div>
              {!bookable(data.show) && (
                <Alert>This showtime is no longer available for booking.</Alert>
              )}
              <div className="screen-line" />
              <p className="screen-label">SCREEN · ALL EYES THIS WAY</p>
              {!data.seats.length && (
                <Alert>
                  Seat inventory is not available for this showtime yet.
                </Alert>
              )}
              <div className="seat-scroll">
                <div
                  className="seat-grid"
                  style={{ "--columns": data.screen.seatPerRow }}
                >
                  {Array.from({ length: data.screen.rows }, (_, r) => {
                    const row = String.fromCharCode(65 + r);
                    return (
                      <div className="seat-row" key={row}>
                        <span className="row-label">{row}</span>
                        {Array.from(
                          { length: data.screen.seatPerRow },
                          (_, c) => {
                            const label = `${row}${c + 1}`,
                              available = data.seats.some(
                                (s) =>
                                  s.seatNumber === label &&
                                  s.status === "AVAILABLE",
                              );
                            return (
                              <button
                                key={label}
                                title={`${label}: ${available ? "available" : "unavailable"}`}
                                aria-label={`Seat ${label}${available ? "" : ", unavailable"}`}
                                aria-pressed={selected.includes(label)}
                                disabled={
                                  !available ||
                                  review ||
                                  busy ||
                                  !bookable(data.show)
                                }
                                className={`seat ${selected.includes(label) ? "selected" : ""} ${!available ? "unavailable" : ""}`}
                                onClick={() =>
                                  setSelected((old) =>
                                    old.includes(label)
                                      ? old.filter((s) => s !== label)
                                      : [...old, label],
                                  )
                                }
                              >
                                {c + 1}
                              </button>
                            );
                          },
                        )}
                        <span className="row-label">{row}</span>
                      </div>
                    );
                  })}
                </div>
              </div>
              <div className="legend">
                <span>
                  <i />
                  Available
                </span>
                <span>
                  <i className="selected" />
                  Selected
                </span>
                <span>
                  <i className="unavailable" />
                  Unavailable
                </span>
              </div>
              <p className="fine-print">
                Seats are confirmed only when your booking succeeds.
              </p>
            </section>
            <aside className="panel summary">
              <span className="eyebrow">YOUR TICKET TO AN ESCAPE</span>
              <h2>Booking summary</h2>
              <h3>{data.movie.title}</h3>
              <p>{date(data.show.startsAt)}</p>
              <p className="muted">{data.screen.name}</p>
              <dl>
                <div>
                  <dt>Selected seats</dt>
                  <dd>{selected.join(", ") || "Choose your seats"}</dd>
                </div>
                <div>
                  <dt>Tickets</dt>
                  <dd>{selected.length}</dd>
                </div>
                <div>
                  <dt>Price per ticket</dt>
                  <dd>{money(data.show.price)}</dd>
                </div>
                <div className="total">
                  <dt>Total amount</dt>
                  <dd>{money(selected.length * Number(data.show.price))}</dd>
                </div>
              </dl>
              <button
                className="primary"
                disabled={!selected.length || busy || !bookable(data.show)}
                onClick={review ? confirm : () => setReview(true)}
              >
                {busy
                  ? "Confirming…"
                  : review
                    ? "Confirm booking ↗"
                    : "Review booking ↗"}
              </button>
              {review && (
                <button
                  className="ghost"
                  disabled={busy}
                  onClick={() => setReview(false)}
                >
                  ← Change seats
                </button>
              )}
              <p className="fine-print">
                The final amount is calculated by the cinema. No payment details
                are collected.
              </p>
            </aside>
          </div>
        )}
      </State>
    </>
  );
}
function Confirmation() {
  const { state } = useLocation();
  if (!state?.booking?.id || state.booking.status !== "CONFIRMED")
    return (
      <div className="empty">
        <h1>Looking for your tickets?</h1>
        <p>Open My Bookings to see your confirmed reservations.</p>
        <Link className="button primary" to="/my-bookings">
          My bookings ↗
        </Link>
      </div>
    );
  return (
    <section className="confirmation panel">
      <div className="checkmark">✓</div>
      <span className="eyebrow">YOU'RE GOING TO THE MOVIES</span>
      <h1>See you at the cinema.</h1>
      <p>Your booking has been confirmed.</p>
      <h2>{state.movie.title}</h2>
      <p>{date(state.show.startsAt)}</p>
      <dl>
        <div>
          <dt>Booking reference</dt>
          <dd>{state.booking.id}</dd>
        </div>
        <div>
          <dt>Seats · {state.booking.seatNumbers.length} tickets</dt>
          <dd>{state.booking.seatNumbers.join(", ")}</dd>
        </div>
        <div>
          <dt>Total amount</dt>
          <dd>{money(state.booking.totalAmount)}</dd>
        </div>
      </dl>
      <Link className="button primary" to="/my-bookings">
        View my bookings ↗
      </Link>
      <Link className="button ghost" to="/">
        Explore more movies
      </Link>
    </section>
  );
}
function MyBookings({ user }) {
  const state = useLoad(async () => {
    const bookings = await bookingApi.mine();
    return Promise.all(
      bookings.map(async (b) => {
        try {
          const show = await movieApi.showtime(b.showtimeId);
          let movie;
          try {
            movie = await movieApi.movie(show.movieId);
          } catch {
            /* Deleted movie: retain booking details. */
          }
          return { ...b, show, movie };
        } catch {
          return b;
        }
      }),
    );
  }, [user.sub]);
  const [error, setError] = useState(""),
    [busy, setBusy] = useState(""),
    [cancelId, setCancelId] = useState(""),
    [message, setMessage] = useState("");
  async function cancel(id) {
    setBusy(id);
    setError("");
    setMessage("");
    try {
      await bookingApi.cancel(id);
      setMessage("Booking cancelled. Your seats have been released.");
      setCancelId("");
      state.reload();
    } catch (e) {
      setError(e.message);
    } finally {
      setBusy("");
    }
  }
  return (
    <>
      <div className="page-heading">
        <span className="eyebrow">YOUR CINEMA JOURNAL</span>
        <h1>
          My bookings<span className="accent">.</span>
        </h1>
        <p className="muted">Great stories. Seats with your name on them.</p>
      </div>
      <Alert>{error}</Alert>
      <Alert success>{message}</Alert>
      <State state={state}>
        {state.data?.length ? (
          <div className="booking-list">
            {state.data.map((b) => (
              <article className="panel booking-card" key={b.id}>
                {b.movie && <Poster movie={b.movie} />}
                <div className="booking-info">
                  <span
                    className={`status ${b.status === "CONFIRMED" ? "confirmed" : ""}`}
                  >
                    {b.status}
                  </span>
                  <h2>{b.movie?.title || "Cinema reservation"}</h2>
                  <p>{date(b.show?.startsAt)}</p>
                  <p>
                    Seats {b.seatNumbers.join(", ")} · {b.seatNumbers.length}{" "}
                    tickets
                  </p>
                  <p className="fine-print">
                    Reference: {b.id}
                    <br />
                    Booked {date(b.createdAt)}
                    {!b.show && (
                      <>
                        <br />
                        Showtime details are no longer available.
                      </>
                    )}
                  </p>
                </div>
                <div className="booking-actions">
                  <strong>{money(b.totalAmount)}</strong>
                  {b.status === "CONFIRMED" &&
                    (!b.show || new Date(b.show.startsAt) > new Date()) &&
                    (cancelId === b.id ? (
                      <>
                        <p>Cancel this reservation?</p>
                        <button
                          className="danger"
                          disabled={!!busy}
                          onClick={() => cancel(b.id)}
                        >
                          {busy ? "Cancelling…" : "Yes, cancel booking"}
                        </button>
                        <button
                          disabled={!!busy}
                          onClick={() => setCancelId("")}
                        >
                          Keep tickets
                        </button>
                      </>
                    ) : (
                      <button
                        className="ghost"
                        disabled={!!busy}
                        onClick={() => setCancelId(b.id)}
                      >
                        Cancel booking
                      </button>
                    ))}
                </div>
              </article>
            ))}
          </div>
        ) : (
          <div className="empty">
            <h2>Your next movie night starts here.</h2>
            <p>You haven't booked any tickets yet.</p>
            <Link className="button primary" to="/">
              Explore movies ↗
            </Link>
          </div>
        )}
      </State>
    </>
  );
}
function Guard({ user, admin, children }) {
  const location = useLocation();
  if (!user)
    return (
      <Navigate
        to="/login"
        state={{
          from: location.pathname,
          message: "Please sign in to continue.",
        }}
        replace
      />
    );
  return admin && user.role !== "ADMIN" ? (
    <div className="empty">
      <h1>Admin access required</h1>
      <Link to="/">Back to movies</Link>
    </div>
  ) : (
    children
  );
}
function Shell() {
  const [token, setToken] = useState(savedToken),
    [notice, setNotice] = useState("");
  const user = claims(token),
    location = useLocation();
  useEffect(() => {
    window.scrollTo(0, 0);
  }, [location.pathname]);
  useEffect(() => {
    const expire = () => {
      setToken(null);
      setNotice("Your session has expired. Please sign in again.");
    };
    const sync = () => setToken(savedToken());
    window.addEventListener("cinebook-expired", expire);
    window.addEventListener("storage", sync);
    const timer = token
      ? setTimeout(
          expire,
          Math.min(
            Math.max(0, (claims(token)?.exp || 0) * 1000 - Date.now()),
            2147483647,
          ),
        )
      : null;
    return () => {
      window.removeEventListener("cinebook-expired", expire);
      window.removeEventListener("storage", sync);
      clearTimeout(timer);
    };
  }, [token]);
  function login(value) {
    localStorage.setItem("cinebook-token", value);
    setToken(value);
    setNotice("");
  }
  function logout() {
    localStorage.removeItem("cinebook-token");
    setToken(null);
    setNotice("");
  }
  return (
    <>
      <header className="navbar">
        <Link className="logo" to="/">
          <span className="logo-icon">C</span>cinebook
          <span className="accent">.</span>
        </Link>
        <nav aria-label="Main navigation">
          <NavLink to="/" end>
            Discover
          </NavLink>
          {user && <NavLink to="/my-bookings">My bookings</NavLink>}
          {user?.role === "ADMIN" && <NavLink to="/admin">Dashboard</NavLink>}
        </nav>
        <div className="account">
          {user ? (
            <>
              <span className="user-label" title={user.email}>
                {user.email?.split("@")[0] || "My account"}
              </span>
              <button className="ghost" onClick={logout}>
                Log out ↗
              </button>
            </>
          ) : (
            <>
              <Link to="/login">Sign in</Link>
              <Link className="button primary small" to="/register">
                Get started ↗
              </Link>
            </>
          )}
        </div>
      </header>
      <main>
        <Alert>{notice}</Alert>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route
            path="/login"
            element={<Auth key="login" user={user} onLogin={login} />}
          />
          <Route
            path="/register"
            element={
              <Auth key="register" register user={user} onLogin={login} />
            }
          />
          <Route
            path="/movies/:id"
            element={<Details key={location.pathname} />}
          />
          <Route
            path="/showtimes/:id/seats"
            element={
              <Guard user={user}>
                <Seats key={location.pathname} />
              </Guard>
            }
          />
          <Route
            path="/booking/confirmation"
            element={
              <Guard user={user}>
                <Confirmation />
              </Guard>
            }
          />
          <Route
            path="/my-bookings"
            element={
              <Guard user={user}>
                <MyBookings user={user} />
              </Guard>
            }
          />
          <Route
            path="/admin"
            element={
              <Guard user={user} admin>
                <Admin />
              </Guard>
            }
          />
          <Route
            path="*"
            element={
              <div className="empty">
                <h1>This scene doesn't exist.</h1>
                <Link className="button primary" to="/">
                  Back to movies
                </Link>
              </div>
            }
          />
        </Routes>
      </main>
      <footer>
        <Link className="logo" to="/">
          cinebook<span className="accent">.</span>
        </Link>
        <p>For the love of the big screen.</p>
        <span>© {new Date().getFullYear()} CineBook</span>
      </footer>
    </>
  );
}
export default function App() {
  return (
    <BrowserRouter>
      <Shell />
    </BrowserRouter>
  );
}
