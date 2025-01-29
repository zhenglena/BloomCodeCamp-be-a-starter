import axios from "axios";

export const validateToken = async (token) => {
    try {
        const response = await axios.get("http://localhost:8080/api/auth/validate", {
            headers: {
                Authorization: `${token}`,
            },
        });
        console.log("Authentication successful:", response.data);
        return response.data;
    } catch (error) {
        console.error("Token validation failed:", error);
        throw error;
    }
};