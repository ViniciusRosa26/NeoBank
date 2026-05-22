import { useState } from "react";
import {
  clearAuth,
  forgotPassword,
  getBalance,
  getStoredAuth,
  login,
  saveAuth
} from "./api";
import Dashboard from "./pages/Dashboard";
import ForgotPassword from "./pages/ForgotPassword";
import Login from "./pages/Login";

function Home({ onGoLogin }) {
  return (
    <section className="card hero">
      <p className="eyebrow">NeoBank Test Auth</p>
      <h1>Front-end temporario para testes de seguranca.</h1>
      <button type="button" onClick={onGoLogin}>
        Ir para Login
      </button>
    </section>
  );
}

export default function App() {
  const storedAuth = getStoredAuth();
  const [page, setPage] = useState(storedAuth?.token ? "dashboard" : "home");
  const [auth, setAuth] = useState(storedAuth);

  async function handleLogin(form) {
    const response = await login(form);
    saveAuth(response);
    setAuth(response);
    setPage("dashboard");
  }

  async function handleForgotPassword(form) {
    return forgotPassword(form);
  }

  async function handleCheckBalance() {
    try {
      return await getBalance(auth.token);
    } catch (error) {
      if (error.status === 401) {
        clearAuth();
        setAuth(null);
        setPage("login");
      }

      throw error;
    }
  }

  function handleLogout() {
    clearAuth();
    setAuth(null);
    setPage("login");
  }

  return (
    <main className="app-shell">
      {page === "home" ? <Home onGoLogin={() => setPage("login")} /> : null}
      {page === "login" ? (
        <Login onLogin={handleLogin} onForgotPassword={() => setPage("forgot")} />
      ) : null}
      {page === "forgot" ? (
        <ForgotPassword
          onSubmit={handleForgotPassword}
          onBack={() => setPage("login")}
        />
      ) : null}
      {page === "dashboard" && auth ? (
        <Dashboard auth={auth} onCheckBalance={handleCheckBalance} onLogout={handleLogout} />
      ) : null}
    </main>
  );
}
