package br.com.projetoteatro.service.validators;

public class ValidadorCPF {

    public static boolean isValido(String cpf) {
        if (cpf == null) return false;

        String cpfLimpo = cpf.replaceAll("[^0-9]", "");

        if (cpfLimpo.length() != 11 || cpfLimpo.matches("(\\d)\\1{10}")) {
            return false;
        }

        int[] digitos = new int[11];
        for (int i = 0; i < 11; i++) {
            digitos[i] = Character.getNumericValue(cpfLimpo.charAt(i));
        }

        int soma1 = 0;
        for (int i = 0; i < 9; i++) {
            soma1 += digitos[i] * (10 - i);
        }
        int digito1 = (soma1 % 11 < 2) ? 0 : 11 - (soma1 % 11);
        if (digitos[9] != digito1) return false;

        int soma2 = 0;
        for (int i = 0; i < 10; i++) {
            soma2 += digitos[i] * (11 - i);
        }
        int digito2 = (soma2 % 11 < 2) ? 0 : 11 - (soma2 % 11);
        return digitos[10] == digito2;
    }
}
