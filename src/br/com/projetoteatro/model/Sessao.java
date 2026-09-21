package br.com.projetoteatro.model;

import br.com.projetoteatro.enums.Turno;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate data;

    private String nomePeca;

    private LocalTime horarioInicio;

    private LocalTime horarioFim;

    @Enumerated(EnumType.STRING)
    private Turno turno;

    @ManyToOne
    private Peca peca;

    @OneToMany(mappedBy = "sessao", cascade = CascadeType.ALL)
    private List<Ingresso> ingressos = new ArrayList<>();

    public Sessao() {
    }

    public Sessao(String nomePeca, LocalTime horarioInicio) {
        this.nomePeca = nomePeca;
        this.horarioInicio = horarioInicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getNomePeca() {
        return nomePeca;
    }

    public void setNomePeca(String nomePeca) {
        this.nomePeca = nomePeca;
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

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public Peca getPeca() {
        return peca;
    }

    public void setPeca(Peca peca) {
        this.peca = peca;
    }

    public List<Ingresso> getIngressos() {
        return ingressos;
    }

    public void setIngressos(List<Ingresso> ingressos) {
        this.ingressos = ingressos;
    }

    public void adicionarIngresso(Ingresso ingresso) {
        ingressos.add(ingresso);
        if (ingresso != null) {
            ingresso.setSessao(this);
        }
    }

    public void removerIngresso(Ingresso ingresso) {
        ingressos.remove(ingresso);
    }

    public boolean conflitaCom(Sessao outra) {

        if (!this.data.equals(outra.data)) {
            return false;
        }

        return this.horarioInicio.isBefore(outra.horarioFim)
                && this.horarioFim.isAfter(outra.horarioInicio);
    }

    public boolean estaDentroDoTurno() {

        switch (turno) {

            case MANHA:
                return !horarioInicio.isBefore(LocalTime.of(8, 0))
                        && !horarioFim.isAfter(LocalTime.of(12, 0));

            case TARDE:
                return !horarioInicio.isBefore(LocalTime.of(13, 0))
                        && !horarioFim.isAfter(LocalTime.of(18, 0));

            case NOITE:
                return !horarioInicio.isBefore(LocalTime.of(19, 0))
                        && !horarioFim.isAfter(LocalTime.of(23, 0));

            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "Sessão: " + this.turno;
    }
}