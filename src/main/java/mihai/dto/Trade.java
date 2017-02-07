package mihai.dto;

import mihai.utils.Constants;
import mihai.utils.TradeDirection;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

/**
 * Created by mcojocariu on 2/6/2017.
 */
public class Trade implements Serializable {
    private String reference;
    private String exchangeReference;
    private ZonedDateTime tradeDate;
    private String isin;
    private TradeDirection direction;
    private Integer quantity;
    private String currency;
    private Float amount;

    private Trade(TradeBuilder tradeBuilder) {
        this.reference = tradeBuilder.reference;
        this.exchangeReference = tradeBuilder.exchangeReference;
        this.tradeDate = tradeBuilder.tradeDate;
        this.isin = tradeBuilder.isin;
        this.direction = tradeBuilder.direction;
        this.quantity = tradeBuilder.quantity;
        this.currency = tradeBuilder.currency;
        this.amount = tradeBuilder.amount;
    }

    public static Trade aTrade(String externalReference) {
        return new Trade.TradeBuilder(externalReference)
                .withReference(randomAlphabetic(10))
                .withTradeDate(ZonedDateTime.now(ZoneId.of(Constants.UTC_TIMEZONE)))
                .withIsin(randomAlphabetic(10))
                .withDirection(TradeDirection.getRandomDirection())
                .withQuantity(100 + ((int) (Math.random() * 1000)))
                .withCurrency("EUR")
                .withAmount((float) (100 + ((int) (Math.random() * 1000))))
                .build();
    }

    public static Trade aTrade() {
        return aTrade(randomAlphabetic(10));
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getExchangeReference() {
        return exchangeReference;
    }

    public void setExchangeReference(String exchangeReference) {
        this.exchangeReference = exchangeReference;
    }

    public ZonedDateTime getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(ZonedDateTime tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public TradeDirection getDirection() {
        return direction;
    }

    public void setDirection(TradeDirection direction) {
        this.direction = direction;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public static class TradeBuilder {
        private String reference;
        private String exchangeReference;
        private ZonedDateTime tradeDate;
        private String isin;
        private TradeDirection direction;
        private Integer quantity;
        private String currency;
        private Float amount;

        public TradeBuilder(String exchangeReference) {
            this.exchangeReference = exchangeReference;
        }

        public TradeBuilder withReference(String reference) {
            this.reference = reference;
            return this;
        }

        public TradeBuilder withTradeDate(ZonedDateTime tradeDate) {
            this.tradeDate = tradeDate;
            return this;
        }

        public TradeBuilder withIsin(String isin) {
            this.isin = isin;
            return this;
        }

        public TradeBuilder withDirection(TradeDirection direction) {
            this.direction = direction;
            return this;
        }

        public TradeBuilder withQuantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public TradeBuilder withCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public TradeBuilder withAmount(Float amount) {
            this.amount = amount;
            return this;
        }

        public Trade build() {
            return new Trade(this);
        }
    }
}
