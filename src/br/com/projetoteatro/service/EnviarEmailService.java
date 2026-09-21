package br.com.projetoteatro.service;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Properties;

public class EnviarEmailService {

    private  String emailSistema;
    private  String senhaSistema;

    public EnviarEmailService() {

    }

    public EnviarEmailService(
            String emailSistema,
            String senhaSistema) {

        this.emailSistema = emailSistema;
        this.senhaSistema = senhaSistema;
    }

    public static boolean enviarEmailCodigoSenha(String destinatario, String assunto, String menssagem) {

        MultiPartEmail email = new MultiPartEmail();

        try {

            email.setHostName("smtp.gmail.com");

            email.setAuthentication("testandoPoo@gmail.com", "ozlo ijvx xotz htrh");

            email.setSSL(true);
            email.addTo(destinatario);
            email.setFrom("testandoPoo@gmail.com");
            email.setSubject(assunto);
            email.setMsg(menssagem);
            email.send();
            System.out.println("Email enviado.....");
            return true;
        } catch (EmailException e) {

            System.out.println("Falha ao enviar email....");
            e.printStackTrace();
            return false;
        }

    }

    public void enviarArquivoPdf(

            String destinatario,
            String assunto,
            String mensagem,
            String caminhoPdf) {

        try {

            Properties props = new Properties();

            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.auth", "true");

            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            Session session = Session.getInstance(
                    props,
                    new Authenticator() {
                        @Override
                        protected PasswordAuthentication
                        getPasswordAuthentication() {

                            return new PasswordAuthentication(
                                    emailSistema,
                                    senhaSistema
                            );
                        }
                    }
            );

            Message email =
                    new MimeMessage(session);

            email.setFrom(
                    new InternetAddress(emailSistema)
            );

            email.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(destinatario)
            );

            email.setSubject(assunto);

            MimeBodyPart corpo =
                    new MimeBodyPart();

            corpo.setText(mensagem);

            MimeBodyPart anexo =
                    new MimeBodyPart();

            File arquivo =
                    new File(caminhoPdf);

            anexo.setDataHandler(
                    new DataHandler(
                            new FileDataSource(arquivo)
                    )
            );

            anexo.setFileName(
                    arquivo.getName()
            );

            Multipart multipart =
                    new MimeMultipart();

            multipart.addBodyPart(corpo);
            multipart.addBodyPart(anexo);

            email.setContent(multipart);

            Transport.send(email);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao enviar e-mail.",
                    e
            );
        }
    }
}