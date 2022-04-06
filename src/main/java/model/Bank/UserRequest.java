package model.bank;

import model.enums.RequestType;

/**
 * Class that represents User Request entity in system.
 */
public class UserRequest {
    private long requestID;
    private long userID;
    private long accountID;
    private RequestType type;

    /**
     * Almost default constructor.
     */
    public UserRequest() {
        requestID = -1;
    }

    /**
     * Method used to create new Account Unblocking Request.
     *
     * @param accountID bank account ID {@see model.bank.BankAccount}
     * @param userID user account ID {@see model.bank.User}
     */
    public void createAccountUnblockingRequest(long accountID, long userID) {
        this.accountID = accountID;
        this.userID = userID;
        type = RequestType.ACCOUNT;
    }

    /**
     * Method used to create new User Unblocking Request.
     *
     * @param userID user account ID {@see model.bank.User}
     */
    public void createUserUnblockingRequest(long userID) {
        this.userID = userID;
        accountID = -1;
        type = RequestType.USER;
    }

    public void setRequestID(long requestID) {
        this.requestID = requestID;
    }

    public long getRequestID() {
        return requestID;
    }

    public long getUserID() {
        return userID;
    }

    public long getAccountID() {
        return accountID;
    }

    public RequestType getType() {
        return type;
    }
}
