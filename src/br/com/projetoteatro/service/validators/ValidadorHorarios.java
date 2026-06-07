package br.com.projetoteatro.service.validators;

import br.com.projetoteatro.exceptions.ConflitoHorarioException;
import br.com.projetoteatro.model.PropostaAluguel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class ValidadorHorarios {

    public void duracaoPecaPorPeriodo(LocalTime horarioInico, LocalTime horarioFim) throws ConflitoHorarioException{
        //vi no Brocode, ver se dá certo
        //manha apartir das 8
        if(!horarioInico.isAfter(LocalTime.of(8,0))&&(!horarioFim.isBefore(LocalTime.of(12,00)))){
            //return true;
        }
        //periodo da tarde
        if(!horarioInico.isAfter(LocalTime.of(13,0))&&(!horarioFim.isBefore(LocalTime.of(18,00)))){
            // return true;
        }
        if(!horarioInico.isAfter(LocalTime.of(17,59))&&(!horarioFim.isBefore(LocalTime.of(23,59)))){
            // return true;
        }
        else
            throw new ConflitoHorarioException("A peça tem que começar e terminar no mesmo turno....");
        //return false;
    }
    public void validarConflitoHorario(LocalDate dataInicio, LocalDate dataFim, LocalTime horaInicio, LocalTime horaFinal, ArrayList<PropostaAluguel> listaPropostas)throws ConflitoHorarioException{

        for(PropostaAluguel atual: listaPropostas){
            if(atual.getDataInicio().equals(dataInicio)){
                if(horaInicio.isBefore(atual.getHorarioFim())&&horaFinal.isAfter(atual.getHorarioInicio())){
                    throw new ConflitoHorarioException("Conflito de horário, já existe uma peça nesse horário...");
                }
            }

}}}
