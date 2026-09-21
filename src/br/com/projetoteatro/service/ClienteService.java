package br.com.projetoteatro.service;

import br.com.projetoteatro.exceptions.CPFInvalidoException;
import br.com.projetoteatro.exceptions.EmailInvalidoException;
import br.com.projetoteatro.model.Usuario;
import br.com.projetoteatro.repository.ClienteRepository;
import br.com.projetoteatro.service.validators.ValidadorCPF;
import br.com.projetoteatro.service.validators.ValidadorEmail;

import java.util.List;

public class ClienteService {

    private ClienteRepository clienteRepo;

    public ClienteService(ClienteRepository clienteRepo) {
        if (clienteRepo == null) {
            throw new IllegalArgumentException(
                    "O Repositório de Cliente não pode ser nulo!");
        }

        this.clienteRepo = clienteRepo;
    }

    public void cadastrarCliente(Usuario cliente)
            throws CPFInvalidoException,
            EmailInvalidoException {

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "O cliente não pode ser nulo!");
        }

        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "O nome do cliente é obrigatório!");
        }

        if (!ValidadorCPF.isValido(cliente.getCpf())) {
            throw new CPFInvalidoException(
                    "CPF inválido!");
        }

        ValidadorEmail.validarEmail(cliente.getEmail());

        if (clienteRepo.buscarCliente(cliente.getCpf()) != null) {
            throw new IllegalArgumentException(
                    "CPF já cadastrado!");
        }

        if (clienteRepo.buscarClienteEmail(cliente.getEmail()) != null) {
            throw new IllegalArgumentException(
                    "E-mail já cadastrado!");
        }

        clienteRepo.adicionarCliente(cliente);
    }

    public Usuario buscarPorCpf(String cpf) {
        return clienteRepo.buscarCliente(cpf);
    }

    public Usuario buscarPorEmail(String email) {
        return clienteRepo.buscarClienteEmail(email);
    }

    public List<Usuario> listarClientes() {
        return clienteRepo.listarCliente();
    }

    public void atualizarCliente(Usuario cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException(
                    "O cliente não pode ser nulo!");
        }

        clienteRepo.atualizar(cliente);
    }

    public void removerCliente(Usuario cliente) {
        if (cliente == null) {
            return;
        }

        clienteRepo.removerCliente(cliente);
    }
}