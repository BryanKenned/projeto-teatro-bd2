package br.com.projetoteatro.service;

import br.com.projetoteatro.model.Peca;
import br.com.projetoteatro.repository.PecaRepository;

import java.util.List;


public class PecaService {

    private final PecaRepository pecaRepository;

    public PecaService(PecaRepository pecaRepository) {
        if (pecaRepository == null) {
            throw new IllegalArgumentException("O repositório de peças não pode ser nulo.");
        }
        this.pecaRepository = pecaRepository;
    }

    public void cadastrarPeca(Peca peca) {
        if (peca == null) {
            throw new IllegalArgumentException("A peça não pode ser nula.");
        }
        if (peca.getNome() == null || peca.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da peça é obrigatório.");
        }
        pecaRepository.adicionarPeca(peca);
    }

    public List<Peca> listarPecas() {
        return pecaRepository.listarPecas();
    }

    public Peca buscarPeca(long id) {
        return pecaRepository.buscarPeca(id);
    }

    public List<Peca> buscarPorNome(String nome) {
        return pecaRepository.buscarPorNome(nome);
    }

    public void atualizar(Peca peca) {
        if (peca == null) {
            throw new IllegalArgumentException("A peça não pode ser nula.");
        }
        pecaRepository.atualizar(peca);
    }

    public void removerPeca(Peca peca) {
        if (peca == null) {
            return;
        }
        pecaRepository.removerPeca(peca);
    }

    public Long contarTotalPecas() {
        return pecaRepository.contarTotalPecas();
    }

    public PecaRepository getPecaRepository() {
        return pecaRepository;
    }
}
