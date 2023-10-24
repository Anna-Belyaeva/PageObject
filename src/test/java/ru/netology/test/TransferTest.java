package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;
import static ru.netology.page.LoginPage.validLogin;


public class TransferTest {
    DashboardPage dashboardPage;

    @BeforeEach
    public void setUpAll() {
        var loginPage = open("http://localhost:9999/", LoginPage.class);
        var authInfo = getAutInfo();
        var verificationPage = validLogin(authInfo);
        var verificationCode = getVerificationCode();
        dashboardPage = verificationPage.validVerify(verificationCode);

    }

    @Test
    void shouldTransferFromFirstCardToSecond() {
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();

        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);

        var amount = generateValidAmount(firstCardBalance);

        var firstCardBalanceBeforeTransfer = firstCardBalance - amount;
        var secondCardBalanceBeforeTransfer = secondCardBalance + amount;

        var transferPage = dashboardPage.selectCardToTransfer(secondCard);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCard);

        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        assertEquals(firstCardBalanceBeforeTransfer, actualFirstCardBalance);
        assertEquals(secondCardBalanceBeforeTransfer,actualSecondCardBalance);
    }

    @Test
    void shouldTransferFromSecondCardToFirst() {
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();

        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);

        var amount = generateValidAmount(firstCardBalance);

        var firstCardBalanceBeforeTransfer = firstCardBalance + amount;
        var secondCardBalanceBeforeTransfer = secondCardBalance - amount;

        var transferPage = dashboardPage.selectCardToTransfer(firstCard);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), secondCard);

        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        assertEquals(firstCardBalanceBeforeTransfer, actualFirstCardBalance);
        assertEquals(secondCardBalanceBeforeTransfer,actualSecondCardBalance);
    }

    @Test
    void shouldNotTransferFromFirstCardToSecond() {
        var firstCard = getFirstCardInfo();
        var secondCard = getSecondCardInfo();

        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);

        var amount = generateInValidAmount(firstCardBalance);

        var transferPage = dashboardPage.selectCardToTransfer(secondCard);
        transferPage.makeTransfer(String.valueOf(amount), firstCard);
        transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающая остаток на карте списания");

        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        assertEquals(firstCardBalance, actualFirstCardBalance);
        assertEquals(secondCardBalance,actualSecondCardBalance);
    }
}
