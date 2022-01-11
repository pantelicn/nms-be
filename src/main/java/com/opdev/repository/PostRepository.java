package com.opdev.repository;

import com.opdev.model.company.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Modifying
    @Query("delete from Post P where P.id = :postId and P.company.id = :companyId")
    void deleteByIdAndCompanyId(@Param("postId") Long postId, @Param("companyId") Long companyId);

    @Query("select P from Post P where P.company.id = :companyId")
    List<Post> findByCompanyId(@Param("companyId") Long companyId);

    @Query("select P from Post P where P.company.id in :companyIds")
    List<Post> findByCompanyIds(@Param("companyIds") List<Long> companyIds);

    // TODO @nikolagudelj Does this work if City or Country are null?
    @Query("select P from Post P " +
            "where upper(P.company.location.country) like concat('%', upper(:country), '%') and " +
            "upper(P.company.location.city) like concat('%', upper(:city) , '%')"
    )
    List<Post> findByCountryAndCity(@Param("country") String country, @Param("city") String city);

}
