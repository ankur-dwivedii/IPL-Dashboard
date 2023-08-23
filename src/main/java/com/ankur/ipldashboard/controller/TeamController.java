package com.ankur.ipldashboard.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ankur.ipldashboard.model.Match;
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

    @GetMapping("/team")
    public List<Team> getAllTeams(){

        return this.teamRepository.findAll();

    }

    @GetMapping("/team/{teamName}")
    public Team getTeam(@PathVariable String teamName){

        Team team = this.teamRepository.findByTeamName(teamName);
        
        team.setMatches(this.matchRepository.findLatestMatchesByTeam(teamName, 4));

        return team;
        
    }

    @GetMapping("/team/{teamName}/matches")
    public List<Match> getMatchesForTeam(@PathVariable String teamName, @RequestParam int year){
        // of(year, month, dayOfMonth), I want startdate as eg, 1 jan 2010 and end date as 1 jan 2011 and get matches between
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year+1, 1, 1);
        return this.matchRepository.getMatchesByTeamBetweenDates(teamName, startDate, endDate);

    }
}
