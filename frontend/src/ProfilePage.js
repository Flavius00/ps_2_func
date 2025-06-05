import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './ProfilePage.css';
import { useNavigate } from 'react-router-dom';

// Import validări simple
import { validateEmail, validatePhone, validateName } from './utils/validation';

function ProfilePage() {
    const [user, setUser] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const navigate = useNavigate();

    // Formularul de editare - state simplu
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        phone: '',
        address: ''
    });

    // Erorile de validare
    const [errors, setErrors] = useState({});

    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        if (storedUser) {
            setUser(storedUser);
            // Setează datele în formular
            setFormData({
                name: storedUser.name || '',
                email: storedUser.email || '',
                phone: storedUser.phone || '',
                address: storedUser.address || ''
            });
        }
    }, []);

    // Validează un câmp individual
    const validateField = (name, value) => {
        let error = '';

        switch (name) {
            case 'name':
                const nameResult = validateName(value);
                error = nameResult.isValid ? '' : nameResult.message;
                break;
            case 'email':
                const emailResult = validateEmail(value);
                error = emailResult.isValid ? '' : emailResult.message;
                break;
            case 'phone':
                const phoneResult = validatePhone(value);
                error = phoneResult.isValid ? '' : phoneResult.message;
                break;
            case 'address':
                if (value && value.length > 300) {
                    error = 'Adresa nu poate avea mai mult de 300 de caractere';
                }
                break;
            default:
                break;
        }

        setErrors(prev => ({
            ...prev,
            [name]: error
        }));

        return error === '';
    };

    // Validează tot formularul
    const validateAllFields = () => {
        const newErrors = {};
        let isValid = true;

        // Validează numele
        const nameResult = validateName(formData.name);
        if (!nameResult.isValid) {
            newErrors.name = nameResult.message;
            isValid = false;
        }

        // Validează email-ul
        const emailResult = validateEmail(formData.email);
        if (!emailResult.isValid) {
            newErrors.email = emailResult.message;
            isValid = false;
        }

        // Validează telefonul
        const phoneResult = validatePhone(formData.phone);
        if (!phoneResult.isValid) {
            newErrors.phone = phoneResult.message;
            isValid = false;
        }

        // Validează adresa (opțională)
        if (formData.address && formData.address.length > 300) {
            newErrors.address = 'Adresa nu poate avea mai mult de 300 de caractere';
            isValid = false;
        }

        setErrors(newErrors);
        return isValid;
    };

    // Handler pentru schimbarea valorilor
    const handleInputChange = (e) => {
        const { name, value } = e.target;

        setFormData(prev => ({
            ...prev,
            [name]: value
        }));

        // Validează câmpul dacă are deja o eroare
        if (errors[name]) {
            validateField(name, value);
        }
    };

    // Handler pentru blur (când utilizatorul iese din câmp)
    const handleInputBlur = (e) => {
        const { name, value } = e.target;
        validateField(name, value);
    };

    const handleSave = async () => {
        setErrorMessage("");

        // Validează toate câmpurile
        if (!validateAllFields()) {
            setErrorMessage("Te rugăm să corectezi erorile din formular înainte de a salva.");
            return;
        }

        setIsSubmitting(true);

        try {
            console.log('Saving user data:', formData);
            await axios.put(`http://localhost:8080/users/update/${user.id}`, formData);

            // Fetch pentru a obține datele actualizate
            const fetchResponse = await axios.get(`http://localhost:8080/users/${user.id}`);
            const updatedUserData = fetchResponse.data;

            // Actualizează utilizatorul
            localStorage.setItem('user', JSON.stringify(updatedUserData));
            setUser(updatedUserData);
            setIsEditing(false);
            setErrorMessage("");
            setErrors({});
        } catch (error) {
            console.error("Eroare la actualizarea profilului:", error);
            if (error.response && error.response.status === 400) {
                setErrorMessage("Datele introduse nu sunt valide. Verifică formatul email-ului și numărul de telefon.");
            } else {
                setErrorMessage("Nu s-a putut actualiza profilul. Vă rugăm încercați din nou.");
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleCancel = () => {
        // Resetează la valorile utilizatorului curent
        if (user) {
            setFormData({
                name: user.name || '',
                email: user.email || '',
                phone: user.phone || '',
                address: user.address || ''
            });
        }
        setIsEditing(false);
        setErrorMessage("");
        setErrors({});
    };

    const handleEdit = () => {
        setIsEditing(true);
        setErrorMessage("");
        setErrors({});
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

                            <div className="form-group">
                                <label htmlFor="name">Nume complet *</label>
                                <input
                                    type="text"
                                    id="name"
                                    name="name"
                                    value={formData.name}
                                    onChange={handleInputChange}
                                    onBlur={handleInputBlur}
                                    placeholder="Ex: Ion Popescu"
                                    disabled={isSubmitting}
                                    className={errors.name ? 'error' : ''}
                                />
                                {errors.name && <div className="field-error">{errors.name}</div>}
                            </div>

                            <div className="form-group">
                                <label htmlFor="email">Email *</label>
                                <input
                                    type="email"
                                    id="email"
                                    name="email"
                                    value={formData.email}
                                    onChange={handleInputChange}
                                    onBlur={handleInputBlur}
                                    placeholder="Ex: ion.popescu@email.com"
                                    disabled={isSubmitting}
                                    className={errors.email ? 'error' : ''}
                                />
                                {errors.email && <div className="field-error">{errors.email}</div>}
                            </div>

                            <div className="form-group">
                                <label htmlFor="phone">Telefon *</label>
                                <input
                                    type="tel"
                                    id="phone"
                                    name="phone"
                                    value={formData.phone}
                                    onChange={handleInputChange}
                                    onBlur={handleInputBlur}
                                    placeholder="Ex: 0712345678"
                                    disabled={isSubmitting}
                                    className={errors.phone ? 'error' : ''}
                                />
                                {errors.phone && <div className="field-error">{errors.phone}</div>}
                            </div>

                            <div className="form-group">
                                <label htmlFor="address">Adresă</label>
                                <textarea
                                    id="address"
                                    name="address"
                                    value={formData.address}
                                    onChange={handleInputChange}
                                    onBlur={handleInputBlur}
                                    placeholder="Ex: Str. Exemplu nr. 123, Cluj-Napoca"
                                    rows={3}
                                    maxLength={300}
                                    disabled={isSubmitting}
                                    className={errors.address ? 'error' : ''}
                                />
                                {errors.address && <div className="field-error">{errors.address}</div>}
                                <div className="char-count">
                                    {formData.address?.length || 0}/300 caractere
                                </div>
                            </div>

                            <div className="form-actions">
                                <button
                                    className="btn btn-save"
                                    onClick={handleSave}
                                    disabled={isSubmitting}
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
                                <button className="btn btn-edit" onClick={handleEdit}>
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