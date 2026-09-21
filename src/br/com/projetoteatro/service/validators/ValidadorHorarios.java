package br.com.projetoteatro.service.validators;

import br.com.projetoteatro.exceptions.ConflitoHorarioException;
import br.com.projetoteatro.model.PropostaAluguel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class ValidadorHorarios {

    public static boolean isHorarioDentroDeTurno(LocalTime inicio, LocalTime fim) {
        boolean manha = !inicio.isBefore(LocalTime.of(8, 0)) && !fim.isAfter(LocalTime.of(12, 0));
        boolean tarde = !inicio.isBefore(LocalTime.of(13, 0)) && !fim.isAfter(LocalTime.of(18, 0));
        boolean noite = !inicio.isBefore(LocalTime.of(19, 0)) && !fim.isAfter(LocalTime.of(23, 0));

        return (manha || tarde || noite);
    }

    public void duracaoPecaPorPeriodo(LocalTime horarioInicio, LocalTime horarioFim) throws ConflitoHorarioException {

        boolean manha = !horarioInicio.isBefore(LocalTime.of(8, 0)) && !horarioFim.isAfter(LocalTime.of(12, 0));
        boolean tarde = !horarioInicio.isBefore(LocalTime.of(13, 0)) && !horarioFim.isAfter(LocalTime.of(18, 0));
        boolean noite = !horarioInicio.isBefore(LocalTime.of(19, 0)) && !horarioFim.isAfter(LocalTime.of(23, 0));

        if (!(manha || tarde || noite)) {
            throw new ConflitoHorarioException("A peça tem que começar e terminar no mesmo turno....");
        }

    }

    public void validarConflitoHorario(LocalDate dataInicio, LocalDate dataFim, LocalTime horaInicio, LocalTime horaFinal, ArrayList<PropostaAluguel> listaPropostas)throws ConflitoHorarioException{

        for(PropostaAluguel atual: listaPropostas){
            boolean choqueDia=!dataFim.isBefore(atual.getDataInicio()) &&  !dataInicio.isAfter(atual.getDataFim());
            boolean choqueHorio= horaInicio.isBefore(atual.getHorarioFim()) &&  horaFinal.isAfter(atual.getHorarioInicio());

            if(choqueDia&&choqueHorio){
                throw new ConflitoHorarioException("Conflito de horário, já existe uma peça nesse horário...");
            }

        }
    }

}
