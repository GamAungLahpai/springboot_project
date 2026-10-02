package com.example.demo.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "orderitems")
public class OrderItem {

    @EmbeddedId
    private OrderItemId id;

    @JsonIgnore
    @MapsId("orderId")
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private Order order;

    @JsonIgnore
    @MapsId("productId")
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    public OrderItem() {
    }

    public OrderItemId getId() {
        return id;
    }

    public void setId(OrderItemId id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
