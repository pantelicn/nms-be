package com.opdev.repository;

import com.opdev.model.post.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Modifying
    @Query("delete from Post p where p.id = :postId and p.company.id = :companyId")
    void deleteByIdAndCompanyId(@Param("postId") Long postId, @Param("companyId") Long companyId);

    @Query("select p from Post p where p.company.id = :companyId")
    Page<Post> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("select p from Post p where p.company.id in :companyIds")
    List<Post> findByCompanyIds(@Param("companyIds") List<Long> companyIds);

    // TODO @nikolagudelj Does this work if City or Country are null?
    @Query("select p from Post p " +
            "where upper(p.company.location.country) like concat('%', upper(:country), '%') and " +
            "upper(p.company.location.city) like concat('%', upper(:city) , '%') order by p.createdOn DESC")
    Page<Post> findByCountryAndCity(@Param("country") String country, @Param("city") String city, Pageable pageable);

    @Query(value = "select * from post where country = :country order by created_on DESC limit 10", nativeQuery = true)
    List<Post> findLatest10ByCountry(@Param("country") String country);

}
