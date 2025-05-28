import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './Login';
import HomePage from './HomePage';
import SpacesPage from './SpacesPage';
import ContractsPage from './ContractsPage';
import ConfirmContractPage from './ConfirmContractPage';
import MapPage from './MapPage';
import SpaceDetailsPage from './SpaceDetailsPage';
import ProfilePage from './ProfilePage';
import BuildingsPage from './BuildingsPage';
import CreateSpacePage from './CreateSpacePage';
import RentalContractPage from './RentalContractPage';
import MessagesPage from './MessagesPage';
import NotificationsPage from './NotificationsPage';

function App() {
    const [user, setUser] = useState(() => {
        const storedUser = localStorage.getItem('user');
        return storedUser ? JSON.parse(storedUser) : null;
    });

    useEffect(() => {
        const handleStorageChange = () => {
            const updatedUser = JSON.parse(localStorage.getItem('user'));
            setUser(updatedUser);
        };

        window.addEventListener('storage', handleStorageChange);
        return () => window.removeEventListener('storage', handleStorageChange);
    }, []);

    return (
        <Router>
            <Routes>
                <Route path="/login" element={user ? <Navigate to="/home" replace /> : <Login setUser={setUser} />} />
                <Route path="/home" element={user ? <HomePage setUser={setUser} /> : <Navigate to="/login" />} />
                <Route path="/spaces" element={user ? <SpacesPage /> : <Navigate to="/login" />} />
                <Route path="/spaces/create" element={user ? <CreateSpacePage /> : <Navigate to="/login" />} />
                <Route path="/" element={<Navigate to="/login" replace />} />
                <Route path="*" element={<Navigate to="/login" replace />} />
                <Route path="/contracts" element={user ? <ContractsPage /> : <Navigate to="/login" />} />
                <Route path="/payment" element={user ? <RentalContractPage />  : <Navigate to="/login" />} />
                <Route path="/payment/confirm" element={user ?  <ConfirmContractPage />: <Navigate to="/login" />} />
                <Route path="/map" element={user ? <MapPage /> : <Navigate to="/login" />} />
                <Route path="/space-details/:id" element={user ? <SpaceDetailsPage /> : <Navigate to="/login" />} />
                <Route path="/profile" element={user ? <ProfilePage /> : <Navigate to="/login" />} />
                <Route path="/buildings" element={user ? <BuildingsPage /> : <Navigate to="/login" />} />
                <Route path="/messages" element={user ? <MessagesPage /> : <Navigate to="/login" />} />
                <Route path="/notifications" element={user ? <NotificationsPage /> : <Navigate to="/login" />} />
            </Routes>
        </Router>
    );
}

export default App;