package br.com.projetoteatro.service;

import br.com.projetoteatro.model.Usuario;
import br.com.projetoteatro.repository.UsuarioRepository;

public class UsuarioService {

    private UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository repo) {
        this.usuarioRepository = repo;
    }

    public Usuario buscarPorCpf(String cpf) {
        return usuarioRepository.buscarPorCpf(cpf);
    }

    public void adicionarUsuario(Usuario u) {
        usuarioRepository.adicionar(u);
    }
}