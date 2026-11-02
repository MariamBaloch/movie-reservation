const bases = {
  auth: import.meta.env.VITE_AUTH_API_URL || "/auth-api",
  movie: import.meta.env.VITE_MOVIE_API_URL || "/movie-api",
  booking: import.meta.env.VITE_BOOKING_API_URL || "/booking-api",
};
export function claims(token) {
  try {
    const part = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
    const data = JSON.parse(
      new TextDecoder().decode(
        Uint8Array.from(atob(part), (c) => c.charCodeAt(0)),
      ),
    );
    return data.sub &&
      ["USER", "ADMIN"].includes(data.role) &&
      data.exp * 1000 > Date.now()
      ? data
      : null;
  } catch {
    return null;
  }
}
export function savedToken() {
  const token = localStorage.getItem("cinebook-token");
  return claims(token) ? token : null;
}
export async function request(
  service,
  path,
  { method = "GET", body, auth = true } = {},
) {
  const token = auth ? savedToken() : null;
  let response;
  try {
    response = await fetch(`${bases[service].replace(/\/$/, "")}${path}`, {
      method,
      headers: {
        ...(body ? { "Content-Type": "application/json" } : {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      ...(body ? { body: JSON.stringify(body) } : {}),
      signal: AbortSignal.timeout(20000),
    });
  } catch {
    throw new Error(
      "Cannot reach the service. Check that the backend is running and try again.",
    );
  }
  const raw = await response.text();
  let data;
  try {
    data = JSON.parse(raw);
  } catch {
    data = raw;
  }
  if (!response.ok) {
    if (response.status === 401 && token && service === "auth") {
      localStorage.removeItem("cinebook-token");
      window.dispatchEvent(new Event("cinebook-expired"));
    }
    const detail =
      data?.detail ||
      data?.message ||
      (typeof data === "string" && !data.includes("<") ? data : "");
    const error = new Error(
      detail ||
        (response.status === 403
          ? "Access denied. Your account does not have permission for this action."
          : `Request failed (${response.status}). Please try again.`),
    );
    error.status = response.status;
    throw error;
  }
  return data;
}
export const movieApi = {
  movies: () => request("movie", "/api/movies", { auth: false }),
  movie: (id) =>
    request("movie", `/api/movies/${encodeURIComponent(id)}`, {
      auth: false,
    }),
  showtimes: (id) =>
    request(
      "movie",
      `/api/showtimes${id ? `/movie/${encodeURIComponent(id)}` : ""}`,
      { auth: false },
    ),
  showtime: (id) =>
    request("movie", `/api/showtimes/${encodeURIComponent(id)}`, {
      auth: false,
    }),
  screen: (id) =>
    request("movie", `/api/screens/${encodeURIComponent(id)}`, {
      auth: false,
    }),
  screens: () => request("movie", "/api/screens", { auth: false }),
};
export const bookingApi = {
  seats: (id) =>
    request(
      "booking",
      `/api/bookings/showtimes/${encodeURIComponent(id)}/seats`,
    ),
  create: (body) =>
    request("booking", "/api/bookings", { method: "POST", body }),
  mine: () => request("booking", "/api/bookings/me"),
  cancel: (id) =>
    request(
      "booking",
      `/api/bookings/${encodeURIComponent(id)}`,
      { method: "DELETE" },
    ),
};
