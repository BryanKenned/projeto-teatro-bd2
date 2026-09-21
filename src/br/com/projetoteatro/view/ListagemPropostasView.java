package br.com.projetoteatro.view;

import br.com.projetoteatro.model.PropostaAluguel;
import br.com.projetoteatro.service.ContratoService;
import br.com.projetoteatro.service.IngressoService;
import br.com.projetoteatro.service.PropostaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ListagemPropostasView extends JPanel {
    private JLabel lblFaturamento;
    private JLabel lblIngressosHoje;
    private JLabel lblPecaDestaque;

    private JTable tabelaEspetaculos;
    private DefaultTableModel modeloTabela;

    private final PropostaService propostaService;
    private final ContratoService contratoService;
    private final IngressoService ingressoService;

    public ListagemPropostasView(
            PropostaService propostaService,
            ContratoService contratoService,
            IngressoService ingressoService) {

        this.propostaService = propostaService;
        this.contratoService = contratoService;
        this.ingressoService = ingressoService;

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 240, 240));

        add(criarPainelCards(), BorderLayout.NORTH);

        add(criarPainelTabela(), BorderLayout.CENTER);

        atualizarDadosDashboard();
    }

    public ListagemPropostasView(PropostaService propostaService) {
        this(propostaService, null, null);
    }

    private JPanel criarPainelCards() {
        JPanel painelCards = new JPanel(new GridLayout(1, 3, 15, 0));
        painelCards.setOpaque(false);

        JPanel cardFaturamento = criarCardEstilizado(" FATURAMENTO BRUTO (MÊS)", new Color(46, 204, 113));
        lblFaturamento = (JLabel) cardFaturamento.getComponent(1);

        JPanel cardIngressos = criarCardEstilizado("INGRESSOS VENDIDOS HOJE", new Color(52, 152, 219));
        lblIngressosHoje = (JLabel) cardIngressos.getComponent(1);

        JPanel cardDestaque = criarCardEstilizado("ESPETÁCULO EM DESTAQUE", new Color(230, 126, 34));
        lblPecaDestaque = (JLabel) cardDestaque.getComponent(1);

        painelCards.add(cardFaturamento);
        painelCards.add(cardIngressos);
        painelCards.add(cardDestaque);

        return painelCards;
    }

    private JPanel criarCardEstilizado(String titulo, Color corFundo) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(corFundo);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel lblValor = new JLabel("Carregando...");
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("Arial", Font.BOLD, 22));

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarPainelTabela() {
        JPanel painelTabela = new JPanel(new BorderLayout(5, 5));
        painelTabela.setOpaque(false);

        JLabel lblTituloTabela = new JLabel("📊 Próximas Apresentações Agendadas");
        lblTituloTabela.setFont(new Font("Arial", Font.BOLD, 16));
        lblTituloTabela.setForeground(new Color(44, 62, 80));
        painelTabela.add(lblTituloTabela, BorderLayout.NORTH);

        String[] colunas = {"ID", "Espetáculo / Peça", "Data", "Status do Contrato"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaEspetaculos = new JTable(modeloTabela);
        tabelaEspetaculos.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(tabelaEspetaculos);
        painelTabela.add(scrollPane, BorderLayout.CENTER);

        return painelTabela;
    }

    public void atualizarDadosDashboard() {
        try {
            modeloTabela.setRowCount(0);
            List<PropostaAluguel> propostas = propostaService != null ? propostaService.getListaPropostas() : List.of();
            for (PropostaAluguel p : propostas) {
                modeloTabela.addRow(new Object[]{p.getId(), p.getNomePeca(), p.getDataInicio(), p.getStatusContrato()});
            }

            if (ingressoService != null) {
                double faturamento = ingressoService.calcularFaturamentoMesAtual();
                String faturamentoFormatado = String.format("R$ %.2f", faturamento).replace(".", ",");
                lblFaturamento.setText(faturamentoFormatado);

                long vendidosHoje = ingressoService.contarIngressosVendidosHoje();
                lblIngressosHoje.setText(String.valueOf(vendidosHoje));
            } else {
                lblFaturamento.setText("R$ 0,00");
                lblIngressosHoje.setText("0");
            }

            if (!propostas.isEmpty()) {
                lblPecaDestaque.setText(propostas.get(propostas.size() - 1).getNomePeca());
            } else {
                lblPecaDestaque.setText("Nenhum");
            }

        } catch (Exception e) {
            System.err.println("Erro ao atualizar dashboard: " + e.getMessage());
        }
    }
}
