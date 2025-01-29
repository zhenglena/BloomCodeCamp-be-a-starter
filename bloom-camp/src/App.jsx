import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Login from "./components/Login";
import LearnerDashboard from "./components/LearnerDashboard";
import ReviewerDashboard from "./components/ReviewerDashboard";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/LearnerDashboard" element={<LearnerDashboard />} />
        <Route path="/ReviewerDashboard" element={<ReviewerDashboard />} />
      </Routes>
    </Router>
  );
}

export default App;

