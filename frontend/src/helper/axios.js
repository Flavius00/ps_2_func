import axios from "axios"

// Creează două instanțe Axios separate pentru cele două servicii
const authInstance = axios.create({
    baseURL: "http://localhost:8081", // Serviciul de autentificare
    headers: {
        "Content-Type": "application/json",
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Headers":
            "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With"
    }
});

const mainInstance = axios.create({
    baseURL: "http://localhost:8080", // Serviciul principal
    headers: {
        "Content-Type": "application/json",
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Headers":
            "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With"
    }
});

// Interceptor pentru instanța auth
authInstance.interceptors.request.use(
    request => {
        const authToken = localStorage.getItem("authToken");
        if (authToken) {
            request.headers['Authorization'] = `Bearer ${authToken}`;
        }
        return request;
    },
    error => {
        return Promise.reject(error);
    }
);

// Interceptor pentru instanța main
mainInstance.interceptors.request.use(
    request => {
        const authToken = localStorage.getItem("authToken");
        if (authToken) {
            request.headers['Authorization'] = `Bearer ${authToken}`;
        }
        return request;
    },
    error => {
        return Promise.reject(error);
    }
);

// Funcție care alege instanța Axios corectă în funcție de endpoint
export const getAxiosInstance = (endpoint) => {
    if (endpoint.includes('/login') || endpoint.includes('/register') || endpoint.includes('/auth')) {
        return authInstance;
    }
    return mainInstance;
};

export { authInstance, mainInstance };