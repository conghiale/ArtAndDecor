package org.artanddecor.repository;

import org.artanddecor.model.BlogCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * BlogCategory Repository Interface
 */
@Repository
public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Long> {

    /**
     * Find by slug
     */
    Optional<BlogCategory> findByBlogCategorySlug(String blogCategorySlug);

    /**
     * Find by name
     */
    Optional<BlogCategory> findByBlogCategoryName(String blogCategoryName);

    /**
     * Find blog categories with filtering
     */
    @Query("SELECT bc FROM BlogCategory bc WHERE " +
           "(:blogCategoryName IS NULL OR LOWER(bc.blogCategoryName) LIKE LOWER(CONCAT('%', :blogCategoryName, '%'))) AND " +
           "(:blogCategoryEnabled IS NULL OR bc.blogCategoryEnabled = :blogCategoryEnabled) AND " +
           "(:blogCategorySlug IS NULL OR bc.blogCategorySlug = :blogCategorySlug) AND " +
           "(:blogTypeId IS NULL OR bc.blogType.blogTypeId = :blogTypeId)")
    Page<BlogCategory> findWithFilters(@Param("blogCategoryName") String blogCategoryName,
                                       @Param("blogCategoryEnabled") Boolean blogCategoryEnabled,
                                       @Param("blogCategorySlug") String blogCategorySlug,
                                       @Param("blogTypeId") Long blogTypeId,
                                       Pageable pageable);
}