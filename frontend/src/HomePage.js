import React from 'react';
import './HomePage.css';
import { useNavigate } from 'react-router-dom';

function HomePage({ setUser }) {
    const navigate = useNavigate();
    const user = JSON.parse(localStorage.getItem('user'));

    const handleLogout = () => {
        localStorage.removeItem('user');
        setUser(null);
        navigate('/login');
    };

    const renderOwnerContent = () => (
        <>
            <div className="info-card">
                <h3>🏢 Manage Properties</h3>
                <p>Track and manage all your commercial spaces in one place.</p>
                <button className="btn btn-sm" onClick={() => navigate('/spaces')}>View My Properties</button>
            </div>
            <div className="info-card">
                <h3>📝 Rental Contracts</h3>
                <p>Access and review all active and pending rental contracts.</p>
                <button className="btn btn-sm" onClick={() => navigate('/contracts')}>Manage Contracts</button>
            </div>
            <div className="info-card">
                <h3>🏗️ Buildings</h3>
                <p>Manage your buildings and facility details.</p>
                <button className="btn btn-sm" onClick={() => navigate('/buildings')}>View Buildings</button>
            </div>
            <div className="info-card">
                <h3>💬 Messages</h3>
                <p>Communicate with tenants and manage inquiries.</p>
                <button className="btn btn-sm" onClick={() => navigate('/messages')}>View Messages</button>
            </div>
        </>
    );

    const renderTenantContent = () => (
        <>
            <div className="info-card">
                <h3>🔍 Find Spaces</h3>
                <p>Explore available commercial spaces for your business needs.</p>
                <button className="btn btn-sm" onClick={() => navigate('/spaces')}>Browse Spaces</button>
            </div>
            <div className="info-card">
                <h3>📝 My Contracts</h3>
                <p>View and manage your active rental contracts.</p>
                <button className="btn btn-sm" onClick={() => navigate('/contracts')}>View Contracts</button>
            </div>
            <div className="info-card">
                <h3>🗺️ Locations</h3>
                <p>Explore commercial properties on an interactive map.</p>
                <button className="btn btn-sm" onClick={() => navigate('/map')}>Open Map</button>
            </div>
            <div className="info-card">
                <h3>💬 Messages</h3>
                <p>Contact property owners and get answers to your questions.</p>
                <button className="btn btn-sm" onClick={() => navigate('/messages')}>View Messages</button>
            </div>
        </>
    );

    const renderAdminContent = () => (
        <>
            <div className="info-card">
                <h3>👥 Users</h3>
                <p>Manage system users including owners and tenants.</p>
                <button className="btn btn-sm" onClick={() => navigate('/profile')}>View Users</button>
            </div>
            <div className="info-card">
                <h3>🏢 All Properties</h3>
                <p>Oversee all commercial spaces in the system.</p>
                <button className="btn btn-sm" onClick={() => navigate('/spaces')}>View Properties</button>
            </div>
            <div className="info-card">
                <h3>📝 All Contracts</h3>
                <p>Monitor and manage all rental contracts.</p>
                <button className="btn btn-sm" onClick={() => navigate('/contracts')}>View Contracts</button>
            </div>
            <div className="info-card">
                <h3>💬 Messages</h3>
                <p>Monitor system-wide communications and support.</p>
                <button className="btn btn-sm" onClick={() => navigate('/messages')}>View Messages</button>
            </div>
        </>
    );

    const renderRoleBasedContent = () => {
        if (!user) return null;

        switch (user.role) {
            case 'OWNER':
                return renderOwnerContent();
            case 'TENANT':
                return renderTenantContent();
            case 'ADMIN':
                return renderAdminContent();
            default:
                return null;
        }
    };

    return (
        <div className="homepage-wrapper">
            <nav className="navbar">
                <div className="navbar-brand">🏢 Commercial Space Rental</div>
                <div className="navbar-menu">
                    <button className="nav-btn" onClick={() => navigate('/spaces')}>Spaces</button>
                    <button className="nav-btn" onClick={() => navigate("/contracts")}>Contracts</button>
                    <button className="nav-btn" onClick={() => navigate("/messages")}>Messages</button>
                    <button className="nav-btn" onClick={() => navigate("/notifications")}>Notifications</button>
                    <button className="nav-btn" onClick={() => navigate("/profile")}>Profile</button>
                    <button className="nav-btn" onClick={() => navigate("/map")}>Map</button>
                    <button className="nav-btn logout" onClick={handleLogout}>Logout</button>
                </div>
            </nav>

            <div className="homepage-main">
                <div className="intro-section">
                    <h1>Welcome to Commercial Space Rental</h1>
                    <p className="welcome-message">Hello, <strong>{user?.name || 'Guest'}</strong>! Manage commercial properties efficiently with our comprehensive rental platform.</p>
                </div>

                <div className="image-with-sides">
                    <div className="left-facts">
                        <h3>📊 Market Insights</h3>
                        <ul>
                            <li>Commercial space demand increased by 8% in 2024</li>
                            <li>Average lease duration: 3.5 years</li>
                            <li>Retail spaces have 95% occupancy rate</li>
                        </ul>
                    </div>

                    <div className="right-reviews">
                        <h3>🌟 Client Feedback</h3>
                        <blockquote>"Streamlined our office leasing process completely."</blockquote>
                        <cite>– Tech Solutions Ltd.</cite>
                        <blockquote>"Found our ideal retail space in just one week!"</blockquote>
                        <cite>– Urban Boutique</cite>
                    </div>
                </div>

                <div className="info-cards">
                    {renderRoleBasedContent()}
                </div>

                <div className="market-highlights">
                    <h2>🔥 Market Highlights</h2>
                    <div className="highlights-grid">
                        <div className="highlight-item">
                            <span className="highlight-label">Most In-Demand</span>
                            <span className="highlight-value">Office Spaces</span>
                        </div>
                        <div className="highlight-item">
                            <span className="highlight-label">Fastest Growing Area</span>
                            <span className="highlight-value">IT Park District</span>
                        </div>
                        <div className="highlight-item">
                            <span className="highlight-label">Avg. Price/sqm</span>
                            <span className="highlight-value">€15.50</span>
                        </div>
                        <div className="highlight-item">
                            <span className="highlight-label">Vacancy Rate</span>
                            <span className="highlight-value">8.3%</span>
                        </div>
                    </div>
                </div>
            </div>

            <footer className="homepage-footer">
                <p>© 2025 Commercial Space Rental | <a href="mailto:support@commercial-rental.com">support@commercial-rental.com</a></p>
                <div className="socials">
                    <a href="https://www.facebook.com" target="_blank" rel="noreferrer">Facebook</a> |
                    <a href="https://www.linkedin.com" target="_blank" rel="noreferrer"> LinkedIn</a> |
                    <a href="https://twitter.com" target="_blank" rel="noreferrer"> Twitter</a>
                </div>
            </footer>
        </div>
    );
}

export default HomePage;