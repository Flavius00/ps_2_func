import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './CreateSpacePage.css';

// Import components for validation
import ValidatedInput from './components/forms/ValidatedInput';
import ValidatedTextarea from './components/forms/ValidatedTextarea';
import ValidatedSelect from './components/forms/ValidatedSelect';

// Assuming validation utils are defined elsewhere
import { validateCoordinates } from './utils/validation';

function CreateSpacePage() {
    const navigate = useNavigate();

    // Basic state management
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [loading, setLoading] = useState(false);
    const [buildings, setBuildings] = useState([]);
    const [user, setUser] = useState(null);
    const [error, setError] = useState('');

    // Form state
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        area: 0,
        pricePerMonth: 0,
        address: '',
        latitude: 0,
        longitude: 0,
        spaceType: 'OFFICE',
        available: true,
        // Office-specific properties
        floors: 1,
        numberOfRooms: 1,
        hasReception: false,
        // Retail-specific properties
        shopWindowSize: 0,
        hasCustomerEntrance: true,
        maxOccupancy: 0,
        // Warehouse-specific properties
        ceilingHeight: 0,
        hasLoadingDock: false,
        securityLevel: 'MEDIUM',
        // Building and other relationships
        buildingId: '',
        amenities: []
    });

    // Validation state
    const [errors, setErrors] = useState({});
    const [touched, setTouched] = useState({});

    // Amenities options
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

    // Load user and buildings on component mount
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

                // Set default building if available
                if (response.data.length > 0) {
                    setFormData(prev => ({
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
    }, [navigate]);

    // Handle form field changes
    const handleChange = useCallback((name, value) => {
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));

        // Mark field as touched
        setTouched(prev => ({
            ...prev,
            [name]: true
        }));
    }, []);

    // Handle amenity checkboxes
    const handleAmenityChange = useCallback((e) => {
        const { value, checked } = e.target;
        const currentAmenities = formData.amenities || [];

        let newAmenities;
        if (checked) {
            newAmenities = [...currentAmenities, value];
        } else {
            newAmenities = currentAmenities.filter(amenity => amenity !== value);
        }

        setFormData(prev => ({
            ...prev,
            amenities: newAmenities
        }));
    }, [formData.amenities]);

    // Handle input blur for validation
    const handleBlur = useCallback((name) => {
        setTouched(prev => ({
            ...prev,
            [name]: true
        }));
        validateField(name);
    }, []);

    // Validate a specific field
    const validateField = useCallback((fieldName) => {
        const newErrors = { ...errors };

        switch (fieldName) {
            case 'name':
                if (!formData.name) {
                    newErrors.name = 'Denumirea spațiului este obligatorie';
                } else if (formData.name.length < 3) {
                    newErrors.name = 'Denumirea trebuie să aibă cel puțin 3 caractere';
                } else {
                    delete newErrors.name;
                }
                break;

            case 'description':
                if (formData.description && formData.description.length < 10) {
                    newErrors.description = 'Descrierea trebuie să aibă cel puțin 10 caractere';
                } else {
                    delete newErrors.description;
                }
                break;

            case 'area':
                if (!formData.area) {
                    newErrors.area = 'Suprafața este obligatorie';
                } else if (formData.area <= 0 || formData.area > 10000) {
                    newErrors.area = 'Suprafața trebuie să fie între 0 și 10000 m²';
                } else {
                    delete newErrors.area;
                }
                break;

            case 'pricePerMonth':
                if (!formData.pricePerMonth) {
                    newErrors.pricePerMonth = 'Prețul este obligatoriu';
                } else if (formData.pricePerMonth <= 0 || formData.pricePerMonth > 100000) {
                    newErrors.pricePerMonth = 'Prețul trebuie să fie între 0 și 100000 €';
                } else {
                    delete newErrors.pricePerMonth;
                }
                break;

            case 'buildingId':
                if (!formData.buildingId) {
                    newErrors.buildingId = 'Selectarea clădirii este obligatorie';
                } else {
                    delete newErrors.buildingId;
                }
                break;

            case 'latitude':
            case 'longitude':
                // Validate coordinates together
                const coordResult = validateCoordinates(formData.latitude, formData.longitude);
                if (!coordResult.isValid) {
                    newErrors.coordinates = Object.values(coordResult.errors).join(', ');
                } else {
                    delete newErrors.coordinates;
                }
                break;

            // Type-specific validations
            case 'floors':
                if (formData.spaceType === 'OFFICE') {
                    if (!formData.floors || formData.floors < 1) {
                        newErrors.floors = 'Numărul de etaje este obligatoriu pentru birouri';
                    } else if (formData.floors > 50) {
                        newErrors.floors = 'Numărul de etaje trebuie să fie între 1 și 50';
                    } else {
                        delete newErrors.floors;
                    }
                } else {
                    delete newErrors.floors;
                }
                break;

            case 'numberOfRooms':
                if (formData.spaceType === 'OFFICE') {
                    if (!formData.numberOfRooms || formData.numberOfRooms < 1) {
                        newErrors.numberOfRooms = 'Numărul de camere este obligatoriu pentru birouri';
                    } else if (formData.numberOfRooms > 100) {
                        newErrors.numberOfRooms = 'Numărul de camere trebuie să fie între 1 și 100';
                    } else {
                        delete newErrors.numberOfRooms;
                    }
                } else {
                    delete newErrors.numberOfRooms;
                }
                break;

            case 'shopWindowSize':
                if (formData.spaceType === 'RETAIL' && formData.shopWindowSize && (formData.shopWindowSize < 0 || formData.shopWindowSize > 50)) {
                    newErrors.shopWindowSize = 'Dimensiunea vitrinei trebuie să fie între 0 și 50 metri';
                } else {
                    delete newErrors.shopWindowSize;
                }
                break;

            case 'maxOccupancy':
                if (formData.spaceType === 'RETAIL' && formData.maxOccupancy && (formData.maxOccupancy < 0 || formData.maxOccupancy > 1000)) {
                    newErrors.maxOccupancy = 'Capacitatea maximă trebuie să fie între 0 și 1000 persoane';
                } else {
                    delete newErrors.maxOccupancy;
                }
                break;

            case 'ceilingHeight':
                if (formData.spaceType === 'WAREHOUSE' && formData.ceilingHeight && (formData.ceilingHeight < 0 || formData.ceilingHeight > 30)) {
                    newErrors.ceilingHeight = 'Înălțimea tavanului trebuie să fie între 0 și 30 metri';
                } else {
                    delete newErrors.ceilingHeight;
                }
                break;

            default:
                break;
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    }, [formData, errors]);

    // Run validation when space type changes
    useEffect(() => {
        if (formData.spaceType === 'OFFICE') {
            validateField('floors');
            validateField('numberOfRooms');
        } else if (formData.spaceType === 'RETAIL') {
            validateField('shopWindowSize');
            validateField('maxOccupancy');
        } else if (formData.spaceType === 'WAREHOUSE') {
            validateField('ceilingHeight');
        }
    }, [formData.spaceType, validateField]);

    // Validate all fields and return if valid
    const validateAllFields = useCallback(() => {
        const fieldsToValidate = [
            'name', 'area', 'pricePerMonth', 'buildingId',
            'latitude', 'longitude'
        ];

        // Add type-specific fields
        if (formData.spaceType === 'OFFICE') {
            fieldsToValidate.push('floors', 'numberOfRooms');
        } else if (formData.spaceType === 'RETAIL') {
            fieldsToValidate.push('shopWindowSize', 'maxOccupancy');
        } else if (formData.spaceType === 'WAREHOUSE') {
            fieldsToValidate.push('ceilingHeight');
        }

        // Mark all as touched
        const newTouched = {};
        fieldsToValidate.forEach(field => {
            newTouched[field] = true;
        });
        setTouched(prev => ({ ...prev, ...newTouched }));

        // Validate each field
        let isValid = true;
        fieldsToValidate.forEach(field => {
            if (!validateField(field)) {
                isValid = false;
            }
        });

        return isValid;
    }, [formData.spaceType, validateField]);

    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        // Validate all fields
        if (!validateAllFields()) {
            setError('Te rugăm să corectezi erorile din formular înainte de a continua.');
            return;
        }

        // Ensure buildingId is properly set
        if (!formData.buildingId || formData.buildingId === '') {
            setError('Vă rugăm să selectați o clădire.');
            return;
        }

        setIsSubmitting(true);

        try {
            console.log('=== CREATE SPACE FRONTEND DEBUG ===');
            console.log('User:', user);
            console.log('Form data before processing:', formData);

            // Create proper data structure matching backend DTO
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
                // Type-specific fields (only if they have values)
                floors: formData.floors ? parseInt(formData.floors) : null,
                numberOfRooms: formData.numberOfRooms ? parseInt(formData.numberOfRooms) : null,
                hasReception: Boolean(formData.hasReception),
                shopWindowSize: formData.shopWindowSize ? parseFloat(formData.shopWindowSize) : null,
                hasCustomerEntrance: Boolean(formData.hasCustomerEntrance),
                maxOccupancy: formData.maxOccupancy ? parseInt(formData.maxOccupancy) : null,
                ceilingHeight: formData.ceilingHeight ? parseFloat(formData.ceilingHeight) : null,
                hasLoadingDock: Boolean(formData.hasLoadingDock),
                securityLevel: formData.securityLevel || 'MEDIUM',
                // Send IDs directly instead of objects
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
            setIsSubmitting(false);
        }
    };

    // Render fields specific to the selected space type
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
                                onChange={(e) => handleChange('floors', e.target.value)}
                                onBlur={() => handleBlur('floors')}
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
                                onChange={(e) => handleChange('numberOfRooms', e.target.value)}
                                onBlur={() => handleBlur('numberOfRooms')}
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
                                onChange={(e) => handleChange('shopWindowSize', e.target.value)}
                                onBlur={() => handleBlur('shopWindowSize')}
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
                                onChange={(e) => handleChange('maxOccupancy', e.target.value)}
                                onBlur={() => handleBlur('maxOccupancy')}
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
                                onChange={(e) => handleChange('ceilingHeight', e.target.value)}
                                onBlur={() => handleBlur('ceilingHeight')}
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
                                onChange={(e) => handleChange('securityLevel', e.target.value)}
                                onBlur={() => handleBlur('securityLevel')}
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

    // Check if user is authorized
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
                        onChange={(e) => handleChange('name', e.target.value)}
                        onBlur={() => handleBlur('name')}
                        error={touched.name ? errors.name : ''}
                        label="Denumire Spațiu"
                        placeholder="Ex: Birou modern în centrul orașului"
                        required
                        disabled={isSubmitting}
                    />

                    <ValidatedTextarea
                        name="description"
                        value={formData.description}
                        onChange={(e) => handleChange('description', e.target.value)}
                        onBlur={() => handleBlur('description')}
                        error={touched.description ? errors.description : ''}
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
                            onChange={(e) => handleChange('area', e.target.value)}
                            onBlur={() => handleBlur('area')}
                            error={touched.area ? errors.area : ''}
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
                            onChange={(e) => handleChange('pricePerMonth', e.target.value)}
                            onBlur={() => handleBlur('pricePerMonth')}
                            error={touched.pricePerMonth ? errors.pricePerMonth : ''}
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
                        onChange={(e) => handleChange('spaceType', e.target.value)}
                        onBlur={() => handleBlur('spaceType')}
                        error={touched.spaceType ? errors.spaceType : ''}
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
                        onChange={(e) => handleChange('buildingId', e.target.value)}
                        onBlur={() => handleBlur('buildingId')}
                        error={touched.buildingId ? errors.buildingId : ''}
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
                        onChange={(e) => handleChange('address', e.target.value)}
                        onBlur={() => handleBlur('address')}
                        error={touched.address ? errors.address : ''}
                        label="Adresă/Detalii Locație"
                        placeholder="Ex: Etaj 3, Aripa Est"
                        disabled={isSubmitting}
                    />

                    <div className="form-row">
                        <ValidatedInput
                            type="number"
                            name="latitude"
                            value={formData.latitude}
                            onChange={(e) => handleChange('latitude', e.target.value)}
                            onBlur={() => handleBlur('latitude')}
                            error={touched.latitude ? (errors.latitude || errors.coordinates) : ''}
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
                            onChange={(e) => handleChange('longitude', e.target.value)}
                            onBlur={() => handleBlur('longitude')}
                            error={touched.longitude ? (errors.longitude || errors.coordinates) : ''}
                            label="Longitudine"
                            placeholder="Ex: 23.6236"
                            step="0.000001"
                            min="-180"
                            max="180"
                            disabled={isSubmitting}
                        />
                    </div>
                </div>

                {/* Display fields specific to the selected space type */}
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
                        disabled={isSubmitting || Object.keys(errors).length > 0}
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