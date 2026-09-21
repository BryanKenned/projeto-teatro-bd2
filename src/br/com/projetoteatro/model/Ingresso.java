package br.com.projetoteatro.model;

import br.com.projetoteatro.enums.TipoSetor;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
public class Ingresso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario cliente;

    @ManyToOne
    private Sessao sessao;

    @ManyToOne
    private Assento assento;

    @Enumerated(EnumType.STRING)
    private TipoSetor setor;

    private double valor;

    private LocalDateTime dataCompra;

    private String codigo;

    private boolean statusVenda;

    @ManyToOne
    private Contrato contrato;

    public Ingresso() {
    }

    public Ingresso(
            Usuario cliente,
            Sessao sessao,
            Assento assento,
            Setor setor,
            boolean statusVenda,
            double valor,
            Contrato contrato) {

        this.cliente = cliente;
        this.sessao = sessao;
        this.assento = assento;
        this.setor = setor.getSetor();

        this.valor = contrato.getValorIngresso();

        this.contrato = contrato;
        this.statusVenda = statusVenda;
        this.dataCompra = LocalDateTime.now();

        gerarCodigo();
    }

    public Ingresso(Contrato contrato, Usuario cliente) {
        this.contrato = contrato;
        this.cliente = cliente;
    }

    public Ingresso(
            Usuario cliente,
            Sessao sessao,
            Assento assento,
            TipoSetor setor,
            boolean statusVenda,
            double valor,
            Contrato contrato) {

        this.cliente = cliente;
        this.sessao = sessao;
        this.assento = assento;
        this.setor = setor;
        this.statusVenda = statusVenda;
        this.valor = valor;
        this.contrato = contrato;
        this.dataCompra = LocalDateTime.now();
        gerarCodigo();
    }

    private void gerarCodigo() {
        this.codigo = UUID.randomUUID().toString();
    }

    public Long getId() {
        return id;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public Sessao getSessao() {
        return sessao;
    }

    public Assento getAssento() {
        return assento;
    }

    public TipoSetor getSetor() {
        return setor;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getDataCompra() {
        return dataCompra;
    }

    public String getCodigo() {
        return codigo;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public boolean isStatusVenda() {
        return statusVenda;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
    }

    public void setAssento(Assento assento) {
        this.assento = assento;
    }

    public void setSetor(TipoSetor setor) {
        this.setor = setor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setDataCompra(LocalDateTime dataCompra) {
        this.dataCompra = dataCompra;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public void setStatusVenda(boolean statusVenda) {
        this.statusVenda = statusVenda;
    }

    @Override
    public String toString() {
        return "Ingresso{" +
                "codigo='" + codigo + '\'' +
                ", cliente=" + (cliente != null ? cliente.getNome() : "N/A") +
                ", sessao=" + (sessao != null ? sessao.getData() : "N/A") +
                ", valor=" + valor +
                '}';
    }
}