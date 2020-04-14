package co.com.sofka.business.repository;

import co.com.sofka.domain.generic.Query;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * The interface Query repository.
 *
 */
public interface QueryRepository {
    /**
     * The enum Sort.
     */
    enum Sort {
        /**
         * Asc sort.
         */
        ASC,
        /**
         * Desc sort.
         */
        DESC
    }

    /**
     * Find all stream.
     *
     * @return the stream
     */
    default Stream findAll() {
        return Stream.empty();
    }

    /**
     * Find all stream.
     *
     * @param sort the sort
     * @return the stream
     */
    default Stream findAll(Sort sort) {
        return Stream.empty();
    }

    /**
     * Find stream.
     *
     * @param query the query
     * @return the stream
     */
    default Stream find(Query query) {
        return Stream.empty();
    }

    /**
     * Find stream.
     *
     * @param query the query
     * @param sort  the sort
     * @return the stream
     */
    default Stream find(Query query, Sort sort) {
        return Stream.empty();
    }

    /**
     * Get optional.
     *
     * @param query the query
     * @return the optional
     */
    default Optional get(Query query) {
        return Optional.empty();
    }
}