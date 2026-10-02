package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.OrderItemId;
import com.example.demo.entity.Product;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderItemController(
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            ProductRepository productRepository
    ) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @PostMapping
    public OrderItem createOrderItem(
            @RequestParam Integer orderId,
            @RequestParam Integer productId,
            @RequestParam Integer quantity,
            @RequestParam BigDecimal unitPrice
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow();

        Product product = productRepository.findById(productId)
                .orElseThrow();

        OrderItem orderItem = new OrderItem();

        orderItem.setId(new OrderItemId(orderId, productId));
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(unitPrice);

        return orderItemRepository.save(orderItem);
    }

    @GetMapping("/order/{orderId}")
    public List<OrderItem> getItemsByOrder(@PathVariable Integer orderId) {
        return orderItemRepository.findAll()
                .stream()
                .filter(item -> item.getId().getOrderId().equals(orderId))
                .toList();
    }
}