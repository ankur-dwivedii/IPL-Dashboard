import './App.css';
//Importing router to route teams/teamName to TeamPage component
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import { TeamPage } from './pages/TeamPage';

function App() {  
  return (
    <div className="App">
      <Router>
        <Routes>
          //Routing Req to TeamPage using teamName present in the request URL
          <Route path="/teams/:teamName" element={<TeamPage/>} />
        </Routes>
      </Router>
      
    </div>
  );
}

export default App;