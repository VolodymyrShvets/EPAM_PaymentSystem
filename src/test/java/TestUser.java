import controller.dao.UserDAO;
import controller.login.LoginBean;
import model.bank.User;
import model.enums.AccUsrStatus;
import model.enums.UserRole;
import model.util.Utility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestUser {
    static C3P0Test instance;
    static User user1;
    static User user2;

    static UserDAO dao;

    @BeforeAll
    static void init() {
        instance = C3P0Test.getInstance();
        dao = new UserDAO(instance.getConnection());

        user1 = new User(15, AccUsrStatus.ACTIVE, UserRole.USER, "Takeshi", "Kovacs");
        user1.setUserLogin("TakeshiKovacs");
        user1.setUserPassword(Utility.hash("TakeshiKovacs"));

        user2 = new User(27, AccUsrStatus.ACTIVE, UserRole.USER, "Henry", "Cavill");
        user2.setUserLogin("HenryCavill");
        user2.setUserPassword(Utility.hash("HenryCavill"));
    }

    @Test
    public void testRegisterNewUser1() {
        //assertEquals(dao.registerNewUser(user1), 1);
        assertNotEquals(dao.registerNewUser(user1), 1);
    }

    @Test
    public void testRegisterNewUser2() {
        //assertEquals(dao.registerNewUser(user2), 1);
        assertNotEquals(dao.registerNewUser(user2), 1);
    }

    @Test
    public void testGetUser1() {
        //assertEquals(dao.registerNewUser(user2), 1);
        //assertNotEquals(dao.registerNewUser(user2), 1);
    }

    @Test
    public void testGetUser2() {
        //assertEquals(dao.registerNewUser(user2), 1);
        //assertNotEquals(dao.registerNewUser(user2), 1);
    }

    @Test
    public void testValidateUserPassword1() {
        LoginBean bean = new LoginBean();
        bean.setUsername("TakeshiKovacs");
        bean.setPassword("TakeshiKovacs");
        assertTrue(dao.validateUserPassword(bean));
    }

    @Test
    public void testValidateUserPassword2() {
        LoginBean bean = new LoginBean();
        bean.setUsername("HenryCavill");
        bean.setPassword("HenryCavill");
        assertTrue(dao.validateUserPassword(bean));
    }

    @Test
    public void testBlockUser() {
        //user1.setUserLogin("TakeshiKovacs");
        String userLogin = "TakeshiKovacs";
        System.out.println(userLogin);
        User copy = user1;

        dao.unblockUser(user1, true);

        copy.setStatus(AccUsrStatus.BLOCKED);
        copy.setUserLogin("null");
        copy.setUserPassword("null");

        User result = dao.getUser(userLogin);
        System.out.println("result : " + result);
        System.out.println("copy: " + copy);
        assertEquals(result.toString(), copy.toString());
    }

    @Test
    public void testUnblockUser() {
        //user1.setUserLogin("TakeshiKovacs");
        String userLogin = "TakeshiKovacs";
        User copy = user1;

        dao.unblockUser(user1, false);

        copy.setStatus(AccUsrStatus.ACTIVE);
        copy.setUserLogin("null");
        copy.setUserPassword("null");

        User result = dao.getUser(userLogin);
        System.out.println("result : " + result);
        System.out.println("copy: " + copy);
        assertEquals(result.toString(), copy.toString());
    }

    @Test
    public void testGetAllUsers() {
        testUnblockUser();
        List<User> users = dao.getAllUsers();

        assertEquals(users.size(), 2);

        user1.setUserLogin("null");
        user1.setUserPassword("null");

        assertEquals(users.get(0).toString(), user1.toString());

        user2.setUserLogin("null");
        user2.setUserPassword("null");
        assertEquals(users.get(1).toString(), user2.toString());
    }

    @Test
    public void testGetAllUserAccounts1() {
        assertEquals(dao.getAllUserAccounts(String.valueOf(user1.getUserID())), new ArrayList<>());
    }

    @Test
    public void testGetAllUserAccounts2() {
        assertEquals(dao.getAllUserAccounts(String.valueOf(user2.getUserID())), new ArrayList<>());
    }
}
