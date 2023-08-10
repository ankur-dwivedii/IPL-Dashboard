package com.ankur.ipldashboard.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ankur.ipldashboard.model.Team;
import com.ankur.ipldashboard.repository.MatchRepository;
import com.ankur.ipldashboard.repository.TeamRepository;

@RestController
@CrossOrigin // To allow different domains to hit this controller like localhost:3000 react
public class TeamController {
    
    private TeamRepository teamRepository;

    private MatchRepository matchRepository;

    public TeamController(TeamRepository teamRepository, MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
    }

    @GetMapping("/team/{teamName}")
    public Team getTeam(@PathVariable String teamName){

        Team team = this.teamRepository.findByTeamName(teamName);
        
        team.setMatches(this.matchRepository.findLatestMatchesByTeam(teamName, 4));

        return team;
        
    }
}
