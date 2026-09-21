package br.com.projetoteatro.service;

import br.com.projetoteatro.exceptions.AdiministradorInvalidoException;
import br.com.projetoteatro.exceptions.EmailInvalidoException;
import br.com.projetoteatro.model.Administrador;
import br.com.projetoteatro.repository.AdministradorRepository;
import br.com.projetoteatro.service.validators.ValidadorCPF;
import br.com.projetoteatro.service.validators.ValidadorEmail;

public class AdministradorService {

    private AdministradorRepository admRepo;

    public AdministradorService(AdministradorRepository admRepo) {

        if (admRepo == null) {
            throw new IllegalArgumentException(
                    "O Repositório de Administrador não pode ser nulo!"
            );
        }

        this.admRepo = admRepo;
    }

    public void cadastrarAdministrador(Administrador a)
            throws AdiministradorInvalidoException,
            EmailInvalidoException {

        if (a == null) {
            throw new AdiministradorInvalidoException("O administrador não pode ser nulo!");
        }

        if (a.getNome() == null || a.getNome().isBlank()) {
            throw new AdiministradorInvalidoException("O nome do administrador é obrigatório!");
        }

        if (a.getSenha() == null || a.getSenha().isBlank()) {
            throw new AdiministradorInvalidoException("A senha é obrigatória!");
        }

        ValidadorEmail.validarEmail(a.getEmail());

        if (!ValidadorCPF.isValido(a.getCpf())) {
            throw new AdiministradorInvalidoException(
                    "CPF inválido! Verifique os números."
            );
        }

        if (admRepo.buscarAdmEmail(a.getEmail()) != null) {
            throw new AdiministradorInvalidoException(
                    "E-mail já cadastrado!"
            );
        }

        if (admRepo.buscarAdm(a.getCpf()) != null) {
            throw new AdiministradorInvalidoException(
                    "CPF já cadastrado!"
            );
        }

        admRepo.adicionarAdm(a);
    }

    public boolean jaExisteAdm() {

        return admRepo.verSeAdmEstaPreenchido();
    }

    public void atualizarSenha(String email, String novaSenha) {

        Administrador adm = admRepo.buscarAdmEmail(email);

        if (adm != null) {

            adm.setSenha(novaSenha);

            admRepo.salvarOuAtualizar(adm);
        }
    }
}