package io.github.k_tomaszewski.fxservice.model;

import io.github.k_tomaszewski.fxservice.api.model.AccountOpeningData;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

import java.math.BigDecimal;

@Entity
public class Account {

    public static BigDecimal ZERO_AMOUNT = BigDecimal.ZERO.setScale(2);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, scale = 2)
    private BigDecimal plnBalance = ZERO_AMOUNT;

    @Column(nullable = false, scale = 2)
    private BigDecimal usdBalance = ZERO_AMOUNT;

    @Version
    private Long version;

    public Account() {
    }

    public Account(AccountOpeningData inputData) {
        firstName = inputData.firstName();
        lastName = inputData.lastName();
        plnBalance = inputData.plnBalance();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public BigDecimal getPlnBalance() {
        return plnBalance;
    }

    public void setPlnBalance(BigDecimal plnBalance) {
        this.plnBalance = plnBalance;
    }

    public BigDecimal getUsdBalance() {
        return usdBalance;
    }

    public void setUsdBalance(BigDecimal usdBalance) {
        this.usdBalance = usdBalance;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
