package caribouapp.caribou.com.cariboucoffee;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import caribouapp.caribou.com.cariboucoffee.mvp.account.model.CardEnum;
import caribouapp.caribou.com.cariboucoffee.util.CardParserUtil;

/**
 * Created by andressegurola on 12/19/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class CardTypeTest {

    /**
     * New test cards grabbed from:
     * https://www.paypalobjects.com/en_AU/vhelp/paypalmanager_help/credit_card_numbers.htm
     */

    @Test
    public void testVisa() {
        Assert.assertEquals(CardParserUtil.CardType.VISA, CardParserUtil.getCardType("4445222299990007", null));
        Assert.assertEquals(CardParserUtil.CardType.VISA, CardParserUtil.getCardType("4111111111111111", null));
        Assert.assertEquals(CardParserUtil.CardType.VISA, CardParserUtil.getCardType("4222222222222", "012"));
        Assert.assertEquals(CardParserUtil.CardType.VISA, CardParserUtil.getCardType("4012888888881881", "012"));
        Assert.assertEquals(CardParserUtil.CardType.VISA, CardParserUtil.getCardType("4111111111111111", "012"));
        Assert.assertEquals(null, CardParserUtil.getCardType("4111111111111111", "0132"));
        Assert.assertEquals(CardEnum.VISA, CardEnum.getCardTypeFromCardNumber("4445222299990007", null));
        Assert.assertEquals(CardEnum.VISA, CardEnum.getCardTypeFromCardNumber("4111111111111111", null));
    }

    @Test
    public void testMaster() {
        Assert.assertEquals(CardParserUtil.CardType.MASTER_CARD, CardParserUtil.getCardType("5555555555554444", null));
        Assert.assertEquals(CardParserUtil.CardType.MASTER_CARD, CardParserUtil.getCardType("5105105105105100", null));
        Assert.assertEquals(CardParserUtil.CardType.MASTER_CARD, CardParserUtil.getCardType("5105105105105100", "123"));
        Assert.assertEquals(null, CardParserUtil.getCardType("5105105105105100", "0132"));
        Assert.assertEquals(CardEnum.MASTER, CardEnum.getCardTypeFromCardNumber("5444009999222205", null));
        Assert.assertEquals(CardEnum.MASTER, CardEnum.getCardTypeFromCardNumber("5555555555554444", null));
    }

    @Test
    public void testDiscover() {
        Assert.assertEquals(CardParserUtil.CardType.DISCOVER, CardParserUtil.getCardType("6011111111111117", null));
        Assert.assertEquals(CardParserUtil.CardType.DISCOVER, CardParserUtil.getCardType("6011000990139424", null));
        Assert.assertEquals(CardParserUtil.CardType.DISCOVER, CardParserUtil.getCardType("6011000990139424", "123"));
        Assert.assertEquals(null, CardParserUtil.getCardType("6011000990139424", "0132"));
        Assert.assertEquals(CardEnum.DISCOVER, CardEnum.getCardTypeFromCardNumber("6011111111111117", null));
        Assert.assertEquals(CardEnum.DISCOVER, CardEnum.getCardTypeFromCardNumber("6011000990911111", null));
        Assert.assertEquals(CardEnum.DISCOVER, CardEnum.getCardTypeFromCardNumber("6221260004598744", null));
    }

    @Test
    public void testAMEX() {
        Assert.assertEquals(CardParserUtil.CardType.AMEX, CardParserUtil.getCardType("378282246310005", null));
        Assert.assertEquals(CardParserUtil.CardType.AMEX, CardParserUtil.getCardType("371449635398431", null));
        Assert.assertEquals(CardParserUtil.CardType.AMEX, CardParserUtil.getCardType("371449635398431", "0123"));
        Assert.assertEquals(CardParserUtil.CardType.AMEX, CardParserUtil.getCardType("378734493671000", "0123"));
        Assert.assertEquals(null, CardParserUtil.getCardType("371449635398431", "132"));
        Assert.assertEquals(CardEnum.AMEX, CardEnum.getCardTypeFromCardNumber("341111597242000", null));
        Assert.assertEquals(CardEnum.AMEX, CardEnum.getCardTypeFromCardNumber("378282246310005", null));
    }

    @Test
    public void testAustralianBankCard() {
        Assert.assertEquals(null, CardParserUtil.getCardType("5610591081018250", null)); // Australian BankCard
    }

    @Test
    public void testDiners() {
        Assert.assertEquals(CardParserUtil.CardType.DINERS, CardParserUtil.getCardType("30569309025904", null));
        Assert.assertEquals(CardParserUtil.CardType.DINERS, CardParserUtil.getCardType("38520000023237", null));
    }

    @Test
    public void testJCB() {
        Assert.assertEquals(CardParserUtil.CardType.JCB, CardParserUtil.getCardType("3530111333300000", null));
        Assert.assertEquals(CardParserUtil.CardType.JCB, CardParserUtil.getCardType("3566002020360505", null));
    }

    @Test
    public void testDankortPBS() {
        Assert.assertEquals(null, CardParserUtil.getCardType("76009244561", null)); // Dankort (PBS)
        Assert.assertEquals(null, CardParserUtil.getCardType("5019717010103742", null)); // Dankort (PBS)
    }

    @Test
    public void testSwitchSolo() {
        Assert.assertEquals(null, CardParserUtil.getCardType("6331101999990016", null)); // Switch/Solo (Paymentech)
    }
}
