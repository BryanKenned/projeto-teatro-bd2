package br.com.projetoteatro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name = "contratante")
public class Contratante extends Pessoa {

    public Contratante() {
        super();
    }

    public Contratante(String nome) {
        super();
        setNome(nome);
    }

    public Contratante(String nome, String email, String telefone, String cpf) {
        super(nome, email, telefone, cpf);
    }

    public Contratante(String nome, String email, String telefone, String cpf, String senha) {
        super(nome, email, telefone, cpf);
        setSenha(senha);
    }
}