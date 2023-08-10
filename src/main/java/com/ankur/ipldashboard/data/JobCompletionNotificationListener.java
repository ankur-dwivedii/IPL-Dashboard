package com.ankur.ipldashboard.data;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import com.ankur.ipldashboard.model.Team;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

  private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

  private final EntityManager em;

  public JobCompletionNotificationListener(EntityManager em) {
    this.em = em;
  }

  @Override

  @Transactional
  public void afterJob(JobExecution jobExecution) {
    if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("!!! JOB FINISHED! Time to verify the results");

      // We want to populate the teams table when the batch job is executed, better to do it here at the app initiation time

      //  To store teams names using map
      Map<String, Team> teamData = new HashMap<>();
      /*  We want to get unique teams from team1 and team2 column of Match table as a team could be in either team1 or team2 column,
        union isn't supported in JPA . So will have to run query for team1 column and team2 separately*/
      //Here i am getting teamName and the number of matches played by that team using group by team1(teamName)
      // As i am getting team name and matches played by that team so we will get List of Object array
      em.createQuery("select m.team1, count(*) from Match m group by m.team1", Object[].class)
      .getResultList()
      .stream()
      .map(e -> new Team((String)e[0],(long)e[1]))
      .forEach(team -> teamData.put(team.getTeamName(), team));
      // As teams instances are already created in above step so we can only add the match numbers to exisitng count assuming all team names were added in above step
      em.createQuery("select m.team2, count(*) from Match m group by m.team2", Object[].class)
      .getResultList()
      .stream()
      .forEach(e -> {
        /*Storing the Value i.e. 'Team' from teamData map using the key i.e teamName or e[0] and storing the Team instance containing
        teamName & teamMatches into team instance*/ 
        Team team = teamData.get((String)e[0]);
        // Now i will add the matches we got from team2 column in the existing count of matches from team1 column
        team.setTotalMatches(team.getTotalMatches() + (long)e[1]);
      });


      // To get team wins we just need to calculate the no of times the team name was mentioned in the wins column
      em.createQuery("select m.matchWinner, count(*) from Match m group by m.matchWinner", Object[].class)
      .getResultList()
      .stream()
      .forEach(e -> {
        Team team = teamData.get((String)e[0]);
        // As some match winners is NA so to avoid error we only count wins if team exist
        if (team != null) 
          team.setTotalWins((long)e[1]);
      });

      // Now saving the Team data into the Team db table using persist() in JPA
      teamData.values().forEach(team -> em.persist(team));
      teamData.values().forEach(team -> System.out.println(team));
    }
  }

  @Override
  public void beforeJob(JobExecution jobExecution) {

  }

}
