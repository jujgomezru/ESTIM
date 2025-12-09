// src/features/auth/useGoogleAccessToken.js
import { useState } from "react";
import { useGoogleLogin } from "@react-oauth/google";

/**
 * Hook that encapsulates:
 *  - talking to Google
 *  - returning an access token
 *  - loading / error handling
 *
 * Caller decides what to do with the token (login, register, link, etc.)
 */
export function useGoogleAccessToken(onToken) {
  const [loading, setLoading] = useState(false);

  const start = useGoogleLogin({
    scope: "openid email profile",
    onSuccess: async (tokenResponse) => {
      try {
        setLoading(true);
        const accessToken = tokenResponse.access_token;
        await onToken(accessToken);
      } finally {
        setLoading(false);
      }
    },
    onError: (err) => {
      console.error("Google login error", err);
      alert("Error al autenticarse con Google");
    },
  });

  return { start, loading };
}
