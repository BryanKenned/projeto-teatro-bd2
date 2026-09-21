package br.com.projetoteatro.dto;

import br.com.projetoteatro.enums.TipoSetor;

import java.time.LocalDate;
import java.time.LocalTime;

public class IngressoResumoDTO {

    private final String codigoIngresso;
    private final String nomeCliente;
    private final String emailCliente;
    private final String nomePeca;
    private final LocalDate dataSessao;
    private final LocalTime horarioInicio;
    private final String codigoAssento;
    private final TipoSetor tipoSetor;
    private final double valor;

    public IngressoResumoDTO(
            String codigoIngresso,
            String nomeCliente,
            String emailCliente,
            String nomePeca,
            LocalDate dataSessao,
            LocalTime horarioInicio,
            String codigoAssento,
            TipoSetor tipoSetor,
            double valor) {

        this.codigoIngresso = codigoIngresso;
        this.nomeCliente = nomeCliente != null ? nomeCliente : "Não Identificado";
        this.emailCliente = emailCliente != null ? emailCliente : "N/A";
        this.nomePeca = nomePeca;
        this.dataSessao = dataSessao;
        this.horarioInicio = horarioInicio;
        this.codigoAssento = codigoAssento != null ? codigoAssento : "Livre";
        this.tipoSetor = tipoSetor;
        this.valor = valor;
    }

    public String getCodigoIngresso() {
        return codigoIngresso;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public String getNomePeca() {
        return nomePeca;
    }

    public LocalDate getDataSessao() {
        return dataSessao;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public String getCodigoAssento() {
        return codigoAssento;
    }

    public TipoSetor getTipoSetor() {
        return tipoSetor;
    }

    public double getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return String.format(
                "IngressoResumoDTO [Código: %s | Cliente: %s | Peça: %s | Data: %s %s | Assento: %s (%s) | Valor: R$ %.2f]",
                codigoIngresso, nomeCliente, nomePeca, dataSessao, horarioInicio, codigoAssento, tipoSetor, valor
        );
    }
}
