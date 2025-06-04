import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import './MessagesPage.css';

// Import-uri pentru validare
import { useFormValidation } from './hooks/useFormValidation';
import { validateMessage } from './utils/validation';
import ValidatedInput from './components/forms/ValidatedInput';
import ValidatedTextarea from './components/forms/ValidatedTextarea';
import ValidatedSelect from './components/forms/ValidatedSelect';

function MessagesPage() {
    const [user, setUser] = useState(null);
    const [conversations, setConversations] = useState([]);
    const [selectedConversation, setSelectedConversation] = useState(null);
    const [messages, setMessages] = useState([]);
    const [loading, setLoading] = useState(true);
    const [unreadCount, setUnreadCount] = useState(0);
    const [users, setUsers] = useState([]);
    const [showNewMessageModal, setShowNewMessageModal] = useState(false);

    const messagesEndRef = useRef(null);

    // Hook de validare pentru mesajul curent în conversație
    const {
        values: messageFormData,
        errors: messageErrors,
        touched: messageTouched,
        isSubmitting: messageSubmitting,
        handleChange: handleMessageChange,
        handleBlur: handleMessageBlur,
        validateAllFields: validateMessageFields,
        setSubmitting: setMessageSubmitting,
        resetForm: resetMessageForm
    } = useFormValidation({
        newMessage: ''
    }, {
        newMessage: [validateMessage]
    });

    // Hook de validare pentru noul mesaj/conversație
    const {
        values: newConversationData,
        errors: newConversationErrors,
        touched: newConversationTouched,
        isSubmitting: newConversationSubmitting,
        handleChange: handleNewConversationChange,
        handleBlur: handleNewConversationBlur,
        validateAllFields: validateNewConversationFields,
        setSubmitting: setNewConversationSubmitting,
        resetForm: resetNewConversationForm
    } = useFormValidation({
        selectedRecipient: '',
        newConversationMessage: ''
    }, {
        selectedRecipient: [(recipient) => {
            if (!recipient || recipient.trim() === '') {
                return { isValid: false, message: 'Selectează un destinatar' };
            }
            return { isValid: true, message: '' };
        }],
        newConversationMessage: [validateMessage]
    });

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

        // Validează mesajul
        if (!validateMessageFields()) {
            return; // Eroarea este afișată automat
        }

        setMessageSubmitting(true);

        try {
            const success = await sendMessage(selectedConversation.id, messageFormData.newMessage);
            if (success) {
                resetMessageForm();
            }
        } catch (error) {
            console.error('Error sending message:', error);
        } finally {
            setMessageSubmitting(false);
        }
    };

    const handleNewConversation = async (e) => {
        e.preventDefault();

        // Validează formularul pentru conversația nouă
        if (!validateNewConversationFields()) {
            return; // Erorile sunt afișate automat
        }

        setNewConversationSubmitting(true);

        try {
            const success = await sendMessage(
                parseInt(newConversationData.selectedRecipient),
                newConversationData.newConversationMessage
            );

            if (success) {
                resetNewConversationForm();
                setShowNewMessageModal(false);

                // Select the new conversation
                const recipient = users.find(u => u.id === parseInt(newConversationData.selectedRecipient));
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
            setNewConversationSubmitting(false);
        }
    };

    const handleConversationSelect = (conversation) => {
        setSelectedConversation(conversation);
        fetchConversation(conversation.senderId === user.id ? conversation.recipientId : conversation.senderId);
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
                                return (
                                    <div
                                        key={`${conversation.id}-${index}`}
                                        className={`conversation-item ${
                                            selectedConversation?.id === otherUser.id ? 'active' : ''
                                        }`}
                                        onClick={() => {
                                            setSelectedConversation(otherUser);
                                            fetchConversation(otherUser.id);
                                        }}
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
                                <ValidatedInput
                                    type="text"
                                    name="newMessage"
                                    value={messageFormData.newMessage}
                                    onChange={handleMessageChange}
                                    onBlur={handleMessageBlur}
                                    error={messageErrors.newMessage}
                                    placeholder="Scrie un mesaj..."
                                    disabled={messageSubmitting}
                                    className="message-input-field"
                                />
                                <button
                                    type="submit"
                                    disabled={messageSubmitting || !validateMessageFields()}
                                    className="message-send-button"
                                >
                                    {messageSubmitting ? 'Se trimite...' : 'Trimite'}
                                </button>
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
                            <ValidatedSelect
                                name="selectedRecipient"
                                value={newConversationData.selectedRecipient}
                                onChange={handleNewConversationChange}
                                onBlur={handleNewConversationBlur}
                                error={newConversationErrors.selectedRecipient}
                                label="Destinatar"
                                placeholder="Selectează un utilizator..."
                                required
                                disabled={newConversationSubmitting}
                                options={availableUsers.map(u => ({
                                    value: u.id,
                                    label: `${u.name} (${u.role === 'OWNER' ? 'Proprietar' :
                                        u.role === 'TENANT' ? 'Chiriaș' : 'Administrator'})`
                                }))}
                            />

                            <ValidatedTextarea
                                name="newConversationMessage"
                                value={newConversationData.newConversationMessage}
                                onChange={handleNewConversationChange}
                                onBlur={handleNewConversationBlur}
                                error={newConversationErrors.newConversationMessage}
                                label="Mesaj"
                                placeholder="Scrie mesajul tău aici..."
                                rows={4}
                                maxLength={2000}
                                showCharCount={true}
                                required
                                disabled={newConversationSubmitting}
                            />

                            <div className="modal-actions">
                                <button
                                    type="submit"
                                    className="btn btn-primary"
                                    disabled={newConversationSubmitting || !validateNewConversationFields()}
                                >
                                    {newConversationSubmitting ? 'Se trimite...' : 'Trimite mesaj'}
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={() => {
                                        setShowNewMessageModal(false);
                                        resetNewConversationForm();
                                    }}
                                    disabled={newConversationSubmitting}
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