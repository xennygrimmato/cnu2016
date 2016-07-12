package com.devfactory.assignment4.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by vaibhavtulsyan on 09/07/16.
 */

@Entity
@Table(name="order_product")
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="opid")
    private int opid;

    @JsonManagedReference
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "oid")
    public Orders orders;

    @JsonManagedReference
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    public Product product;

    @Column(name="quantity")
    private int quantity;

    @Column(name="buying_cost")
    private BigDecimal buyingCost;

    @Column(name="selling_cost")
    private BigDecimal sellingCost;

    public int getOpid() {
        return opid;
    }

    public void setOpid(int opid) {
        this.opid = opid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getBuyingCost() {
        return buyingCost;
    }

    public void setBuyingCost(BigDecimal buyingCost) {
        this.buyingCost = buyingCost;
    }

    public BigDecimal getSellingCost() {
        return sellingCost;
    }

    public void setSellingCost(BigDecimal sellingCost) {
        this.sellingCost = sellingCost;
    }

}
