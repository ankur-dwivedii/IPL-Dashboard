package com.ankur.ipldashboard.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ankur.ipldashboard.model.Match;

public interface MatchRepository extends JpaRepository<Match, Long>{
    
    // As we want matches of the team but team can be in team1 or team2 column so using JPA we can do it easily using OR
    // Below will look where Team1 = teamName1 & Team2 = teamName2, order by Date desc & Pageable will tell that this method result
    // can be accessed page by page.
    List<Match> getByTeam1OrTeam2OrderByDateDesc(String teamName1, String teamName2, Pageable pageable);

    // As we are using data domain i.e pageable in controller which is not recommended, so either we can create a service class to
    // do that and call repo class or as in new java we can use default class to call repo own methods like this
    default List<Match> findLatestMatchesByTeam(String teamName, int noOfMatches){

        // Here i am saying i want page 0 and of size 4 (4 fields only)
        return getByTeam1OrTeam2OrderByDateDesc(teamName, teamName, PageRequest.of(0, noOfMatches));
    }

}
