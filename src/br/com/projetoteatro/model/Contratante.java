package br.com.projetoteatro.model;

public class Contratante extends Pessoa {
 private Pessoa pessoa;
 private long id;
     private String senha;

    public Contratante(String nome) {
        this.setNome(nome);
    }

    public Contratante(String nome, String email, String telefone,String cpf) {
     super(nome,email,telefone,cpf);
     this.id = System.currentTimeMillis();
    }

 public Contratante(String nome, String email, String telefone, String cpf,String senha) {
  super(nome, email, telefone, cpf);
  this.id = System.currentTimeMillis();
  this.senha=senha;
 }

 public void setPessoa(Pessoa pessoa) {
  this.pessoa = pessoa;
 }

 public void setId(long id) {
  this.id = id;
 }

 public void setSenha(String senha) {
  this.senha = senha;
 }

 public Pessoa getPessoa() {
  return pessoa;
 }

 public long getId() {
  return id;
 }

 public String getSenha() {
  return senha;
 }
}
