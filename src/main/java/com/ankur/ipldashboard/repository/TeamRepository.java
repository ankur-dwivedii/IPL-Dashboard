package com.ankur.ipldashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ankur.ipldashboard.model.Team;

public interface TeamRepository extends JpaRepository<Team, Long>{
    
    Team findByTeamName(String teamName);
}
