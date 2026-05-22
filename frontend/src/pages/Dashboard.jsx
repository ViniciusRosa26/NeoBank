import { useState } from "react";

export default function Dashboard({ auth, onCheckBalance, onLogout }) {
  const [balance, setBalance] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleBalance() {
    setLoading(true);
    setError("");

    try {
      const response = await onCheckBalance();
      setBalance(response.balance);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="card">
      <h2>Dashboard</h2>
      <p>Ola, usuario.</p>
      <p><strong>Email:</strong> {auth.email}</p>
      <p><strong>Role:</strong> {auth.role}</p>

      <div className="actions">
        <button type="button" onClick={handleBalance} disabled={loading}>
          {loading ? "Consultando..." : "Ver saldo"}
        </button>
        <button type="button" className="secondary" onClick={onLogout}>
          Sair
        </button>
      </div>

      {balance !== null ? <p className="balance">Saldo: R$ {Number(balance).toFixed(2)}</p> : null}
      {error ? <p className="error">{error}</p> : null}
    </section>
  );
}
