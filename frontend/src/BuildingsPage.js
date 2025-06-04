import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './BuildingsPage.css';

// ADĂUGAT: Import-uri pentru validare
import { useFormValidation } from './hooks/useFormValidation';
import { buildingValidationRules, validateCoordinates } from './utils/validation';
import ValidatedInput from './components/forms/ValidatedInput';

function BuildingsPage() {
    const [buildings, setBuildings] = useState([]);
    const [filteredBuildings, setFilteredBuildings] = useState([]);
    const [user, setUser] = useState(null);
    const [isCreating, setIsCreating] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentBuilding, setCurrentBuilding] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [sortOption, setSortOption] = useState('name');

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
        setFormValues,
        resetForm
    } = useFormValidation({
        name: '',
        address: '',
        totalFloors: 1,
        yearBuilt: 2000,
        latitude: 0,
        longitude: 0
    }, {
        ...buildingValidationRules,
        // Validare specială pentru coordonate
        coordinates: [() => {
            const coordResult = validateCoordinates(formData.latitude, formData.longitude);
            if (!coordResult.isValid) {
                return {
                    isValid: false,
                    message: Object.values(coordResult.errors).join(', ')
                };
            }
            return { isValid: true, message: '' };
        }]
    });

    const navigate = useNavigate();

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

    useEffect(() => {
        handleFilter();
    }, [searchTerm, sortOption, buildings]);

    const handleFilter = () => {
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
    };

    // ACTUALIZAT: handleCreateSubmit cu validare
    const handleCreateSubmit = async (e) => {
        e.preventDefault();

        // Validează toate câmpurile
        if (!validateAllFields()) {
            alert('Te rugăm să corectezi erorile din formular înainte de a continua.');
            return;
        }

        setSubmitting(true);

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
            setSubmitting(false);
        }
    };

    // ACTUALIZAT: handleUpdateSubmit cu validare
    const handleUpdateSubmit = async (e) => {
        e.preventDefault();

        // Validează toate câmpurile
        if (!validateAllFields()) {
            alert('Te rugăm să corectezi erorile din formular înainte de a continua.');
            return;
        }

        setSubmitting(true);

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
            setSubmitting(false);
        }
    };

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

    // ACTUALIZAT: handleEdit cu setarea valorilor în formularul de validare
    const handleEdit = (building) => {
        setCurrentBuilding(building);
        setFormValues({
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

    // ACTUALIZAT: handleCancel
    const handleCancel = () => {
        setIsCreating(false);
        setIsEditing(false);
        setCurrentBuilding(null);
        resetForm();
    };

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
                            {/* ÎNLOCUIT: Input-urile clasice cu ValidatedInput */}
                            <ValidatedInput
                                type="text"
                                name="name"
                                value={formData.name}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={errors.name}
                                label="Nume clădire"
                                placeholder="Ex: Business Center Plaza"
                                required
                                disabled={isSubmitting}
                            />

                            <ValidatedInput
                                type="text"
                                name="address"
                                value={formData.address}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                error={errors.address}
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
                                    onChange={handleChange}
                                    onBlur={handleBlur}
                                    error={errors.totalFloors}
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
                                    onChange={handleChange}
                                    onBlur={handleBlur}
                                    error={errors.yearBuilt}
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

                            <div className="form-actions">
                                <button
                                    type="submit"
                                    className="btn btn-save"
                                    disabled={isSubmitting || !validateAllFields()}
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