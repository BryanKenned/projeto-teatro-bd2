package br.com.projetoteatro.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AgendaTeatroView extends JLabel {
    // Componentes da tela da Agenda
    private JLabel lblTitulo, lblNotaMargem;
    private JLabel lblSelecioneMes, lblFiltrarPeca;
    private JComboBox<String> cbxMeses, cbxPecas;
    private JTable tabelaAgenda;
    private DefaultTableModel modeloTabela;

    public AgendaTeatroView() {
        // Define o layout principal como BorderLayout e fundo branco
        this.setLayout(new BorderLayout(15, 15));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inicializarComponentes();
        montarLayout();
    }

    private void inicializarComponentes() {
        // 1. Título Superior
        lblTitulo = new JLabel("AGENDA DO TEATRO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));

        // 2. Filtros (Comboboxes)
        lblSelecioneMes = new JLabel("Selecione o Mês:");
        lblSelecioneMes.setFont(new Font("Arial", Font.PLAIN, 13));

        String[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        cbxMeses = new JComboBox<>(meses);
        cbxMeses.setSelectedItem("Junho"); // Começa em Junho como no seu desenho

        lblFiltrarPeca = new JLabel("Filtrar Peça:");
        lblFiltrarPeca.setFont(new Font("Arial", Font.PLAIN, 13));

        String[] pecasExemplo = {"Todas", "Casamento Blindado", "Pluft, o Fantasminha"};
        cbxPecas = new JComboBox<>(pecasExemplo);

        // 3. Tabela de Cronograma
        String[] colunas = {"Data Exibição", "Turno", "Horário (Peça)", "Espetáculo Cadastrado"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaAgenda = new JTable(modeloTabela);

        // Dados fictícios baseados no seu rascunho para popular a tela
        modeloTabela.addRow(new Object[]{"06/06/2026", "Noite", "19:00 - 21:00", "Casamento Blindado"});
        modeloTabela.addRow(new Object[]{"07/06/2026", "Tarde", "14:00 - 16:30", "Pluft, o Fantasminha"});

        // 4. Nota de Rodapé
        lblNotaMargem = new JLabel("* Nota: Horários já incluem as margens de 1h antes e 1h depois.");
        lblNotaMargem.setFont(new Font("Arial", Font.ITALIC, 12));
        lblNotaMargem.setForeground(Color.DARK_GRAY);
    }

    private void montarLayout() {
        // --- TOPO: Título + Painel de Filtros ---
        JPanel painelTopo = new JPanel(new BorderLayout(10, 10));
        painelTopo.setBackground(Color.WHITE);
        painelTopo.add(lblTitulo, BorderLayout.NORTH);

        // Sub-painel organizando os filtros lado a lado
        JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        painelFiltros.setBackground(Color.WHITE);
        painelFiltros.add(lblSelecioneMes);
        painelFiltros.add(cbxMeses);
        painelFiltros.add(lblFiltrarPeca);
        painelFiltros.add(cbxPecas);

        painelTopo.add(painelFiltros, BorderLayout.SOUTH);
        this.add(painelTopo, BorderLayout.NORTH);

        // --- CENTRO: Tabela com scroll ---
        JPanel painelCentro = new JPanel(new BorderLayout(5, 5));
        painelCentro.setBackground(Color.WHITE);

        JLabel lblCronograma = new JLabel("Cronograma de Ocupação do Teatro:");
        lblCronograma.setFont(new Font("Arial", Font.BOLD, 14));
        painelCentro.add(lblCronograma, BorderLayout.NORTH);

        JScrollPane scrollTabela = new JScrollPane(tabelaAgenda);
        painelCentro.add(scrollTabela, BorderLayout.CENTER);

        this.add(painelCentro, BorderLayout.CENTER);

        // --- RODAPÉ: Nota de margem de horário ---
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelRodape.setBackground(Color.WHITE);
        painelRodape.add(lblNotaMargem);

        this.add(painelRodape, BorderLayout.SOUTH);
    }
}
