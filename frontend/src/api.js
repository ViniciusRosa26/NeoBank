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

export async function createUser(payload) {
  return request("/v1/users", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });
}

export async function getUserSummary(token) {
  return request("/v1/users/me", {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`
    }
  });
}

export async function updateEmail(token, payload) {
  return request("/v1/users/me/email", {
    method: "PUT",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });
}

export async function createTransaction(token, payload) {
  return request("/v1/transactions", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });
}

export async function createPixKey(token, payload) {
  return request("/v1/pix-keys", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });
}

export function createBalanceSocket(token) {
  return new WebSocket(buildWebSocketUrl("/ws/balance", token));
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

function buildWebSocketUrl(path, token) {
  const resolvedUrl = new URL(buildUrl(path), window.location.origin);
  resolvedUrl.protocol = resolvedUrl.protocol === "https:" ? "wss:" : "ws:";
  resolvedUrl.searchParams.set("token", token);
  return resolvedUrl.toString();
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
    return "Nao autorizado (401). O token pode estar invalido, expirado ou nao estar chegando corretamente ao backend.";
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

  return `Erro na requisicao (${response.status})`;
}
