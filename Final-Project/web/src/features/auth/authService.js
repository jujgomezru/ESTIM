import { apiPost } from "../../api/apiClient";
import { ENDPOINTS } from "../../api/endpoints";

export function registerUser(data) {
  return apiPost(ENDPOINTS.REGISTER, data);
}

export function loginUser(data) {
  return apiPost(ENDPOINTS.LOGIN, data);
}
