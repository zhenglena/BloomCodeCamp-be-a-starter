import React, { useEffect, useState } from "react";
import axios from "axios";
import "./LearnerDashboard.css";

const LearnerDashboard = () => {
    const [assignments, setAssignments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem("authToken");

        if (token) {
            axios.get("http://localhost:8080/api/assignments", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
                .then((response) => {
                    setAssignments(response.data);
                    setLoading(false);
                })
                .catch((err) => {
                    console.error("Failed to fetch assignments:", err);
                    setError("Failed to load assignments.");
                    setLoading(false);
                });
        }
    }, []);

    if (loading) return <div>Loading Dashboard...</div>;
    if (error) return <div>{error}</div>;

    // Grouping assignments by their status
    // SUBMITTED, IN REVIEW, RESUBMITTED will be under SUBMITTED
    // NEEDS UPDATE will be under NEEDS UPDATE
    // COMPLETED will be under COMPLETED
    // PENDING SUBMISSION will be ignored
    const getStatusGroup = (status) => {
        if (["Submitted", "Resubmitted", "In Review"].includes(status)) {
            return "SUBMITTED";
        }
        if (status === "Needs Update") {
            return "NEEDS UPDATE";
        }
        if (status === "Completed") {
            return "COMPLETED";
        }
        if (status === "Pending Submission") {
            return "";
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

    return (
        <div>
            <h1>Learner Dashboard</h1>
            {assignments.length === 0 ? (
                <p>No assignments available.</p>
            ) : (
                Object.entries(groupedAssignments).map(([status, assignmentsGroup]) => (
                    <div key={status} className="assignment-group">
                        <h2>{status}</h2>
                        <div className="assignment-container">
                            <ul>
                                {assignmentsGroup.map((assignment) => (
                                    <li key={assignment.number} className="assignment-item">
                                        <h3>{assignment.name}</h3>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    </div>
                ))
            )}
        </div>
    );
};

export default LearnerDashboard;
