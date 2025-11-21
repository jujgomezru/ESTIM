import { apiPost } from "../../api/apiClient";
import { ENDPOINTS } from "../../api/endpoints";

export function registerUser(data) {
  return apiPost(ENDPOINTS.REGISTER, data);
}

export async function loginUser(data) {
  const result = await apiPost(ENDPOINTS.LOGIN, data);

  // Save token for authenticated requests like /me/library
  if (result && result.accessToken) {
    localStorage.setItem("accessToken", result.accessToken);
  }

  return result;
}