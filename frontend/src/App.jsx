import { useState } from "react";
import {
  clearAuth,
  createUser,
  forgotPassword,
  getStoredAuth,
  login,
  saveAuth
} from "./api";
import Dashboard from "./pages/Dashboard";
import ForgotPassword from "./pages/ForgotPassword";
import Login from "./pages/Login";
import Register from "./pages/Register";

function Home({ onGoLogin, onGoRegister }) {
  return (
    <section className="landing-shell">
      <div className="hero-panel">
        <p className="eyebrow">NeoBank</p>
        <h1>Seu saldo, Pix, transferencias e cartoes em uma tela clara e rapida.</h1>
        <p className="muted">
          Interface mobile-first com acoes em destaque, visual roxo e formularios ligados ao que o backend realmente recebe.
        </p>

        <div className="hero-actions">
          <button type="button" onClick={onGoLogin}>
            Entrar
          </button>
          <button type="button" className="ghost-button" onClick={onGoRegister}>
            Abrir conta
          </button>
        </div>
      </div>

      <div className="hero-preview">
        <div className="mini-balance-card">
          <span>Saldo disponivel</span>
          <strong>R$ 12.480,90</strong>
          <small>Pix, transferencia, cartao e perfil em um unico painel</small>
        </div>

        <div className="mini-actions">
          <div className="mini-action">Pix</div>
          <div className="mini-action">Transferir</div>
          <div className="mini-action">Cartoes</div>
          <div className="mini-action">Perfil</div>
        </div>
      </div>
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

  async function handleRegister(form) {
    return createUser(form);
  }

  async function handleForgotPassword(form) {
    return forgotPassword(form);
  }

  function handleLogout() {
    clearAuth();
    setAuth(null);
    setPage("login");
  }

  return (
    <main className="app-shell">
      {page === "home" ? (
        <Home onGoLogin={() => setPage("login")} onGoRegister={() => setPage("register")} />
      ) : null}
      {page === "login" ? (
        <Login
          onLogin={handleLogin}
          onForgotPassword={() => setPage("forgot")}
          onRegister={() => setPage("register")}
        />
      ) : null}
      {page === "register" ? (
        <Register onRegister={handleRegister} onBackToLogin={() => setPage("login")} />
      ) : null}
      {page === "forgot" ? (
        <ForgotPassword
          onSubmit={handleForgotPassword}
          onBack={() => setPage("login")}
        />
      ) : null}
      {page === "dashboard" && auth ? (
        <Dashboard auth={auth} onLogout={handleLogout} onForgotPassword={handleForgotPassword} />
      ) : null}
    </main>
  );
}
