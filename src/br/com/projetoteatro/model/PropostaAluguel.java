package br.com.projetoteatro.model;

import br.com.projetoteatro.enums.StatusContrato;
import br.com.projetoteatro.enums.StatusProposta;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class PropostaAluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Contratante contratante;

    private String nomePeca;

    private double valorIngresso;

    private double valorAluguel;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private LocalTime horarioInicio;

    private LocalTime horarioFim;

    @Enumerated(EnumType.STRING)
    private StatusProposta statusProposta;

    @Enumerated(EnumType.STRING)
    private StatusContrato statusContrato;

    private LocalDate dataEncerramento;

    public PropostaAluguel() {
    }

    public PropostaAluguel(
            Contratante contratante,
            String nomePeca,
            double valorAluguel,
            LocalDate dataInicio,
            LocalDate dataFim,
            LocalTime horarioInicio,
            LocalTime horarioFim,
            double valorIngresso) {

        this.contratante = contratante;
        this.nomePeca = nomePeca;
        this.valorAluguel = valorAluguel;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.valorIngresso = valorIngresso;

        this.statusProposta = StatusProposta.EM_CONTRATACAO;
        this.statusContrato = StatusContrato.PENDENTE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contratante getContratante() {
        return contratante;
    }

    public void setContratante(Contratante contratante) {
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

    public StatusProposta getStatusProposta() {
        return statusProposta;
    }

    public void setStatusProposta(StatusProposta statusProposta) {
        this.statusProposta = statusProposta;
    }

    public StatusContrato getStatusContrato() {
        return statusContrato;
    }

    public void setStatusContrato(StatusContrato statusContrato) {
        this.statusContrato = statusContrato;
    }

    public LocalDate getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(LocalDate dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    public boolean estaEncerrada() {
        return statusProposta == StatusProposta.ENCERRADO;
    }

    public void contratar() {

        if (statusProposta != StatusProposta.EM_CONTRATACAO) {
            throw new IllegalStateException(
                    "A proposta não pode ser contratada.");
        }

        statusProposta = StatusProposta.CONTRATADO;
    }

    public void encerrarContrato() {

        if (estaEncerrada()) {
            throw new IllegalStateException(
                    "Contrato já encerrado.");
        }

        statusProposta = StatusProposta.ENCERRADO;
        dataEncerramento = LocalDate.now();
    }

    public void estenderContrato(LocalDate novaDataFim) {

        if (!novaDataFim.isAfter(dataFim)) {
            throw new IllegalArgumentException(
                    "A nova data deve ser posterior à data atual.");
        }

        dataFim = novaDataFim;
        statusProposta = StatusProposta.ALTERADO;
    }

    @Override
    public String toString() {
        return "PropostaAluguel{" +
                "id=" + id +
                ", contratante=" +
                (contratante != null ? contratante.getNome() : "N/A") +
                ", nomePeca='" + nomePeca + '\'' +
                ", status=" + statusProposta +
                '}';
    }
}