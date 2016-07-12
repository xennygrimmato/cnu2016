package com.devfactory.assignment4.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by vaibhavtulsyan on 09/07/16.
 */

@Entity
@Table(name="orders")
public class Orders {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="oid")
    private int orderId;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "uid", referencedColumnName = "id")
    public Customer customer;

    @Column(name="amount")
    private BigDecimal amount;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="timestamp")
    private Date date;

    @Transient
    @JsonProperty("date")
    private String formattedDate;

    @Column(name="status")
    private String status;


    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormattedDate() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(getDate());
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

}
