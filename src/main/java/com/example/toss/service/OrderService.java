package com.example.toss.service;

import com.example.toss.dto.ItemOrderDto;
import com.example.toss.dto.PaymentConfirmDto;
import com.example.toss.entity.Item;
import com.example.toss.entity.ItemOrder;
import com.example.toss.repo.ItemRepository;
import com.example.toss.repo.OrderRepository;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final TossHttpService tossService;

    private final ItemRepository itemRepository;
    // 주문은 ItemOrder에다가 넣기로 했으니까 그것에 대한 OrderRepository도 의존성을 받는다
    private final OrderRepository orderRepository;


    public Object confirmPayment(PaymentConfirmDto dto) {
        // HTTP 요청이 보내진다.
        Object tossPaymentObj = tossService.confirmPayment(dto);
        log.info(tossPaymentObj.toString());
        // TODO 사용자가 결제한 물품 + 결제 정보에 대한 내용을 DB에 저장한다.
        // 1. 결제한 물품 정보를 응답 Body에서 찾는다 (OrderName)
        String orderName = ((LinkedHashMap<String, Object>) tossPaymentObj)
                .get("orderName").toString();
        // 2. orderName 에서 itemId 를 회수하고, 그에 해당하는 Item 엔티티를 조회한다.
        Long itemId = Long.parseLong(orderName.split("-")[0]);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        // 3. Item 엔티티를 바탕으로 ItemOrder를 만들자 -> 결과를 dto 로 포장해서 보내보자 -> ItemOrderDto 의 FromEntity
        return ItemOrderDto.fromEntity(orderRepository.save(ItemOrder.builder()
                .item(item)
                .tossPaymentKey(dto.getPaymentKey())
                .tossOrderId(dto.getOrderId())
                .status("DONE")
                .build()));

        // return tossPaymentObj;
    }


    // readAll
    public List<ItemOrderDto> readAll() {
        return orderRepository.findAll().stream()
                .map(ItemOrderDto::fromEntity)
                .toList();
    }


    // readOne
    public ItemOrderDto readOne(Long id) {
        return orderRepository.findById(id)
                .map(ItemOrderDto::fromEntity)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND)); // 당신의 주문 요청은 존재하지 않습니다.
    }

    // readTossPayment : payment 객체를 읽어서 어떤 간편결제, 카드로 결제했는지 알아보기
    public Object readTossPayment(Long id) {
        // id 를 가지고 주문 정보를 조회한다.
        ItemOrder order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));
        // 2. 주문정보에 포함된 결제정보키(paymentKey)를 바탕으로
        // Toss에 요청을 보내 결제 정보를 받는다.
        Object response = tossService.getPayment(order.getTossPaymentKey());
        log.info(response.toString());
        // 3. 해당 결제정보를 반환한다.
        return response;
    }


}
