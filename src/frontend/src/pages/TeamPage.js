import { React, useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { MatchDetailCard } from "../components/MatchDetailCard";
import { MatchSmallCard } from "../components/MatchSmallCard";
import { PieChart } from "react-minimal-pie-chart";

import "./TeamPage.scss";

export const TeamPage = () => {
  const [team, setTeam] = useState({ matches: [] }); //useState will store team data into team by grabbing it from setTeam fxn below
  const { teamName } = useParams(); //This will grab the teamName from req URL
  useEffect(
    //useEffect runs the given code if there is any change in the react
    () => {
      const fetchTeam = async () => {
        const response = await fetch(`${process.env.REACT_APP_API_ROOT_URL}/team/${teamName}`);
        const data = await response.json();
        //console.log(data);
        setTeam(data);
      };
      fetchTeam();
    },
    [teamName] //this empty array will stop effect to run infinite times and thus it only run once, adding
    // teamName so it changes when teamName changes(when we click on teams name hyperlink)
  );
  if (!team || !team.teamName) {
    return <h1>Team Not Found</h1>;
  } //if teamName is incorrect we will handle error this way
  return (
    <div className="TeamPage">
      <div className="team-name-section">
        <h1 className="team-name">{team.teamName}</h1>
      </div>
      <div className="win-loss-section">
        Wins / Losses
        <PieChart
          data={[
            { title: "Losses", value: team.totalMatches - team.totalWins, color: "#a34d5d" },
            { title: "Wins", value: team.totalWins, color: "#4da375" },
          ]}
        />
      </div>
      <div className="match-detail-section">
        {/* Showing 1st match here */}
        <h3>Latest Matches</h3>
        <MatchDetailCard teamName={team.teamName} match={team.matches[0]} />
      </div>

      {/* slice will ignore 1st match and map will show other 3 */}
      {team.matches.slice(1).map((match) => (
        // Key is so React can differentiate between these different match small cards by match.id which is unique for every card
        <MatchSmallCard key={match.id} teamName={team.teamName} match={match} />
      ))}
      <div className="more-link">
      <Link to={`/teams/${teamName}/matches/${process.env.REACT_APP_DATA_END_YEAR}`}>More ></Link>
      </div>
    </div>
  );
};
