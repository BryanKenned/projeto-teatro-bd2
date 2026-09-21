package br.com.projetoteatro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name = "administrador")
public class Administrador extends Pessoa {

    public Administrador() {
        super();
    }

    public Administrador(String email, String senha) {
        super(email);
        setSenha(senha);
    }

    public Administrador(String nome, String email, String senha) {
        super(nome, email);
        setSenha(senha);
    }

    public Administrador(String nome, String email, String telefone, String cpf, String senha) {
        super(nome, email, telefone, cpf);
        setSenha(senha);
    }
}