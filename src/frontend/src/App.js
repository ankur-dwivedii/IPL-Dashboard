import "./App.scss";
//Importing router to route teams/teamName to TeamPage component
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { TeamPage } from "./pages/TeamPage";
import { MatchPage } from "./pages/MatchPage";

function App() {
  return (
    <div className="App">
      <Router>
        <Routes>
          //Routing Req to TeamPage using teamName present in the request URL
          <Route path="/teams/:teamName" element={<TeamPage />} />

          <Route path="/teams/:teamName/matches/:year" element={<MatchPage />} />
        </Routes>
      </Router>
    </div>
  );
}

export default App;
