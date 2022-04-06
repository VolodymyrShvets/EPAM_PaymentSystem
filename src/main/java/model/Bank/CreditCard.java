package model.bank;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Class that represents Credit Card entity in system.
 */
public class CreditCard {
    private final long cardNumber;
    private final int cvv2;
    private final LocalDate expirationDate;
    private double moneyAmount;

    /**
     * Constructor user to create new Credit Card.
     *
     * @param cardNumber     credit card number
     * @param cvv2           cvv2 confirmation code
     * @param expirationDate expiration date of card
     * @param moneyAmount    money amount
     */
    public CreditCard(long cardNumber, int cvv2, LocalDate expirationDate, double moneyAmount) {
        this.cardNumber = cardNumber;
        this.cvv2 = cvv2;
        this.expirationDate = expirationDate;
        this.moneyAmount = moneyAmount;
    }

    public long getCardNumber() {
        return cardNumber;
    }

    public int getCvv2() {
        return cvv2;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public double getMoneyAmount() {
        return moneyAmount;
    }

    /**
     * Method used to add sum to Credit Card
     *
     * @param fundingSum sum that being added to account
     */
    public void funding(double fundingSum) {
        BigDecimal res = new BigDecimal(moneyAmount).add(new BigDecimal(fundingSum));
        moneyAmount = res.doubleValue();
    }

    /**
     * Method used to withdraw sum from Credit Card
     *
     * @param withdrawalSum the amount to be deducted from the account
     */
    public void withdrawal(double withdrawalSum) {
        BigDecimal res = new BigDecimal(moneyAmount).subtract(new BigDecimal(withdrawalSum));
        moneyAmount = res.doubleValue();
    }

    @Override
    public String toString() {
        return "Card:" +
                "\n\t\tCard Number: " + cardNumber +
                ",\n\t\tCVV2: " + cvv2 +
                ",\n\t\tExpiration Date: " + expirationDate +
                ",\n\t\tMoney Amount: " + moneyAmount;
    }
}
