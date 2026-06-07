package br.com.projetoteatro.repository;

import br.com.projetoteatro.model.PropostaAluguel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ContratoRepository {
    private XStream xstream = new XStream(new StaxDriver());
    private final File ARQUIVO = new File("contratos.xml");

    public ContratoRepository() {
        this.xstream.addPermission(AnyTypePermission.ANY);
        this.xstream.alias("proposta", PropostaAluguel.class);
        this.xstream.alias("contratos", List.class);
    }

    public void salvarContrato(PropostaAluguel proposta) {
        List<PropostaAluguel> propostas = carregarContratos();
        propostas.add(proposta);
        String xml = xstream.toXML(propostas);

        try {
            if(!ARQUIVO.exists()) {
                ARQUIVO.createNewFile();
            }

            try (PrintWriter gravar = new PrintWriter(ARQUIVO)) {
                gravar.print(xml);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<PropostaAluguel> carregarContratos() {
        try {
            if (!ARQUIVO.exists() || ARQUIVO.length() == 0) {
                return new ArrayList<PropostaAluguel>();
            }

            String xml = new String(Files.readAllBytes(ARQUIVO.toPath()));
            Object objetoLido = xstream.fromXML(xml);

            if (objetoLido instanceof List) {
                return (List<PropostaAluguel>) objetoLido;
            }
            else if (objetoLido instanceof PropostaAluguel) {
                List<PropostaAluguel> listaTratada = new ArrayList<PropostaAluguel>();
                listaTratada.add((PropostaAluguel) objetoLido);
                return listaTratada;
            }

            return new ArrayList<PropostaAluguel>();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PropostaAluguel buscaContratoPorId(long id) {
        List<PropostaAluguel> propostas = carregarContratos();

        if (propostas == null || propostas.isEmpty()) {
            return null;
        }

        for (PropostaAluguel proposta : propostas) {
            if (proposta.getId() == id) {
                return proposta;
            }
        }
        return null;
    }

    public List<PropostaAluguel> listarTodos() {
        return carregarContratos();
    }

    // <<< SEU MÉTODO DE FILTRO COM SINTAXE COMUM >>>
    public List<PropostaAluguel> listarComFiltros(String nomePeca, String status) {
        List<PropostaAluguel> todos = carregarContratos();
        List<PropostaAluguel> filtrados = new ArrayList<PropostaAluguel>();

        for (PropostaAluguel c : todos) {
            boolean bateNome = false;
            boolean bateStatus = false;

            // 1. Valida o filtro de Nome da Peça
            if (nomePeca == null || nomePeca.isEmpty()) {
                bateNome = true;
            } else if (c.getNomePeca().toLowerCase().contains(nomePeca.toLowerCase())) {
                bateNome = true;
            }

            // 2. Valida o filtro de Status
            if (status.equals("Todos")) {
                bateStatus = true;
            } else if (c.getStatusProposta().toString().equalsIgnoreCase(status)) {
                bateStatus = true;
            }

            // Se passar nos dois filtros, adiciona na lista
            if (bateNome && bateStatus) {
                filtrados.add(c);
            }
        }

        return filtrados;
    }

    public void atualizarContrato(PropostaAluguel propostaAtualizada) {
        List<PropostaAluguel> propostas = carregarContratos();

        // Procura a proposta antiga dentro da lista do XML
        for (int i = 0; i < propostas.size(); i++) {
            if (propostas.get(i).getId() == propostaAtualizada.getId()) {
                // Substitui a proposta antiga pela nova (com status alterado)
                propostas.set(i, propostaAtualizada);
                break;
            }
        }

        // Grava a lista atualizada de volta no arquivo XML
        String xml = xstream.toXML(propostas);
        try {
            try (PrintWriter gravar = new PrintWriter(ARQUIVO)) {
                gravar.print(xml);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao atualizar o XML: " + e.getMessage());
        }
    }
}
