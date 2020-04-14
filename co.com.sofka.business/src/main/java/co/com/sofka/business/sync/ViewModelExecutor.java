package co.com.sofka.business.sync;

import co.com.sofka.business.repository.QueryMapperRepository;
import co.com.sofka.business.repository.QueryRepository;
import co.com.sofka.domain.generic.Query;

import java.util.function.Function;

/**
 * Query executor
 *
 * @param <Q>
 * @param <T>
 */
public abstract class ViewModelExecutor<Q extends Query, T> implements Function<Q, T> {

    private QueryMapperRepository<T> queryMapperRepository;
    private QueryRepository queryRepository;

    /**
     * Witch query mapper repository
     *
     * @param queryMapperRepository
     */
    public ViewModelExecutor<Q, T> witchQueryMapperRepository(QueryMapperRepository<T> queryMapperRepository){
        this.queryMapperRepository = queryMapperRepository;
        return this;
    }

    /**
     * Witch query repository
     *
     * @param queryRepository
     */
    public ViewModelExecutor<Q, T> witchQueryRepository(QueryRepository queryRepository){
        this.queryRepository = queryRepository;
        return this;
    }

    /**
     * query mapper repository
     *
     * @return
     */
    public QueryMapperRepository<T> queryMapperRepository() {
        return queryMapperRepository;
    }

    /**
     * Query repository
     *
     * @return
     */
    public QueryRepository queryRepository() {
        return queryRepository;
    }

}