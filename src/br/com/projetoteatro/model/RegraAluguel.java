package br.com.projetoteatro.model;

import br.com.projetoteatro.enums.DiasDaSemana;
import br.com.projetoteatro.enums.Meses;
import br.com.projetoteatro.enums.Turno;

import java.time.LocalTime;

public class RegraAluguel {
    private long id;
    private Double valorHora;
    private DiasDaSemana diaDaSemana;
    private Turno turno;
    private Meses mes;
    private LocalTime horarioComeco;
    private LocalTime horarioFim;

    public RegraAluguel(Double valorHora, DiasDaSemana diaDaSemana, Turno turno,Meses mes,LocalTime horarioComeco,LocalTime horarioFim ){
        this.id=System.currentTimeMillis();
        this.valorHora=valorHora;
        this.diaDaSemana=diaDaSemana;
        this.turno=turno;
        this.mes=mes;
        this.horarioComeco=horarioComeco;
        this.horarioFim=horarioFim;

    }
    //Getter and Setter

    public long getId() {

        return id;
    }
    public void setId(long id) {

        this.id = id;
    }
    public Meses getMes() {

        return mes;
    }
    public void setData(Meses mes) {

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

    public String toString(){
        return "A proposta de id: "+getId()+" tem valor da hora R$ "+getValorHora()+"no mês de "+" nsa "+getDiaDaSemana()+" no turno da "+getTurno();
    }}
