package org.artanddecor.repository;

import org.artanddecor.model.BlogType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * BlogType Repository Interface
 */
@Repository
public interface BlogTypeRepository extends JpaRepository<BlogType, Long> {

    /**
     * Find by slug
     */
    Optional<BlogType> findByBlogTypeSlug(String blogTypeSlug);

    /**
     * Find by name
     */
    Optional<BlogType> findByBlogTypeName(String blogTypeName);

    /**
     * Find blog types with filtering
     */
    @Query("SELECT bt FROM BlogType bt WHERE " +
           "(:blogTypeName IS NULL OR LOWER(bt.blogTypeName) LIKE LOWER(CONCAT('%', :blogTypeName, '%'))) AND " +
           "(:blogTypeEnabled IS NULL OR bt.blogTypeEnabled = :blogTypeEnabled) AND " +
           "(:blogTypeSlug IS NULL OR bt.blogTypeSlug = :blogTypeSlug)")
    Page<BlogType> findWithFilters(@Param("blogTypeName") String blogTypeName,
                                   @Param("blogTypeEnabled") Boolean blogTypeEnabled,
                                   @Param("blogTypeSlug") String blogTypeSlug,
                                   Pageable pageable);
}