# CineBook frontend

React + Vite cinema reservation frontend. Backend files are unchanged.

## Start

From the repository root (PowerShell):

```powershell
cd frontend
npm.cmd install
npm.cmd run dev -- --host 127.0.0.1
```

Open http://localhost:5173. On other shells, `npm` works in place of `npm.cmd`.
The three backend services must be running on ports 8081, 8082, and 8083 with their existing database setup.

```powershell
npm.cmd run build
npm.cmd run preview -- --host 127.0.0.1
```

Preview opens on http://localhost:4173. Both development and preview use same-origin proxies to avoid changing backend CORS. No environment file is required locally. Copy `.env.example` to `.env.local` to override targets. The supported VITE_AUTH_API_URL, VITE_MOVIE_API_URL, and VITE_BOOKING_API_URL can also point directly to http://localhost:8081, http://localhost:8082, and http://localhost:8083 when backend CORS is configured. Static production hosting needs matching reverse proxies for `/auth-api`, `/movie-api`, and `/booking-api`, plus SPA fallback to index.html; Vite proxies do not ship in dist.

## Implemented

- Login/registration with validation, JWT persistence, expiry handling, logout, protected routes, and ADMIN navigation.
- Real movie listing, title search and local filtering of API-provided genres, details and upcoming showtimes.
- Seat map from actual screen rows and seatPerRow, reconciled against Booking Service seat inventory. Unknown/booked seats are disabled.
- Selection, pricing, review, confirmed booking response, conflict refresh, and confirmation.
- My Bookings with movie/showtime enrichment and cancellation through the existing endpoint.
- ADMIN movie, screen, and showtime CRUD. Screen dimensions cannot change during edit, per backend rules. Existing showtime movie/screen are fixed in the UI because the backend update does not regenerate seat inventory.
- Responsive dark cinematic UI, loading/error/empty states, poster fallback.

## Exact API contracts

Auth: POST /auth/register `{name,email,password}`; POST /auth/login `{email,password}` returns `{message,token}`. JWT `sub` supplies userId and `role` controls navigation. GET /auth/me exists but returns only a string, so the UI uses JWT claims.

Movie: GET/POST /api/movies; GET/PUT/DELETE /api/movies/{id}. Movie request: `{title,description,duration,posterUrl,genres}`.

Screen: GET/POST /api/screens; GET/PUT/DELETE /api/screens/{id}. Request: `{name,rows,seatsPerRow}`; response uses `seatPerRow`.

Showtime: GET/POST /api/showtimes; GET/PUT/DELETE /api/showtimes/{id}; GET /api/showtimes/movie/{movieId}. Request: `{movieId,screenId,startsAt,price}`. Only future SCHEDULED showtimes with positive price can be booked.

Booking: GET /api/bookings/showtimes/{showtimeId}/seats returns `{seatNumber,status}[]`; POST /api/bookings accepts `{userId,showtimeId,seatNumbers}`; GET /api/bookings/users/{userId}; DELETE /api/bookings/{bookingId}?userId={userId}. Amount and confirmation status come from the server.

## Backend limitations and verification

No all-bookings admin endpoint exists, so no admin booking list is fabricated. Search/genre filtering is local because no corresponding query endpoints exist. Currency is not specified in backend DTOs, so amounts are displayed without an invented currency.

Booking Service currently trusts userId from the request and does not enforce JWT ownership. The frontend derives userId exclusively from the signed-in session and provides no way to select another user, but backend authorization must be completed by the backend team. Movie Service does enforce ADMIN permissions in SecurityConfig.

Production build and ESLint passed during implementation. Local HTTP probes to ports 8081, 8082, and 8083 did not respond, so a live end-to-end reservation could not be verified. Start the existing services and verify registration → login → film → showtime → seats → review → confirmation. Verify a competing booking returns a conflict and refreshes availability. There are no mock API responses or hardcoded credentials.
