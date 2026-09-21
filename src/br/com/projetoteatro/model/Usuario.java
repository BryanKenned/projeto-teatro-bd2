package br.com.projetoteatro.model;

import br.com.projetoteatro.enums.Genero;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;


@Entity
@Table(name = "usuario")
public class Usuario extends Pessoa {

    public Usuario() {
        super();
    }

    public Usuario(String nome, String cpf, String email) {
        super(nome, cpf, email);
    }

    public Usuario(String email, String senha) {
        super();
        setEmail(email);
        setSenha(senha);
    }

    public Usuario(String nome, String CPF, String email,
            Genero sexo, LocalDate dataNascimento,
            String telefone, String senha) {

        super(nome, CPF, email, sexo, dataNascimento, telefone);
        setSenha(senha);
    }

    public Usuario(String nome, String email,
            String telefone, String cpf, String senha) {

        super(nome, email, telefone, cpf);
        setSenha(senha);
    }
}