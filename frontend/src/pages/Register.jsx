import { useState } from "react";

const OCCUPATIONS = [
  "AGRICULTOR",
  "AUTONOMO",
  "BALCONISTA",
  "CAMAREIRA",
  "COMERCIANTE",
  "DENTISTA",
  "DESIGNER",
  "DESENVOLVEDOR",
  "ELETRICISTA",
  "ENGENHEIRO",
  "ESTUDANTE",
  "FREELANCER",
  "GERENTE",
  "MEDICO",
  "MOTORISTA",
  "PROFESSOR",
  "RECEPCIONISTA",
  "OUTROS"
];

const ACCOUNT_TYPES = ["CLT", "PJ"];

function toApiDate(value) {
  if (!value) {
    return "";
  }

  const [year, month, day] = value.split("-");
  return `${day}-${month}-${year}`;
}

export default function Register({ onRegister, onBackToLogin }) {
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    occupationEnum: "DESENVOLVEDOR",
    cpf: "",
    phone: "",
    salary: "",
    typeAccountEnum: "CLT",
    dateNasciment: ""
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
      await onRegister({
        ...form,
        salary: Number(form.salary),
        dateNasciment: toApiDate(form.dateNasciment)
      });
      setMessage("Conta criada com sucesso. Agora faca login.");
      setForm((current) => ({
        ...current,
        password: ""
      }));
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="auth-card register-card">
      <div className="section-heading">
        <p className="eyebrow">Abrir conta</p>
        <h1>Crie seu acesso ao NeoBank</h1>
        <p className="muted">Preencha os dados aceitos pelo backend para habilitar conta, saldo e cartao.</p>
      </div>

      <form onSubmit={handleSubmit} className="form two-columns">
        <label>
          Nome completo
          <input name="name" value={form.name} onChange={handleChange} required />
        </label>

        <label>
          Email
          <input name="email" type="email" value={form.email} onChange={handleChange} required />
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

        <label>
          CPF
          <input name="cpf" value={form.cpf} onChange={handleChange} required />
        </label>

        <label>
          Telefone
          <input name="phone" value={form.phone} onChange={handleChange} required />
        </label>

        <label>
          Renda mensal
          <input
            name="salary"
            type="number"
            min="0"
            step="0.01"
            value={form.salary}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          Ocupacao
          <select name="occupationEnum" value={form.occupationEnum} onChange={handleChange}>
            {OCCUPATIONS.map((occupation) => (
              <option key={occupation} value={occupation}>
                {occupation}
              </option>
            ))}
          </select>
        </label>

        <label>
          Tipo de conta
          <select name="typeAccountEnum" value={form.typeAccountEnum} onChange={handleChange}>
            {ACCOUNT_TYPES.map((type) => (
              <option key={type} value={type}>
                {type}
              </option>
            ))}
          </select>
        </label>

        <label className="full-width">
          Data de nascimento
          <input
            name="dateNasciment"
            type="date"
            value={form.dateNasciment}
            onChange={handleChange}
            required
          />
        </label>

        {message ? <p className="success full-width">{message}</p> : null}
        {error ? <p className="error full-width">{error}</p> : null}

        <button type="submit" disabled={loading} className="full-width">
          {loading ? "Criando conta..." : "Criar conta"}
        </button>
      </form>

      <button type="button" className="ghost-button" onClick={onBackToLogin}>
        Ja tenho conta
      </button>
    </section>
  );
}
