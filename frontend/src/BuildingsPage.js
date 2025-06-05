import React, { useEffect, useState, useCallback } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './BuildingsPage.css';

// Import validation components
import ValidatedInput from './components/forms/ValidatedInput';
import { validateCoordinates } from './utils/validation';

function BuildingsPage() {
    const [buildings, setBuildings] = useState([]);
    const [filteredBuildings, setFilteredBuildings] = useState([]);
    const [user, setUser] = useState(null);
    const [isCreating, setIsCreating] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentBuilding, setCurrentBuilding] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [sortOption, setSortOption] = useState('name');
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Form state
    const [formData, setFormData] = useState({
        name: '',
        address: '',
        totalFloors: 1,
        yearBuilt: 2000,
        latitude: 0,
        longitude: 0
    });

    // Validation state
    const [errors, setErrors] = useState({});
    const [touched, setTouched] = useState({});

    const navigate = useNavigate();

    // Fetch buildings on component mount
    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        setUser(storedUser);

        const fetchBuildings = async () => {
            try {
                const response = await axios.get('http://localhost:8080/buildings');
                setBuildings(response.data);
                setFilteredBuildings(response.data);
            } catch (error) {
                console.error('Error fetching buildings:', error);
            }
        };

        fetchBuildings();
    }, []);

    // Filter buildings when searchTerm, sortOption, or buildings change
    useEffect(() => {
        handleFilter();
    }, [searchTerm, sortOption, buildings]);

    // Filter and sort buildings
    const handleFilter = useCallback(() => {
        let filtered = [...buildings];

        // Apply search filter
        if (searchTerm.trim()) {
            const lowercasedSearch = searchTerm.toLowerCase();
            filtered = filtered.filter(building =>
                building.name.toLowerCase().includes(lowercasedSearch) ||
                building.address.toLowerCase().includes(lowercasedSearch)
            );
        }

        // Apply sorting
        if (sortOption === 'name') {
            filtered.sort((a, b) => a.name.localeCompare(b.name));
        } else if (sortOption === 'yearDesc') {
            filtered.sort((a, b) => b.yearBuilt - a.yearBuilt);
        } else if (sortOption === 'yearAsc') {
            filtered.sort((a, b) => a.yearBuilt - b.yearBuilt);
        } else if (sortOption === 'floorsDesc') {
            filtered.sort((a, b) => b.totalFloors - a.totalFloors);
        }

        setFilteredBuildings(filtered);
    }, [buildings, searchTerm, sortOption]);

    // Handle form input changes
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
                    newErrors.name = 'Numele clădirii este obligatoriu';
                } else if (formData.name.length < 3) {
                    newErrors.name = 'Numele trebuie să aibă cel puțin 3 caractere';
                } else {
                    delete newErrors.name;
                }
                break;

            case 'address':
                if (!formData.address) {
                    newErrors.address = 'Adresa este obligatorie';
                } else if (formData.address.length < 5) {
                    newErrors.address = 'Adresa trebuie să aibă cel puțin 5 caractere';
                } else {
                    delete newErrors.address;
                }
                break;

            case 'totalFloors':
                if (!formData.totalFloors) {
                    newErrors.totalFloors = 'Numărul de etaje este obligatoriu';
                } else if (formData.totalFloors < 1 || formData.totalFloors > 100) {
                    newErrors.totalFloors = 'Numărul de etaje trebuie să fie între 1 și 100';
                } else {
                    delete newErrors.totalFloors;
                }
                break;

            case 'yearBuilt':
                const currentYear = new Date().getFullYear();
                if (!formData.yearBuilt) {
                    newErrors.yearBuilt = 'Anul construcției este obligatoriu';
                } else if (formData.yearBuilt < 1800 || formData.yearBuilt > currentYear) {
                    newErrors.yearBuilt = `Anul construcției trebuie să fie între 1800 și ${currentYear}`;
                } else {
                    delete newErrors.yearBuilt;
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

            default:
                break;
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    }, [formData, errors]);

    // Validate all fields
    const validateAllFields = useCallback(() => {
        const fieldsToValidate = ['name', 'address', 'totalFloors', 'yearBuilt', 'latitude', 'longitude'];

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
    }, [validateField]);

    // Reset form
    const resetForm = useCallback(() => {
        setFormData({
            name: '',
            address: '',
            totalFloors: 1,
            yearBuilt: 2000,
            latitude: 0,
            longitude: 0
        });
        setErrors({});
        setTouched({});
    }, []);

    // Create new building
    const handleCreateSubmit = async (e) => {
        e.preventDefault();

        // Validate all fields
        if (!validateAllFields()) {
            alert('Te rugăm să corectezi erorile din formular înainte de a continua.');
            return;
        }

        setIsSubmitting(true);

        try {
            const response = await axios.post('http://localhost:8080/buildings', formData);
            setBuildings([...buildings, response.data]);
            setIsCreating(false);
            resetForm();
            alert('Clădirea a fost adăugată cu succes!');
        } catch (error) {
            console.error('Error creating building:', error);
            if (error.response && error.response.status === 400) {
                alert('Datele introduse nu sunt valide. Verifică toate câmpurile și încearcă din nou.');
            } else {
                alert('Eroare la adăugarea clădirii.');
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    // Update existing building
    const handleUpdateSubmit = async (e) => {
        e.preventDefault();

        // Validate all fields
        if (!validateAllFields()) {
            alert('Te rugăm să corectezi erorile din formular înainte de a continua.');
            return;
        }

        setIsSubmitting(true);

        try {
            const response = await axios.put(`http://localhost:8080/buildings/${currentBuilding.id}`, formData);
            const updatedBuildings = buildings.map(b =>
                b.id === currentBuilding.id ? response.data : b
            );
            setBuildings(updatedBuildings);
            setIsEditing(false);
            setCurrentBuilding(null);
            resetForm();
            alert('Clădirea a fost actualizată cu succes!');
        } catch (error) {
            console.error('Error updating building:', error);
            if (error.response && error.response.status === 400) {
                alert('Datele introduse nu sunt valide. Verifică toate câmpurile și încearcă din nou.');
            } else {
                alert('Eroare la actualizarea clădirii.');
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    // Delete building
    const handleDelete = async (buildingId) => {
        if (window.confirm('Sigur doriți să ștergeți această clădire?')) {
            try {
                await axios.delete(`http://localhost:8080/buildings/${buildingId}`);
                setBuildings(buildings.filter(b => b.id !== buildingId));
                alert('Clădirea a fost ștearsă cu succes!');
            } catch (error) {
                console.error('Error deleting building:', error);
                alert('Eroare la ștergerea clădirii.');
            }
        }
    };

    // Set form data for editing
    const handleEdit = (building) => {
        setCurrentBuilding(building);
        setFormData({
            name: building.name,
            address: building.address,
            totalFloors: building.totalFloors,
            yearBuilt: building.yearBuilt,
            latitude: building.latitude,
            longitude: building.longitude
        });
        setIsEditing(true);
        setIsCreating(false);
    };

    // Cancel form
    const handleCancel = () => {
        setIsCreating(false);
        setIsEditing(false);
        setCurrentBuilding(null);
        resetForm();
    };

    // View spaces in a building
    const handleViewSpaces = (buildingId) => {
        navigate('/spaces', { state: { buildingFilter: buildingId } });
    };

    const canManageBuildings = user?.role === 'OWNER' || user?.role === 'ADMIN';

    return (
        <div className="buildings-container">
            <div className="buildings-header">
                <h2>Clădiri</h2>
                {canManageBuildings && (
                    <button className="btn btn-create" onClick={() => {
                        setIsCreating(true);
                        setIsEditing(false);
                        resetForm();
                    }}>
                        + Adaugă Clădire
                    </button>
                )}
            </div>

            {(isCreating || isEditing) && (
                <div className="building-form-container">
                    <div className="building-form">
                        <h3>{isCreating ? 'Adaugă clădire nouă' : 'Editează clădire'}</h3>
                        <form onSubmit={isCreating ? handleCreateSubmit : handleUpdateSubmit}>
                            <ValidatedInput
                                type="text"
                                name="name"
                                value={formData.name}
                                onChange={(e) => handleChange('name', e.target.value)}
                                onBlur={() => handleBlur('name')}
                                error={touched.name ? errors.name : ''}
                                label="Nume clădire"
                                placeholder="Ex: Business Center Plaza"
                                required
                                disabled={isSubmitting}
                            />

                            <ValidatedInput
                                type="text"
                                name="address"
                                value={formData.address}
                                onChange={(e) => handleChange('address', e.target.value)}
                                onBlur={() => handleBlur('address')}
                                error={touched.address ? errors.address : ''}
                                label="Adresă"
                                placeholder="Ex: Str. Memorandumului nr. 28, Cluj-Napoca"
                                required
                                disabled={isSubmitting}
                            />

                            <div className="form-row">
                                <ValidatedInput
                                    type="number"
                                    name="totalFloors"
                                    value={formData.totalFloors}
                                    onChange={(e) => handleChange('totalFloors', e.target.value)}
                                    onBlur={() => handleBlur('totalFloors')}
                                    error={touched.totalFloors ? errors.totalFloors : ''}
                                    label="Număr etaje"
                                    placeholder="Ex: 5"
                                    min="1"
                                    max="100"
                                    disabled={isSubmitting}
                                />

                                <ValidatedInput
                                    type="number"
                                    name="yearBuilt"
                                    value={formData.yearBuilt}
                                    onChange={(e) => handleChange('yearBuilt', e.target.value)}
                                    onBlur={() => handleBlur('yearBuilt')}
                                    error={touched.yearBuilt ? errors.yearBuilt : ''}
                                    label="An construcție"
                                    placeholder="Ex: 2020"
                                    min="1800"
                                    max={new Date().getFullYear()}
                                    disabled={isSubmitting}
                                />
                            </div>

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

                            <div className="form-actions">
                                <button
                                    type="submit"
                                    className="btn btn-save"
                                    disabled={isSubmitting || Object.keys(errors).length > 0}
                                >
                                    {isSubmitting ? 'Se procesează...' : (isCreating ? 'Adaugă' : 'Actualizează')}
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-cancel"
                                    onClick={handleCancel}
                                    disabled={isSubmitting}
                                >
                                    Anulează
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            <div className="filter-bar">
                <div className="search-container">
                    <input
                        type="text"
                        placeholder="Caută după nume sau adresă..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>
                <div className="sort-container">
                    <label>Sortare:</label>
                    <select value={sortOption} onChange={(e) => setSortOption(e.target.value)}>
                        <option value="name">Nume (A-Z)</option>
                        <option value="yearDesc">An construcție (desc)</option>
                        <option value="yearAsc">An construcție (asc)</option>
                        <option value="floorsDesc">Număr etaje (desc)</option>
                    </select>
                </div>
            </div>

            {filteredBuildings.length === 0 ? (
                <p className="no-data-message">Nu s-au găsit clădiri care să corespundă criteriilor.</p>
            ) : (
                <div className="buildings-grid">
                    {filteredBuildings.map(building => (
                        <div key={building.id} className="building-card">
                            <div className="building-header">
                                <h3>{building.name}</h3>
                                <div className="building-year">
                                    Construit în {building.yearBuilt}
                                </div>
                            </div>
                            <div className="building-details">
                                <div className="detail-item">
                                    <span className="detail-label">Adresă:</span>
                                    <span className="detail-value">{building.address}</span>
                                </div>
                                <div className="detail-item">
                                    <span className="detail-label">Etaje:</span>
                                    <span className="detail-value">{building.totalFloors}</span>
                                </div>
                                <div className="detail-item">
                                    <span className="detail-label">Coordonate:</span>
                                    <span className="detail-value">
                                        {building.latitude.toFixed(6)}, {building.longitude.toFixed(6)}
                                    </span>
                                </div>
                            </div>
                            <div className="building-actions">
                                <button
                                    className="btn btn-view"
                                    onClick={() => handleViewSpaces(building.id)}
                                >
                                    Vezi spații
                                </button>
                                {canManageBuildings && (
                                    <>
                                        <button
                                            className="btn btn-edit"
                                            onClick={() => handleEdit(building)}
                                        >
                                            Editează
                                        </button>
                                        <button
                                            className="btn btn-delete"
                                            onClick={() => handleDelete(building.id)}
                                        >
                                            Șterge
                                        </button>
                                    </>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default BuildingsPage;