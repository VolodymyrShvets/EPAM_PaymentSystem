package model.bank;

import model.enums.AccUsrStatus;
import model.util.Utility;

import java.time.LocalDate;

/**
 * Class that represents Bank Account entity in system.
 */
public class BankAccount {
    private final long accountID;
    private final CreditCard card;
    private AccUsrStatus status;
    private long userID;

    /*public BankAccount(CreditCard card) {
        this.card = card;
        this.status = AccUsrStatus.ACTIVE;
        accountID = Utility.createRandomNumber(9);
    }*/

    /**
     * Constructor used to create new Active Account with new CreditCard.
     */
    public BankAccount() {
        accountID = Utility.createRandomNumber(9);
        card = new CreditCard(
                Utility.createCardNumber(),
                (int) Utility.createRandomNumber(3),
                LocalDate.now().plusYears(8).plusMonths(1).withDayOfMonth(1),
                0
        );
        status = AccUsrStatus.ACTIVE;
    }

    /**
     * Constructor used to represent existed Bank Account
     * received from Database.
     *
     * @param accountID Account ID
     * @param card      CreditCard {@see model.bank.CreditCard}
     * @param status    Account Status {@see model.enums.AccUserStatus}
     * @param userID    User ID
     */
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

    /**
     * see {@link model.bank.CreditCard#funding(double)}
     *
     * @param fundingSum sum that being added to account
     */
    public void funding(double fundingSum) {
        card.funding(fundingSum);
    }

    // снятие

    /**
     * see {@link model.bank.CreditCard#withdrawal(double)}
     *
     * @param withdrawalSum the amount to be deducted from the account
     */
    public void withdrawal(double withdrawalSum) {
        card.withdrawal(withdrawalSum);
    }

    public void printAccountState() {
        System.out.println("\tAccountID: " + accountID);
        System.out.println("\tUserID: " + userID);
        System.out.println("\tBank.Account Status: " + status);
        System.out.println("\t" + card);
    }
}
