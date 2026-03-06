import { createContext, useState, useEffect, useContext } from 'react';
import api from '../services/api';
import { useNavigate } from 'react-router-dom';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    // Optional navigate if needed inside context. Wait, App is wrapping Router, 
    // so we can't use useNavigate here unless we put AuthProvider inside Router. 
    // It is inside Router in App.jsx.

    useEffect(() => {
        // Check if user is logged in
        api.get('/auth/me')
            .then(res => {
                setUser(res.data);
            })
            .catch(() => {
                setUser(null);
            })
            .finally(() => {
                setLoading(false);
            });
    }, []);

    const login = async (credentials) => {
        const res = await api.post('/auth/login', credentials);
        setUser(res.data.user);
        return res.data.user;
    };

    const logout = async () => {
        await api.post('/auth/logout');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, loading, setUser }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};
