package br.com.projetoteatro.enums;

public enum Turno {
    MANHA("Manhã"),
    TARDE("Tarde"),
    NOITE("Noite");

    private String descricao;

    Turno(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
