package br.com.projetoteatro.model;

import br.com.projetoteatro.enums.TipoSetor;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Setor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoSetor tipoSetor;

    private double valor;

    private int capacidade;

    @OneToMany(mappedBy = "setor", cascade = CascadeType.ALL)
    private List<Assento> assentos = new ArrayList<>();

    public Setor() {
    }

    public Setor(TipoSetor setor, double valor, int capacidade) {
        this.tipoSetor = setor;
        this.valor = valor;
        this.capacidade = capacidade;
        this.assentos = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public TipoSetor getSetor() {
        return tipoSetor;
    }

    public void setSetor(TipoSetor setor) {
        this.tipoSetor = setor;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public List<Assento> getAssentos() {
        return assentos;
    }

    public void setAssentos(List<Assento> assentos) {
        this.assentos = assentos;
    }

    public void adicionarAssento(Assento assento) {
        assentos.add(assento);
        assento.setSetor(this);
    }

    public void removerAssento(Assento assento) {
        assentos.remove(assento);
        assento.setSetor(null);
    }

    @Override
    public String toString() {
        return this.tipoSetor.toString();
    }
}