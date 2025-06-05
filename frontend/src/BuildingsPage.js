import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './BuildingsPage.css';

function BuildingsPage() {
    const [buildings, setBuildings] = useState([]);
    const [filteredBuildings, setFilteredBuildings] = useState([]);
    const [user, setUser] = useState(null);
    const [isCreating, setIsCreating] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [currentBuilding, setCurrentBuilding] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [sortOption, setSortOption] = useState('name');
    const [formData, setFormData] = useState({
        name: '',
        address: '',
        totalFloors: 1,
        yearBuilt: 2000,
        latitude: 0,
        longitude: 0
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

    const handleInputChange = (e) => {
        const { name, value } = e.target;

        // Convert numeric fields
        if (name === 'totalFloors' || name === 'yearBuilt') {
            setFormData({ ...formData, [name]: parseInt(value) || 0 });
        } else if (name === 'latitude' || name === 'longitude') {
            setFormData({ ...formData, [name]: parseFloat(value) || 0 });
        } else {
            setFormData({ ...formData, [name]: value });
        }
    };

    const handleCreateSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/buildings', formData);
            setBuildings([...buildings, response.data]);
            setIsCreating(false);
            resetForm();
            alert('Clădirea a fost adăugată cu succes!');
        } catch (error) {
            console.error('Error creating building:', error);
            alert('Eroare la adăugarea clădirii.');
        }
    };

    const handleUpdateSubmit = async (e) => {
        e.preventDefault();
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
            alert('Eroare la actualizarea clădirii.');
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

    const resetForm = () => {
        setFormData({
            name: '',
            address: '',
            totalFloors: 1,
            yearBuilt: 2000,
            latitude: 0,
            longitude: 0
        });
    };

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
                            <div className="form-group">
                                <label htmlFor="name">Nume clădire *</label>
                                <input
                                    type="text"
                                    id="name"
                                    name="name"
                                    value={formData.name}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="address">Adresă *</label>
                                <input
                                    type="text"
                                    id="address"
                                    name="address"
                                    value={formData.address}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                            <div className="form-row">
                                <div className="form-group">
                                    <label htmlFor="totalFloors">Număr etaje</label>
                                    <input
                                        type="number"
                                        id="totalFloors"
                                        name="totalFloors"
                                        min="1"
                                        value={formData.totalFloors}
                                        onChange={handleInputChange}
                                    />
                                </div>
                                <div className="form-group">
                                    <label htmlFor="yearBuilt">An construcție</label>
                                    <input
                                        type="number"
                                        id="yearBuilt"
                                        name="yearBuilt"
                                        min="1900"
                                        max={new Date().getFullYear()}
                                        value={formData.yearBuilt}
                                        onChange={handleInputChange}
                                    />
                                </div>
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
                                        onChange={handleInputChange}
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
                                        onChange={handleInputChange}
                                    />
                                </div>
                            </div>
                            <div className="form-actions">
                                <button type="submit" className="btn btn-save">
                                    {isCreating ? 'Adaugă' : 'Actualizează'}
                                </button>
                                <button type="button" className="btn btn-cancel" onClick={handleCancel}>
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