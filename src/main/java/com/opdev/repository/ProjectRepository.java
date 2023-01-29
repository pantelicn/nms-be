package com.opdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opdev.model.talent.Project;
import com.opdev.model.talent.Talent;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByIdAndTalent(Long id, Talent talent);

}
