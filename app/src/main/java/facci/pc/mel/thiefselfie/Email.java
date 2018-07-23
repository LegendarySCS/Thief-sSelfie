package facci.pc.mel.thiefselfie;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.util.Date;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Email extends javax.mail.Authenticator {

    private String _user;
    private String _pass;

    private String _to;
    private String _from;

    private String _port;
    private String _sport;

    private String _host;

    private String _subject;
    private String _body;
    private String _fileName;
    private boolean _auth;

    private boolean _debuggable;

    private Multipart _multipart;

    public Email() {
        _host = "smtp.gmail.com"; // default smtp server
        _port = "465"; // default smtp port
        _sport = "465"; // default socketfactory port

        _user = ""; // usuario
        _pass = ""; // Contraseña
        _from = ""; // correo de donde enviar
        _subject = ""; // email subject
        _body = ""; // email cuero

        _debuggable = false;
        _auth = true;

        _multipart = new MimeMultipart();

        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    public Email(String user, String pass) {
        this();

        _user = user;
        _pass = pass;
    }

    public void setPictureFileName(String fileName)
    {
        _fileName=fileName;
    }

    public boolean send() throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        Session session  = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(_user, _pass);
            }
        });

        try
        {

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(_from));
            message.setRecipients(MimeMessage.RecipientType.TO, _to);
            message.setSubject(_subject);
            MimeMultipart multipart = new MimeMultipart("related");
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText =  _body + " <img src=\"cid:image\">";
            messageBodyPart.setContent(htmlText, "text/html");

            multipart.addBodyPart(messageBodyPart);
            messageBodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource(_fileName);
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");

            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Sent message successfully....");
//            Log.e("Mensaje",_from);
//            Log.e("Mensaje",_to);
//            Log.e("Mensaje",_body);
            return true;
        }
        catch (MessagingException e)
        {
//            Log.d("Mensaje",_from);
//            Log.d("Mensaje",_to);
//            Log.d("Mensaje",_body);
            e.printStackTrace();
            return false;
        }



    }

    public void addAttachment(String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);

        _multipart.addBodyPart(messageBodyPart);
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(_user, _pass);
    }



    public String getBody() {
        return _body;
    }

    public void setBody(String _body) {
        this._body = _body;
    }

    public void setTo(String toArr) {
        this._to = toArr;
    }

    public void setFrom(String string) {
        this._from = string;
    }

    public void setSubject(String string) {
        this._subject = string;
    }



}
