package edu.iis.mto.testreactor.exc3;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AtmMachineTest {

    @Mock
    BankService bankService;
    @Mock
    CardProviderService cardProviderService;
    @Mock
    MoneyDepot moneyDepot;
    AtmMachine atmMachine;
    Money money;
    Card card;

    @Before
    public void setup(){
        atmMachine = new AtmMachine(cardProviderService,bankService,moneyDepot);
        money = Money.builder().withAmount(10).withCurrency(Currency.PL).build();
        card = Card.builder().withCardNumber("TEST").withPinNumber(1234).build();
    }


    @Test
    public void itCompiles() {
        assertThat(true, equalTo(true));
    }

    @Test(expected = WrongMoneyAmountException.class)
    public void shouldThrowExceptionIfAmountIsLessOrEqualsZero(){

        Money moneyForTestScope = Money.builder().withAmount(-10).withCurrency(Currency.PL).build();
        atmMachine.withdraw(moneyForTestScope, card);
    }

    @Test(expected =CardAuthorizationException.class)
    public void shouldThrowExceptionIfCardAuthorizationFailed(){

        Mockito.when(cardProviderService.authorize(Mockito.any())).thenReturn(Optional.empty());
        atmMachine.withdraw(money,card);
    }

}
