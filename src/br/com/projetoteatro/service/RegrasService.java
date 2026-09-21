package br.com.projetoteatro.service;

import br.com.projetoteatro.enums.DiasDaSemana;
import br.com.projetoteatro.enums.Meses;
import br.com.projetoteatro.enums.Turno;
import br.com.projetoteatro.exceptions.RegraInvalidaException;
import br.com.projetoteatro.model.PropostaAluguel;
import br.com.projetoteatro.model.RegraAluguel;
import br.com.projetoteatro.repository.RegrasPrecoRepository;

import java.util.ArrayList;

public class RegrasService {

    private RegrasPrecoRepository regraRepo;

    public RegrasService(RegrasPrecoRepository regraRepo) {
        this.regraRepo = regraRepo;
    }

    public ArrayList<RegraAluguel> getListaRegras() {
        return new ArrayList<>(regraRepo.listarRegras());
    }

    public void cadastrarRegra(RegraAluguel regra) {
        regraRepo.adicionarRegra(regra);
    }

    public RegraAluguel buscarRegra(long id)
            throws RegraInvalidaException {

        RegraAluguel regra = regraRepo.buscarRegra(id);

        if (regra == null) {
            throw new RegraInvalidaException(
                    "Não existe uma regra cadastrada com o id " + id
            );
        }

        return regra;
    }

    public void editarRegra(long id, Double novoValor)
            throws RegraInvalidaException {

        RegraAluguel regra = buscarRegra(id);

        regra.setValorHora(novoValor);

        regraRepo.atualizar(regra);
    }

    public boolean excluirRegra(long id)
            throws RegraInvalidaException {

        RegraAluguel regra = buscarRegra(id);

        regraRepo.removerRegra(regra);

        return true;
    }

    public Double calcularAluguel(PropostaAluguel p) {

        int horas =
                p.getHorarioFim().getHour()
                        - p.getHorarioInicio().getHour();
        if (horas <= 0) {
            horas = 1;
        }

        long dias = java.time.temporal.ChronoUnit.DAYS.between(
                p.getDataInicio(),
                p.getDataFim()
        ) + 1;
        if (dias <= 0) {
            dias = 1;
        }

        Double valorMaisAlto = 5.0;

        Turno t;

        if (p.getHorarioInicio().getHour() < 13) {
            t = Turno.MANHA;
        } else if (p.getHorarioInicio().getHour() < 19) {
            t = Turno.TARDE;
        } else {
            t = Turno.NOITE;
        }

        DiasDaSemana d = null;

        if (p.getDataInicio().getDayOfWeek().getValue() == 1) {
            d = DiasDaSemana.SEGUNDA;
        } else if (p.getDataInicio().getDayOfWeek().getValue() == 2) {
            d = DiasDaSemana.TERCA;
        } else if (p.getDataInicio().getDayOfWeek().getValue() == 3) {
            d = DiasDaSemana.QUARTA;
        } else if (p.getDataInicio().getDayOfWeek().getValue() == 4) {
            d = DiasDaSemana.QUINTA;
        } else if (p.getDataInicio().getDayOfWeek().getValue() == 5) {
            d = DiasDaSemana.SEXTA;
        } else if (p.getDataInicio().getDayOfWeek().getValue() == 6) {
            d = DiasDaSemana.SABADO;
        } else if (p.getDataInicio().getDayOfWeek().getValue() == 7) {
            d = DiasDaSemana.DOMINGO;
        }

        Meses mesProposta =
                Meses.values()[
                        p.getDataInicio().getMonthValue() - 1
                        ];

        for (RegraAluguel r : regraRepo.listarRegras()) {

            if ((r.getTurno() == null || r.getTurno() == t)
                    && (r.getDiaDaSemana() == null
                    || r.getDiaDaSemana() == d)
                    && (r.getMes() == null
                    || r.getMes() == mesProposta)) {

                if (r.getValorHora() > valorMaisAlto) {
                    valorMaisAlto = r.getValorHora();
                }
            }
        }

        return valorMaisAlto * horas * dias;
    }
}