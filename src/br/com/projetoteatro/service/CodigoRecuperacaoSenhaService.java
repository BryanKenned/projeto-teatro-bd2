package br.com.projetoteatro.service;

public class CodigoRecuperacaoSenhaService {

    private String codigoGerado;
    private final EnviarEmailService emailService;

    private String gerarCodigoAleatorio() {
        return String.format("%06d", new java.util.Random().nextInt(1000000));
    }

    public CodigoRecuperacaoSenhaService(EnviarEmailService emailService) {
        this.emailService = emailService;
    }

    public void solicitarCodigo(String emailDestino) {
        this.codigoGerado = gerarCodigoAleatorio();
        emailService.enviarEmailCodigoSenha(emailDestino, "Seu código de recuperação",
                "Seu código é: " + this.codigoGerado);
    }

    public boolean validarCodigo(String codigoDigitado) {
        return codigoDigitado.equals(this.codigoGerado);
    }

}
