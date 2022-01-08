package com.opdev.repository;

import com.opdev.model.company.Post;
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
    List<Post> findByCompanyId(@Param("companyId") Long companyId);

    @Query("select p from Post p where p.company.id in :companyIds")
    List<Post> findByCompanyIds(@Param("companyIds") List<Long> companyIds);

    // TODO @nikolagudelj Does this work if City or Country are null?
    @Query("select p from Post p where :country in (p.company.location.country) or :city in (p.company.location.city)")
    List<Post> findByLocation(@Param("country") String country, @Param("city") String city);

    @Query(value = "select * from post where country = :country order by created_on DESC limit 10", nativeQuery = true)
    List<Post> findLatest10ByCountry(@Param("country") String country);

}
