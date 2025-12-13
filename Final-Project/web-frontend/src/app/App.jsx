import AppRoutes from "./routes";
import "../styles/globals.css";
import { GoogleOAuthProvider } from "@react-oauth/google";

export default function App() {
  const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;

  if (!clientId) {
    console.error("Missing VITE_GOOGLE_CLIENT_ID. Google login disabled.");
    return <AppRoutes />;
  }

  return (
    <GoogleOAuthProvider clientId={clientId}>
      <AppRoutes />
    </GoogleOAuthProvider>
  );
}
