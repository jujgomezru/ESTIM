// src/features/auth/authService.js
import { apiPost } from "../../api/apiClient";
import { ENDPOINTS } from "../../api/endpoints";

const ACCESS_TOKEN_KEY = "accessToken";
const CURRENT_USER_KEY = "currentUser";

export function registerUser(data) {
  return apiPost(ENDPOINTS.REGISTER, data);
}

// --- Shared helper: store tokens + user in localStorage ---
function persistAuthFromLoginResult(result, fallbackEmail = null) {
  if (result && result.accessToken) {
    localStorage.setItem(ACCESS_TOKEN_KEY, result.accessToken);
  }

  const backendUser =
    result?.user ||
    result?.currentUser ||
    result?.profile ||
    null;

  const userToStore =
    backendUser ||
    (fallbackEmail ? { email: fallbackEmail } : null) ||
    null;

  if (userToStore) {
    localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(userToStore));
  }

  window.dispatchEvent(new Event("auth:login"));
}

// --- Classic email/password login ---
export async function loginUser(data) {
  const result = await apiPost(ENDPOINTS.LOGIN, data);
  persistAuthFromLoginResult(result, data.email);
  return result;
}

// --- OAuth login (expects account to be already linked) ---
export async function oauthLogin(provider, accessToken) {
  const payload = {
    provider,
    externalToken: accessToken,
    redirectUri: window.location.origin,
  };

  try {
    const result = await apiPost(ENDPOINTS.OAUTH_LOGIN, payload);
    persistAuthFromLoginResult(result, null);
    return { ok: true, result };
  } catch (error) {
    const isNotLinked =
      error.status === 401 &&
      error.data?.code === "OAUTH_LOGIN_FAILED" &&
      typeof error.data?.message === "string" &&
      error.data.message.toLowerCase().includes("not linked");

    if (isNotLinked) {
      return { ok: false, reason: "not_linked", error };
    }
    throw error;
  }
}

// --- OAuth register (or login+create) ---
export async function oauthRegister(provider, accessToken) {
  const payload = {
    provider,
    externalToken: accessToken,
    redirectUri: window.location.origin,
  };

  const result = await apiPost(ENDPOINTS.OAUTH_REGISTER, payload);

  // Backend returns AuthenticationResult → same as normal login
  persistAuthFromLoginResult(result, null);

  return result;
}

// --- Link OAuth to an existing logged-in account ---
export async function oauthLink(provider, accessToken) {
  const payload = {
    provider,
    externalToken: accessToken,
    redirectUri: window.location.origin,
  };

  const result = await apiPost(ENDPOINTS.OAUTH_LINK, payload);
  return result;
}

// --- Session helpers ---
export function logoutUser() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(CURRENT_USER_KEY);

  window.dispatchEvent(new Event("auth:logout"));
}

export function getCurrentUser() {
  const raw = localStorage.getItem(CURRENT_USER_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

export function isLoggedIn() {
  return !!localStorage.getItem(ACCESS_TOKEN_KEY);
}
