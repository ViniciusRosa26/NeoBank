import { useEffect, useState } from "react";
import {
  clearAuth,
  createBalanceSocket,
  createPixKey,
  createTransaction,
  getUserSummary,
  updateEmail
} from "../api";

const ACTIONS = [
  { id: "pix", label: "Pix", icon: "P" },
  { id: "transfer", label: "Transferir", icon: "T" },
  { id: "deposit", label: "Depositar", icon: "D" },
  { id: "withdraw", label: "Sacar", icon: "S" },
  { id: "cards", label: "Cartoes", icon: "C" },
  { id: "profile", label: "Perfil", icon: "U" }
];

const PIX_TYPES = ["PHONE", "CPF", "RANDOM_KEY"];

function formatCurrency(value) {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL"
  }).format(Number(value || 0));
}

function formatOccupation(value) {
  return value?.toLowerCase().replaceAll("_", " ") || "-";
}

function emptyTransactionForm() {
  return {
    amount: "",
    description: "",
    destinationAccountId: "",
    destinationPixKey: "",
    pixKeyType: "PHONE"
  };
}

export default function Dashboard({ auth, onLogout, onForgotPassword }) {
  const [summary, setSummary] = useState(null);
  const [activeAction, setActiveAction] = useState("pix");
  const [loadingSummary, setLoadingSummary] = useState(true);
  const [screenError, setScreenError] = useState("");
  const [feedback, setFeedback] = useState("");
  const [panelError, setPanelError] = useState("");
  const [authWarning, setAuthWarning] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const [pixForm, setPixForm] = useState(emptyTransactionForm());
  const [transferForm, setTransferForm] = useState(emptyTransactionForm());
  const [depositForm, setDepositForm] = useState(emptyTransactionForm());
  const [withdrawForm, setWithdrawForm] = useState(emptyTransactionForm());
  const [pixKeyForm, setPixKeyForm] = useState({
    keyValue: "",
    keyType: "PHONE"
  });
  const [emailForm, setEmailForm] = useState({
    email: auth.email || ""
  });
  const [passwordForm, setPasswordForm] = useState({
    cpf: "",
    phone: "",
    newPassword: ""
  });

  useEffect(() => {
    setEmailForm({ email: auth.email || "" });
  }, [auth.email]);

  useEffect(() => {
    loadSummary();
  }, []);

  useEffect(() => {
    if (!auth.token) {
      return undefined;
    }

    const socket = createBalanceSocket(auth.token);

    socket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);

        if (typeof data.balance !== "number") {
          return;
        }

        setSummary((current) => {
          if (!current) {
            return current;
          }

          if (data.accountId && current.accountId !== data.accountId) {
            return current;
          }

          return {
            ...current,
            balance: data.balance
          };
        });
      } catch {
        // ignora mensagens invalidas para nao quebrar a tela
      }
    };

    return () => {
      socket.close();
    };
  }, [auth.token]);

  async function loadSummary() {
    setLoadingSummary(true);
    setScreenError("");
    setAuthWarning("");

    try {
      const response = await getUserSummary(auth.token);
      setSummary(response);
      setPasswordForm((current) => ({
        ...current,
        cpf: response.cpf || "",
        phone: response.phone || ""
      }));
      setEmailForm({ email: response.email || "" });
    } catch (error) {
      handleAuthError(error, "Nao foi possivel carregar os dados da conta.");
      setScreenError(error.message);
    } finally {
      setLoadingSummary(false);
    }
  }

  function handleAuthError(error, contextMessage = "") {
    if (error.status === 401) {
      setAuthWarning(
        [
          contextMessage,
          "A API retornou 401 para esta operacao.",
          "O frontend manteve sua sessao aberta para facilitar o diagnostico."
        ]
          .filter(Boolean)
          .join(" ")
      );
    }
  }

  function resetMessages() {
    setFeedback("");
    setPanelError("");
    setAuthWarning("");
  }

  function handleFieldChange(setter) {
    return (event) => {
      const { name, value } = event.target;
      setter((current) => ({ ...current, [name]: value }));
    };
  }

  async function submitTransaction(type, form, resetForm) {
    setSubmitting(true);
    resetMessages();

    try {
      await createTransaction(auth.token, {
        amount: Number(form.amount),
        type,
        description: form.description || null,
        destinationAccountId:
          type === "TRANSFER" ? Number(form.destinationAccountId) : null,
        destinationPixKey: type === "PIX" ? form.destinationPixKey : null,
        pixKeyType: type === "PIX" ? form.pixKeyType : null
      });

      await loadSummary();
      resetForm();
      setFeedback("Operacao concluida com sucesso.");
    } catch (error) {
      handleAuthError(error, `Falha ao executar ${type}.`);
      setPanelError(error.message);
    } finally {
      setSubmitting(false);
    }
  }

  async function handleCreatePixKey(event) {
    event.preventDefault();
    setSubmitting(true);
    resetMessages();

    try {
      await createPixKey(auth.token, {
        ...pixKeyForm,
        accountId: summary.accountId
      });
      setPixKeyForm({
        keyValue: "",
        keyType: "PHONE"
      });
      setFeedback("Chave Pix cadastrada com sucesso.");
    } catch (error) {
      handleAuthError(error, "Falha ao cadastrar a chave Pix.");
      setPanelError(error.message);
    } finally {
      setSubmitting(false);
    }
  }

  async function handleUpdateEmail(event) {
    event.preventDefault();
    setSubmitting(true);
    resetMessages();

    try {
      await updateEmail(auth.token, emailForm);
      clearAuth();
      setFeedback("Email atualizado. Entre novamente com o novo endereco.");
      setTimeout(() => onLogout(), 1200);
    } catch (error) {
      handleAuthError(error, "Falha ao atualizar o email.");
      setPanelError(error.message);
    } finally {
      setSubmitting(false);
    }
  }

  async function handleChangePassword(event) {
    event.preventDefault();
    setSubmitting(true);
    resetMessages();

    try {
      await onForgotPassword({
        email: summary.email,
        cpf: passwordForm.cpf,
        phone: passwordForm.phone,
        newPassword: passwordForm.newPassword
      });
      setPasswordForm((current) => ({
        ...current,
        newPassword: ""
      }));
      setFeedback("Senha atualizada com sucesso.");
    } catch (error) {
      setPanelError(error.message);
    } finally {
      setSubmitting(false);
    }
  }

  function renderPanel() {
    if (!summary) {
      return null;
    }

    if (activeAction === "pix") {
      return (
        <div className="panel-grid">
          <section className="surface-card">
            <div className="section-heading">
              <p className="eyebrow">Enviar Pix</p>
              <h3>Transferencia por chave</h3>
            </div>
            <form
              className="form"
              onSubmit={(event) => {
                event.preventDefault();
                submitTransaction("PIX", pixForm, () => setPixForm(emptyTransactionForm()));
              }}
            >
              <label>
                Valor
                <input
                  name="amount"
                  type="number"
                  min="0"
                  step="0.01"
                  value={pixForm.amount}
                  onChange={handleFieldChange(setPixForm)}
                  required
                />
              </label>
              <label>
                Chave Pix de destino
                <input
                  name="destinationPixKey"
                  value={pixForm.destinationPixKey}
                  onChange={handleFieldChange(setPixForm)}
                  required
                />
              </label>
              <label>
                Tipo da chave
                <select
                  name="pixKeyType"
                  value={pixForm.pixKeyType}
                  onChange={handleFieldChange(setPixForm)}
                >
                  {PIX_TYPES.map((type) => (
                    <option key={type} value={type}>
                      {type}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Descricao
                <input
                  name="description"
                  value={pixForm.description}
                  onChange={handleFieldChange(setPixForm)}
                />
              </label>
              <button type="submit" disabled={submitting}>
                {submitting ? "Enviando..." : "Fazer Pix"}
              </button>
            </form>
          </section>

          <section className="surface-card">
            <div className="section-heading">
              <p className="eyebrow">Minhas chaves</p>
              <h3>Cadastrar chave Pix</h3>
            </div>
            <form className="form" onSubmit={handleCreatePixKey}>
              <label>
                Valor da chave
                <input
                  name="keyValue"
                  value={pixKeyForm.keyValue}
                  onChange={handleFieldChange(setPixKeyForm)}
                  required
                />
              </label>
              <label>
                Tipo da chave
                <select
                  name="keyType"
                  value={pixKeyForm.keyType}
                  onChange={handleFieldChange(setPixKeyForm)}
                >
                  {PIX_TYPES.map((type) => (
                    <option key={type} value={type}>
                      {type}
                    </option>
                  ))}
                </select>
              </label>
              <button type="submit" disabled={submitting}>
                {submitting ? "Salvando..." : "Cadastrar chave"}
              </button>
            </form>
          </section>
        </div>
      );
    }

    if (activeAction === "transfer") {
      return (
        <section className="surface-card">
          <div className="section-heading">
            <p className="eyebrow">Transferencia</p>
            <h3>Enviar para outra conta</h3>
          </div>
          <form
            className="form"
            onSubmit={(event) => {
              event.preventDefault();
              submitTransaction("TRANSFER", transferForm, () => setTransferForm(emptyTransactionForm()));
            }}
          >
            <label>
              Valor
              <input
                name="amount"
                type="number"
                min="0"
                step="0.01"
                value={transferForm.amount}
                onChange={handleFieldChange(setTransferForm)}
                required
              />
            </label>
            <label>
              ID da conta de destino
              <input
                name="destinationAccountId"
                type="number"
                min="1"
                value={transferForm.destinationAccountId}
                onChange={handleFieldChange(setTransferForm)}
                required
              />
            </label>
            <label>
              Descricao
              <input
                name="description"
                value={transferForm.description}
                onChange={handleFieldChange(setTransferForm)}
              />
            </label>
            <button type="submit" disabled={submitting}>
              {submitting ? "Transferindo..." : "Transferir"}
            </button>
          </form>
        </section>
      );
    }

    if (activeAction === "deposit") {
      return (
        <section className="surface-card">
          <div className="section-heading">
            <p className="eyebrow">Deposito</p>
            <h3>Adicionar saldo na conta</h3>
          </div>
          <form
            className="form"
            onSubmit={(event) => {
              event.preventDefault();
              submitTransaction("DEPOSIT", depositForm, () => setDepositForm(emptyTransactionForm()));
            }}
          >
            <label>
              Valor
              <input
                name="amount"
                type="number"
                min="0"
                step="0.01"
                value={depositForm.amount}
                onChange={handleFieldChange(setDepositForm)}
                required
              />
            </label>
            <label>
              Descricao
              <input
                name="description"
                value={depositForm.description}
                onChange={handleFieldChange(setDepositForm)}
              />
            </label>
            <button type="submit" disabled={submitting}>
              {submitting ? "Processando..." : "Depositar"}
            </button>
          </form>
        </section>
      );
    }

    if (activeAction === "withdraw") {
      return (
        <section className="surface-card">
          <div className="section-heading">
            <p className="eyebrow">Saque</p>
            <h3>Retirar saldo da conta</h3>
          </div>
          <form
            className="form"
            onSubmit={(event) => {
              event.preventDefault();
              submitTransaction("WITHDRAW", withdrawForm, () => setWithdrawForm(emptyTransactionForm()));
            }}
          >
            <label>
              Valor
              <input
                name="amount"
                type="number"
                min="0"
                step="0.01"
                value={withdrawForm.amount}
                onChange={handleFieldChange(setWithdrawForm)}
                required
              />
            </label>
            <label>
              Descricao
              <input
                name="description"
                value={withdrawForm.description}
                onChange={handleFieldChange(setWithdrawForm)}
              />
            </label>
            <button type="submit" disabled={submitting}>
              {submitting ? "Processando..." : "Sacar"}
            </button>
          </form>
        </section>
      );
    }

    if (activeAction === "cards") {
      return (
        <div className="panel-grid">
          <section className="credit-card-visual">
            <span className="card-chip" />
            <p className="credit-brand">NeoBank Platinum</p>
            <strong>{summary.creditCardNumber || "**** **** **** ----"}</strong>
            <div className="credit-meta">
              <span>{summary.creditCardHolder || summary.name}</span>
              <span>{summary.creditCardExpirationDate || "--/--"}</span>
            </div>
          </section>

          <section className="surface-card">
            <div className="section-heading">
              <p className="eyebrow">Cartoes</p>
              <h3>Limites e dados principais</h3>
            </div>
            <div className="stats-grid">
              <article className="stat-card">
                <span>Limite de credito</span>
                <strong>{formatCurrency(summary.creditCardLimit)}</strong>
              </article>
              <article className="stat-card">
                <span>Conta vinculada</span>
                <strong>#{summary.accountId}</strong>
              </article>
              <article className="stat-card">
                <span>Limite Pix dia</span>
                <strong>{formatCurrency(summary.dailyPixLimit)}</strong>
              </article>
              <article className="stat-card">
                <span>Limite Pix noite</span>
                <strong>{formatCurrency(summary.nightPixLimit)}</strong>
              </article>
            </div>
          </section>
        </div>
      );
    }

    return (
      <div className="panel-grid">
        <section className="surface-card">
          <div className="section-heading">
            <p className="eyebrow">Atualizar email</p>
            <h3>Altere seu acesso principal</h3>
          </div>
          <form className="form" onSubmit={handleUpdateEmail}>
            <label>
              Novo email
              <input
                name="email"
                type="email"
                value={emailForm.email}
                onChange={handleFieldChange(setEmailForm)}
                required
              />
            </label>
            <button type="submit" disabled={submitting}>
              {submitting ? "Salvando..." : "Atualizar email"}
            </button>
          </form>
        </section>

        <section className="surface-card">
          <div className="section-heading">
            <p className="eyebrow">Trocar senha</p>
            <h3>Validacao com CPF e telefone</h3>
          </div>
          <form className="form" onSubmit={handleChangePassword}>
            <label>
              Email atual
              <input value={summary.email} disabled />
            </label>
            <label>
              CPF
              <input
                name="cpf"
                value={passwordForm.cpf}
                onChange={handleFieldChange(setPasswordForm)}
                required
              />
            </label>
            <label>
              Telefone
              <input
                name="phone"
                value={passwordForm.phone}
                onChange={handleFieldChange(setPasswordForm)}
                required
              />
            </label>
            <label>
              Nova senha
              <input
                name="newPassword"
                type="password"
                value={passwordForm.newPassword}
                onChange={handleFieldChange(setPasswordForm)}
                required
              />
            </label>
            <button type="submit" disabled={submitting}>
              {submitting ? "Atualizando..." : "Atualizar senha"}
            </button>
          </form>
        </section>
      </div>
    );
  }

  return (
    <section className="dashboard-shell">
      <header className="dashboard-topbar">
        <div>
          <p className="eyebrow">Painel principal</p>
          <h1>Ola, {summary?.name || auth.email}</h1>
        </div>
        <button type="button" className="ghost-button" onClick={onLogout}>
          Sair
        </button>
      </header>

      {loadingSummary ? (
        <section className="surface-card">
          <p className="muted">Carregando sua conta...</p>
        </section>
      ) : null}

      {screenError ? (
        <section className="surface-card">
          <p className="error">{screenError}</p>
          <div className="inline-actions">
            <button type="button" className="ghost-button" onClick={loadSummary}>
              Tentar novamente
            </button>
            <button type="button" className="ghost-button" onClick={onLogout}>
              Sair
            </button>
          </div>
        </section>
      ) : null}

      {summary ? (
        <>
          <section className="balance-hero">
            <div>
              <p className="eyebrow">Saldo disponivel</p>
              <h2>{formatCurrency(summary.balance)}</h2>
              <p className="muted">
                Conta #{summary.accountId} • {summary.accountType} • {formatOccupation(summary.occupation)}
              </p>
            </div>

            <div className="hero-aside">
              <div className="hero-metric">
                <span>Limite do cartao</span>
                <strong>{formatCurrency(summary.creditCardLimit)}</strong>
              </div>
              <div className="hero-metric">
                <span>Email</span>
                <strong>{summary.email}</strong>
              </div>
            </div>
          </section>

          <section className="surface-card">
            <div className="action-grid">
              {ACTIONS.map((action) => (
                <button
                  key={action.id}
                  type="button"
                  className={`round-action ${activeAction === action.id ? "is-active" : ""}`}
                  onClick={() => {
                    setActiveAction(action.id);
                    resetMessages();
                  }}
                >
                  <span className="round-icon">{action.icon}</span>
                  <span>{action.label}</span>
                </button>
              ))}
            </div>
          </section>

          {authWarning ? <p className="warning inline-feedback">{authWarning}</p> : null}
          {feedback ? <p className="success inline-feedback">{feedback}</p> : null}
          {panelError ? <p className="error inline-feedback">{panelError}</p> : null}

          {renderPanel()}
        </>
      ) : null}
    </section>
  );
}
