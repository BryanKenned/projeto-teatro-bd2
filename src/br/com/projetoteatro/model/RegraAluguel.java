package br.com.projetoteatro.model;

import br.com.projetoteatro.enums.DiasDaSemana;
import br.com.projetoteatro.enums.Meses;
import br.com.projetoteatro.enums.Turno;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalTime;


@Entity
public class RegraAluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double valorHora;

    @Enumerated(EnumType.STRING)
    private DiasDaSemana diaDaSemana;

    @Enumerated(EnumType.STRING)
    private Turno turno;

    @Enumerated(EnumType.STRING)
    private Meses mes;

    private LocalTime horarioComeco;
    private LocalTime horarioFim;

    public RegraAluguel() {
    }

    public RegraAluguel(Double valorHora, DiasDaSemana diaDaSemana, Turno turno, Meses mes, LocalTime horarioComeco,
            LocalTime horarioFim) {
        this.valorHora = valorHora;
        this.diaDaSemana = diaDaSemana;
        this.turno = turno;
        this.mes = mes;
        this.horarioComeco = horarioComeco;
        this.horarioFim = horarioFim;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {

        this.id = id;
    }

    public Meses getMes() {

        return mes;
    }

    public void setMes(Meses mes) {

        this.mes = mes;
    }

    public Turno getTurno() {

        return turno;
    }

    public void setTurno(Turno turno) {

        this.turno = turno;
    }

    public DiasDaSemana getDiaDaSemana() {

        return diaDaSemana;
    }

    public void setDiaDaSemana(DiasDaSemana diaDaSemana) {

        this.diaDaSemana = diaDaSemana;
    }

    public Double getValorHora() {

        return valorHora;
    }

    public void setValorHora(Double valorHora) {

        this.valorHora = valorHora;
    }

    public LocalTime getHorarioFim() {

        return horarioFim;
    }

    public LocalTime getHorarioComeco() {

        return horarioComeco;
    }

    public void setHorarioComeco(LocalTime horarioComeco) {
        this.horarioComeco = horarioComeco;
    }

    public void setHorarioFim(LocalTime horarioFim) {
        this.horarioFim = horarioFim;
    }

    public String toString() {
        return "A proposta de id: " + getId() + " tem valor da hora R$ " + getValorHora() + "no mês de " + " nsa "
                + getDiaDaSemana() + " no turno da " + getTurno();
    }
}
