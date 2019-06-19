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


    public void runFake()   throws AddressException, MessagingException, IOException {

        try {
            Stock stock = YahooFinance.get("CRWD");
            BigDecimal price = stock.getQuote().getPrice();

            log.info("*CRWD* {}",price);
        }
        catch(IOException ex){
          log.info("exception: ",ex);

        }
        String transactionId = "5cdbf89db93bc32fce8a1b69";
        String content="<html><head>"
                +"<style>"
                +"<h2>Trade"+transactionId+"confirmed</h2>"
                +"table {"
                +" font-family: arial, sans-serif;"
                + "border-collapse: collapse;"
                +" width: 100%;"
                +"}"
                +"td, th {"
                +"border: 1px solid #dddddd;"
                +"text-align: left;"
                +"padding: 8px;"
                +"}"
                +"tr:nth-child(even) {"
                +"background-color: #dddddd;"
                +"}"
                +"</style>"
                + "</head>"
                + "<body>"
                +"<table>"
                +"<tr>"
                +"<th>ID</th>"
                +"<th>Asset</th>"
                +"<th>type</th>"
                +"<th>transaction</th>"
                +"<th>volume</th>"
                +"<th>price</th>"
                +"<th>time</th>"
                +"</tr>"
                +"<tr>"

                +"<td>"+transactionId+"</td>"
                +"<td>"+transactionId+"</td>"
                +"<td>"+transactionId+"</td>"
                +"<td>"+transactionId+"</td>"
                +"<td>"+transactionId+"</td>"
                +"<td>"+transactionId+"</td>"
              /*  +"<td>"+transactionId+"</td>"
                +"<td>"+transaction.getOrder().getAsset().getSymbol()+"</td>"
                +"<td>"+transaction.getOrder().getOrderType().toString()+"</td>"
                +"<td>"+transaction.getVolume()+"</td>"
                +"<td>"+transaction.getPrice()+"</td>"
                +"<td>"+transaction.getTime()+"</td>"*/
                +"</tr>"
                +"</table>"
                + "</body>";

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
            message.setSubject("Trade notification");
            //message.setContent("Trade executed", "text/html");
            message.setSentDate(new Date());
            // add message body
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(content, "text/html");

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


