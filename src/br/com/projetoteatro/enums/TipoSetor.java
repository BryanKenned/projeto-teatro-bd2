package br.com.projetoteatro.enums;

public enum TipoSetor {
    PLATEIA("Plateia"),
    CAMAROTE("Camarote"),
    BALCAO("Balcão");

    private String descricao;

    TipoSetor(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
