package br.com.projetoteatro.dto;

public class EstatisticaVendasDTO {

    private final Long totalIngressos;
    private final Double faturamentoTotal;
    private final Double precoMedio;

    public EstatisticaVendasDTO(Long totalIngressos, Double faturamentoTotal, Double precoMedio) {
        this.totalIngressos = totalIngressos != null ? totalIngressos : 0L;
        this.faturamentoTotal = faturamentoTotal != null ? faturamentoTotal : 0.0;
        this.precoMedio = precoMedio != null ? precoMedio : 0.0;
    }

    public Long getTotalIngressos() {
        return totalIngressos;
    }

    public Double getFaturamentoTotal() {
        return faturamentoTotal;
    }

    public Double getPrecoMedio() {
        return precoMedio;
    }

    @Override
    public String toString() {
        return String.format(
                "EstatisticaVendasDTO [Total Vendidos: %d | Faturamento Total: R$ %.2f | Preço Médio: R$ %.2f]",
                totalIngressos, faturamentoTotal, precoMedio);
    }
}
