import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './ProfilePage.css';
import { useNavigate } from 'react-router-dom';

// ADĂUGAT: Import-uri pentru validare
import { useFormValidation } from './hooks/useFormValidation';
import { userValidationRules } from './utils/validation';
import ValidatedInput from './components/forms/ValidatedInput';
import ValidatedTextarea from './components/forms/ValidatedTextarea';

function ProfilePage() {
    const [user, setUser] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const navigate = useNavigate();

    // ÎNLOCUIT: useState pentru formData cu useFormValidation
    const {
        values: formData,
        errors,
        touched,
        isSubmitting,
        handleChange,
        handleBlur,
        validateAllFields,
        setSubmitting,
        setFormValues
    } = useFormValidation({
        name: '',
        email: '',
        phone: '',
        address: ''
    }, {
        ...userValidationRules,
        // Address nu e obligatoriu, dar validăm lungimea dacă e completat
        address: [(addr) => {
            if (!addr || addr.trim() === '') return { isValid: true, message: '' };
            if (addr.length > 300) return { isValid: false, message: 'Adresa nu poate avea mai mult de 300 de caractere' };
            return { isValid: true, message: '' };
        }]
    });

    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        if (storedUser) {
            setUser(storedUser);
            // ADĂUGAT: Setează valorile în formularul de validare
            setFormValues({
                name: storedUser.name || '',
                email: storedUser.email || '',
                phone: storedUser.phone || '',
                address: storedUser.address || ''
            });
        }
    }, [setFormValues]);

    // ACTUALIZAT: handleSave cu validare
    const handleSave = async () => {
        setErrorMessage("");

        // Validează toate câmpurile
        if (!validateAllFields()) {
            setErrorMessage("Te rugăm să corectezi erorile din formular înainte de a salva.");
            return;
        }

        setSubmitting(true);

        try {
            console.log('Saving user data:', formData);
            await axios.put(`http://localhost:8080/users/update/${user.id}`, formData);

            // Fetch separat pentru a obține datele actualizate ale utilizatorului
            const fetchResponse = await axios.get(`http://localhost:8080/users/${user.id}`);
            const updatedUserData = fetchResponse.data;
            console.log('Updated user data:', updatedUserData);

            // Actualizează utilizatorul în localStorage și state
            localStorage.setItem('user', JSON.stringify(updatedUserData));
            setUser(updatedUserData);
            setIsEditing(false);
            setErrorMessage("");
        } catch (error) {
            console.error("Eroare la actualizarea profilului:", error);
            if (error.response && error.response.status === 400) {
                setErrorMessage("Datele introduse nu sunt valide. Verifică formatul email-ului și numărul de telefon.");
            } else {
                setErrorMessage("Nu s-a putut actualiza profilul. Vă rugăm încercați din nou.");
            }
        } finally {
            setSubmitting(false);
        }
    };

    const handleCancel = () => {
        // ACTUALIZAT: Resetează la valorile utilizatorului curent
        setFormValues({
            name: user.name || '',
            email: user.email || '',
            phone: user.phone || '',
            address: user.address || ''
        });
        setIsEditing(false);
        setErrorMessage("");
    };

    const handleLogout = () => {
        localStorage.removeItem('user');
        navigate('/login');
    };

    if (!user) {
        return <div className="loading-container">Se încarcă...</div>;
    }

    return (
        <div className="profile-container">
            <div className="profile-header">
                <h1>Profilul meu</h1>
                <button className="btn-logout" onClick={handleLogout}>Deconectare</button>
            </div>

            <div className="profile-content">
                <div className="profile-sidebar">
                    <div className="profile-role">
                        <span className={`role-badge ${user.role?.toLowerCase()}`}>
                            {user.role === 'OWNER' ? 'Proprietar' :
                                user.role === 'TENANT' ? 'Chiriaș' :
                                    user.role === 'ADMIN' ? 'Administrator' : 'Utilizator'}
                        </span>
                    </div>
                    <div className="profile-navigation">
                        <button className="nav-btn" onClick={() => navigate('/home')}>Pagina principală</button>
                        <button className="nav-btn" onClick={() => navigate('/spaces')}>Spații</button>
                        <button className="nav-btn" onClick={() => navigate('/contracts')}>Contracte</button>
                    </div>
                </div>

                <div className="profile-details">
                    {isEditing ? (
                        <div className="profile-edit-form">
                            <h2>Editare Profil</h2>
                            {errorMessage && <div className="error-message">{errorMessage}</div>}

                            {/* ÎNLOCUIT: Input-urile clasice cu ValidatedInput */}
                            <ValidatedInput
                                type="text"
                                name="name"
                                value={formData.name}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={errors.name}
                                label="Nume complet"
                                placeholder="Ex: Ion Popescu"
                                required
                                disabled={isSubmitting}
                            />

                            <ValidatedInput
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={errors.email}
                                label="Email"
                                placeholder="Ex: ion.popescu@email.com"
                                required
                                disabled={isSubmitting}
                            />

                            <ValidatedInput
                                type="tel"
                                name="phone"
                                value={formData.phone}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={errors.phone}
                                label="Telefon"
                                placeholder="Ex: 0712345678"
                                required
                                disabled={isSubmitting}
                            />

                            <ValidatedTextarea
                                name="address"
                                value={formData.address}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={errors.address}
                                label="Adresă"
                                placeholder="Ex: Str. Exemplu nr. 123, Cluj-Napoca"
                                rows={3}
                                maxLength={300}
                                showCharCount={true}
                                disabled={isSubmitting}
                            />

                            <div className="form-actions">
                                <button
                                    className="btn btn-save"
                                    onClick={handleSave}
                                    disabled={isSubmitting || !validateAllFields()}
                                >
                                    {isSubmitting ? 'Se salvează...' : 'Salvează'}
                                </button>
                                <button
                                    className="btn btn-cancel"
                                    onClick={handleCancel}
                                    disabled={isSubmitting}
                                >
                                    Anulează
                                </button>
                            </div>
                        </div>
                    ) : (
                        <div className="profile-info">
                            <div className="profile-info-header">
                                <h2>Informații profil</h2>
                                <button className="btn btn-edit" onClick={() => setIsEditing(true)}>
                                    Editează
                                </button>
                            </div>

                            <div className="info-section">
                                <div className="info-item">
                                    <span className="info-label">Nume complet:</span>
                                    <span className="info-value">{user.name}</span>
                                </div>

                                <div className="info-item">
                                    <span className="info-label">Email:</span>
                                    <span className="info-value">{user.email}</span>
                                </div>

                                <div className="info-item">
                                    <span className="info-label">Nume utilizator:</span>
                                    <span className="info-value">{user.username}</span>
                                </div>

                                <div className="info-item">
                                    <span className="info-label">Telefon:</span>
                                    <span className="info-value">{user.phone || 'Nespecificat'}</span>
                                </div>

                                <div className="info-item">
                                    <span className="info-label">Adresă:</span>
                                    <span className="info-value">{user.address || 'Nespecificat'}</span>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default ProfilePage;