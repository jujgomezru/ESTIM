// src/features/auth/GoogleBrandButton.jsx
import React from "react";

export default function GoogleBrandButton({
  text = "Continuar con Google",
  onClick,
  disabled = false,
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        gap: 8,
        width: "100%",
        padding: "10px 16px",
        borderRadius: 4,
        border: "1px solid #dadce0",
        backgroundColor: "#ffffff",
        cursor: disabled ? "default" : "pointer",
        opacity: disabled ? 0.7 : 1,
        boxShadow: "0 1px 2px rgba(0,0,0,0.1)",
        fontSize: 14,
        fontWeight: 500,
        color: "#3c4043",
      }}
    >
      {/* Google “G” logo as inline SVG */}
      <span
        style={{
          width: 18,
          height: 18,
          display: "inline-flex",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <svg viewBox="0 0 48 48" style={{ width: "18px", height: "18px" }}>
          <path
            fill="#EA4335"
            d="M24 9.5c3.36 0 5.64 1.45 6.93 2.66l5.06-4.94C32.83 4.06 28.89 2 24 2 14.82 2 7.16 7.99 4.3 16.09l5.9 4.56C11.57 14.15 17.21 9.5 24 9.5z"
          />
          <path
            fill="#4285F4"
            d="M46.5 24.5c0-1.57-.14-2.73-.44-3.93H24v7.41h12.94c-.26 1.86-1.66 4.66-4.78 6.54l7.37 5.72C43.98 35.83 46.5 30.71 46.5 24.5z"
          />
          <path
            fill="#FBBC05"
            d="M10.2 28.35C9.4 26.45 9 24.31 9 22c0-2.31.4-4.45 1.2-6.35L4.3 11.09C2.82 14.41 2 18.09 2 22s.82 7.59 2.3 10.91l5.9-4.56z"
          />
          <path
            fill="#34A853"
            d="M24 46c4.89 0 8.99-1.6 11.98-4.35l-7.37-5.72C27.18 36.79 25.76 37.5 24 37.5c-6.79 0-12.43-4.65-13.8-11.15l-5.9 4.56C7.16 40.01 14.82 46 24 46z"
          />
          <path fill="none" d="M2 2h44v44H2z" />
        </svg>
      </span>
      <span>{text}</span>
    </button>
  );
}
