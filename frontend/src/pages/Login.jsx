import { useState } from "react";

export default function Login({ onLogin, onForgotPassword, onRegister }) {
  const [form, setForm] = useState({
    email: "",
    password: ""
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setError("");

    try {
      await onLogin(form);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  return (
    <section className="auth-card">
      <div className="section-heading">
        <p className="eyebrow">Acesse sua conta</p>
        <h1>Entrar no NeoBank</h1>
        <p className="muted">Consulte saldo, movimente a conta e atualize seus dados em um unico painel.</p>
      </div>

      <form onSubmit={handleSubmit} className="form">
        <label>
          Email
          <input
            name="email"
            type="email"
            value={form.email}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          Senha
          <input
            name="password"
            type="password"
            value={form.password}
            onChange={handleChange}
            required
          />
        </label>

        {error ? <p className="error">{error}</p> : null}

        <button type="submit" disabled={loading}>
          {loading ? "Entrando..." : "Entrar"}
        </button>
      </form>

      <div className="stack-actions">
        <button type="button" className="ghost-button" onClick={onForgotPassword}>
          Esqueci minha senha
        </button>
        <button type="button" className="ghost-button" onClick={onRegister}>
          Criar conta
        </button>
      </div>
    </section>
  );
}
