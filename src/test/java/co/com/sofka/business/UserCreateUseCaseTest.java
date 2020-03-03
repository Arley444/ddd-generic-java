package co.com.sofka.business;

import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.domain.events.UserCreated;
import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.domain.values.UserName;
import co.com.sofka.domain.values.UserPassword;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Flow;

public class UserCreateUseCaseTest {

    private UserCreateUseCase useCase;
    public UserCreateUseCaseTest(){
        this.useCase = new UserCreateUseCase();
    }
    @Test
    public void createUserWithEmitSuccess(){
        var userName = new UserName("rauloko");
        var userPassword = new UserPassword("asdasd");

        UserCreateUseCase.Request request = new UserCreateUseCase.Request(userName, userPassword);

        UseCaseHandler.getInstance()
                .asyncExecutor(useCase, request)
                .subscribe(new Flow.Subscriber<>() {
                    @Override
                    public void onSubscribe(Flow.Subscription subscription) {

                    }

                    @Override
                    public void onNext(DomainEvent event) {
                        Assertions.assertEquals("user.created", event.type);
                        var userCreatedEvent = (UserCreated)event;
                        Assertions.assertEquals("asdasd", userCreatedEvent.getUserPassword().value());
                        Assertions.assertEquals("rauloko", userCreatedEvent.getUserName().value());
                        Assertions.assertEquals(1, userCreatedEvent.getVersionType());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Assertions.fail("A problem inside usecase");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}