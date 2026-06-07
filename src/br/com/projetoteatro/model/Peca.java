package br.com.projetoteatro.model;

import br.com.projetoteatro.enums.StatusProposta;

import java.time.LocalDate;
import java.util.List;

public class Peca {
    private long id;
    private String nome;
    private Contratante artistaResponsavel;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private double precoIngresso;
    private StatusProposta status;
    private List<Sessao> sessoes;
    private double valorAluguel;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void adicionarSessao(Sessao sessao){
        for (Sessao s : sessoes) {
            if (s.conflitaCom(sessao)) {
                throw new IllegalArgumentException(
                        "Conflito de horário encontrado."
                );
            }
        }
        sessoes.add(sessao);
    }

    public boolean estaEncerrada(){
        return status == StatusProposta.ENCERRADO;

    }

    public void encerrarContrato(){
        status = StatusProposta.ENCERRADO;

    }

    public void estenderContrato(LocalDate novaData) {
        if (novaData.isAfter(dataFim)) {
            dataFim = novaData;
            status = StatusProposta.ALTERADO;
        }
    }

    public double calcularArrecadacao(){
        double total = 0;

        for (Sessao sessao : sessoes) {

            for (Ingresso ingresso : sessao.getIngressos()) {
                total += ingresso.getValor();
            }
        }
        return total;
    }

}