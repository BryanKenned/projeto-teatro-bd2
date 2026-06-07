package br.com.projetoteatro.view;

import javax.swing.*;
import java.awt.*;

public class DashBoardView extends JFrame {
    // Atributo mapeado corretamente
    private GerenciarEspetaculosView gerenciarEspetaculosView;
    private BilheteriaView bilheteriaView;
    private DashBoardVendasView dashBoardVendasView;
    private AgendaTeatroView agendaTeatroView;
    private RelatorioView relatorioView;

    public DashBoardView() {
        setTitle("Gerenciamento Teatro");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //barra superior
        JPanel barraSuperior = new JPanel();
        barraSuperior.setPreferredSize(new Dimension(0,60));
        barraSuperior.setBackground(new Color(215,230,245));
        barraSuperior.setLayout(new BorderLayout());
        barraSuperior.setBorder(BorderFactory.createEmptyBorder(0,20,0,20));

        JLabel lblBoasVindas = new JLabel("Bem-vindo, Operador de Bilheteria");
        lblBoasVindas.setFont(new Font("Arial", Font.BOLD, 16));
        lblBoasVindas.setForeground(new Color(50, 50, 50));
        barraSuperior.add(lblBoasVindas, BorderLayout.WEST);

        JButton btnSair = new JButton("SAIR");
        btnSair.setBackground(new Color(240, 100, 100));
        btnSair.setForeground(Color.WHITE);
        btnSair.setFocusPainted(false);
        btnSair.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        barraSuperior.add(btnSair, BorderLayout.EAST);

        add(barraSuperior, BorderLayout.NORTH);

        //barra lateral
        JPanel barraLateral = new JPanel();
        barraLateral.setPreferredSize(new Dimension(250, getHeight()));
        barraLateral.setBackground(new Color(95, 170, 245));
        barraLateral.setLayout(new BoxLayout(barraLateral, BoxLayout.Y_AXIS));
        barraLateral.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        Dimension tamanhoBotao = new Dimension(220, 70);

        JButton btnDashBoard = new JButton("DashBoard de Vendas");
        btnDashBoard.setMaximumSize(tamanhoBotao);
        btnDashBoard.putClientProperty("JButton.arc", 15);
        btnDashBoard.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnGerenEspetaculos = new JButton("Gerenciar Espetáculos");
        btnGerenEspetaculos.setMaximumSize(tamanhoBotao);
        btnGerenEspetaculos.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnAgenda = new JButton("Agenda e Sessões");
        btnAgenda.setMaximumSize(tamanhoBotao);
        btnAgenda.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnBilheteria = new JButton("Bilheteria e Venda");
        btnBilheteria.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));
        btnBilheteria.setBackground(new Color(224, 229, 239));
        btnBilheteria.setMaximumSize(tamanhoBotao);
        btnBilheteria.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnMapaAssentos = new JButton("Mapa de Assentos");
        btnMapaAssentos.setMaximumSize(tamanhoBotao);
        btnMapaAssentos.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnRelatorio = new JButton("Relatórios");
        btnRelatorio.setMaximumSize(tamanhoBotao);
        btnRelatorio.setAlignmentX(Component.CENTER_ALIGNMENT);

        barraLateral.add(btnDashBoard);
        barraLateral.add(Box.createRigidArea(new Dimension(0, 15)));
        barraLateral.add(btnGerenEspetaculos);
        barraLateral.add(Box.createRigidArea(new Dimension(0, 15)));
        barraLateral.add(btnAgenda);
        barraLateral.add(Box.createRigidArea(new Dimension(0, 15)));
        barraLateral.add(btnBilheteria);
        barraLateral.add(Box.createRigidArea(new Dimension(0, 15)));
        barraLateral.add(btnMapaAssentos);
        barraLateral.add(Box.createRigidArea(new Dimension(0, 15)));
        barraLateral.add(btnRelatorio);

        add(barraLateral, BorderLayout.WEST);

        //menu principal
        CardLayout cardLayout = new CardLayout();
        JPanel contentPanel = new JPanel(cardLayout);

        this.gerenciarEspetaculosView = new GerenciarEspetaculosView();
        this.bilheteriaView = new BilheteriaView();
        this.dashBoardVendasView = new DashBoardVendasView();
        this.agendaTeatroView = new AgendaTeatroView();
        this.relatorioView = new RelatorioView();

        JPanel cardRelatorio = new JPanel();
        cardRelatorio.setBackground(Color.GRAY);
        cardRelatorio.add(new JLabel("TELA DE RELATÓRIOS"));

        JPanel cardBilheteria = new JPanel();
        cardBilheteria.setBackground(Color.DARK_GRAY);
        cardBilheteria.add(new JLabel("TELA DE BILHETERIA"));

        contentPanel.add(dashBoardVendasView, "home");
        contentPanel.add(this.agendaTeatroView, "agenda");
        contentPanel.add(this.relatorioView, "relatorio");
        contentPanel.add(bilheteriaView, "bilheteria");

        contentPanel.add(this.gerenciarEspetaculosView, "espetaculos");
        contentPanel.add(this.bilheteriaView, "bilheteria");

        add(contentPanel, BorderLayout.CENTER);


        btnGerenEspetaculos.addActionListener(e -> {
            cardLayout.show(contentPanel, "espetaculos");
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        btnRelatorio.addActionListener(e -> {
            cardLayout.show(contentPanel, "relatorio");
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        btnDashBoard.addActionListener(e -> {
            this.dashBoardVendasView.atualizarDadosDashboard();
            cardLayout.show(contentPanel, "home");
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        btnBilheteria.addActionListener(e -> {
            cardLayout.show(contentPanel, "bilheteria");
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        // Coloque junto com as ações dos outros botões lá embaixo:
        btnAgenda.addActionListener(e -> {
            cardLayout.show(contentPanel, "agenda");
            contentPanel.revalidate();
            contentPanel.repaint();
        });
    }
}
