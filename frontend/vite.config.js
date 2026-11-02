import react from "@vitejs/plugin-react";
import { defineConfig, loadEnv } from "vite";

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, ".", "");
  const proxy = Object.fromEntries(
    ["auth", "movie", "booking"].map((name, i) => [
      `/${name}-api`,
      {
        target:
          env[`${name.toUpperCase()}_PROXY_TARGET`] ||
          `http://localhost:${8081 + i}`,
        changeOrigin: true,
        rewrite: (path) => path.replace(`/${name}-api`, ""),
      },
    ]),
  );
  return { plugins: [react()], server: { proxy }, preview: { proxy } };
});
