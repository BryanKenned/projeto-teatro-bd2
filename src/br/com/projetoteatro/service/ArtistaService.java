package br.com.projetoteatro.service;

import br.com.projetoteatro.exceptions.CPFInvalidoException;
import br.com.projetoteatro.exceptions.ContratanteInvalidoException;
import br.com.projetoteatro.exceptions.EmailInvalidoException;
import br.com.projetoteatro.model.Contratante;
import br.com.projetoteatro.repository.ArtistaRepository;
import br.com.projetoteatro.service.validators.ValidadorCPF;
import br.com.projetoteatro.service.validators.ValidadorEmail;

public class ArtistaService {

    private ArtistaRepository artistaRepo;

    public ArtistaService(ArtistaRepository artistaRepo) {

        if (artistaRepo == null) {
            throw new IllegalArgumentException(
                    "O Repositório de Artista não pode ser nulo!"
            );
        }

        this.artistaRepo = artistaRepo;
    }

    public void cadastrarArtista(Contratante artista)
            throws CPFInvalidoException,
            ContratanteInvalidoException,
            EmailInvalidoException {

        if (artista == null) {
            throw new ContratanteInvalidoException(
                    "O artista não pode ser nulo!"
            );
        }

        if (!ValidadorCPF.isValido(artista.getCpf())) {
            throw new CPFInvalidoException(
                    "CPF inválido!"
            );
        }

        ValidadorEmail.validarEmail(artista.getEmail());

        if (artistaRepo.buscarContratante(artista.getCpf()) != null) {
            throw new ContratanteInvalidoException(
                    "CPF já cadastrado!"
            );
        }

        if (artistaRepo.buscarContratanteEmail(artista.getEmail()) != null) {
            throw new ContratanteInvalidoException(
                    "E-mail já cadastrado!"
            );
        }

        artistaRepo.adicionarArtista(artista);
    }

    public Contratante buscarPorCpf(String cpf) {

        return artistaRepo.buscarContratante(cpf);
    }

    public Contratante buscarPorEmail(String email) {

        return artistaRepo.buscarContratanteEmail(email);
    }

    public void excluirArtista(Contratante artista)
            throws CPFInvalidoException,
            ContratanteInvalidoException {

        if (artista == null) {
            throw new ContratanteInvalidoException(
                    "O artista não pode ser nulo!"
            );
        }

        artistaRepo.excluirArtista(artista);
    }
}