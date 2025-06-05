import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './CreateSpacePage.css';

function CreateSpacePage() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [buildings, setBuildings] = useState([]);
    const [user, setUser] = useState(null);
    const [error, setError] = useState('');
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

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;

        if (type === 'checkbox') {
            setFormData(prev => ({
                ...prev,
                [name]: checked
            }));
        } else if (type === 'number') {
            setFormData(prev => ({
                ...prev,
                [name]: parseFloat(value) || 0
            }));
        } else {
            setFormData(prev => ({
                ...prev,
                [name]: value
            }));
        }
    };

    const handleAmenityChange = (e) => {
        const { value, checked } = e.target;

        if (checked) {
            setFormData(prev => ({
                ...prev,
                amenities: [...prev.amenities, value]
            }));
        } else {
            setFormData(prev => ({
                ...prev,
                amenities: prev.amenities.filter(amenity => amenity !== value)
            }));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            // CRITICAL FIX: Ensure buildingId is properly set
            if (!formData.buildingId || formData.buildingId === '') {
                setError('Vă rugăm să selectați o clădire.');
                setLoading(false);
                return;
            }

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
            setLoading(false);
        }
    };

    const renderTypeSpecificFields = () => {
        switch (formData.spaceType) {
            case 'OFFICE':
                return (
                    <div className="form-section">
                        <h3>Detalii Birou</h3>
                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="floors">Număr de etaje:</label>
                                <input
                                    type="number"
                                    id="floors"
                                    name="floors"
                                    min="1"
                                    value={formData.floors}
                                    onChange={handleChange}
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="numberOfRooms">Număr de camere:</label>
                                <input
                                    type="number"
                                    id="numberOfRooms"
                                    name="numberOfRooms"
                                    min="1"
                                    value={formData.numberOfRooms}
                                    onChange={handleChange}
                                />
                            </div>
                        </div>
                        <div className="form-group checkbox">
                            <label>
                                <input
                                    type="checkbox"
                                    name="hasReception"
                                    checked={formData.hasReception}
                                    onChange={handleChange}
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
                            <div className="form-group">
                                <label htmlFor="shopWindowSize">Dimensiune vitrină (m):</label>
                                <input
                                    type="number"
                                    id="shopWindowSize"
                                    name="shopWindowSize"
                                    step="0.1"
                                    min="0"
                                    value={formData.shopWindowSize}
                                    onChange={handleChange}
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="maxOccupancy">Capacitate maximă (persoane):</label>
                                <input
                                    type="number"
                                    id="maxOccupancy"
                                    name="maxOccupancy"
                                    min="0"
                                    value={formData.maxOccupancy}
                                    onChange={handleChange}
                                />
                            </div>
                        </div>
                        <div className="form-group checkbox">
                            <label>
                                <input
                                    type="checkbox"
                                    name="hasCustomerEntrance"
                                    checked={formData.hasCustomerEntrance}
                                    onChange={handleChange}
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
                            <div className="form-group">
                                <label htmlFor="ceilingHeight">Înălțime tavan (m):</label>
                                <input
                                    type="number"
                                    id="ceilingHeight"
                                    name="ceilingHeight"
                                    step="0.1"
                                    min="0"
                                    value={formData.ceilingHeight}
                                    onChange={handleChange}
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="securityLevel">Nivel de securitate:</label>
                                <select
                                    id="securityLevel"
                                    name="securityLevel"
                                    value={formData.securityLevel}
                                    onChange={handleChange}
                                >
                                    <option value="LOW">Scăzut</option>
                                    <option value="MEDIUM">Mediu</option>
                                    <option value="HIGH">Ridicat</option>
                                </select>
                            </div>
                        </div>
                        <div className="form-group checkbox">
                            <label>
                                <input
                                    type="checkbox"
                                    name="hasLoadingDock"
                                    checked={formData.hasLoadingDock}
                                    onChange={handleChange}
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
                    <div className="form-group">
                        <label htmlFor="name">Denumire Spațiu *</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="description">Descriere</label>
                        <textarea
                            id="description"
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            rows="4"
                        />
                    </div>
                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="area">Suprafață (m²) *</label>
                            <input
                                type="number"
                                id="area"
                                name="area"
                                step="0.01"
                                min="0"
                                value={formData.area}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="pricePerMonth">Preț lunar (€) *</label>
                            <input
                                type="number"
                                id="pricePerMonth"
                                name="pricePerMonth"
                                min="0"
                                value={formData.pricePerMonth}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>
                    <div className="form-group">
                        <label htmlFor="spaceType">Tip Spațiu *</label>
                        <select
                            id="spaceType"
                            name="spaceType"
                            value={formData.spaceType}
                            onChange={handleChange}
                            required
                        >
                            <option value="OFFICE">Birou</option>
                            <option value="RETAIL">Spațiu Comercial</option>
                            <option value="WAREHOUSE">Depozit</option>
                        </select>
                    </div>
                </div>

                <div className="form-section">
                    <h3>Locație</h3>
                    <div className="form-group">
                        <label htmlFor="buildingId">Clădire *</label>
                        <select
                            id="buildingId"
                            name="buildingId"
                            value={formData.buildingId}
                            onChange={handleChange}
                            required
                        >
                            <option value="">Selectează o clădire...</option>
                            {buildings.map(building => (
                                <option key={building.id} value={building.id}>
                                    {building.name} - {building.address}
                                </option>
                            ))}
                        </select>
                    </div>
                    <div className="form-group">
                        <label htmlFor="address">Adresă/Detalii Locație</label>
                        <input
                            type="text"
                            id="address"
                            name="address"
                            value={formData.address}
                            onChange={handleChange}
                            placeholder="Ex: Etaj 3, Aripa Est"
                        />
                    </div>
                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="latitude">Latitudine</label>
                            <input
                                type="number"
                                id="latitude"
                                name="latitude"
                                step="0.000001"
                                value={formData.latitude}
                                onChange={handleChange}
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="longitude">Longitudine</label>
                            <input
                                type="number"
                                id="longitude"
                                name="longitude"
                                step="0.000001"
                                value={formData.longitude}
                                onChange={handleChange}
                            />
                        </div>
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
                                        checked={formData.amenities.includes(amenity.label)}
                                        onChange={handleAmenityChange}
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
                                onChange={handleChange}
                            />
                            Disponibil pentru închiriere
                        </label>
                    </div>
                </div>

                <div className="form-actions">
                    <button
                        type="submit"
                        className="btn btn-save"
                        disabled={loading}
                    >
                        {loading ? 'Se creează...' : 'Creează Spațiu'}
                    </button>
                    <button
                        type="button"
                        className="btn btn-cancel"
                        onClick={() => navigate('/spaces')}
                        disabled={loading}
                    >
                        Anulează
                    </button>
                </div>
            </form>
        </div>
    );
}

export default CreateSpacePage;