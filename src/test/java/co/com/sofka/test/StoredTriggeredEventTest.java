package co.com.sofka.test;

import co.com.sofka.domain.user.events.UserCreated;
import co.com.sofka.domain.user.values.UserId;
import co.com.sofka.domain.user.values.UserName;
import co.com.sofka.domain.user.values.UserPassword;
import co.com.sofka.infraestructure.store.StoredEvent;
import co.com.sofka.infraestructure.store.StoredEventSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class StoredTriggeredEventTest {

    @Test
    @DisplayName("Se debe serializar y deserializar el evento store")
    public void createEventWrapEventSerialized() {
        UserCreated event = new UserCreated(UserId.create(), new UserName("raul"), new UserPassword("*****"));
        StoredEvent eventStore = StoredEvent.wrapEvent(event);
        String serialized = eventStore.toString();

        StoredEvent store = StoredEventSerializer.instance().deserialize(serialized, StoredEvent.class);
        Assertions.assertEquals(serialized, store.toString());
    }


    @Test
    @DisplayName("Deserializar el domain event object")
    public void deserializedDomainEvent() throws ClassNotFoundException {
        UserCreated event = new UserCreated(UserId.create(), new UserName("raul"), new UserPassword("*****"));
        StoredEvent eventStore = StoredEvent.wrapEvent(event);
        String serialized = eventStore.toString();

        StoredEvent store = StoredEventSerializer.instance().deserialize(serialized, StoredEvent.class);

        var eventSerialized = (UserCreated) store.deserializeEvent();
        Assertions.assertEquals(event.type, eventSerialized.type);
        Assertions.assertEquals(event.getUserName().value(), eventSerialized.getUserName().value());
        Assertions.assertEquals(event.getUserPassword().value(), eventSerialized.getUserPassword().value());
    }


}