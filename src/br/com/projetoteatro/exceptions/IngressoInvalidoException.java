package br.com.projetoteatro.exceptions;

public class IngressoInvalidoException extends RuntimeException {
    public IngressoInvalidoException(String message) {
        super(message);
    }
}
