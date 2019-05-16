package edu.iis.mto.testreactor.exc3;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

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

    @Captor
    ArgumentCaptor<List<Banknote>> banknoteList;

    @Captor
    ArgumentCaptor<AuthenticationToken> authenticationTokenGlobal;

    @Captor
    ArgumentCaptor<Card> cardArgumentCaptor;

    AuthenticationToken authenticationToken;

    @Before
    public void setup(){
        atmMachine = new AtmMachine(cardProviderService,bankService,moneyDepot);
        money = Money.builder().withAmount(10).withCurrency(Currency.PL).build();
        card = Card.builder().withCardNumber("TEST").withPinNumber(1234).build();
        authenticationToken = AuthenticationToken.builder().withUserId("1").withAuthorizationCode(123).build();
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

        when(cardProviderService.authorize(Mockito.any())).thenReturn(Optional.empty());
        atmMachine.withdraw(money,card);
    }

    @Test
    public void shouldRunServicesInProperOrderIfAllIsGood(){


        when(cardProviderService.authorize(Mockito.any())).thenReturn(Optional.of((authenticationToken)));
        when(bankService.charge(Mockito.any(),Mockito.any())).thenReturn(true);
        when(moneyDepot.releaseBanknotes(Mockito.anyList())).thenReturn(true);

        atmMachine.withdraw(money,card);

        InOrder inOrder = Mockito.inOrder(bankService, cardProviderService, moneyDepot);
        inOrder.verify(cardProviderService).authorize(card);
        inOrder.verify(moneyDepot).releaseBanknotes(banknoteList.capture());
        inOrder.verify(bankService).commit(authenticationTokenGlobal.capture());

    }

}
