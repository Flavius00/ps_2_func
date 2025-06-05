import React, { useState } from 'react';
import { authInstance } from './helper/axios';
import { useNavigate, Link } from 'react-router-dom';
import './RegisterPage.css';
import { validateName, validateEmail, validatePhone } from './utils/validation';

function RegisterPage() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        username: '',
        password: '',
        confirmPassword: '',
        phone: '',
        address: '',
        role: 'TENANT' // Default role
    });

    const [errors, setErrors] = useState({});
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [serverError, setServerError] = useState('');
    const [passwordVisible, setPasswordVisible] = useState(false);
    const [confirmPasswordVisible, setConfirmPasswordVisible] = useState(false);

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
            case 'username':
                if (!value) {
                    error = 'Numele de utilizator este obligatoriu';
                } else if (value.length < 3) {
                    error = 'Numele de utilizator trebuie să aibă cel puțin 3 caractere';
                } else if (value.length > 50) {
                    error = 'Numele de utilizator nu poate depăși 50 de caractere';
                }
                break;
            case 'password':
                if (!value) {
                    error = 'Parola este obligatorie';
                } else if (value.length < 8) {
                    error = 'Parola trebuie să aibă cel puțin 8 caractere';
                } else if (!/(?=.*[a-z])/.test(value)) {
                    error = 'Parola trebuie să conțină cel puțin o literă mică';
                } else if (!/(?=.*[A-Z])/.test(value)) {
                    error = 'Parola trebuie să conțină cel puțin o literă mare';
                } else if (!/(?=.*\d)/.test(value)) {
                    error = 'Parola trebuie să conțină cel puțin o cifră';
                } else if (!/(?=.*[@$!%*?&])/.test(value)) {
                    error = 'Parola trebuie să conțină cel puțin un caracter special (@$!%*?&)';
                }
                break;
            case 'confirmPassword':
                if (value !== formData.password) {
                    error = 'Parolele nu se potrivesc';
                }
                break;
            case 'phone':
                if (value) {
                    const phoneResult = validatePhone(value);
                    error = phoneResult.isValid ? '' : phoneResult.message;
                }
                break;
            case 'address':
                if (value && value.length > 500) {
                    error = 'Adresa nu poate depăși 500 de caractere';
                }
                break;
            case 'role':
                if (!value) {
                    error = 'Tipul contului este obligatoriu';
                }
                break;
            default:
                break;
        }

        return error;
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        console.log(`Field changed: ${name} = ${value}`); // Debug log

        setFormData({ ...formData, [name]: value });

        // Validate field
        const error = validateField(name, value);
        setErrors({ ...errors, [name]: error });

        // Special case for password confirmation
        if (name === 'password') {
            const confirmError = formData.confirmPassword
                ? (value === formData.confirmPassword ? '' : 'Parolele nu se potrivesc')
                : '';
            setErrors(prev => ({ ...prev, confirmPassword: confirmError }));
        }
    };

    const validateForm = () => {
        const newErrors = {};
        let isValid = true;

        // Validate each field
        Object.keys(formData).forEach(key => {
            if (key === 'address' || key === 'phone') {
                // These fields are optional
                if (formData[key]) {
                    const error = validateField(key, formData[key]);
                    if (error) {
                        newErrors[key] = error;
                        isValid = false;
                    }
                }
            } else {
                const error = validateField(key, formData[key]);
                if (error) {
                    newErrors[key] = error;
                    isValid = false;
                }
            }
        });

        setErrors(newErrors);
        return isValid;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setServerError('');

        if (!validateForm()) {
            return;
        }

        setIsSubmitting(true);

        // Log the data being sent for debugging
        console.log("Registration data being sent:", {
            name: formData.name,
            email: formData.email,
            username: formData.username,
            password: "********", // Don't log the actual password
            phone: formData.phone || null,
            address: formData.address || null,
            role: formData.role
        });

        try {
            const response = await authInstance.post('/auth/register', {
                name: formData.name,
                email: formData.email,
                username: formData.username,
                password: formData.password,
                phone: formData.phone || null,
                address: formData.address || null,
                role: formData.role // Make sure this field is included
            });

            console.log("Registration response:", response.data); // Debug log

            if (response.data && response.data.success) {
                // Registration successful
                alert('Înregistrare reușită! Acum te poți autentifica.');
                navigate('/login');
            } else {
                // Something went wrong
                setServerError(response.data?.message || 'Eroare la înregistrare. Vă rugăm încercați din nou.');
            }
        } catch (error) {
            console.error('Registration error:', error);

            if (error.response) {
                // The request was made and the server responded with a status code
                // that falls out of the range of 2xx
                console.error('Error response:', error.response.data); // Debug log

                if (error.response.status === 409) {
                    // Conflict - usually means username or email already exists
                    const fieldError = error.response.data?.field;
                    if (fieldError === 'username') {
                        setErrors(prev => ({ ...prev, username: 'Acest nume de utilizator există deja' }));
                    } else if (fieldError === 'email') {
                        setErrors(prev => ({ ...prev, email: 'Această adresă de email este deja folosită' }));
                    } else {
                        setServerError(error.response.data?.message || 'Numele de utilizator sau emailul există deja');
                    }
                } else {
                    setServerError(error.response.data?.message || 'Eroare la înregistrare. Vă rugăm încercați din nou.');
                }
            } else if (error.request) {
                // The request was made but no response was received
                console.error('No response received:', error.request); // Debug log
                setServerError('Nu s-a primit răspuns de la server. Verificați conexiunea la internet.');
            } else {
                // Something happened in setting up the request that triggered an Error
                setServerError('Eroare la trimiterea cererii de înregistrare.');
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    const togglePasswordVisibility = () => {
        setPasswordVisible(!passwordVisible);
    };

    const toggleConfirmPasswordVisibility = () => {
        setConfirmPasswordVisible(!confirmPasswordVisible);
    };

    return (
        <div className="register-container">
            <div className="register-form-container">
                <h2>Creează un cont nou</h2>
                {serverError && <div className="server-error">{serverError}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="name">Nume complet*</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            className={errors.name ? 'error' : ''}
                            disabled={isSubmitting}
                        />
                        {errors.name && <div className="error-message">{errors.name}</div>}
                    </div>

                    <div className="form-group">
                        <label htmlFor="email">Email*</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            className={errors.email ? 'error' : ''}
                            disabled={isSubmitting}
                        />
                        {errors.email && <div className="error-message">{errors.email}</div>}
                    </div>

                    <div className="form-group">
                        <label htmlFor="username">Nume de utilizator*</label>
                        <input
                            type="text"
                            id="username"
                            name="username"
                            value={formData.username}
                            onChange={handleChange}
                            className={errors.username ? 'error' : ''}
                            disabled={isSubmitting}
                        />
                        {errors.username && <div className="error-message">{errors.username}</div>}
                    </div>

                    <div className="form-group password-group">
                        <label htmlFor="password">Parolă*</label>
                        <div className="password-input-container">
                            <input
                                type={passwordVisible ? "text" : "password"}
                                id="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                className={errors.password ? 'error' : ''}
                                disabled={isSubmitting}
                            />
                            <button
                                type="button"
                                className="toggle-password"
                                onClick={togglePasswordVisibility}
                            >
                                {passwordVisible ? "👁️" : "👁️‍🗨️"}
                            </button>
                        </div>
                        {errors.password && <div className="error-message">{errors.password}</div>}
                        <div className="password-requirements">
                            Parola trebuie să conțină cel puțin 8 caractere, o literă mare, o literă mică, o cifră și un caracter special.
                        </div>
                    </div>

                    <div className="form-group password-group">
                        <label htmlFor="confirmPassword">Confirmare parolă*</label>
                        <div className="password-input-container">
                            <input
                                type={confirmPasswordVisible ? "text" : "password"}
                                id="confirmPassword"
                                name="confirmPassword"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                className={errors.confirmPassword ? 'error' : ''}
                                disabled={isSubmitting}
                            />
                            <button
                                type="button"
                                className="toggle-password"
                                onClick={toggleConfirmPasswordVisibility}
                            >
                                {confirmPasswordVisible ? "👁️" : "👁️‍🗨️"}
                            </button>
                        </div>
                        {errors.confirmPassword && <div className="error-message">{errors.confirmPassword}</div>}
                    </div>

                    <div className="form-group">
                        <label htmlFor="phone">Telefon</label>
                        <input
                            type="tel"
                            id="phone"
                            name="phone"
                            value={formData.phone}
                            onChange={handleChange}
                            className={errors.phone ? 'error' : ''}
                            disabled={isSubmitting}
                        />
                        {errors.phone && <div className="error-message">{errors.phone}</div>}
                    </div>

                    <div className="form-group">
                        <label htmlFor="address">Adresă</label>
                        <textarea
                            id="address"
                            name="address"
                            value={formData.address}
                            onChange={handleChange}
                            className={errors.address ? 'error' : ''}
                            disabled={isSubmitting}
                            rows="3"
                        ></textarea>
                        {errors.address && <div className="error-message">{errors.address}</div>}
                    </div>

                    <div className="form-group">
                        <label htmlFor="role">Tip cont*</label>
                        <select
                            id="role"
                            name="role"
                            value={formData.role}
                            onChange={handleChange}
                            disabled={isSubmitting}
                            className={errors.role ? 'error' : ''}
                        >
                            <option value="TENANT">Chiriaș</option>
                            <option value="OWNER">Proprietar</option>
                        </select>
                        {errors.role && <div className="error-message">{errors.role}</div>}
                    </div>

                    <button type="submit" className="register-button" disabled={isSubmitting}>
                        {isSubmitting ? 'Se procesează...' : 'Înregistrează-te'}
                    </button>
                </form>

                <div className="login-link">
                    Ai deja un cont? <Link to="/login">Autentifică-te</Link>
                </div>
            </div>
        </div>
    );
}

export default RegisterPage;