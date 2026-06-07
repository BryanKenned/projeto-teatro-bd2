package br.com.projetoteatro.service;

import br.com.projetoteatro.enums.DiasDaSemana;
import br.com.projetoteatro.enums.Meses;
import br.com.projetoteatro.enums.Turno;
import br.com.projetoteatro.exceptions.RegraInvalidaException;
import br.com.projetoteatro.model.PropostaAluguel;
import br.com.projetoteatro.model.RegraAluguel;

import java.util.ArrayList;

public class RegrasService {

    private ArrayList<RegraAluguel> listaRegras;

    public RegrasService(){
        listaRegras=new ArrayList<>();
    }

    //listar Regras obs: acho que tem que sobrescrever tostring
    public ArrayList<RegraAluguel> getListaRegras() {
        return listaRegras;
    }

    //cadastrando regras
    public void cadastrarRegra(RegraAluguel regra){
        listaRegras.add(regra);
    }

    //buscar regra por id
    public RegraAluguel buscarRegra(long id) throws RegraInvalidaException {
        for(RegraAluguel x:listaRegras){
            if(x.getId()==id){
                return x;
            }
        }
        throw new RegraInvalidaException("Não existe uma regra cadsatrada com o id"+id);

    }
    //editar a regra de aluguel

    public void editarRegra(long id,Double novoValor) throws RegraInvalidaException {
        RegraAluguel regra=buscarRegra(id);
        regra.setValorHora(novoValor);
    }

    //excluir
    public boolean excluirRegra(long id) throws RegraInvalidaException {
        RegraAluguel regra=buscarRegra(id);
        return listaRegras.remove(regra);
    }
    //calcular aluguel
    public Double calcularAluguel(PropostaAluguel p){
        int horas=p.getHorarioFim().getHour()-p.getHorarioInicio().getHour();
        int dias=p.getDataFim().getDayOfMonth()-p.getDataInicio().getDayOfMonth()+1;
        Double valorMaisAlto=5.0;
        Turno t;
        if(p.getHorarioInicio().getHour()<13){
            t=Turno.MANHA;
        }if(p.getHorarioInicio().getHour()<19){
            t=Turno.TARDE;
        }else{
            t=Turno.NOITE;
        }
        DiasDaSemana d=null;
        if(p.getDataInicio().getDayOfWeek().getValue()==1){
            d=DiasDaSemana.SEGUNDA;
        }
        if(p.getDataInicio().getDayOfWeek().getValue()==2){
            d=DiasDaSemana.TERCA;
        }
        if(p.getDataInicio().getDayOfWeek().getValue()==3){
            d=DiasDaSemana.QUARTA;
        }
        if(p.getDataInicio().getDayOfWeek().getValue()==4){
            d=DiasDaSemana.QUINTA;
        }
        if(p.getDataInicio().getDayOfWeek().getValue()==5){
            d=DiasDaSemana.SEXTA;
        }
        if(p.getDataInicio().getDayOfWeek().getValue()==6){
            d=DiasDaSemana.SABADO;
        }
        if(p.getDataInicio().getDayOfWeek().getValue()==7){
            d=DiasDaSemana.DOMINGO;
        }
        //transformando mes da proposta em um mes do enum
        //ver se funciona como o indiano explicou
        Meses mesProposta=Meses.values()[p.getDataInicio().getMonthValue()-1];
        for(RegraAluguel r: listaRegras ){
            if(((r.getTurno() == null || r.getTurno() == t)&& (r.getDiaDaSemana() == null || r.getDiaDaSemana() == d)&& (r.getMes() == null || r.getMes() == mesProposta))){

                if(r.getValorHora()>valorMaisAlto){
                    valorMaisAlto=r.getValorHora();
                }
            }
        }
        return (valorMaisAlto*horas*dias);

    }
}
