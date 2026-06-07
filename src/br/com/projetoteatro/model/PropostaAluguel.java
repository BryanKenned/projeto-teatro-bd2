package br.com.projetoteatro.model;

import br.com.projetoteatro.enums.StatusProposta;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class PropostaAluguel {

    private long id;
    private Contratante contratante;
    private String nomePeca;
    private double valorIngresso;
    private double valorAluguel;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private StatusProposta statusProposta;
    private LocalDate dataEncerramento;

    public void setId(long id) {
        this.id = id;
    }

    public void setContratante(Contratante contratante) {
        this.contratante = contratante;
    }

    public void setNomePeca(String nomePeca) {
        this.nomePeca = nomePeca;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public void setHorarioFim(LocalTime horarioFim) {
        this.horarioFim = horarioFim;
    }

    public void setDataEncerramento(LocalDate dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
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

        this.id = System.currentTimeMillis();
        this.contratante = contratante;
        this.nomePeca = nomePeca;
        this.valorAluguel = valorAluguel;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.valorIngresso = valorIngresso;

        this.statusProposta = StatusProposta.EM_CONTRATACAO;
    }

    public boolean estaEncerrada() {
        return statusProposta == StatusProposta.ENCERRADO;
    }

    public void contratar() {
        if (statusProposta != StatusProposta.EM_CONTRATACAO) {
            throw new IllegalStateException(
                    "A proposta não pode ser contratada."
            );
        }

        statusProposta = StatusProposta.CONTRATADO;
    }

    public void encerrarContrato() {
        if (estaEncerrada()) {
            throw new IllegalStateException(
                    "Contrato já encerrado."
            );
        }

        statusProposta = StatusProposta.ENCERRADO;
        dataEncerramento = LocalDate.now();
    }

    public void estenderContrato(LocalDate novaDataFim) {
        if (!novaDataFim.isAfter(dataFim)) {
            throw new IllegalArgumentException(
                    "A nova data deve ser posterior à data atual."
            );
        }

        dataFim = novaDataFim;
        statusProposta = StatusProposta.ALTERADO;
    }


    public long getId() {
        return id;
    }
    public Contratante getContratante() {
        return contratante;
    }
    public String getNomePeca() {
        return nomePeca;
    }
    public double getValorIngresso() {
        return valorIngresso;
    }
    public double getValorAluguel() {
        return valorAluguel;
    }
    public LocalDate getDataInicio() {
        return dataInicio;
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
    public LocalTime getHorarioFim() {
        return horarioFim;
    }
    public StatusProposta getStatusProposta() {
        return statusProposta;
    }
    public LocalDate getDataEncerramento() {
        return dataEncerramento;
    }
    public void setStatusProposta(StatusProposta statusProposta) {
        this.statusProposta = statusProposta;
    }

    public void setValorIngresso(double valorIngresso) {
        this.valorIngresso = valorIngresso;
    }
    public void setValorAluguel(double valorAluguel) {
        this.valorAluguel = valorAluguel;
    }


    @Override
    public String toString() {
        return "PropostaAluguel{" +
                "id=" + id +
                ", contratante=" + contratante.getNome() +
                ", nomePeca='" + nomePeca + '\'' +
                ", status=" + statusProposta +
                '}';
    }

}