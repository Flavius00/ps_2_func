import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './NotificationComponent.css';

function NotificationComponent({ user }) {
    const [unreadCount, setUnreadCount] = useState(0);
    const [recentNotifications, setRecentNotifications] = useState([]);
    const [showDropdown, setShowDropdown] = useState(false);
    const [loading, setLoading] = useState(false);
    const dropdownRef = useRef(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (user) {
            fetchUnreadCount();
            fetchRecentNotifications();

            // Poll for new notifications every 30 seconds
            const interval = setInterval(() => {
                fetchUnreadCount();
                fetchRecentNotifications();
            }, 30000);

            return () => clearInterval(interval);
        }
    }, [user]);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setShowDropdown(false);
            }
        };

        if (showDropdown) {
            document.addEventListener('mousedown', handleClickOutside);
        }

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [showDropdown]);

    const fetchUnreadCount = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/notifications/unread-count/${user.id}`);
            setUnreadCount(response.data.count || 0);
        } catch (error) {
            console.error('Error fetching unread count:', error);
        }
    };

    const fetchRecentNotifications = async () => {
        if (!showDropdown) return;

        setLoading(true);
        try {
            const response = await axios.get(`http://localhost:8080/notifications/recent/${user.id}`);
            setRecentNotifications(response.data.slice(0, 5)); // Show only 5 most recent
        } catch (error) {
            console.error('Error fetching recent notifications:', error);
            setRecentNotifications([]);
        } finally {
            setLoading(false);
        }
    };

    const handleNotificationClick = async (notification) => {
        // Mark as read if unread
        if (!notification.isRead) {
            try {
                await axios.post(`http://localhost:8080/notifications/mark-read/${notification.id}`);
                fetchUnreadCount();
            } catch (error) {
                console.error('Error marking notification as read:', error);
            }
        }

        setShowDropdown(false);

        // Navigate to related content or notifications page
        if (notification.actionUrl) {
            window.location.href = notification.actionUrl;
        } else {
            navigate('/notifications');
        }
    };

    const handleDropdownToggle = () => {
        setShowDropdown(!showDropdown);
        if (!showDropdown) {
            fetchRecentNotifications();
        }
    };

    const formatTime = (timestamp) => {
        const date = new Date(timestamp);
        const now = new Date();
        const diffInHours = (now - date) / (1000 * 60 * 60);

        if (diffInHours < 1) {
            const diffInMinutes = Math.floor((now - date) / (1000 * 60));
            return `${diffInMinutes}m`;
        } else if (diffInHours < 24) {
            const hours = Math.floor(diffInHours);
            return `${hours}h`;
        } else {
            const days = Math.floor(diffInHours / 24);
            return `${days}d`;
        }
    };

    const getNotificationIcon = (type) => {
        switch (type) {
            case 'CONTRACT_CREATED':
                return '📄';
            case 'CONTRACT_EXPIRING':
                return '⏰';
            case 'CONTRACT_TERMINATED':
                return '🚫';
            case 'SPACE_AVAILABLE':
                return '🏢';
            case 'MESSAGE_RECEIVED':
                return '💬';
            case 'PAYMENT_DUE':
                return '💰';
            case 'SYSTEM_ALERT':
                return '⚠️';
            default:
                return '🔔';
        }
    };

    return (
        <div className="notification-component" ref={dropdownRef}>
            <button
                className="notification-bell"
                onClick={handleDropdownToggle}
                aria-label="Notifications"
            >
                🔔
                {unreadCount > 0 && (
                    <span className="notification-badge">
                        {unreadCount > 99 ? '99+' : unreadCount}
                    </span>
                )}
            </button>

            {showDropdown && (
                <div className="notification-dropdown">
                    <div className="notification-dropdown-header">
                        <h3>Notificări</h3>
                        {unreadCount > 0 && (
                            <span className="unread-count">{unreadCount} necitite</span>
                        )}
                    </div>

                    <div className="notification-dropdown-content">
                        {loading ? (
                            <div className="notification-loading">Se încarcă...</div>
                        ) : recentNotifications.length === 0 ? (
                            <div className="no-notifications-dropdown">
                                <div className="no-notifications-icon">🔔</div>
                                <p>Nu aveți notificări noi</p>
                            </div>
                        ) : (
                            recentNotifications.map(notification => (
                                <div
                                    key={notification.id}
                                    className={`notification-dropdown-item ${!notification.isRead ? 'unread' : ''}`}
                                    onClick={() => handleNotificationClick(notification)}
                                >
                                    <div className="notification-dropdown-icon">
                                        {getNotificationIcon(notification.type)}
                                    </div>
                                    <div className="notification-dropdown-content-text">
                                        <h4>{notification.title}</h4>
                                        <p>{notification.message}</p>
                                        <span className="notification-dropdown-time">
                                            {formatTime(notification.createdAt)}
                                        </span>
                                    </div>
                                    {!notification.isRead && (
                                        <div className="notification-dropdown-unread-dot"></div>
                                    )}
                                </div>
                            ))
                        )}
                    </div>

                    <div className="notification-dropdown-footer">
                        <button
                            className="view-all-notifications"
                            onClick={() => {
                                setShowDropdown(false);
                                navigate('/notifications');
                            }}
                        >
                            Vezi toate notificările
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}

export default NotificationComponent;