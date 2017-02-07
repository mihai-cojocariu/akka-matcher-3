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
public class CcpTrade implements Serializable {
    private String exchangeReference;
    private ZonedDateTime tradeDate;
    private String isin;
    private TradeDirection direction;
    private Integer quantity;
    private String currency;
    private Float amount;

    private CcpTrade(CcpTradeBuilder ccpTradeBuilder) {
        this.exchangeReference = ccpTradeBuilder.exchangeReference;
        this.tradeDate = ccpTradeBuilder.tradeDate;
        this.isin = ccpTradeBuilder.isin;
        this.direction = ccpTradeBuilder.direction;
        this.quantity = ccpTradeBuilder.quantity;
        this.currency = ccpTradeBuilder.currency;
        this.amount = ccpTradeBuilder.amount;
    }

    public static CcpTrade aCcpTrade(String externalReference) {
        return new CcpTrade.CcpTradeBuilder(externalReference)
                .withTradeDate(ZonedDateTime.now(ZoneId.of(Constants.UTC_TIMEZONE)))
                .withIsin(randomAlphabetic(10))
                .withDirection(TradeDirection.getRandomDirection())
                .withQuantity(100 + ((int) (Math.random() * 1000)))
                .withCurrency("EUR")
                .withAmount((float) (100 + ((int) (Math.random() * 1000))))
                .build();
    }

    public static CcpTrade aCcpTrade() {
        return aCcpTrade(randomAlphabetic(10));
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

    public static class CcpTradeBuilder {
        private String exchangeReference;
        private ZonedDateTime tradeDate;
        private String isin;
        private TradeDirection direction;
        private Integer quantity;
        private String currency;
        private Float amount;

        public CcpTradeBuilder(String exchangeReference) {
            this.exchangeReference = exchangeReference;
        }

        public CcpTradeBuilder withTradeDate(ZonedDateTime tradeDate) {
            this.tradeDate = tradeDate;
            return this;
        }

        public CcpTradeBuilder withIsin(String isin) {
            this.isin = isin;
            return this;
        }

        public CcpTradeBuilder withDirection(TradeDirection direction) {
            this.direction = direction;
            return this;
        }

        public CcpTradeBuilder withQuantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public CcpTradeBuilder withCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public CcpTradeBuilder withAmount(Float amount) {
            this.amount = amount;
            return this;
        }

        public CcpTrade build() {
            return new CcpTrade(this);
        }
    }
}
