package br.com.projetoteatro.service;

import br.com.projetoteatro.exceptions.CPFInvalidoException;
import br.com.projetoteatro.exceptions.ContratanteInvalidoException;
import br.com.projetoteatro.exceptions.LoginInvalidoException;
import br.com.projetoteatro.model.Administrador;
import br.com.projetoteatro.model.Contratante;
import br.com.projetoteatro.model.Pessoa;
import br.com.projetoteatro.model.Usuario;
import br.com.projetoteatro.repository.AdministradorRepository;
import br.com.projetoteatro.repository.ArtistaRepository;
import br.com.projetoteatro.repository.ClienteRepository;

import java.util.HashMap;
import java.util.Map;

public class LoginService {

    private Map<String, String> codigosRecuperacaoSenha = new HashMap<>();

    private ClienteRepository clienteRepository;
    private AdministradorRepository admRepository;
    private ArtistaRepository artistaRepository;

    public LoginService(
            AdministradorRepository admRepository,
            ClienteRepository clienteRepository,
            ArtistaRepository artistaRepository) {

        this.clienteRepository = clienteRepository;
        this.admRepository = admRepository;
        this.artistaRepository = artistaRepository;
    }

    public Pessoa autenticar(String email, String senha)
            throws LoginInvalidoException {

        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new LoginInvalidoException("Preencha o e-mail e a senha!");
        }

        String emailLimpo = email.trim();

        Contratante artista =
                artistaRepository.buscarContratanteEmail(emailLimpo);

        if (artista != null) {
            if (artista.getSenha() != null && artista.getSenha().equals(senha)) {
                return artista;
            }

            throw new LoginInvalidoException("Senha inválida!");
        }

        Administrador adm =
                admRepository.buscarAdmEmail(emailLimpo);

        if (adm != null) {
            if (adm.getSenha() != null && adm.getSenha().equals(senha)) {
                return adm;
            }

            throw new LoginInvalidoException("Senha inválida!");
        }

        Usuario cliente =
                clienteRepository.buscarClienteEmail(emailLimpo);

        if (cliente != null) {
            if (cliente.getSenha() != null && cliente.getSenha().equals(senha)) {
                return cliente;
            }

            throw new LoginInvalidoException("Senha inválida!");
        }

        throw new LoginInvalidoException("Usuário não encontrado!");
    }

    private Pessoa buscarPessoa(String cpf) {

        Usuario cliente =
                clienteRepository.buscarCliente(cpf);

        if (cliente != null) {
            return cliente;
        }

        Contratante artista =
                artistaRepository.buscarContratante(cpf);

        if (artista != null) {
            return artista;
        }

        Administrador adm =
                admRepository.buscarAdm(cpf);

        if (adm != null) {
            return adm;
        }

        return null;
    }

    public void solicitarMudancaSenha(String cpf)
            throws CPFInvalidoException, ContratanteInvalidoException {

        Pessoa pessoa = buscarPessoa(cpf);

        if (pessoa == null) {
            throw new CPFInvalidoException("CPF não cadastrado...");
        }

        String codigo =
                String.valueOf((int) (Math.random() * 10000));

        codigosRecuperacaoSenha.put(cpf, codigo);

        EmailService.enviarEmailCodigoSenha(
                pessoa.getEmail(),
                "Mudança de SENHA",
                "Segue o código validador para mudança de senha "
                        + codigo
        );

        System.out.println("Código gerado: " + codigo);
        System.out.println("Email destino: " + pessoa.getEmail());
        System.out.println("CPF: " + pessoa.getCpf());
    }

    public void redefinirSenha(
            String cpf,
            String codigo,
            String novaSenha)
            throws CPFInvalidoException, ContratanteInvalidoException {

        Pessoa pessoa = buscarPessoa(cpf);

        if (pessoa == null) {
            throw new CPFInvalidoException("CPF não cadastrado...");
        }

        String codigoGuardado =
                codigosRecuperacaoSenha.get(cpf);

        if (codigoGuardado == null ||
                !codigoGuardado.equals(codigo)) {

            throw new IllegalArgumentException("Código inválido");
        }

        pessoa.setSenha(novaSenha);

        if (pessoa instanceof Administrador) {
            admRepository.salvarOuAtualizar((Administrador) pessoa);
        } else if (pessoa instanceof Contratante) {
            artistaRepository.atualizar((Contratante) pessoa);
        } else if (pessoa instanceof Usuario) {
            clienteRepository.atualizar((Usuario) pessoa);
        }

        codigosRecuperacaoSenha.remove(cpf);
    }
}