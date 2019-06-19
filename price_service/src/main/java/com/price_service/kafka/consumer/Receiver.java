package com.price_service.kafka.consumer;

import com.price_service.kafka.producer.Sender;
import com.price_service.model.*;
import com.price_service.repository.AccountRepository;
import com.price_service.repository.AssetRepository;
import com.price_service.repository.OrderRepository;
import com.price_service.repository.TransactionRepository;
import com.sun.org.apache.xml.internal.utils.URI;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Service
public class Receiver {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    AssetRepository assetRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    Sender sender;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private String findUserMail(String accountId){
        String url = "http://localhost:8762/fms/portfolio/mailByAccount/"+accountId;
        String address = restTemplate.getForObject(url, String.class);
        log.info("RESPONSE " + address);
        return address;
    }



    private void sendMailNotification(String receiverAddress,String subject,String content) throws MessagingException, UnsupportedEncodingException {
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
        InternetAddress senderAddress = new InternetAddress("stocksimulator.messages@gmail.com");
        senderAddress.setPersonal("Stock Simulator");
        message.setFrom(senderAddress);
        // set receiver
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverAddress));
        //set message parameters
        message.setSubject(subject);
        message.setContent(content, "text/html");
        message.setSentDate(new Date());
        Transport.send(message);
    }

    @KafkaListener(topics = "${kafka.topic.receiver.filledOrders}")
    public void listen(@Payload String orderId)throws MessagingException, UnsupportedEncodingException {
        log.info("order {}  filled", orderId);
        Order order =(Order)orderRepository.findById(orderId) .orElseThrow(() ->
                new NoSuchElementException("Order Not Found with id : " + orderId));
        String content="<html><head>"
                +"<style>"
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
                +"<h2>Order "+orderId+" filled</h2>"
                +"<table>"
                +"<tr>"
                +"<th>ID</th>"
                +"<th>Asset</th>"
                +"<th>transaction</th>"
                +"<th>type</th>"
                +"<th>volume</th>"
                +"</tr>"
                +"<tr>"
                +"<td>"+orderId+"</td>"
                +"<td>"+order.getAsset().getSymbol()+"</td>"
                +"<td>"+order.getTransactionType()+"</td>"
                +"<td>"+order.getOrderType()+"</td>"
                +"<td>"+order.getQuantity()+"</td>"
                +"</tr>"
                +"</table>"
                + "</body>";
        sendMailNotification(findUserMail(order.getAccountId().toHexString()),"Order Filled",content);

                if (order.getOrderType() == OrderType.LIMIT && order.getTransactionType() == TransactionType.BUY) {
               List<LimitOrder> bidList =orderRepository.findBid(new ObjectId(order.getAsset().getId()));
               if (!bidList.isEmpty()) {
                   log.info("Old Bid{}  ", order.getAsset().getBid());
                   order.getAsset().setBid(Price.builder().value(bidList.get(0).getLimitPrice()).currency(Currency.getInstance(Locale.US)).build());
                   log.info("New Bid{}  ", order.getAsset().getBid());
                   log.info("BID{}  ", bidList.get(0).getLimitPrice());
                   assetRepository.save(order.getAsset());
               }
            }
            if (order.getOrderType() == OrderType.LIMIT && order.getTransactionType() == TransactionType.SELL) {
               List<LimitOrder> askList =orderRepository.findAsk(new ObjectId(order.getAsset().getId()));
               if (!askList.isEmpty()) {
                   order.getAsset().setAsk(Price.builder().value(askList.get(0).getLimitPrice()).currency(Currency.getInstance(Locale.US)).build());
                   log.info("ASK{}  ", askList.get(0).getLimitPrice());
                   assetRepository.save(order.getAsset());
               }
            }




    }
    @KafkaListener(topics = "${kafka.topic.receiver.transactions}")
    public void listenTransaction (@Payload String transactionId) throws MessagingException, UnsupportedEncodingException {
        Transaction transaction = (Transaction)transactionRepository.findById(transactionId) .orElseThrow(() ->
                new NoSuchElementException("Transaction Not Found with id : " + transactionId));
        Account account = (Account)accountRepository.findById(transaction.getAccountId()).orElseThrow(() ->
                new NoSuchElementException("Account Not Found with id : " + transaction.getAccountId()));
        account.updateAccountMetrics(transaction.getPrice(),
                transaction.getCommission(),
                transaction.getOrder().getTransactionType());
        accountRepository.save(account);
        log.info("transaction {}  ", transactionId);
        // send mail notification
        String content="<html><head>"
                +"<style>"
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
                +"<h2>Trade "+transactionId+", confirmed</h2>"
                +"<table>"
                +"<tr>"
                +"<th>ID</th>"
                +"<th>Asset</th>"
                +"<th>Transaction</th>"
                +"<th>Type</th>"
                +"<th>Volume</th>"
                +"<th>Price</th>"
                +"<th>Time</th>"
                +"<th>Commission</th>"
                +"</tr>"
                +"<tr>"
                +"<td>"+transactionId+"</td>"
                +"<td>"+transaction.getOrder().getAsset().getSymbol()+"</td>"
                +"<td>"+transaction.getOrder().getTransactionType()+"</td>"
                +"<td>"+transaction.getOrder().getOrderType()+"</td>"
                +"<td>"+transaction.getVolume()+"</td>"
                +"<td>"+transaction.getPrice()+account.getCurrency().getSymbol()+"</td>"
                +"<td>"+transaction.getTime()+"</td>"
                +"<td>"+transaction.getCommission()+account.getCurrency().getSymbol()+"</td>"
                +"</tr>"
                +"</table>"
                + "</body>";
        sendMailNotification(findUserMail(transaction.getAccountId()),"Trade Confirmed",content);

        //send notification to UI
        messagingTemplate.convertAndSend("/topic/"+transaction.getAccountId(), transaction);

        if (transaction.getOrder().getAsset().getPrice().peekFirst().getValue()!=transaction.getPrice()) {
                transaction.getOrder().getAsset().updatePrice(Price.builder().value(transaction.getPrice()).currency(Currency.getInstance(Locale.US)).build());
                assetRepository.save(transaction.getOrder().getAsset());
            }
        List<StopLossOrder> stopLossOrders = orderRepository.findStopLossOrders(new ObjectId(transaction.getOrder().getAsset().getId()),
                    transaction.getPrice());
        if (!stopLossOrders.isEmpty()){
                log.info("StopSize{}  ", stopLossOrders.size());
                for(StopLossOrder stopLossOrder : stopLossOrders){
                    Order order = new Order(new ObjectId(),
                            stopLossOrder.getAccountId(),
                            stopLossOrder.getAsset(),
                            stopLossOrder.getTransactionType(),
                            stopLossOrder.getQuantity(),
                            stopLossOrder.getFilled(),
                            stopLossOrder.getOrderType(),
                            stopLossOrder.getDuration(),
                            ZonedDateTime.now(),
                            stopLossOrder.getState());
                    sender.send(order);

                }
            }




    }

}