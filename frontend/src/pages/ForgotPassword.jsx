import { useState } from "react";

export default function ForgotPassword({ onSubmit, onBack }) {
  const [form, setForm] = useState({
    email: "",
    cpf: "",
    phone: "",
    newPassword: ""
  });
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setMessage("");
    setError("");

    try {
      const response = await onSubmit(form);
      setMessage(response.message);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="card">
      <h2>Recuperar Senha</h2>
      <form onSubmit={handleSubmit} className="form">
        <label>
          Email
          <input name="email" type="email" value={form.email} onChange={handleChange} required />
        </label>

        <label>
          CPF
          <input name="cpf" value={form.cpf} onChange={handleChange} required />
        </label>

        <label>
          Telefone
          <input name="phone" value={form.phone} onChange={handleChange} required />
        </label>

        <label>
          Nova senha
          <input
            name="newPassword"
            type="password"
            value={form.newPassword}
            onChange={handleChange}
            required
          />
        </label>

        {message ? <p className="success">{message}</p> : null}
        {error ? <p className="error">{error}</p> : null}

        <button type="submit" disabled={loading}>
          {loading ? "Atualizando..." : "Atualizar senha"}
        </button>
      </form>

      <button type="button" className="secondary" onClick={onBack}>
        Voltar para login
      </button>
    </section>
  );
}
