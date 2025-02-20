import React, { useEffect, useState } from "react";
import "./Login.css";
import axios from "axios";
import { validateToken } from "./Validate";
import { useNavigate } from "react-router-dom";

/**
 * This takes in a username and password from the user. When submitted, it will
 * call the POST endpoint /api/auth/login which will wait for a token.
 * When a token is received, it will store the token internally,
 * then it will call the GET endpoint /api/auth/validate (Validate.jsx)
 * Once validated, the user will be directed to the appropriate dashboard.
 */
const Login = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        document.title = "Login";
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post("http://localhost:8080/api/auth/login", {
                username,
                password,
            });
            console.log("Login successful");
            const token = response.data;
            localStorage.setItem("authToken", token); //stores the token in localstorage
            const authority = await validateToken(token); //calls on the validate endpoint

            if (authority === "ROLE_LEARNER") {
                navigate("/LearnerDashboard");
            } else if (authority === "ROLE_REVIEWER") {
                navigate("/ReviewerDashboard");
            } else {
                throw error;
            }

        } catch (error) {
            alert("Login failed");
        }
    }

    return (
        <div className="welcome-container">
            <div>
                <h1>Welcome to Bloom Camp!</h1>
            </div>
            <div className="login-container">
                <form className="login-form" onSubmit={handleSubmit}>
                    <h2>Login</h2>
                    <div className="input-group">
                        <label>Username</label>
                        <input
                            type="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>
                    <div className="input-group">
                        <label>Password</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit" className="login-button">Login</button>
                </form>
            </div>
        </div>
    );
};

export default Login;
