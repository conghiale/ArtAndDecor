package org.artanddecor.repository;

import org.artanddecor.model.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Blog Repository Interface
 */
@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    /**
     * Find by slug
     */
    Optional<Blog> findByBlogSlug(String blogSlug);

    /**
     * Find blogs with filtering including slug support
     */
    @Query("SELECT b FROM Blog b WHERE " +
           "(:blogTitle IS NULL OR LOWER(b.blogTitle) LIKE LOWER(CONCAT('%', :blogTitle, '%'))) AND " +
           "(:blogEnabled IS NULL OR b.blogEnabled = :blogEnabled) AND " +
           "(:blogCategoryId IS NULL OR b.blogCategory.blogCategoryId = :blogCategoryId) AND " +
           "(:fromDate IS NULL OR b.createdDt >= :fromDate) AND " +
           "(:toDate IS NULL OR b.createdDt <= :toDate) AND " +
           "(:blogCategorySlug IS NULL OR b.blogCategory.blogCategorySlug = :blogCategorySlug) AND " +
           "(:blogTypeSlug IS NULL OR b.blogCategory.blogType.blogTypeSlug = :blogTypeSlug)")
    Page<Blog> findWithFilters(@Param("blogTitle") String blogTitle,
                               @Param("blogEnabled") Boolean blogEnabled,
                               @Param("blogCategoryId") Long blogCategoryId,
                               @Param("fromDate") LocalDateTime fromDate,
                               @Param("toDate") LocalDateTime toDate,
                               @Param("blogCategorySlug") String blogCategorySlug,
                               @Param("blogTypeSlug") String blogTypeSlug,
                               Pageable pageable);
}