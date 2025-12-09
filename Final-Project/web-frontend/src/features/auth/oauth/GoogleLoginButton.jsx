// src/features/auth/oauth/GoogleLoginButton.jsx
import { useNavigate } from "react-router-dom";
import GoogleBrandButton from "./GoogleBrandButton";
import { oauthLogin } from "../authService";
import { useGoogleAccessToken } from "./useGoogleAccessToken";

export default function GoogleLoginButton() {
  const navigate = useNavigate();

  const { start, loading } = useGoogleAccessToken(async (accessToken) => {
    const result = await oauthLogin("GOOGLE", accessToken);

    if (result.ok) {
      alert("Inicio de sesión con Google exitoso");
      navigate("/library");
    } else if (result.reason === "not_linked") {
      alert(
        "Esta cuenta de Google no está vinculada a ninguna cuenta ESTIM.\n" +
          "Por ahora: inicia sesión con tu correo y vincula Google desde tu perfil."
      );
    }
  });

  return (
    <GoogleBrandButton
      text={loading ? "Conectando con Google..." : "Continuar con Google"}
      onClick={() => !loading && start()}
      disabled={loading}
    />
  );
}
