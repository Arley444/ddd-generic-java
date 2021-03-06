package co.com.sofka.business.generic;

import co.com.sofka.business.repository.DomainEventRepository;
import co.com.sofka.business.support.ResponseEvents;
import co.com.sofka.business.support.TriggeredEvent;
import co.com.sofka.domain.generic.DomainEvent;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


/**
 * The type Use case.
 *
 * @param <Q> the type parameter
 * @param <P> the type parameter
 */
public abstract class UseCase<Q extends UseCase.RequestValues, P extends UseCase.ResponseValues> {

    private Q request;
    private String identify;

    private UseCaseFormat<P> useCaseFormat;
    private ServiceBuilder serviceBuilder;
    private DomainEventRepository repository;
    private Set<UseCaseWrap> useCases;


    /**
     * Request q.
     *
     * @return the q
     */
    protected Q request() {
        return request;
    }

    /**
     * Sets request.
     *
     * @param request the request
     */
    public void setRequest(Q request) {
        this.request = request;
    }

    /**
     * Emit use case format.
     *
     * @return the use case format
     */
    public UseCaseFormat<P> emit() {
        return useCaseFormat;
    }

    /**
     * Sets use case callback.
     *
     * @param useCaseFormat the use case format
     */
    public void setUseCaseCallback(UseCaseFormat<P> useCaseFormat) {
        this.useCaseFormat = useCaseFormat;
    }


    /**
     * Run.
     */
    protected void run() {
        try {
            executeUseCase(request);
        } catch (BusinessException e) {
            useCaseFormat.onError(e);
        } catch (RuntimeException e) {
            var exception = new UnexpectedException(identify, "There is an unexpected problem in the use case", e);
            useCaseFormat.onError(exception);
        }
    }

    /**
     * With service builder.
     *
     * @param serviceBuilder the service builder constructor
     */
    public void addServiceBuilder(ServiceBuilder serviceBuilder) {
        this.serviceBuilder = Objects.requireNonNull(serviceBuilder);
    }

    /**
     * add Use Cases Set
     * @param useCases set
     */
    public void addUseCases(Set<UseCaseWrap> useCases) {
        this.useCases = Objects.requireNonNull(useCases);
    }

    /**
     * Get service class
     * @param class of the service
     * @return class service optional
     */
    public <T> Optional<T> getService(Class<T> clasz) {
        Objects.requireNonNull(serviceBuilder, "The service build cannot be null, you allow use the annotation ExtensionService");
        return serviceBuilder.getService(clasz);
    }

    @SuppressWarnings("unchecked")
    public List<DomainEvent> retrieveEvents(String aggregateId) {
        return UseCaseReplyUtil.retry(() -> {
            var events = repository().getEventsBy(aggregateId);
            if (!events.isEmpty()) {
                return events;
            }
            throw new BusinessException(aggregateId, "Reply event for use case");
        }, 5);
    }

    @SuppressWarnings("unchecked")
    public List<DomainEvent> retrieveEvents() {
        String aggregateId = identify;
        return UseCaseReplyUtil.retry(() -> {
            var events = repository().getEventsBy(aggregateId);
            if (!events.isEmpty()) {
                return events;
            }
            throw new BusinessException(aggregateId, "Reply event for use case");
        }, 5);
    }

    public final Optional<ResponseEvents> requestUseCase(DomainEvent domainEvent) {
        var event = Objects.requireNonNull(domainEvent);
        var wrap = useCases.stream()
                .filter(useCaseWrap -> useCaseWrap.eventType.equals(domainEvent.type))
                .findFirst().orElseThrow(() -> new BusinessException(identify, "The use case event listener not registered"));
        return UseCaseHandler.getInstance()
                .syncExecutor(wrap.usecase, new TriggeredEvent<>(event));
    }

    /**
     * Execute use case.
     *
     * @param objectInput the object input
     */
    public abstract void executeUseCase(Q input);

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public void addRepository(DomainEventRepository repository) {
        this.repository = repository;
    }

    public DomainEventRepository repository() {
        return repository;
    }

    /**
     * The interface Request event.
     */
    public interface RequestEvent extends RequestValues {
        /**
         * Gets domain event.
         *
         * @return the domain event
         */
        DomainEvent getDomainEvent();
    }

    /**
     * The interface Response values.
     */
    public interface ResponseValues {
    }

    /**
     * The interface Request values.
     */
    public interface RequestValues {
    }

    /**
     * The interface Use case format.
     *
     * @param <R> the type parameter
     */
    public interface UseCaseFormat<R> {
        /**
         * On success.
         *
         * @param output the output
         */
        void onSuccess(R output);

        /**
         * On error.
         *
         * @param exception the exception
         */
        void onError(RuntimeException exception);
    }

    /**
     * Use case wrap for associate event type and use case
     */
    public static class UseCaseWrap {
        private final UseCase<TriggeredEvent<? extends DomainEvent>, ResponseEvents> usecase;
        private final String eventType;


        public UseCaseWrap(String eventType, UseCase<TriggeredEvent<? extends DomainEvent>, ResponseEvents> usecase) {
            this.usecase = usecase;
            this.eventType = eventType;
        }


        public UseCase<TriggeredEvent<? extends DomainEvent>, ResponseEvents> usecase() {
            return usecase;
        }

        public String eventType() {
            return eventType;
        }
    }

}
