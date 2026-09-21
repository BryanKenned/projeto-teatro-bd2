package br.com.projetoteatro.service.validators;

import br.com.projetoteatro.exceptions.EmailInvalidoException;

public class ValidadorEmail {

    public static boolean validarEmail(String email) throws EmailInvalidoException {
        if (email == null) {
            throw new EmailInvalidoException("Digite um email válido no campo");
        }
        if (email.isBlank()) {
            throw new EmailInvalidoException("O campo não pode estar vazio!");
        }

        int contadorArroba = 0;
        for (char c : email.toCharArray()) {
            if (c == '@') contadorArroba++;
        }
        if (contadorArroba != 1) {
            throw new EmailInvalidoException("O e-mail deve conter exatamente um '@'");
        }

        String caracteresProibidos = "!#$%^&*()+=[]{}|\\;:'\",<>/?";
        for (char c : email.toCharArray()) {
            if (caracteresProibidos.indexOf(c) != -1) {
                throw new EmailInvalidoException("O e-mail contém caracteres inválidos!");
            }
        }

        if (email.startsWith("@")) throw new EmailInvalidoException("Um e-mail não pode iniciar com @");
        if (email.endsWith("@")) throw new EmailInvalidoException("Um e-mail não pode terminar com @");

        int posicaoArroba = email.indexOf('@');
        String antesArroba = email.substring(0, posicaoArroba);
        String depoisArroba = email.substring(posicaoArroba + 1);

        if (antesArroba.contains(" ")) {
            throw new EmailInvalidoException("O e-mail não pode conter espaços");
        }
        if (antesArroba.isEmpty()) {
            throw new EmailInvalidoException("Deve haver caracteres antes do @");
        }

        if (!depoisArroba.contains(".")) {
            throw new EmailInvalidoException("O domínio deve conter um ponto (.)");
        }
        if (depoisArroba.startsWith(".")) {
            throw new EmailInvalidoException("O domínio não pode iniciar com ponto");
        }
        if (depoisArroba.endsWith(".")) {
            throw new EmailInvalidoException("O domínio não pode terminar com ponto");
        }
        if (depoisArroba.contains(" ")) {
            throw new EmailInvalidoException("O e-mail não pode conter espaços");
        }

        return true;
    }

}
