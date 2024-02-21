package com.example.toss.service;

import com.example.toss.dto.PaymentCancelDto;
import com.example.toss.dto.PaymentConfirmDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/payments")
public interface TossHttpService {


    @PostExchange("/confirm")
    Object confirmPayment(@RequestBody PaymentConfirmDto dto);

    @GetExchange("/{paymentKey}")
    // 응답의 결과는 Payment 객체이지만 지금은 너무 복잡하므로 Object 로 퉁치기
    Object getPayment(
            @PathVariable("paymentKey")
            String paymentKey
    );

    @PostExchange("/{paymentKey}/cancel")
    Object cancelPayment(
            @PathVariable("paymentKey")
            String paymentKey,
            @RequestBody
            PaymentCancelDto dto
    );
}
