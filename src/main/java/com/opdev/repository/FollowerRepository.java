package com.opdev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opdev.model.company.Company;
import com.opdev.model.user.Follower;
import com.opdev.model.user.User;

public interface FollowerRepository extends JpaRepository<Follower, Long> {

    boolean existsByCompanyAndFollower(Company company, User follower);

    List<Follower> findByFollower(User follower);

    long deleteByCompanyIdAndFollower(Long companyId, User follower);

}
