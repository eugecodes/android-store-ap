package caribouapp.caribou.com.cariboucoffee;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import caribouapp.caribou.com.cariboucoffee.mvp.authentication.PasswordUtil;

/**
 * Created by andressegurola on 11/13/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class PasswordFormatTest {

    @Test
    public void testPasswordSuccess1() {
        Assert.assertTrue(PasswordUtil.validatePassword("es1iSjsk09", true).isEmpty());
    }

    @Test
    public void testPasswordSuccess2() {
        Assert.assertTrue(PasswordUtil.validatePassword("autoRojo1212", true).isEmpty());
    }

    @Test
    public void testPasswordSuccess3() {
        Assert.assertTrue(PasswordUtil.validatePassword("autoRojo~", true).isEmpty());
    }

    @Test
    public void testPasswordFailLength() {
        Assert.assertFalse(PasswordUtil.validatePassword("toRoj12", true).isEmpty());
    }

    @Test
    public void testPasswordFailNoNumber() {
        Assert.assertFalse(PasswordUtil.validatePassword("hondaDelSol", true).isEmpty());
    }

    @Test
    public void testPasswordFailNoUpperCase() {
        Assert.assertFalse(PasswordUtil.validatePassword("autorojo1212", true).isEmpty());
    }

    @Test
    public void testPasswordFailNoLowerCase() {
        Assert.assertFalse(PasswordUtil.validatePassword("AUTOROJO1212", true).isEmpty());
    }
    @Test
    public void testPasswordConsecutiveCharacterInARowEmpty() {
        Assert.assertFalse(PasswordUtil.validatePassword("", true).isEmpty());
    }
    @Test
    public void testPasswordConsecutiveCharacterInARowNormalPassword() {
        Assert.assertTrue(PasswordUtil.validatePassword("Caribou1", true).isEmpty());
    }
    @Test
    public void testPasswordConsecutiveCharacterInARowNormalPassword1() {
        Assert.assertTrue(PasswordUtil.validatePassword("11Caribou1", true).isEmpty());
    }
    @Test
    public void testPasswordConsecutiveCharacterInARowNormalPassword2() {
        Assert.assertTrue(PasswordUtil.validatePassword("Cariibou1", true).isEmpty());
    }
    @Test
    public void testPasswordConsecutiveCharacterInARowError1() {
        Assert.assertFalse(PasswordUtil.validatePassword("Cariiibou1", true).isEmpty());
    }
    @Test
    public void testPasswordConsecutiveCharacterInARowError2() {
        Assert.assertFalse(PasswordUtil.validatePassword("CCCariibou1", true).isEmpty());
    }
    @Test
    public void testPasswordConsecutiveCharacterInARowError3() {
        Assert.assertFalse(PasswordUtil.validatePassword("CCariibou111", true).isEmpty());
    }

    @Test
    public void whenPasswordAndConfirmPasswordAreNull_thenValidateConfirmPasswordIsTrue() {
        Assert.assertFalse(PasswordUtil.validatePasswordConfirm(null, null));
    }

    @Test
    public void whenPasswordAndConfirmPasswordAreEqual_thenValidateConfirmPasswordIsTrue() {
        Assert.assertTrue(PasswordUtil.validatePasswordConfirm("Password11", "Password11"));
    }

    @Test
    public void whenPasswordNotNullAndConfirmPasswordNull_thenValidateConfirmPasswordIsFalse() {
        Assert.assertFalse(PasswordUtil.validatePasswordConfirm("", null));
    }

    @Test
    public void whenPasswordNullAndConfirmPasswordNotNull_thenValidateConfirmPasswordIsFalse() {
        Assert.assertFalse(PasswordUtil.validatePasswordConfirm(null, ""));
    }

    @Test
    public void whenPasswordAndConfirmPasswordAreDifferent_thenValidateConfirmPasswordIsTrue() {
        Assert.assertFalse(PasswordUtil.validatePasswordConfirm("Password11", "Password112"));
    }
}
