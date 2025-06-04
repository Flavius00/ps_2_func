import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './CreateSpacePage.css';

// ADĂUGAT: Import-uri pentru validare
import { useFormValidation } from './hooks/useFormValidation';
import { spaceValidationRules, validateCoordinates } from './utils/validation';
import ValidatedInput from './components/forms/ValidatedInput';
import ValidatedTextarea from './components/forms/ValidatedTextarea';
import ValidatedSelect from './components/forms/ValidatedSelect';

function CreateSpacePage() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [buildings, setBuildings] = useState([]);
    const [user, setUser] = useState(null);
    const [error, setError] = useState('');

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
        description: '',
        area: 0,
        pricePerMonth: 0,
        address: '',
        latitude: 0,
        longitude: 0,
        spaceType: 'OFFICE',
        available: true,
        // Proprietăți specifice pentru birouri
        floors: 1,
        numberOfRooms: 1,
        hasReception: false,
        // Proprietăți specifice pentru spații comerciale
        shopWindowSize: 0,
        hasCustomerEntrance: true,
        maxOccupancy: 0,
        // Proprietăți specifice pentru depozite
        ceilingHeight: 0,
        hasLoadingDock: false,
        securityLevel: 'MEDIUM',
        // Clădire și alte relații
        buildingId: '',
        amenities: []
    }, {
        ...spaceValidationRules,
        // Validări suplimentare
        buildingId: [(id) => id ? { isValid: true, message: '' } : { isValid: false, message: 'Selectează o clădire' }],
        coordinates: [() => {
            const coordResult = validateCoordinates(formData.latitude, formData.longitude);
            if (!coordResult.isValid) {
                return {
                    isValid: false,
                    message: Object.values(coordResult.errors).join(', ')
                };
            }
            return { isValid: true, message: '' };
        }],
        // Validări specifice pentru tipuri de spații
        floors: [(floors) => {
            if (formData.spaceType === 'OFFICE' && (!floors || floors < 1)) {
                return { isValid: false, message: 'Numărul de etaje este obligatoriu pentru birouri' };
            }
            if (floors && (floors < 1 || floors > 50)) {
                return { isValid: false, message: 'Numărul de etaje trebuie să fie între 1 și 50' };
            }
            return { isValid: true, message: '' };
        }],
        numberOfRooms: [(rooms) => {
            if (formData.spaceType === 'OFFICE' && (!rooms || rooms < 1)) {
                return { isValid: false, message: 'Numărul de camere este obligatoriu pentru birouri' };
            }
            if (rooms && (rooms < 1 || rooms > 100)) {
                return { isValid: false, message: 'Numărul de camere trebuie să fie între 1 și 100' };
            }
            return { isValid: true, message: '' };
        }],
        shopWindowSize: [(size) => {
            if (formData.spaceType === 'RETAIL' && size && (size < 0 || size > 50)) {
                return { isValid: false, message: 'Dimensiunea vitrinei trebuie să fie între 0 și 50 metri' };
            }
            return { isValid: true, message: '' };
        }],
        maxOccupancy: [(occupancy) => {
            if (formData.spaceType === 'RETAIL' && occupancy && (occupancy < 0 || occupancy > 1000)) {
                return { isValid: false, message: 'Capacitatea maximă trebuie să fie între 0 și 1000 persoane' };
            }
            return { isValid: true, message: '' };
        }],
        ceilingHeight: [(height) => {
            if (formData.spaceType === 'WAREHOUSE' && height && (height < 0 || height > 30)) {
                return { isValid: false, message: 'Înălțimea tavanului trebuie să fie între 0 și 30 metri' };
            }
            return { isValid: true, message: '' };
        }]
    });

    // Lista de facilități pentru checkbox-uri
    const amenitiesOptions = [
        { id: 'air-conditioning', label: 'Aer condiționat' },
        { id: 'heating', label: 'Încălzire' },
        { id: 'internet', label: 'Internet de mare viteză' },
        { id: 'parking', label: 'Parcare' },
        { id: 'security', label: 'Securitate 24/7' },
        { id: 'reception', label: 'Recepție' },
        { id: 'meeting-rooms', label: 'Săli de ședințe' },
        { id: 'kitchen', label: 'Bucătărie/Chicinetă' },
        { id: 'elevator', label: 'Lift' },
        { id: 'disabled-access', label: 'Acces persoane cu dizabilități' },
        { id: 'loading-dock', label: 'Rampă de încărcare' },
        { id: 'storage', label: 'Spațiu depozitare' }
    ];

    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        if (!storedUser || (storedUser.role !== 'OWNER' && storedUser.role !== 'ADMIN')) {
            navigate('/spaces');
            return;
        }
        setUser(storedUser);

        const fetchBuildings = async () => {
            try {
                const response = await axios.get('http://localhost:8080/buildings');
                setBuildings(response.data);

                // Setează implicit prima clădire dacă există
                if (response.data.length > 0) {
                    setFormValues(prev => ({
                        ...prev,
                        buildingId: response.data[0].id
                    }));
                }
            } catch (error) {
                console.error('Eroare la încărcarea clădirilor:', error);
                setError('Nu s-au putut încărca clădirile. Verificați conexiunea și încercați din nou.');
            }
        };

        fetchBuildings();
    }, [navigate, setFormValues]);

    // ACTUALIZAT: handleAmenityChange pentru a lucra cu hook-ul de validare
    const handleAmenityChange = (e) => {
        const { value, checked } = e.target;
        const currentAmenities = formData.amenities || [];

        let newAmenities;
        if (checked) {
            newAmenities = [...currentAmenities, value];
        } else {
            newAmenities = currentAmenities.filter(amenity => amenity !== value);
        }

        handleChange('amenities', newAmenities);
    };

    // ACTUALIZAT: handleSubmit cu validare
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        // Validează toate câmpurile
        if (!validateAllFields()) {
            setError('Te rugăm să corectezi erorile din formular înainte de a continua.');
            return;
        }

        // CRITICAL FIX: Ensure buildingId is properly set
        if (!formData.buildingId || formData.buildingId === '') {
            setError('Vă rugăm să selectați o clădire.');
            return;
        }

        setSubmitting(true);

        try {
            console.log('=== CREATE SPACE FRONTEND DEBUG ===');
            console.log('User:', user);
            console.log('Form data before processing:', formData);

            // CRITICAL FIX: Create proper data structure matching backend DTO
            const spaceData = {
                name: formData.name,
                description: formData.description,
                area: parseFloat(formData.area) || 0,
                pricePerMonth: parseFloat(formData.pricePerMonth) || 0,
                address: formData.address,
                latitude: parseFloat(formData.latitude) || 0,
                longitude: parseFloat(formData.longitude) || 0,
                spaceType: formData.spaceType,
                available: formData.available,
                amenities: formData.amenities || [],
                // Type-specific fields (doar dacă au valori)
                floors: formData.floors ? parseInt(formData.floors) : null,
                numberOfRooms: formData.numberOfRooms ? parseInt(formData.numberOfRooms) : null,
                hasReception: Boolean(formData.hasReception),
                shopWindowSize: formData.shopWindowSize ? parseFloat(formData.shopWindowSize) : null,
                hasCustomerEntrance: Boolean(formData.hasCustomerEntrance),
                maxOccupancy: formData.maxOccupancy ? parseInt(formData.maxOccupancy) : null,
                ceilingHeight: formData.ceilingHeight ? parseFloat(formData.ceilingHeight) : null,
                hasLoadingDock: Boolean(formData.hasLoadingDock),
                securityLevel: formData.securityLevel || 'MEDIUM',
                // CRITICAL FIX: Send IDs directly instead of objects
                ownerId: user.id,
                buildingId: parseInt(formData.buildingId)
            };

            console.log('Space data to send:', spaceData);
            console.log('Owner ID:', spaceData.ownerId);
            console.log('Building ID:', spaceData.buildingId);

            // Send data to server
            const response = await axios.post('http://localhost:8080/spaces/create', spaceData, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            console.log('Space created successfully:', response.data);
            console.log('=== END CREATE SPACE FRONTEND DEBUG ===');

            // Navigate back to spaces page with success message
            navigate('/spaces', {
                state: {
                    message: 'Spațiul a fost adăugat cu succes!',
                    newSpace: response.data
                }
            });
        } catch (error) {
            console.error('Eroare la crearea spațiului:', error);
            if (error.response) {
                console.error('Server response status:', error.response.status);
                console.error('Server response data:', error.response.data);

                // Handle validation errors
                if (error.response.status === 400 && error.response.data.errors) {
                    const validationErrors = error.response.data.errors;
                    const errorMessages = Object.entries(validationErrors)
                        .map(([field, message]) => `${field}: ${message}`)
                        .join('\n');
                    setError(`Erori de validare:\n${errorMessages}`);
                } else if (error.response.data.message) {
                    setError(`Nu s-a putut crea spațiul: ${error.response.data.message}`);
                } else {
                    setError(`Nu s-a putut crea spațiul: ${error.response.data || 'Eroare nespecificată'}`);
                }
            } else {
                setError('Nu s-a putut crea spațiul. Verificați datele introduse și încercați din nou.');
            }
        } finally {
            setSubmitting(false);
        }
    };

    const renderTypeSpecificFields = () => {
        switch (formData.spaceType) {
            case 'OFFICE':
                return (
                    <div className="form-section">
                        <h3>Detalii Birou</h3>
                        <div className="form-row">
                            <ValidatedInput
                                type="number"
                                name="floors"
                                value={formData.floors}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={errors.floors}
                                label="Număr de etaje"
                                placeholder="Ex: 2"
                                min="1"
                                max="50"
                                disabled={isSubmitting}
                            />

                            <ValidatedInput
                                type="number"
                                name="numberOfRooms"
                                value={formData.numberOfRooms}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={errors.numberOfRooms}
                                label="Număr de camere"
                                placeholder="Ex: 5"
                                min="1"
                                max="100"
                                disabled={isSubmitting}
                            />
                        </div>
                        <div className="form-group checkbox">
                            <label>
                                <input
                                    type="checkbox"
                                    name="hasReception"
                                    checked={formData.hasReception}
                                    onChange={(e) => handleChange('hasReception', e.target.checked)}
                                    disabled={isSubmitting}
                                />
                                Are zonă de recepție
                            </label>
                        </div>
                    </div>
                );
            case 'RETAIL':
                return (
                    <div className="form-section">
                        <h3>Detalii Spațiu Comercial</h3>
                        <div className="form-row">
                            <ValidatedInput
                                type="number"
                                name="shopWindowSize"
                                value={formData.shopWindowSize}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={errors.shopWindowSize}
                                label="Dimensiune vitrină (m)"
                                placeholder="Ex: 3.5"
                                step="0.1"
                                min="0"
                                max="50"
                                disabled={isSubmitting}
                            />

                            <ValidatedInput
                                type="number"
                                name="maxOccupancy"
                                value={formData.maxOccupancy}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={errors.maxOccupancy}
                                label="Capacitate maximă (persoane)"
                                placeholder="Ex: 50"
                                min="0"
                                max="1000"
                                disabled={isSubmitting}
                            />
                        </div>
                        <div className="form-group checkbox">
                            <label>
                                <input
                                    type="checkbox"
                                    name="hasCustomerEntrance"
                                    checked={formData.hasCustomerEntrance}
                                    onChange={(e) => handleChange('hasCustomerEntrance', e.target.checked)}
                                    disabled={isSubmitting}
                                />
                                Are intrare separată pentru clienți
                            </label>
                        </div>
                    </div>
                );
            case 'WAREHOUSE':
                return (
                    <div className="form-section">
                        <h3>Detalii Depozit</h3>
                        <div className="form-row">
                            <ValidatedInput
                                type="number"
                                name="ceilingHeight"
                                value={formData.ceilingHeight}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={errors.ceilingHeight}
                                label="Înălțime tavan (m)"
                                placeholder="Ex: 4.5"
                                step="0.1"
                                min="0"
                                max="30"
                                disabled={isSubmitting}
                            />

                            <ValidatedSelect
                                name="securityLevel"
                                value={formData.securityLevel}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={errors.securityLevel}
                                label="Nivel de securitate"
                                disabled={isSubmitting}
                                options={[
                                    { value: 'LOW', label: 'Scăzut' },
                                    { value: 'MEDIUM', label: 'Mediu' },
                                    { value: 'HIGH', label: 'Ridicat' }
                                ]}
                            />
                        </div>
                        <div className="form-group checkbox">
                            <label>
                                <input
                                    type="checkbox"
                                    name="hasLoadingDock"
                                    checked={formData.hasLoadingDock}
                                    onChange={(e) => handleChange('hasLoadingDock', e.target.checked)}
                                    disabled={isSubmitting}
                                />
                                Are rampă de încărcare
                            </label>
                        </div>
                    </div>
                );
            default:
                return null;
        }
    };

    if (!user || (user.role !== 'OWNER' && user.role !== 'ADMIN')) {
        return <div className="loading-container">Redirecționare...</div>;
    }

    return (
        <div className="create-space-container">
            <div className="create-space-header">
                <h2>Adaugă Spațiu Nou</h2>
                <button className="btn-back" onClick={() => navigate('/spaces')}>
                    ← Înapoi la Spații
                </button>
            </div>

            {error && <div className="error-message">{error}</div>}

            <form onSubmit={handleSubmit} className="create-space-form">
                <div className="form-section">
                    <h3>Informații de Bază</h3>

                    <ValidatedInput
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        onBlur={handleBlur}
                        error={errors.name}
                        label="Denumire Spațiu"
                        placeholder="Ex: Birou modern în centrul orașului"
                        required
                        disabled={isSubmitting}
                    />

                    <ValidatedTextarea
                        name="description"
                        value={formData.description}
                        onChange={handleChange}
                        onBlur={handleBlur}
                        error={errors.description}
                        label="Descriere"
                        placeholder="Descrierea detaliată a spațiului..."
                        rows={4}
                        maxLength={1000}
                        showCharCount={true}
                        disabled={isSubmitting}
                    />

                    <div className="form-row">
                        <ValidatedInput
                            type="number"
                            name="area"
                            value={formData.area}
                            onChange={handleChange}
                            onBlur={handleBlur}
                            error={errors.area}
                            label="Suprafață (m²)"
                            placeholder="Ex: 50"
                            step="0.01"
                            min="0"
                            max="10000"
                            required
                            disabled={isSubmitting}
                        />

                        <ValidatedInput
                            type="number"
                            name="pricePerMonth"
                            value={formData.pricePerMonth}
                            onChange={handleChange}
                            onBlur={handleBlur}
                            error={errors.pricePerMonth}
                            label="Preț lunar (€)"
                            placeholder="Ex: 500"
                            min="0"
                            max="100000"
                            required
                            disabled={isSubmitting}
                        />
                    </div>

                    <ValidatedSelect
                        name="spaceType"
                        value={formData.spaceType}
                        onChange={handleChange}
                        onBlur={handleBlur}
                        error={errors.spaceType}
                        label="Tip Spațiu"
                        required
                        disabled={isSubmitting}
                        options={[
                            { value: 'OFFICE', label: 'Birou' },
                            { value: 'RETAIL', label: 'Spațiu Comercial' },
                            { value: 'WAREHOUSE', label: 'Depozit' }
                        ]}
                    />
                </div>

                <div className="form-section">
                    <h3>Locație</h3>

                    <ValidatedSelect
                        name="buildingId"
                        value={formData.buildingId}
                        onChange={handleChange}
                        onBlur={handleBlur}
                        error={errors.buildingId}
                        label="Clădire"
                        required
                        disabled={isSubmitting}
                        placeholder="Selectează o clădire..."
                        options={buildings.map(building => ({
                            value: building.id,
                            label: `${building.name} - ${building.address}`
                        }))}
                    />

                    <ValidatedInput
                        type="text"
                        name="address"
                        value={formData.address}
                        onChange={handleChange}
                        onBlur={handleBlur}
                        error={errors.address}
                        label="Adresă/Detalii Locație"
                        placeholder="Ex: Etaj 3, Aripa Est"
                        disabled={isSubmitting}
                    />

                    <div className="form-row">
                        <ValidatedInput
                            type="number"
                            name="latitude"
                            value={formData.latitude}
                            onChange={handleChange}
                            onBlur={handleBlur}
                            error={errors.latitude || (errors.coordinates && errors.coordinates.includes('Latitudine') ? errors.coordinates : '')}
                            label="Latitudine"
                            placeholder="Ex: 46.7712"
                            step="0.000001"
                            min="-90"
                            max="90"
                            disabled={isSubmitting}
                        />

                        <ValidatedInput
                            type="number"
                            name="longitude"
                            value={formData.longitude}
                            onChange={handleChange}
                            onBlur={handleBlur}
                            error={errors.longitude || (errors.coordinates && errors.coordinates.includes('Longitudine') ? errors.coordinates : '')}
                            label="Longitudine"
                            placeholder="Ex: 23.6236"
                            step="0.000001"
                            min="-180"
                            max="180"
                            disabled={isSubmitting}
                        />
                    </div>
                </div>

                {/* Afișează câmpurile specifice tipului de spațiu selectat */}
                {renderTypeSpecificFields()}

                <div className="form-section">
                    <h3>Facilități</h3>
                    <div className="amenities-container">
                        {amenitiesOptions.map(amenity => (
                            <div key={amenity.id} className="form-group checkbox">
                                <label>
                                    <input
                                        type="checkbox"
                                        name="amenities"
                                        value={amenity.label}
                                        checked={(formData.amenities || []).includes(amenity.label)}
                                        onChange={handleAmenityChange}
                                        disabled={isSubmitting}
                                    />
                                    {amenity.label}
                                </label>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="form-section">
                    <h3>Status</h3>
                    <div className="form-group checkbox">
                        <label>
                            <input
                                type="checkbox"
                                name="available"
                                checked={formData.available}
                                onChange={(e) => handleChange('available', e.target.checked)}
                                disabled={isSubmitting}
                            />
                            Disponibil pentru închiriere
                        </label>
                    </div>
                </div>

                <div className="form-actions">
                    <button
                        type="submit"
                        className="btn btn-save"
                        disabled={isSubmitting || !validateAllFields()}
                    >
                        {isSubmitting ? 'Se creează...' : 'Creează Spațiu'}
                    </button>
                    <button
                        type="button"
                        className="btn btn-cancel"
                        onClick={() => navigate('/spaces')}
                        disabled={isSubmitting}
                    >
                        Anulează
                    </button>
                </div>
            </form>
        </div>
    );
}

export default CreateSpacePage;