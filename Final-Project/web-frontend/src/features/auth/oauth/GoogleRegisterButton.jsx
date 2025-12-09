// src/features/auth/oauth/GoogleRegisterButton.jsx
import { useNavigate } from "react-router-dom";
import GoogleBrandButton from "./GoogleBrandButton";
import { oauthRegister } from "../authService";
import { useGoogleAccessToken } from "./useGoogleAccessToken";

export default function GoogleRegisterButton() {
  const navigate = useNavigate();

  const { start, loading } = useGoogleAccessToken(async (accessToken) => {
    try {
      const result = await oauthRegister("GOOGLE", accessToken);
      console.log("OAuth register/login result:", result);
      alert("Cuenta creada o iniciada con Google correctamente");
      navigate("/library");
    } catch (error) {
      console.error("Error en registro OAuth:", error);
      const backendMessage =
        typeof error.data === "object" ? error.data?.message : null;

      alert(backendMessage || "Error al registrarse con Google");
    }
  });

  return (
    <GoogleBrandButton
      text={loading ? "Conectando con Google..." : "Registrarse con Google"}
      onClick={() => !loading && start()}
      disabled={loading}
    />
  );
}
