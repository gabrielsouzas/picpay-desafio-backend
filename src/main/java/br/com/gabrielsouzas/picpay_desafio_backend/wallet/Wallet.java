package br.com.gabrielsouzas.picpay_desafio_backend.wallet;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("wallets")
public record Wallet(
        @Id Long id,
        String fullName,
        Long cpf,
        String email,
        String password,
        int type,
        BigDecimal balance) {

    public Wallet debit(BigDecimal value) {
        return new Wallet(
                this.id,
                this.fullName,
                this.cpf,
                this.email,
                this.password,
                this.type,
                this.balance.subtract(value));
    }

    public Wallet credit(BigDecimal value) {
        return new Wallet(
                this.id,
                this.fullName,
                this.cpf,
                this.email,
                this.password,
                this.type,
                this.balance.add(value));
    }

}
