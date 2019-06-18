package com.price_service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
@Service
@Slf4j

public class FakeService {


    @PostConstruct
    public void runFake()   throws AddressException, MessagingException, IOException {

        try {
            Stock stock = YahooFinance.get("CRWD");
            BigDecimal price = stock.getQuote().getPrice();

            log.info("*CRWD* {}",price);
        }
        catch(IOException ex){
          log.info("exception: ",ex);

        }

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("stocksimulator.messages@gmail.com", "kefwhfuemzgvboxi");
                }
            });
            // set sender
            Message message = new MimeMessage(session);
            InternetAddress sender = new InternetAddress("stocksimulator.messages@gmail.com");
            sender.setPersonal("Stock Simulator");
            message.setFrom(sender);
            // set receiver
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("maha.jabnouni@etudiant-enit.utm.tn"));
            //set message parameters
            message.setSubject("Trade executed");
            message.setContent("Trade executed", "text/html");
            message.setSentDate(new Date());
            // add message body
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent("Trade executed", "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            // add attachment
            //MimeBodyPart attachPart = new MimeBodyPart();
            //attachPart.attachFile("pathToFile");
            //multipart.addBodyPart(attachPart);
            message.setContent(multipart);
            Transport.send(message);
        }
    }


