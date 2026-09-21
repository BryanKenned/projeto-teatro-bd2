package br.com.projetoteatro.service;

import br.com.projetoteatro.model.Sessao;
import br.com.projetoteatro.repository.SessaoRepository;

import java.util.List;

public class SessaoService {

    private final SessaoRepository sessaoRepository;

    public SessaoService(SessaoRepository sessaoRepository) {
        if (sessaoRepository == null) {
            throw new IllegalArgumentException("O repositório de sessão não pode ser nulo.");
        }
        this.sessaoRepository = sessaoRepository;
    }

    public void adicionarSessao(Sessao sessao) {
        if (sessao == null) {
            throw new IllegalArgumentException("A sessão não pode ser nula.");
        }
        sessaoRepository.adicionarSessao(sessao);
    }

    public void cadastrarSessaoCompleta(Sessao sessao) {
        if (sessao == null) {
            throw new IllegalArgumentException("A sessão não pode ser nula.");
        }
        if (sessao.getData() == null) {
            throw new IllegalArgumentException("A data da sessão é obrigatória.");
        }
        if (sessao.getHorarioInicio() == null || sessao.getHorarioFim() == null) {
            throw new IllegalArgumentException("Os horários de início e término são obrigatórios.");
        }
        if (!sessao.getHorarioInicio().isBefore(sessao.getHorarioFim())) {
            throw new IllegalArgumentException("O horário de início deve ser anterior ao horário de término.");
        }

        if (sessao.getTurno() != null && !sessao.estaDentroDoTurno()) {
            throw new IllegalArgumentException(
                    "O horário da sessão está fora dos limites permitidos para o turno " + sessao.getTurno() + ".");
        }

        List<Sessao> sessoesExistentes = sessaoRepository.listarSessao();
        for (Sessao s : sessoesExistentes) {
            if (s.getId() != null && s.getId().equals(sessao.getId())) {
                continue;
            }
            if (s.conflitaCom(sessao)) {
                throw new IllegalArgumentException("Conflito de agenda: já existe sessão agendada (" +
                        s.getNomePeca() + ") no mesmo período (" + s.getHorarioInicio() + " às " + s.getHorarioFim()
                        + ").");
            }
        }

        sessaoRepository.adicionarSessao(sessao);
    }

    public List<Sessao> listarSessao() {
        return sessaoRepository.listarSessao();
    }

    public Sessao buscarSessao(long id) {
        return sessaoRepository.buscarSessao(id);
    }

    public void atualizar(Sessao sessao) {
        if (sessao == null) {
            throw new IllegalArgumentException("A sessão não pode ser nula.");
        }
        sessaoRepository.atualizar(sessao);
    }

    public void removerSessao(Sessao sessao) {
        if (sessao == null) {
            return;
        }
        sessaoRepository.removerSessao(sessao);
    }

    public List<Sessao> buscarPorPeca(String nomePeca) {
        return sessaoRepository.buscarPorPeca(nomePeca);
    }

    public SessaoRepository getSessaoRepository() {
        return sessaoRepository;
    }
}
