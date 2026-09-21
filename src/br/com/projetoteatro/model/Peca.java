package br.com.projetoteatro.model;

import br.com.projetoteatro.enums.StatusProposta;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Peca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @ManyToOne
    private Contratante artistaResponsavel;

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private double precoIngresso;

    @Enumerated(EnumType.STRING)
    private StatusProposta status;

    @OneToMany(mappedBy = "peca", cascade = CascadeType.ALL)
    private List<Sessao> sessoes = new ArrayList<>();

    private double valorAluguel;

    public Peca() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Contratante getArtistaResponsavel() {
        return artistaResponsavel;
    }

    public void setArtistaResponsavel(Contratante artistaResponsavel) {
        this.artistaResponsavel = artistaResponsavel;
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

    public double getPrecoIngresso() {
        return precoIngresso;
    }

    public void setPrecoIngresso(double precoIngresso) {
        this.precoIngresso = precoIngresso;
    }

    public StatusProposta getStatus() {
        return status;
    }

    public void setStatus(StatusProposta status) {
        this.status = status;
    }

    public List<Sessao> getSessoes() {
        return sessoes;
    }

    public void setSessoes(List<Sessao> sessoes) {
        this.sessoes = sessoes;
    }

    public double getValorAluguel() {
        return valorAluguel;
    }

    public void setValorAluguel(double valorAluguel) {
        this.valorAluguel = valorAluguel;
    }

    public void adicionarSessao(Sessao sessao) {

        for (Sessao s : sessoes) {

            if (s.conflitaCom(sessao)) {
                throw new IllegalArgumentException(
                        "Conflito de horário encontrado.");
            }
        }

        sessoes.add(sessao);
        sessao.setPeca(this);
    }

    public void removerSessao(Sessao sessao) {
        sessoes.remove(sessao);
        sessao.setPeca(null);
    }

    public boolean estaEncerrada() {
        return status == StatusProposta.ENCERRADO;
    }

    public void encerrarContrato() {
        status = StatusProposta.ENCERRADO;
    }

    public void estenderContrato(LocalDate novaData) {

        if (novaData.isAfter(dataFim)) {
            dataFim = novaData;
            status = StatusProposta.ALTERADO;
        }
    }

    public double calcularArrecadacao() {

        double total = 0;

        for (Sessao sessao : sessoes) {

            for (Ingresso ingresso : sessao.getIngressos()) {
                total += ingresso.getValor();
            }
        }

        return total;
    }

    @Override
    public String toString() {
        return "Peça: " + nome;
    }
}