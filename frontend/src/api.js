const API_URL = import.meta.env.VITE_API_URL?.trim() || "/api";
const AUTH_STORAGE_KEY = "neobank-auth";

export async function login(payload) {
  return request("/v1/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });
}

export async function forgotPassword(payload) {
  return request("/v1/auth/forgot-password", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });
}

export async function getBalance(token) {
  return request("/v1/accounts/me/balance", {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`
    }
  });
}

export function saveAuth(authData) {
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(authData));
}

export function getStoredAuth() {
  const data = localStorage.getItem(AUTH_STORAGE_KEY);
  return data ? JSON.parse(data) : null;
}

export function clearAuth() {
  localStorage.removeItem(AUTH_STORAGE_KEY);
}

async function parseResponse(response) {
  const text = await response.text();
  const data = parseJsonSafely(text);

  if (!response.ok) {
    const error = new Error(getErrorMessage(response, data, text));
    error.status = response.status;
    throw error;
  }

  return data;
}

async function request(path, options) {
  let response;

  try {
    response = await fetch(buildUrl(path), options);
  } catch {
    throw new Error("Nao foi possivel conectar ao servidor. Verifique se o backend esta rodando.");
  }

  return parseResponse(response);
}

function buildUrl(path) {
  return `${API_URL}${path}`;
}

function parseJsonSafely(text) {
  if (!text) {
    return null;
  }

  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}

function getErrorMessage(response, data, text) {
  if (typeof data?.message === "string" && data.message.trim()) {
    return data.message;
  }

  if (response.status === 401) {
    return "Nao autorizado. Verifique suas credenciais.";
  }

  if (response.status === 403) {
    return "Acesso negado para esta operacao.";
  }

  if (response.status >= 500) {
    return "O servidor retornou um erro interno.";
  }

  if (text?.trim()) {
    return text.trim();
  }

  return "Erro na requisicao";
}
