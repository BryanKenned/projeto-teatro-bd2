package br.com.projetoteatro.model;

import br.com.projetoteatro.enums.StatusContrato;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    private PropostaAluguel proposta;

    @Enumerated(EnumType.STRING)
    private StatusContrato statusContrato;

    private String contratante;
    private String email;
    private String nomePeca;
    private double valorIngresso;
    private double valorAluguel;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private LocalDate dataEncerramento;

    public Contrato() {
    }

    public Contrato(
            String contratante,
            String nomePeca,
            String email,
            double valorAluguel,
            LocalDate dataInicio,
            LocalDate dataFim,
            LocalTime horarioInicio,
            LocalTime horarioFim,
            LocalDate dataEncerramento,
            PropostaAluguel proposta) {

        this.contratante = proposta.getContratante().getNome();
        this.nomePeca = proposta.getNomePeca();
        this.valorAluguel = proposta.getValorAluguel();
        this.dataInicio = proposta.getDataInicio();
        this.dataFim = proposta.getDataFim();
        this.horarioInicio = proposta.getHorarioInicio();
        this.horarioFim = proposta.getHorarioFim();
        this.valorIngresso = proposta.getValorIngresso();
        this.email = proposta.getContratante().getEmail();
        this.proposta = proposta;
        this.statusContrato = StatusContrato.ATIVO;
        this.dataEncerramento = dataEncerramento;
    }

    public Contrato(PropostaAluguel proposta) {
        this.proposta = proposta;
        if (proposta != null) {
            if (proposta.getContratante() != null) {
                this.contratante = proposta.getContratante().getNome();
                this.email = proposta.getContratante().getEmail();
            }
            this.nomePeca = proposta.getNomePeca();
            this.valorAluguel = proposta.getValorAluguel();
            this.dataInicio = proposta.getDataInicio();
            this.dataFim = proposta.getDataFim();
            this.horarioInicio = proposta.getHorarioInicio();
            this.horarioFim = proposta.getHorarioFim();
            this.valorIngresso = proposta.getValorIngresso();
            this.statusContrato = StatusContrato.ATIVO;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PropostaAluguel getProposta() {
        return proposta;
    }

    public void setProposta(PropostaAluguel proposta) {
        this.proposta = proposta;
    }

    public StatusContrato getStatusContrato() {
        return statusContrato;
    }

    public void setStatusContrato(StatusContrato statusContrato) {
        this.statusContrato = statusContrato;
    }

    public String getContratante() {
        return contratante;
    }

    public void setContratante(String contratante) {
        this.contratante = contratante;
    }

    public String getNomePeca() {
        return nomePeca;
    }

    public void setNomePeca(String nomePeca) {
        this.nomePeca = nomePeca;
    }

    public double getValorIngresso() {
        return valorIngresso;
    }

    public void setValorIngresso(double valorIngresso) {
        this.valorIngresso = valorIngresso;
    }

    public double getValorAluguel() {
        return valorAluguel;
    }

    public void setValorAluguel(double valorAluguel) {
        this.valorAluguel = valorAluguel;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(LocalTime horarioFim) {
        this.horarioFim = horarioFim;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(LocalDate dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    public boolean estaEncerrada() {
        return statusContrato == StatusContrato.INATIVO;
    }

    public void encerrarContrato() {

        if (estaEncerrada()) {
            throw new IllegalStateException(
                    "Contrato já encerrada......");
        }

        statusContrato = StatusContrato.INATIVO;
        dataEncerramento = LocalDate.now();
    }

    public void estender(LocalDate novaDataFim) {

        if (!novaDataFim.isAfter(dataFim)) {
            throw new IllegalArgumentException(
                    "Nova data deve ser posterior à atual....");
        }

        this.dataFim = novaDataFim;
    }

    @Override
    public String toString() {
        return this.getNomePeca();
    }
}