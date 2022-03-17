package model.Bank;

import model.enums.AccUsrStatus;
import model.util.Util;

import java.time.LocalDate;

// Рахунок
public class BankAccount {
    private final long accountID;
    private final CreditCard card;
    private AccUsrStatus status;
    private long userID;

    public BankAccount(CreditCard card) {
        this.card = card;
        this.status = AccUsrStatus.ACTIVE;
        accountID = Util.createRandomNumber(9);
    }

    public BankAccount() {
        accountID = Util.createRandomNumber(9);
        card = new CreditCard(
                Util.createCardNumber(),
                (int) Util.createRandomNumber(3),
                LocalDate.now().plusYears(8).plusMonths(1).withDayOfMonth(1),
                0
        );
        status = AccUsrStatus.ACTIVE;
    }

    public BankAccount(long accountID, CreditCard card, AccUsrStatus status, long userID) {
        this.accountID = accountID;
        this.card = card;
        this.status = status;
        this.userID = userID;
    }

    public boolean isBlocked() {
        return status == AccUsrStatus.BLOCKED;
    }

    public void block() {
        this.setStatus(AccUsrStatus.BLOCKED);
    }

    public void unblock() {
        this.setStatus(AccUsrStatus.ACTIVE);
    }

    public long getAccountID() {
        return accountID;
    }

    public CreditCard getCard() {
        return card;
    }

    public AccUsrStatus getStatus() {
        return status;
    }

    public void setStatus(AccUsrStatus status) {
        this.status = status;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    // пополнение
    public void funding(double fundingSum) {
        if (status.equals(AccUsrStatus.ACTIVE)) {
            System.out.println("funding... to " + accountID);
            card.funding(fundingSum);
            System.out.println("done.");
        } else
            System.out.println("Account BLOCKED. Unable to perform replenishment.");
    }

    // снятие
    public void withdrawal(double withdrawalSum) {
        if (status.equals(AccUsrStatus.ACTIVE)) {
            System.out.println("withdrawal... from " + accountID);
            if (withdrawalSum <= card.getMoneyAmount()) {
                card.withdrawal(withdrawalSum);
                System.out.println("done.");
            } else
                throw new IllegalArgumentException("Withdrawal sum is bigger than amount of money on card!!!");
        } else
            System.out.println("Account BLOCKED. Unable to perform withdrawal.");
    }

    public void printAccountState() {
        System.out.println("\tAccountID: " + accountID);
        System.out.println("\tUserID: " + userID);
        System.out.println("\tBank.Account Status: " + status);
        System.out.println("\t" + card);
    }
}
