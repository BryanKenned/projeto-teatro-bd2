package br.com.projetoteatro.service;

import br.com.projetoteatro.model.PropostaAluguel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;

public class PdfService {

    public static String gerarProposta(PropostaAluguel proposta) {
        String nomeArquivo ="Proposta_" + proposta.getId() + ".pdf";

        try {

            Document documento = new Document();

            PdfWriter.getInstance(
                    documento,
                    new FileOutputStream(nomeArquivo)
            );
            documento.open();

            Font titulo = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    18
            );

            Font texto = FontFactory.getFont(
                    FontFactory.HELVETICA,
                    12
            );

            Paragraph cabecalho =
                    new Paragraph(
                            "PROPOSTA DE ALUGUEL DO TEATRO",
                            titulo
                    );

            cabecalho.setAlignment(Element.ALIGN_CENTER);
            documento.add(cabecalho);

            documento.add(new Paragraph(" "));
            documento.add(new Paragraph(
                    "ID da proposta: "
                            + proposta.getId(),
                    texto
            ));

            documento.add(new Paragraph(
                    "Contratante: "
                            + proposta.getContratante().getNome(),
                    texto
            ));

            documento.add(new Paragraph(
                    "Peça: "
                            + proposta.getNomePeca(),
                    texto
            ));

            documento.add(new Paragraph(
                    "Período: "
                            + proposta.getDataInicio()
                            + " até "
                            + proposta.getDataFim(),
                    texto
            ));

            documento.add(new Paragraph(
                    "Horário: "
                            + proposta.getHorarioInicio()
                            + " às "
                            + proposta.getHorarioFim(),
                    texto
            ));

            documento.add(new Paragraph(
                    "Valor do aluguel: R$ "
                            + proposta.getValorAluguel(),
                    texto
            ));

            documento.add(new Paragraph(" "));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph(
                    "As partes concordam com os termos estabelecidos neste contrato."
            ));


            documento.add(new Paragraph(" "));
            Paragraph termos=new Paragraph(
                    "- O locatário está ciente de que o primeiro aluguel será pago no ato da assinatura do contrato de locação, e os\n" +
                            "seguintes serão pagos nos meses vencidos, conforme legislação vigente, acrescido do IPTU e condomínio (em caso de\n" +
                            "conjunto comercial e apartamento).\n" +
                            "- O proprietário(a)/locador(a), esta ciente de que a comissão será paga no ato da assinatura do contrato de locação.\n" +
                            "- Aceita a proposta, a elaboração do contrato e efetiva realização da locação estão condicionadas a apresentação de\n" +
                            "documentos e informações do locatário(s) exigido conforme relação fornecida.\n" +
                            "- Os honorários de locação pelos serviços prestados de intermediação, correspondente a 100% (cem por cento) do\n" +
                            "valor proposto de locação mês, sem qualquer desconto, bonificação ou carência, que será pago pelo(s) proprietário(s)\n" +
                            "a Berti, no ato da assinatura do contrato.\n" +
                            "- Após a aceitação a presente proposta, caso haja arrependimento ou desistência pelo locatário ou locador, ou mesmo\n" +
                            "se concluir intermédio de outro corretor, empresa imobiliária ou diretamente com o proprietário, a Berti terá o direito\n" +
                            "aos honorários correspondentes a 100% (cem por cento) do valor do aluguel mensal nominal, que serão pagos por\n" +
                            "quem der causa.\n" +
                            "- Os honorários de intermediação não serão devidos salvo em caso de impossibilidade de conclusão da locação por\n" +
                            "estar os documentos inaptos do proponente locatário ou do imóvel.\n");
            termos.setAlignment(Element.ALIGN_JUSTIFIED);
            documento.add(termos);
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph(
                    "_________________________________"
            ));

            documento.add(new Paragraph(
                    "Assinatura do Contratante"
            ));

            documento.close();

            return nomeArquivo;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Erro ao gerar PDF.", e
            );
        }
    }


}