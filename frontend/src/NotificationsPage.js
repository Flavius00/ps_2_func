import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './NotificationsPage.css';

function NotificationsPage() {
    const [user, setUser] = useState(null);
    const [notifications, setNotifications] = useState([]);
    const [filteredNotifications, setFilteredNotifications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filter, setFilter] = useState('all'); // all, unread, read
    const [typeFilter, setTypeFilter] = useState(''); // specific notification type
    const [unreadCount, setUnreadCount] = useState(0);

    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        setUser(storedUser);

        if (storedUser) {
            fetchNotifications(storedUser.id);
            fetchUnreadCount(storedUser.id);
        }
    }, []);

    useEffect(() => {
        applyFilters();
    }, [notifications, filter, typeFilter]);

    const fetchNotifications = async (userId) => {
        setLoading(true);
        try {
            const response = await axios.get(`http://localhost:8080/notifications/user/${userId}`);
            setNotifications(response.data);
        } catch (error) {
            console.error('Error fetching notifications:', error);
            setNotifications([]);
        } finally {
            setLoading(false);
        }
    };

    const fetchUnreadCount = async (userId) => {
        try {
            const response = await axios.get(`http://localhost:8080/notifications/unread-count/${userId}`);
            setUnreadCount(response.data.count || 0);
        } catch (error) {
            console.error('Error fetching unread count:', error);
        }
    };

    const applyFilters = () => {
        let filtered = [...notifications];

        // Apply read/unread filter
        if (filter === 'unread') {
            filtered = filtered.filter(n => !n.isRead);
        } else if (filter === 'read') {
            filtered = filtered.filter(n => n.isRead);
        }

        // Apply type filter
        if (typeFilter) {
            filtered = filtered.filter(n => n.type === typeFilter);
        }

        // Sort by creation date (newest first)
        filtered.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

        setFilteredNotifications(filtered);
    };

    const markAsRead = async (notificationId) => {
        try {
            await axios.post(`http://localhost:8080/notifications/mark-read/${notificationId}`);

            // Update local state
            setNotifications(prev =>
                prev.map(n =>
                    n.id === notificationId ? { ...n, isRead: true } : n
                )
            );

            // Update unread count
            if (user) {
                fetchUnreadCount(user.id);
            }
        } catch (error) {
            console.error('Error marking notification as read:', error);
        }
    };

    const markAllAsRead = async () => {
        if (!user) return;

        try {
            await axios.post(`http://localhost:8080/notifications/mark-all-read/${user.id}`);

            // Update local state
            setNotifications(prev =>
                prev.map(n => ({ ...n, isRead: true }))
            );

            // Update unread count
            fetchUnreadCount(user.id);
        } catch (error) {
            console.error('Error marking all notifications as read:', error);
        }
    };

    const deleteNotification = async (notificationId) => {
        if (!user) return;

        if (window.confirm('Sigur doriți să ștergeți această notificare?')) {
            try {
                await axios.delete(`http://localhost:8080/notifications/${notificationId}?userId=${user.id}`);

                // Update local state
                setNotifications(prev => prev.filter(n => n.id !== notificationId));

                // Update unread count
                fetchUnreadCount(user.id);
            } catch (error) {
                console.error('Error deleting notification:', error);
                alert('Nu s-a putut șterge notificarea. Încearcă din nou.');
            }
        }
    };

    const handleNotificationClick = (notification) => {
        if (!notification.isRead) {
            markAsRead(notification.id);
        }

        // Navigate to related content if actionUrl exists
        if (notification.actionUrl) {
            window.location.href = notification.actionUrl;
        }
    };

    const formatTime = (timestamp) => {
        const date = new Date(timestamp);
        const now = new Date();
        const diffInHours = (now - date) / (1000 * 60 * 60);

        if (diffInHours < 1) {
            const diffInMinutes = Math.floor((now - date) / (1000 * 60));
            return `acum ${diffInMinutes} ${diffInMinutes === 1 ? 'minut' : 'minute'}`;
        } else if (diffInHours < 24) {
            const hours = Math.floor(diffInHours);
            return `acum ${hours} ${hours === 1 ? 'oră' : 'ore'}`;
        } else {
            return date.toLocaleDateString('ro-RO', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });
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

    const getNotificationTypeLabel = (type) => {
        switch (type) {
            case 'CONTRACT_CREATED':
                return 'Contract creat';
            case 'CONTRACT_EXPIRING':
                return 'Contract expiră';
            case 'CONTRACT_TERMINATED':
                return 'Contract terminat';
            case 'SPACE_AVAILABLE':
                return 'Spațiu disponibil';
            case 'MESSAGE_RECEIVED':
                return 'Mesaj primit';
            case 'PAYMENT_DUE':
                return 'Plată restantă';
            case 'SYSTEM_ALERT':
                return 'Alertă sistem';
            default:
                return 'Notificare';
        }
    };

    const notificationTypes = [
        'CONTRACT_CREATED',
        'CONTRACT_EXPIRING',
        'CONTRACT_TERMINATED',
        'SPACE_AVAILABLE',
        'MESSAGE_RECEIVED',
        'PAYMENT_DUE',
        'SYSTEM_ALERT'
    ];

    if (loading) {
        return <div className="loading-container">Se încarcă notificările...</div>;
    }

    return (
        <div className="notifications-container">
            <div className="notifications-header">
                <h2>Notificări</h2>
                {unreadCount > 0 && (
                    <div className="unread-count-badge">
                        {unreadCount} necitite
                    </div>
                )}
                {unreadCount > 0 && (
                    <button
                        className="btn btn-mark-all-read"
                        onClick={markAllAsRead}
                    >
                        Marchează toate ca citite
                    </button>
                )}
            </div>

            <div className="notifications-filters">
                <div className="filter-group">
                    <label>Stare:</label>
                    <select value={filter} onChange={(e) => setFilter(e.target.value)}>
                        <option value="all">Toate</option>
                        <option value="unread">Necitite</option>
                        <option value="read">Citite</option>
                    </select>
                </div>

                <div className="filter-group">
                    <label>Tip:</label>
                    <select value={typeFilter} onChange={(e) => setTypeFilter(e.target.value)}>
                        <option value="">Toate tipurile</option>
                        {notificationTypes.map(type => (
                            <option key={type} value={type}>
                                {getNotificationTypeLabel(type)}
                            </option>
                        ))}
                    </select>
                </div>
            </div>

            <div className="notifications-list">
                {filteredNotifications.length === 0 ? (
                    <div className="no-notifications">
                        <div className="no-notifications-icon">🔔</div>
                        <h3>Nu aveți notificări</h3>
                        <p>
                            {filter === 'unread'
                                ? 'Nu aveți notificări necitite în acest moment.'
                                : 'Nu s-au găsit notificări care să corespundă criteriilor selectate.'
                            }
                        </p>
                    </div>
                ) : (
                    filteredNotifications.map(notification => (
                        <div
                            key={notification.id}
                            className={`notification-item ${!notification.isRead ? 'unread' : ''}`}
                            onClick={() => handleNotificationClick(notification)}
                        >
                            <div className="notification-icon">
                                {getNotificationIcon(notification.type)}
                            </div>

                            <div className="notification-content">
                                <div className="notification-header">
                                    <h4 className="notification-title">
                                        {notification.title}
                                    </h4>
                                    <div className="notification-meta">
                                        <span className="notification-type">
                                            {getNotificationTypeLabel(notification.type)}
                                        </span>
                                        <span className="notification-time">
                                            {formatTime(notification.createdAt)}
                                        </span>
                                    </div>
                                </div>

                                <p className="notification-message">
                                    {notification.message}
                                </p>

                                {!notification.isRead && (
                                    <div className="unread-indicator-dot"></div>
                                )}
                            </div>

                            <div className="notification-actions">
                                {!notification.isRead && (
                                    <button
                                        className="btn-mark-read"
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            markAsRead(notification.id);
                                        }}
                                        title="Marchează ca citită"
                                    >
                                        ✓
                                    </button>
                                )}

                                <button
                                    className="btn-delete"
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        deleteNotification(notification.id);
                                    }}
                                    title="Șterge notificarea"
                                >
                                    🗑️
                                </button>
                            </div>
                        </div>
                    ))
                )}
            </div>

            {filteredNotifications.length > 0 && (
                <div className="notifications-footer">
                    <p>Se afișează {filteredNotifications.length} din {notifications.length} notificări</p>
                </div>
            )}
        </div>
    );
}

export default NotificationsPage;