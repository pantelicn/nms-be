package com.opdev.follower;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.company.service.CompanyService;
import com.opdev.exception.AlreadyFollowException;
import com.opdev.model.company.Company;
import com.opdev.model.user.Follower;
import com.opdev.model.user.User;
import com.opdev.repository.FollowerRepository;
import com.opdev.user.UserService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowerServiceImpl implements FollowerService {

    private final FollowerRepository repository;
    private final UserService userService;
    private final CompanyService companyService;

    @Override
    @Transactional
    public Follower followCompany(@NonNull final String username, @NonNull final Long companyId) {
        User follower = userService.getByUsername(username);
        Company toFollow = companyService.getById(companyId);
        if (repository.existsByCompanyAndFollower(toFollow, follower)) {
            throw new AlreadyFollowException(toFollow.getId());
        }
        Follower newFollower = Follower.builder()
                .follower(follower)
                .company(toFollow)
                .build();
        newFollower.setCreatedBy(follower);
        return repository.save(newFollower);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Follower> findByFollower(@NonNull final String username) {
        User follower = userService.getByUsername(username);
        return repository.findByFollower(follower);
    }

    @Override
    @Transactional
    public void unfollowCompany(@NonNull final String username, @NonNull final Long companyId) {
        User follower = userService.getByUsername(username);
        repository.deleteByCompanyIdAndFollower(companyId, follower);
    }

}
