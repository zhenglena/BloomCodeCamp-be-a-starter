import React, { useEffect, useState } from "react";
import axios from "axios";
import { Navigate, useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import "./ReviewerDashboard.css"

const ReviewerDashboard = () => {
    const [assignments, setAssignments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [username, setUsername] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        document.title = "Reviewer Dashboard";
        const token = localStorage.getItem("authToken");

        if (!token) {
            setError("Unauthorized. Please log in.");
            setLoading(false);
            return;
        }

        try {
            const decodedToken = jwtDecode(token);
            setUsername(decodedToken.sub);
        } catch (error) {
            console.error("Invalid token:", error);
        }

        axios.get("http://localhost:8080/api/assignments", {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        })
            .then((response) => {
                setAssignments(response.data);
            })
            .catch((err) => {
                console.error("Failed to fetch assignments:", err);
                setError("Failed to load assignments.");
            })
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <div>Loading Dashboard...</div>;
    if (error) return <div>{error}</div>;

    const getStatusGroup = (status) => {
        if (["Submitted", "Resubmitted"].includes(status)) {
            return "SUBMITTED";
        }
        if (status === "In Review") {
            return "IN REVIEW"
        }

        if (status === "Completed") {
            return "COMPLETED";
        }

        return "OTHER";
    }

    const groupAssignmentsByStatusGroup = (assignments) => {
        return assignments.reduce((acc, assignment) => {
            const group = getStatusGroup(assignment.status);
            if (!acc[group]) {
                acc[group] = [];
            }
            acc[group].push(assignment);
            return acc;
        }, {});
    };

    const groupedAssignments = groupAssignmentsByStatusGroup(assignments);

    const handleLogout = () => {
        localStorage.removeItem("authToken");
        navigate("/");
    };

    return (
        <div>
            <div className="header">
                <h1>Reviewer Dashboard</h1>
                <h2>Welcome {username}!</h2>
                <div className="buttons">
                    <button type="button" onClick={handleLogout}>Logout</button>
                </div>
            </div>
            <div className="dashboard">
                {assignments.length === 0 ? (
                    <p>No assignments available.</p>
                ) : (
                    Object.entries(groupedAssignments).map(([status, assignmentsGroup]) => (
                        <div key={status} className="assignment-group">
                            <h2>{status}</h2>
                            <div className="assignment-container">
                                <div>
                                    {assignmentsGroup.map((assignment) => (
                                        <div key={assignment.number} className="assignment-item">
                                            <h3>{assignment.name}</h3>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}

export default ReviewerDashboard;