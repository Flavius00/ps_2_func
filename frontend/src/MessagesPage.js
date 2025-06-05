import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import './MessagesPage.css';

function MessagesPage() {
    const [user, setUser] = useState(null);
    const [conversations, setConversations] = useState([]);
    const [selectedConversation, setSelectedConversation] = useState(null);
    const [messages, setMessages] = useState([]);
    const [loading, setLoading] = useState(true);
    const [unreadCount, setUnreadCount] = useState(0);
    const [users, setUsers] = useState([]);
    const [showNewMessageModal, setShowNewMessageModal] = useState(false);

    // Simplified form states without complex validation hooks
    const [newMessage, setNewMessage] = useState('');
    const [messageError, setMessageError] = useState('');
    const [messageSubmitting, setMessageSubmitting] = useState(false);

    const [selectedRecipient, setSelectedRecipient] = useState('');
    const [newConversationMessage, setNewConversationMessage] = useState('');
    const [recipientError, setRecipientError] = useState('');
    const [conversationMessageError, setConversationMessageError] = useState('');
    const [conversationSubmitting, setConversationSubmitting] = useState(false);

    const messagesEndRef = useRef(null);

    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        setUser(storedUser);

        if (storedUser) {
            fetchRecentConversations(storedUser.id);
            fetchUnreadCount(storedUser.id);
            fetchUsers();
        }
    }, []);

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    const fetchRecentConversations = async (userId) => {
        try {
            const response = await axios.get(`http://localhost:8080/messages/conversations/${userId}`);
            setConversations(response.data);
        } catch (error) {
            console.error('Error fetching conversations:', error);
            setConversations([]);
        }
    };

    const fetchUnreadCount = async (userId) => {
        try {
            const response = await axios.get(`http://localhost:8080/messages/unread-count/${userId}`);
            setUnreadCount(response.data.count || 0);
        } catch (error) {
            console.error('Error fetching unread count:', error);
        }
    };

    const fetchUsers = async () => {
        try {
            const response = await axios.get('http://localhost:8080/users');
            setUsers(response.data);
        } catch (error) {
            console.error('Error fetching users:', error);
        } finally {
            setLoading(false);
        }
    };

    const fetchConversation = async (otherUserId) => {
        try {
            const response = await axios.get(`http://localhost:8080/messages/conversation/${user.id}/${otherUserId}`);
            setMessages(response.data);

            // Mark messages as read
            await axios.post('http://localhost:8080/messages/mark-read', {
                userId: user.id,
                senderId: otherUserId
            });

            // Update unread count
            fetchUnreadCount(user.id);
        } catch (error) {
            console.error('Error fetching conversation:', error);
            setMessages([]);
        }
    };

    const validateMessage = (message) => {
        if (!message || !message.trim()) {
            return 'Mesajul nu poate fi gol';
        }
        if (message.length > 2000) {
            return 'Mesajul nu poate avea mai mult de 2000 de caractere';
        }
        return '';
    };

    const sendMessage = async (recipientId, content) => {
        if (!content.trim()) return;

        try {
            await axios.post('http://localhost:8080/messages/send', {
                senderId: user.id,
                recipientId: recipientId,
                content: content.trim(),
                messageType: 'TEXT'
            });

            // Refresh the conversation
            if (selectedConversation) {
                fetchConversation(recipientId);
            }

            // Refresh conversations list
            fetchRecentConversations(user.id);

            return true;
        } catch (error) {
            console.error('Error sending message:', error);
            alert('Nu s-a putut trimite mesajul. Încearcă din nou.');
            return false;
        }
    };

    const handleSendMessage = async (e) => {
        e.preventDefault();

        if (!selectedConversation) {
            alert('Nu este selectată nicio conversație.');
            return;
        }

        const error = validateMessage(newMessage);
        if (error) {
            setMessageError(error);
            return;
        }

        setMessageError('');
        setMessageSubmitting(true);

        try {
            const success = await sendMessage(selectedConversation.id, newMessage);
            if (success) {
                setNewMessage('');
            }
        } catch (error) {
            console.error('Error sending message:', error);
        } finally {
            setMessageSubmitting(false);
        }
    };

    const handleNewConversation = async (e) => {
        e.preventDefault();

        // Validate recipient
        if (!selectedRecipient) {
            setRecipientError('Selectează un destinatar');
            return;
        }
        setRecipientError('');

        // Validate message
        const messageErr = validateMessage(newConversationMessage);
        if (messageErr) {
            setConversationMessageError(messageErr);
            return;
        }
        setConversationMessageError('');

        setConversationSubmitting(true);

        try {
            const success = await sendMessage(parseInt(selectedRecipient), newConversationMessage);

            if (success) {
                setSelectedRecipient('');
                setNewConversationMessage('');
                setShowNewMessageModal(false);

                // Select the new conversation
                const recipient = users.find(u => u.id === parseInt(selectedRecipient));
                if (recipient) {
                    setSelectedConversation({
                        id: recipient.id,
                        name: recipient.name,
                        role: recipient.role
                    });
                    fetchConversation(recipient.id);
                }
            }
        } catch (error) {
            console.error('Error creating new conversation:', error);
        } finally {
            setConversationSubmitting(false);
        }
    };

    const handleConversationSelect = (conversation) => {
        if (!user) return;

        const otherUser = conversation.senderId === user.id ? {
            id: conversation.recipientId,
            name: conversation.recipientName,
            role: conversation.recipientRole
        } : {
            id: conversation.senderId,
            name: conversation.senderName,
            role: conversation.senderRole
        };

        setSelectedConversation(otherUser);
        fetchConversation(otherUser.id);
    };

    const formatTime = (timestamp) => {
        return new Date(timestamp).toLocaleString('ro-RO', {
            hour: '2-digit',
            minute: '2-digit',
            day: '2-digit',
            month: '2-digit'
        });
    };

    const getOtherUserFromConversation = (conversation) => {
        if (!user) return null;

        return conversation.senderId === user.id ? {
            id: conversation.recipientId,
            name: conversation.recipientName,
            role: conversation.recipientRole
        } : {
            id: conversation.senderId,
            name: conversation.senderName,
            role: conversation.senderRole
        };
    };

    const availableUsers = users.filter(u => u.id !== user?.id);

    if (loading) {
        return <div className="loading-container">Se încarcă mesajele...</div>;
    }

    return (
        <div className="messages-container">
            <div className="messages-header">
                <h2>Mesaje</h2>
                {unreadCount > 0 && (
                    <div className="unread-badge">{unreadCount} necitite</div>
                )}
                <button
                    className="btn btn-new-message"
                    onClick={() => setShowNewMessageModal(true)}
                >
                    + Mesaj nou
                </button>
            </div>

            <div className="messages-content">
                <div className="conversations-sidebar">
                    <h3>Conversații</h3>
                    {conversations.length === 0 ? (
                        <div className="no-conversations">
                            <p>Nu aveți încă conversații.</p>
                            <button
                                className="btn btn-primary"
                                onClick={() => setShowNewMessageModal(true)}
                            >
                                Începe o conversație
                            </button>
                        </div>
                    ) : (
                        <div className="conversations-list">
                            {conversations.map((conversation, index) => {
                                const otherUser = getOtherUserFromConversation(conversation);
                                if (!otherUser) return null;

                                return (
                                    <div
                                        key={`${conversation.id}-${index}`}
                                        className={`conversation-item ${
                                            selectedConversation?.id === otherUser.id ? 'active' : ''
                                        }`}
                                        onClick={() => handleConversationSelect(conversation)}
                                    >
                                        <div className="conversation-info">
                                            <h4>{otherUser.name}</h4>
                                            <p className="conversation-preview">
                                                {conversation.content.length > 50
                                                    ? conversation.content.substring(0, 50) + '...'
                                                    : conversation.content
                                                }
                                            </p>
                                        </div>
                                        <div className="conversation-meta">
                                            <span className="conversation-time">
                                                {formatTime(conversation.sentAt)}
                                            </span>
                                            {!conversation.isRead && conversation.recipientId === user.id && (
                                                <div className="unread-indicator"></div>
                                            )}
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </div>

                <div className="chat-area">
                    {selectedConversation ? (
                        <>
                            <div className="chat-header">
                                <h3>{selectedConversation.name}</h3>
                                <span className="user-role">
                                    {selectedConversation.role === 'OWNER' ? 'Proprietar' :
                                        selectedConversation.role === 'TENANT' ? 'Chiriaș' : 'Administrator'}
                                </span>
                            </div>

                            <div className="messages-list">
                                {messages.map(message => (
                                    <div
                                        key={message.id}
                                        className={`message ${message.senderId === user.id ? 'sent' : 'received'}`}
                                    >
                                        <div className="message-content">
                                            {message.content}
                                        </div>
                                        <div className="message-meta">
                                            <span className="message-time">
                                                {formatTime(message.sentAt)}
                                            </span>
                                            {message.senderId === user.id && (
                                                <span className={`message-status ${message.isRead ? 'read' : 'unread'}`}>
                                                    {message.isRead ? '✓✓' : '✓'}
                                                </span>
                                            )}
                                        </div>
                                    </div>
                                ))}
                                <div ref={messagesEndRef} />
                            </div>

                            <form className="message-input-form" onSubmit={handleSendMessage}>
                                <div className="input-group">
                                    <input
                                        type="text"
                                        value={newMessage}
                                        onChange={(e) => {
                                            setNewMessage(e.target.value);
                                            if (messageError) setMessageError('');
                                        }}
                                        placeholder="Scrie un mesaj..."
                                        disabled={messageSubmitting}
                                        className={`message-input-field ${messageError ? 'error' : ''}`}
                                    />
                                    <button
                                        type="submit"
                                        disabled={messageSubmitting || !newMessage.trim()}
                                        className="message-send-button"
                                    >
                                        {messageSubmitting ? 'Se trimite...' : 'Trimite'}
                                    </button>
                                </div>
                                {messageError && (
                                    <div className="error-message">{messageError}</div>
                                )}
                            </form>
                        </>
                    ) : (
                        <div className="no-chat-selected">
                            <h3>Selectează o conversație</h3>
                            <p>Alege o conversație din listă pentru a vedea mesajele.</p>
                        </div>
                    )}
                </div>
            </div>

            {/* Modal pentru mesaj nou */}
            {showNewMessageModal && (
                <div className="modal-overlay" onClick={() => setShowNewMessageModal(false)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3>Mesaj nou</h3>
                            <button
                                className="close-button"
                                onClick={() => setShowNewMessageModal(false)}
                            >
                                ×
                            </button>
                        </div>
                        <form onSubmit={handleNewConversation}>
                            <div className="form-group">
                                <label>Destinatar *</label>
                                <select
                                    value={selectedRecipient}
                                    onChange={(e) => {
                                        setSelectedRecipient(e.target.value);
                                        if (recipientError) setRecipientError('');
                                    }}
                                    disabled={conversationSubmitting}
                                    className={recipientError ? 'error' : ''}
                                >
                                    <option value="">Selectează un utilizator...</option>
                                    {availableUsers.map(u => (
                                        <option key={u.id} value={u.id}>
                                            {u.name} ({u.role === 'OWNER' ? 'Proprietar' :
                                            u.role === 'TENANT' ? 'Chiriaș' : 'Administrator'})
                                        </option>
                                    ))}
                                </select>
                                {recipientError && (
                                    <div className="error-message">{recipientError}</div>
                                )}
                            </div>

                            <div className="form-group">
                                <label>Mesaj *</label>
                                <textarea
                                    value={newConversationMessage}
                                    onChange={(e) => {
                                        setNewConversationMessage(e.target.value);
                                        if (conversationMessageError) setConversationMessageError('');
                                    }}
                                    placeholder="Scrie mesajul tău aici..."
                                    rows={4}
                                    maxLength={2000}
                                    disabled={conversationSubmitting}
                                    className={conversationMessageError ? 'error' : ''}
                                />
                                <div className="char-count">
                                    {newConversationMessage.length}/2000 caractere
                                </div>
                                {conversationMessageError && (
                                    <div className="error-message">{conversationMessageError}</div>
                                )}
                            </div>

                            <div className="modal-actions">
                                <button
                                    type="submit"
                                    className="btn btn-primary"
                                    disabled={conversationSubmitting || !selectedRecipient || !newConversationMessage.trim()}
                                >
                                    {conversationSubmitting ? 'Se trimite...' : 'Trimite mesaj'}
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={() => {
                                        setShowNewMessageModal(false);
                                        setSelectedRecipient('');
                                        setNewConversationMessage('');
                                        setRecipientError('');
                                        setConversationMessageError('');
                                    }}
                                    disabled={conversationSubmitting}
                                >
                                    Anulează
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

export default MessagesPage;