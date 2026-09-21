package br.com.projetoteatro.model;

import br.com.projetoteatro.enums.StatusAssento;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;


@Entity
public class Assento {

    @Id
    private String codigo;

    @Enumerated(EnumType.STRING)
    private StatusAssento status;

    @ManyToOne
    private Setor setor;

    public Assento() {
    }

    public Assento(String numeroAssento) {
        this.codigo = numeroAssento;
    }

    public Assento(String codigo, StatusAssento status, Setor setor) {
        this.codigo = codigo;
        this.status = status;
        this.setor = setor;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public StatusAssento getStatus() {
        return status;
    }

    public void setStatus(StatusAssento status) {
        this.status = status;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    @Override
    public String toString() {
        return "Codigo: " + codigo + System.lineSeparator() +
                "Status: " + status + System.lineSeparator() +
                "Setor: " + (setor != null ? setor.getSetor() : "Sem setor");
    }
}