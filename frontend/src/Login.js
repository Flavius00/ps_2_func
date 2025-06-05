import React, { useState } from 'react';
import { authInstance } from './helper/axios';
import { useNavigate, Link } from 'react-router-dom';
import './LoginPage.css';

function Login({ setUser }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');
        setIsLoading(true);

        try {
            const response = await authInstance.post('/auth/login', {
                username,
                password
            });

            if (response.data && response.data.success) {
                // Salvează utilizatorul în localStorage
                localStorage.setItem('user', JSON.stringify(response.data));

                // Opțional: salvează un token de autentificare
                // localStorage.setItem('authToken', response.data.token);

                // Actualizează starea utilizatorului în aplicație
                setUser(response.data);
                navigate('/home');
            } else {
                setError('Credențiale invalide');
            }
        } catch (error) {
            console.error(error);
            if (error.response && error.response.data) {
                setError(error.response.data.message || 'Autentificare eșuată. Vă rugăm verificați credențialele.');
            } else {
                setError('Autentificare eșuată. Vă rugăm verificați credențialele.');
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="login-container">
            <form onSubmit={handleLogin}>
                <h2>Închiriere Spații Comerciale - Login</h2>
                <div className="form-group">
                    <input
                        type="text"
                        placeholder="Nume utilizator"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        disabled={isLoading}
                    />
                </div>
                <div className="form-group">
                    <input
                        type="password"
                        placeholder="Parolă"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        disabled={isLoading}
                    />
                </div>
                {error && <div className="error-message">{error}</div>}
                <button type="submit" disabled={isLoading}>
                    {isLoading ? 'Se procesează...' : 'Autentificare'}
                </button>
                <div className="register-link">
                    Nu ai cont? <Link to="/register">Înregistrează-te</Link>
                </div>
                <div className="login-helper">
                    <p>Conturi demo:</p>
                    <ul>
                        <li>Proprietar: username: adrianp / parolă: owner123</li>
                        <li>Chiriaș: username: elenad / parolă: tenant123</li>
                        <li>Admin: username: admin / parolă: admin123</li>
                    </ul>
                </div>
            </form>
        </div>
    );
}

export default Login;