package model.bank;

import model.enums.AccUsrStatus;
import model.enums.UserRole;
import model.util.Utility;

import java.util.List;

/**
 * Class that represents User entity in system.
 */
public class User {
    private long userID;
    private AccUsrStatus status;
    private UserRole userRole;
    private String firstName;
    private String lastName;
    private String userLogin;
    private String userPassword;
    private List<BankAccount> accounts;

    /**
     * Constructor used to create absolute new system User
     * with Active Status {@see model.enums.AccUserStatus}
     * and User Role {@see model.enums.UserRole}
     */
    public User() {
        userID = Utility.createRandomNumber(9);
        status = AccUsrStatus.ACTIVE;
        userRole = UserRole.USER;
    }

    /**
     * Another constructor used to create User received from database.
     *
     * @param userID    user ID
     * @param status    user status {@see model.enums.AccUserStatus}
     * @param userRole  user role {@see model.enums.UserRole}
     * @param firstName user first name
     * @param lastName  user last name
     */
    public User(long userID, AccUsrStatus status, UserRole userRole,
                String firstName, String lastName) {
        this.userID = userID;
        this.status = status;
        this.userRole = userRole;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public void setStatus(AccUsrStatus status) {
        this.status = status;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public long getUserID() {
        return userID;
    }

    public boolean isBlocked() {
        return status == AccUsrStatus.BLOCKED;
    }

    public AccUsrStatus getStatus() {
        return status;
    }

    public void block() {
        this.setStatus(AccUsrStatus.BLOCKED);
    }

    public void unblock() {
        this.setStatus(AccUsrStatus.ACTIVE);
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

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<BankAccount> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return "User:" +
                "\n  id = " + userID +
                "\n  status = " + status +
                "\n  userLogin = '" + userLogin + '\'' +
                "\n  userPassword = '" + userPassword + '\'' +
                "\n  firstName = " + firstName +
                "\n  lastName = " + lastName;
    }
}
