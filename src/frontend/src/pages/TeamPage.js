import { React, useEffect, useState } from "react";
import { useParams } from 'react-router-dom';
import { MatchDetailCard } from "../components/MatchDetailCard";
import { MatchSmallCard } from "../components/MatchSmallCard";

export const TeamPage = () => {

  const [team, setTeam] = useState({matches : []});//useState will store team data into team by grabbing it from setTeam fxn below
  const { teamName } = useParams();//This will grab the teamName from req URL
  useEffect(//useEffect runs the given code if there is any change in the react
    () => {
      const fetchMatches = async () => {
          const response = await fetch(`http://localhost:8080/team/${teamName}`);
          const data = await response.json();
          //console.log(data);
          setTeam(data);
      };
      fetchMatches();


    }, []//this empty array will stop effect to run infinite times and thus it only run once
    
  );
if (!team || !team.teamName){
    return <h1>Team Not Found</h1>
}//if teamName is incorrect we will handle error this way
  return (
    <div className="TeamPage">
      <h1>{team.teamName}</h1>
      <MatchDetailCard match={team.matches[0]}/>//Showing 1st match here
      {team.matches.slice(1).map(match=> <MatchSmallCard match={match} />)}//slice will ignore 1st match and map will show other 3
      
    </div>
  );
};
