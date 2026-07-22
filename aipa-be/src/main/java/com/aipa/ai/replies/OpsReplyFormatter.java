package com.aipa.ai.replies;

import com.aipa.domain.catalog.ErrorCodeCatalog;
import com.aipa.domain.model.PaymentTransaction;
import com.aipa.domain.model.TransactionStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Formats payment data into concise French replies for support operators.
 */
@Component
public class OpsReplyFormatter {

    private static final Locale FR = Locale.FRANCE;
    private static final DateTimeFormatter WHEN = DateTimeFormatter
            .ofPattern("d MMM yyyy 'à' HH:mm", FR)
            .withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter DAY = DateTimeFormatter
            .ofPattern("d MMMM yyyy", FR);

    public String transactionNotFound(String reference) {
        return "Aucune transaction trouvée pour la référence "
                + reference + ".";
    }

    public String singleTransaction(PaymentTransaction tx) {
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction ").append(tx.reference()).append(" — ")
                .append(statusLabel(tx.status())).append("\n\n");
        sb.append("• Montant : ").append(money(tx.amount(), tx.currency())).append('\n');
        sb.append("• Opérateur : ").append(tx.provider().displayName()).append('\n');
        sb.append("• Créée : ").append(when(tx.createdAt())).append('\n');
        if (!tx.updatedAt().equals(tx.createdAt())) {
            sb.append("• Mise à jour : ").append(when(tx.updatedAt())).append('\n');
        }
        if (tx.errorCode() != null && !tx.errorCode().isBlank()) {
            sb.append("• Code erreur : ").append(tx.errorCode());
            ErrorCodeCatalog.describe(tx.errorCode()).ifPresent(desc -> sb.append(" (").append(desc).append(')'));
            sb.append('\n');
        }
        return sb.toString().trim();
    }

    public String failureExplanation(PaymentTransaction tx) {
        if (tx.status() != TransactionStatus.FAILED) {
            return "La transaction " + tx.reference()
                    + " n’est pas en échec (statut actuel : "
                    + statusLabel(tx.status()) + ").\n\n"
                    + singleTransaction(tx);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(tx.reference()).append(" a échoué")
                .append(" (").append(tx.provider().displayName())
                .append(" · ").append(money(tx.amount(), tx.currency()))
                .append(").\n\n");

        if (tx.errorCode() == null || tx.errorCode().isBlank()) {
            sb.append("Aucun code d’erreur n’est renseigné sur cette transaction.");
        } else {
            sb.append("Cause : ").append(tx.errorCode());
            ErrorCodeCatalog.describe(tx.errorCode()).ifPresentOrElse(
                    desc -> sb.append(" — ").append(desc).append('.'),
                    () -> sb.append(" — descriptif métier inconnu."));
        }
        sb.append("\nÉchec enregistré le ").append(when(tx.updatedAt())).append('.');
        return sb.toString();
    }

    public String failureNotFound(String reference) {
        return transactionNotFound(reference);
    }

    public String failedList(LocalDate day, List<PaymentTransaction> failed) {
        if (failed.isEmpty()) {
            return "Aucun paiement en échec le " + DAY.format(day) + ".";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(failed.size())
                .append(failed.size() > 1 ? " paiements ont échoué" : " paiement a échoué")
                .append(" le ").append(DAY.format(day)).append(".\n\n");
        sb.append(numberedTransactions(failed));
        return sb.toString().trim();
    }

    public String providerList(
            String providerLabel,
            List<PaymentTransaction> txs,
            Integer limit) {
        if (txs.isEmpty()) {
            return "Aucune transaction pour " + providerLabel + ".";
        }
        int shown = limit == null ? txs.size() : Math.min(limit, txs.size());
        StringBuilder sb = new StringBuilder();
        sb.append(txs.size()).append(" transaction")
                .append(txs.size() > 1 ? "s" : "")
                .append(" ").append(providerLabel);
        if (shown < txs.size()) {
            sb.append(" (affichage des ").append(shown).append(" plus récentes)");
        }
        sb.append(".\n\n");
        sb.append(numberedTransactions(txs.stream().limit(shown).toList()));
        return sb.toString().trim();
    }

    public String providerAboveAmount(
            String providerLabel,
            double minAmount,
            List<PaymentTransaction> txs) {
        String threshold = money(BigDecimal.valueOf(minAmount), "XOF");
        if (txs.isEmpty()) {
            return "Aucune transaction " + providerLabel
                    + " ≥ " + threshold + ".";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(txs.size()).append(" transaction")
                .append(txs.size() > 1 ? "s" : "")
                .append(" ").append(providerLabel).append(" ≥ ")
                .append(threshold).append(".\n\n");
        sb.append(numberedTransactions(txs));
        return sb.toString().trim();
    }

    public String statusCount(TransactionStatus status, long count) {
        return "Il y a " + count + " transaction"
                + (count > 1 ? "s" : "")
                + " au statut " + statusLabel(status) + ".";
    }

    public String statistics(Map<String, Object> stats) {
        StringBuilder sb = new StringBuilder();
        sb.append("Synthèse du ledger\n\n");
        sb.append("• Total : ").append(stats.getOrDefault("total", 0)).append('\n');
        sb.append("• Échecs aujourd’hui : ")
                .append(stats.getOrDefault("failedToday", 0)).append('\n');
        sb.append("• En attente : ")
                .append(stats.getOrDefault("pendingTotal", 0)).append('\n');

        Object byStatus = stats.get("byStatus");
        if (byStatus instanceof Map<?, ?> map && !map.isEmpty()) {
            sb.append("\nPar statut :\n");
            map.forEach((k, v) -> sb.append("• ").append(k).append(" : ")
                    .append(v).append('\n'));
        }

        Object byProvider = stats.get("byProvider");
        if (byProvider instanceof Map<?, ?> map && !map.isEmpty()) {
            sb.append("\nPar opérateur :\n");
            map.forEach((k, v) -> sb.append("• ").append(k).append(" : ")
                    .append(v).append('\n'));
        }
        return sb.toString().trim();
    }

    public String customersWithFailures(List<Map<String, Object>> customers, int min) {
        if (customers.isEmpty()) {
            return "Aucun client avec " + min
                    + "+ échecs sur les 7 derniers jours.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(customers.size()).append(" client")
                .append(customers.size() > 1 ? "s ont" : " a")
                .append(" cumulé plusieurs échecs cette semaine")
                .append(" (≥ ").append(min).append(").\n\n");
        int i = 1;
        for (Map<String, Object> row : customers) {
            sb.append(i++).append(". ");
            Object name = row.get("fullname");
            sb.append(name != null ? name : row.get("customerId"));
            sb.append(" — ").append(row.get("failureCount"))
                    .append(" échecs");
            if (row.get("phone") != null) {
                sb.append(" · ").append(row.get("phone"));
            }
            sb.append('\n');
        }
        return sb.toString().trim();
    }

    public String help() {
        return """
                Je peux vous aider sur le ledger paiements :
                • localiser une transaction (ex. TX45892)
                • expliquer un échec (code métier)
                • lister les échecs du jour ou par opérateur
                • repérer les clients à échecs répétés
                Reformulez votre question avec une référence, un opérateur ou une date.
                """.trim();
    }

    private String numberedTransactions(List<PaymentTransaction> txs) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (PaymentTransaction tx : txs) {
            sb.append(i++).append(". ").append(tx.reference()).append(" — ")
                    .append(statusLabel(tx.status()))
                    .append(" · ").append(tx.provider().displayName())
                    .append(" · ").append(money(tx.amount(), tx.currency()));
            if (tx.errorCode() != null && !tx.errorCode().isBlank()) {
                sb.append(" · ").append(tx.errorCode());
            }
            sb.append('\n');
        }
        return sb.toString().trim();
    }

    private static String money(BigDecimal amount, String currency) {
        NumberFormat nf = NumberFormat.getNumberInstance(FR);
        nf.setMaximumFractionDigits(0);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        String label = "XOF".equalsIgnoreCase(currency) ? "FCFA" : currency;
        return nf.format(amount) + " " + label;
    }

    private static String when(Instant instant) {
        return WHEN.format(instant);
    }

    private static String statusLabel(TransactionStatus status) {
        return switch (status) {
            case SUCCESS -> "SUCCESS (confirmée)";
            case FAILED -> "FAILED (échouée)";
            case PENDING -> "PENDING (en attente)";
            case CANCELLED -> "CANCELLED (annulée)";
            case EXPIRED -> "EXPIRED (expirée)";
        };
    }
}
