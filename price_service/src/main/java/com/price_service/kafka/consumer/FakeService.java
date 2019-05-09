package com.price_service.kafka.consumer;


import com.price_service.model.*;
import com.price_service.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

import static jdk.nashorn.internal.objects.NativeMath.log;

@Service
@Slf4j

public class FakeService {

    @Autowired
    OrderRepository rep;

    @PostConstruct
    public void runFake() {


        List<LimitOrder>res = rep.findAsk(new ObjectId("5cd17e5651c62bc277d05949"));

        log.info("***********size {}",res.size());
       // log.info("***********ask {}",res.get(0).getLimitPrice());

        List<LimitOrder>res1 = rep.findBid(new ObjectId("5cd17e5651c62bc277d05949"));

        log.info("***********size {}",res1.size());
        //log.info("***********bid {}",res1.get(0).getLimitPrice());
    }

}
