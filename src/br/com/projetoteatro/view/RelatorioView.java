package br.com.projetoteatro.view;

import br.com.projetoteatro.model.Contrato;
import br.com.projetoteatro.model.Ingresso;
import br.com.projetoteatro.service.ContratoService;
import br.com.projetoteatro.service.IngressoService;
import br.com.projetoteatro.service.PdfService;
import br.com.projetoteatro.service.SessaoService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class RelatorioView extends JPanel {

    private final ContratoService contratoService;
    private final IngressoService ingressoService;
    private final SessaoService sessaoService;

    private JComboBox<Contrato> cbxPecasLista;
    private JComboBox<Contrato> cbxPecasFinanceiro;
    private JTextField txtDataInicio;
    private JTextField txtDataFim;
    private JButton btnGerarLista;
    private JButton btnConsolidarPeca;
    private JButton btnGerarFaturamento;

    public RelatorioView(
            ContratoService contratoService,
            IngressoService ingressoService,
            SessaoService sessaoService) {

        this.contratoService = contratoService;
        this.ingressoService = ingressoService;
        this.sessaoService = sessaoService;

        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inicializarComponentes();
        montarLayout();
        atualizarDados();
    }

    public RelatorioView() {
        this(null, null, null);
    }

    private void inicializarComponentes() {
        cbxPecasLista = new JComboBox<>();
        btnGerarLista = new JButton("GERAR LISTA (PDF)");
        btnGerarLista.setBackground(new Color(52, 152, 219));
        btnGerarLista.setForeground(Color.WHITE);
        btnGerarLista.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cbxPecasFinanceiro = new JComboBox<>();
        btnConsolidarPeca = new JButton("CONSOLIDAR PEÇA (PDF)");
        btnConsolidarPeca.setBackground(new Color(46, 204, 113));
        btnConsolidarPeca.setForeground(Color.WHITE);
        btnConsolidarPeca.setCursor(new Cursor(Cursor.HAND_CURSOR));

        LocalDate hoje = LocalDate.now();
        txtDataInicio = new JTextField(hoje.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 10);
        txtDataFim = new JTextField(hoje.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 10);

        btnGerarFaturamento = new JButton("GERAR RELATÓRIO DO TEATRO (PDF)");
        btnGerarFaturamento.setBackground(new Color(142, 68, 173));
        btnGerarFaturamento.setForeground(Color.WHITE);
        btnGerarFaturamento.setFont(new Font("Arial", Font.BOLD, 13));
        btnGerarFaturamento.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnGerarLista.addActionListener(e -> gerarListaEspectadores());
        btnConsolidarPeca.addActionListener(e -> gerarConsolidacaoFinanceira());
        btnGerarFaturamento.addActionListener(e -> gerarRelatorioGeralTeatro());
    }

    private void montarLayout() {
        JLabel lblTitulo = new JLabel("CENTRAL DE RELATÓRIOS E EXPORTAÇÕES (PDF)", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 0, 10, 0);

        JPanel painelSecao1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        painelSecao1.setBackground(Color.WHITE);
        painelSecao1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "1. LISTA DE PRESENÇA / ESPECTADORES POR ESPETÁCULO", 0, 0, new Font("Arial", Font.BOLD, 13)));
        painelSecao1.add(new JLabel("Selecione o Espetáculo / Contrato:"));
        painelSecao1.add(cbxPecasLista);
        painelSecao1.add(btnGerarLista);

        gbc.gridy = 0;
        painelCentral.add(painelSecao1, gbc);

        JPanel painelSecao2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        painelSecao2.setBackground(Color.WHITE);
        painelSecao2.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "2. FECHAMENTO FINANCEIRO POR CONTRATO DE LOCAÇÃO", 0, 0, new Font("Arial", Font.BOLD, 13)));
        painelSecao2.add(new JLabel("Selecione o Espetáculo / Contrato:"));
        painelSecao2.add(cbxPecasFinanceiro);
        painelSecao2.add(btnConsolidarPeca);

        gbc.gridy = 1;
        painelCentral.add(painelSecao2, gbc);

        JPanel painelSecao3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        painelSecao3.setBackground(Color.WHITE);
        painelSecao3.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "3. INDICADORES GERAIS E FATURAMENTO GLOBAL DO TEATRO", 0, 0, new Font("Arial", Font.BOLD, 13)));
        painelSecao3.add(new JLabel("Data Inicial:"));
        painelSecao3.add(txtDataInicio);
        painelSecao3.add(new JLabel("Data Final:"));
        painelSecao3.add(txtDataFim);
        painelSecao3.add(btnGerarFaturamento);

        gbc.gridy = 2;
        painelCentral.add(painelSecao3, gbc);

        add(painelCentral, BorderLayout.CENTER);
    }

    public void atualizarDados() {
        cbxPecasLista.removeAllItems();
        cbxPecasFinanceiro.removeAllItems();

        if (contratoService != null) {
            List<Contrato> contratos = contratoService.listarContratos();
            for (Contrato c : contratos) {
                cbxPecasLista.addItem(c);
                cbxPecasFinanceiro.addItem(c);
            }
        }
    }

    private void gerarListaEspectadores() {
        Contrato contrato = (Contrato) cbxPecasLista.getSelectedItem();
        if (contrato == null) {
            JOptionPane.showMessageDialog(this, "Selecione um espetáculo/contrato cadastrado.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Ingresso> ingressos = ingressoService != null
                    ? ingressoService.listarIngressosContrato(contrato.getId())
                    : List.of();

            if (ingressos.isEmpty() && ingressoService != null && contrato.getNomePeca() != null) {
                ingressos = ingressoService.getIngressoRepo().buscarIngressosPorNomePecaComJoin(contrato.getNomePeca());
            }

            String caminhoArquivo = PdfService.gerarRelatorioEspectadores(
                    contrato.getNomePeca(),
                    "Período: " + contrato.getDataInicio() + " a " + contrato.getDataFim(),
                    ingressos);

            JOptionPane.showMessageDialog(this,
                    "Relatório de Espectadores gerado com sucesso!\n\n"
                            + "Espetáculo: " + contrato.getNomePeca() + "\n"
                            + "Total de Ingressos/Espectadores: " + ingressos.size() + "\n"
                            + "Arquivo salvo em:\n" + caminhoArquivo,
                    "Relatório Salvo (PDF)", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao gerar Relatório de Espectadores: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerarConsolidacaoFinanceira() {
        Contrato contrato = (Contrato) cbxPecasFinanceiro.getSelectedItem();
        if (contrato == null) {
            JOptionPane.showMessageDialog(this, "Selecione um espetáculo/contrato cadastrado.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Ingresso> ingressos = ingressoService != null
                    ? ingressoService.listarIngressosContrato(contrato.getId())
                    : List.of();

            if (ingressos.isEmpty() && ingressoService != null && contrato.getNomePeca() != null) {
                ingressos = ingressoService.getIngressoRepo().buscarIngressosPorNomePecaComJoin(contrato.getNomePeca());
            }

            double faturamentoBilheteria = 0.0;
            for (Ingresso i : ingressos) {
                faturamentoBilheteria += i.getValor();
            }

            String caminhoArquivo = PdfService.gerarConsolidacaoFinanceira(
                    contrato,
                    ingressos.size(),
                    faturamentoBilheteria);

            JOptionPane.showMessageDialog(this,
                    "Consolidação Financeira gerada com sucesso!\n\n"
                            + "Espetáculo: " + contrato.getNomePeca() + "\n"
                            + "Ingressos Vendidos: " + ingressos.size() + "\n"
                            + "Faturamento de Bilheteria: R$ " + String.format("%.2f", faturamentoBilheteria) + "\n"
                            + "Valor do Aluguel: R$ " + String.format("%.2f", contrato.getValorAluguel()) + "\n"
                            + "Arquivo salvo em:\n" + caminhoArquivo,
                    "Fechamento Financeiro (PDF)", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao gerar Consolidação Financeira: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerarRelatorioGeralTeatro() {
        try {
            LocalDate inicio = null;
            LocalDate fim = null;
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                inicio = LocalDate.parse(txtDataInicio.getText().trim(), fmt);
                fim = LocalDate.parse(txtDataFim.getText().trim(), fmt);
            } catch (Exception ignored) {
            }

            long totalIngressos = 0;
            double faturamentoTotal = 0.0;
            double precoMedio = 0.0;

            if (ingressoService != null && ingressoService.getIngressoRepo() != null) {
                totalIngressos = ingressoService.getIngressoRepo().contarTotalIngressosVendidos();
                faturamentoTotal = ingressoService.getIngressoRepo().somarFaturamentoTotal();
                precoMedio = ingressoService.getIngressoRepo().calcularPrecoMedioIngresso();
            }

            List<Contrato> contratos = contratoService != null
                    ? contratoService.listarContratos()
                    : List.of();

            String caminhoArquivo = PdfService.gerarRelatorioGeralTeatro(
                    inicio,
                    fim,
                    totalIngressos,
                    faturamentoTotal,
                    precoMedio,
                    contratos);

            JOptionPane.showMessageDialog(this,
                    "Relatório Geral do Teatro gerado com sucesso!\n\n"
                            + "Total de Ingressos Vendidos: " + totalIngressos + "\n"
                            + "Faturamento Global Arrecadado: R$ " + String.format("%.2f", faturamentoTotal) + "\n"
                            + "Ticket Médio: R$ " + String.format("%.2f", precoMedio) + "\n"
                            + "Contratos Gerenciados: " + contratos.size() + "\n"
                            + "Arquivo salvo em:\n" + caminhoArquivo,
                    "Relatório Geral Salvo (PDF)", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao gerar Relatório Geral do Teatro: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
