import React, { useEffect, useState } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './SpaceDetailsPage.css';

function SpaceDetailsPage() {
    const { id } = useParams();
    const location = useLocation();
    const navigate = useNavigate();
    const [space, setSpace] = useState(null);
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({});

    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        setUser(storedUser);

        console.log('Current user:', storedUser); // DEBUG

        const fetchSpace = async () => {
            setLoading(true);
            try {
                // Use space data from state if available, otherwise fetch from API
                if (location.state?.spaceData) {
                    const spaceData = location.state.spaceData;
                    console.log('Space data from state:', spaceData); // DEBUG
                    setSpace(spaceData);
                    setFormData(spaceData);
                } else {
                    console.log('Fetching space from API with ID:', id); // DEBUG
                    const response = await axios.get(`http://localhost:8080/spaces/details/${id}`);
                    console.log('Space data from API:', response.data); // DEBUG
                    setSpace(response.data);
                    setFormData(response.data);
                }
            } catch (error) {
                console.error('Error fetching space details:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchSpace();
    }, [id, location.state]);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;

        if (type === 'checkbox') {
            setFormData(prev => ({ ...prev, [name]: checked }));
        } else if (type === 'number') {
            setFormData(prev => ({ ...prev, [name]: parseFloat(value) || 0 }));
        } else {
            setFormData(prev => ({ ...prev, [name]: value }));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // CORECTARE CRITICĂ: Trimite doar datele care pot fi editate
            // NU trimite owner sau building pentru a evita suprascrierea lor
            const updateData = {
                id: space.id,
                name: formData.name,
                description: formData.description,
                area: formData.area,
                pricePerMonth: formData.pricePerMonth,
                address: formData.address,
                available: formData.available,
                latitude: formData.latitude || space.latitude,
                longitude: formData.longitude || space.longitude,
                // Include doar câmpurile specifice tipului de spațiu dacă există
                ...(formData.floors && { floors: formData.floors }),
                ...(formData.numberOfRooms && { numberOfRooms: formData.numberOfRooms }),
                ...(formData.hasReception !== undefined && { hasReception: formData.hasReception }),
                ...(formData.shopWindowSize && { shopWindowSize: formData.shopWindowSize }),
                ...(formData.hasCustomerEntrance !== undefined && { hasCustomerEntrance: formData.hasCustomerEntrance }),
                ...(formData.maxOccupancy && { maxOccupancy: formData.maxOccupancy }),
                ...(formData.ceilingHeight && { ceilingHeight: formData.ceilingHeight }),
                ...(formData.hasLoadingDock !== undefined && { hasLoadingDock: formData.hasLoadingDock }),
                ...(formData.securityLevel && { securityLevel: formData.securityLevel })
            };

            console.log('Updating space with data:', updateData); // DEBUG

            const response = await axios.post('http://localhost:8080/spaces/update', updateData);

            // Actualizează space-ul local cu datele returnat de server
            setSpace(response.data);
            setFormData(response.data);
            setIsEditing(false);
            alert('Space updated successfully!');
        } catch (error) {
            console.error('Error updating space:', error);
            if (error.response) {
                console.error('Server response:', error.response.data);
                alert(`Failed to update space: ${error.response.data}`);
            } else {
                alert('Failed to update space. Please try again.');
            }
        }
    };

    const handleDelete = async () => {
        if (window.confirm('Are you sure you want to delete this space? This action cannot be undone.')) {
            try {
                await axios.post(`http://localhost:8080/spaces/delete/${id}`);
                navigate('/spaces');
                alert('Space deleted successfully!');
            } catch (error) {
                console.error('Error deleting space:', error);
                alert('Failed to delete space. Please try again.');
            }
        }
    };

    const handleRent = () => {
        navigate('/payment', { state: { selectedSpace: space } });
    };

    if (loading) {
        return <div className="loading">Loading space details...</div>;
    }

    if (!space) {
        return <div className="error-message">Space not found</div>;
    }

    // CORECTARE CRITICĂ: Verificarea ownership-ului folosind proprietățile JSON
    const canEdit = user?.role === 'OWNER' && (
        space.ownerId === user.id ||
        (space.owner && space.owner.id === user.id)
    );

    const canRent = user?.role === 'TENANT' && space.available;

    console.log('Can edit check:', {
        userRole: user?.role,
        userId: user?.id,
        spaceOwnerId: space.ownerId,
        spaceOwnerFromObj: space.owner?.id,
        canEdit
    }); // DEBUG

    const renderAmenities = () => {
        if (!space.amenities || space.amenities.length === 0) {
            return <p>No amenities listed</p>;
        }

        return (
            <ul className="amenities-list">
                {space.amenities.map((amenity, index) => (
                    <li key={index}>{amenity}</li>
                ))}
            </ul>
        );
    };

    const renderSpecificDetails = () => {
        switch (space.spaceType) {
            case 'OFFICE':
                return (
                    <div className="specific-details">
                        <h3>Office Details</h3>
                        <div className="detail-grid">
                            <div className="detail-item">
                                <span className="detail-label">Floors:</span>
                                <span className="detail-value">{space.floors || 'N/A'}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Number of Rooms:</span>
                                <span className="detail-value">{space.numberOfRooms || 'N/A'}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Reception Area:</span>
                                <span className="detail-value">{space.hasReception ? 'Yes' : 'No'}</span>
                            </div>
                        </div>
                    </div>
                );
            case 'RETAIL':
                return (
                    <div className="specific-details">
                        <h3>Retail Details</h3>
                        <div className="detail-grid">
                            <div className="detail-item">
                                <span className="detail-label">Shop Window Size:</span>
                                <span className="detail-value">{space.shopWindowSize || 'N/A'} m</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Customer Entrance:</span>
                                <span className="detail-value">{space.hasCustomerEntrance ? 'Yes' : 'No'}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Max Occupancy:</span>
                                <span className="detail-value">{space.maxOccupancy || 'N/A'} people</span>
                            </div>
                        </div>
                    </div>
                );
            case 'WAREHOUSE':
                return (
                    <div className="specific-details">
                        <h3>Warehouse Details</h3>
                        <div className="detail-grid">
                            <div className="detail-item">
                                <span className="detail-label">Ceiling Height:</span>
                                <span className="detail-value">{space.ceilingHeight || 'N/A'} m</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Loading Dock:</span>
                                <span className="detail-value">{space.hasLoadingDock ? 'Yes' : 'No'}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Security Level:</span>
                                <span className="detail-value">{space.securityLevel || 'N/A'}</span>
                            </div>
                        </div>
                    </div>
                );
            default:
                return null;
        }
    };

    return (
        <div className="space-details-container">
            <div className="details-header">
                <button className="btn btn-back" onClick={() => navigate('/spaces')}>
                    ← Back to Spaces
                </button>
                <h2>{space.name}</h2>
                <div className="space-type-badge">{space.spaceType}</div>
            </div>

            {isEditing ? (
                <form className="edit-form" onSubmit={handleSubmit}>
                    <div className="form-section">
                        <h3>Basic Information</h3>
                        <div className="form-group">
                            <label>Name:</label>
                            <input
                                type="text"
                                name="name"
                                value={formData.name || ''}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Description:</label>
                            <textarea
                                name="description"
                                value={formData.description || ''}
                                onChange={handleChange}
                                rows="4"
                            />
                        </div>
                        <div className="form-row">
                            <div className="form-group">
                                <label>Price (€/month):</label>
                                <input
                                    type="number"
                                    name="pricePerMonth"
                                    value={formData.pricePerMonth || ''}
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label>Area (m²):</label>
                                <input
                                    type="number"
                                    name="area"
                                    value={formData.area || ''}
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                        </div>
                        <div className="form-group">
                            <label>Address:</label>
                            <input
                                type="text"
                                name="address"
                                value={formData.address || ''}
                                onChange={handleChange}
                            />
                        </div>
                        <div className="form-group checkbox">
                            <label>
                                <input
                                    type="checkbox"
                                    name="available"
                                    checked={formData.available || false}
                                    onChange={handleChange}
                                />
                                Available for Rent
                            </label>
                        </div>
                    </div>

                    <div className="form-actions">
                        <button type="submit" className="btn btn-save">Save Changes</button>
                        <button
                            type="button"
                            className="btn btn-cancel"
                            onClick={() => setIsEditing(false)}
                        >
                            Cancel
                        </button>
                    </div>
                </form>
            ) : (
                <div className="details-content">
                    <div className="main-details">
                        <div className="details-section">
                            <h3>Space Overview</h3>
                            <p className="space-description">{space.description}</p>

                            <div className="key-details">
                                <div className="detail-item">
                                    <span className="detail-label">Price:</span>
                                    <span className="detail-value">{space.pricePerMonth} €/month</span>
                                </div>
                                <div className="detail-item">
                                    <span className="detail-label">Area:</span>
                                    <span className="detail-value">{space.area} m²</span>
                                </div>
                                <div className="detail-item">
                                    <span className="detail-label">Building:</span>
                                    <span className="detail-value">{space.buildingName || 'N/A'}</span>
                                </div>
                                <div className="detail-item">
                                    <span className="detail-label">Address:</span>
                                    <span className="detail-value">{space.address || space.buildingAddress || 'N/A'}</span>
                                </div>
                                <div className="detail-item">
                                    <span className="detail-label">Status:</span>
                                    <span className={`detail-value status ${space.available ? 'available' : 'rented'}`}>
                                        {space.available ? 'Available' : 'Rented'}
                                    </span>
                                </div>
                                <div className="detail-item">
                                    <span className="detail-label">Owner:</span>
                                    <span className="detail-value">{space.ownerName || 'N/A'}</span>
                                </div>
                                {space.parking && (
                                    <div className="detail-item">
                                        <span className="detail-label">Parking:</span>
                                        <span className="detail-value">
                                            {space.parking.numberOfSpots} spots ({space.parking.pricePerSpot} €/spot)
                                        </span>
                                    </div>
                                )}
                            </div>
                        </div>

                        {renderSpecificDetails()}

                        <div className="details-section">
                            <h3>Amenities</h3>
                            {renderAmenities()}
                        </div>
                    </div>

                    <div className="details-sidebar">
                        <div className="action-card">
                            <h3>Actions</h3>
                            {canEdit && (
                                <>
                                    <button
                                        className="btn btn-edit"
                                        onClick={() => setIsEditing(true)}
                                    >
                                        Edit Space
                                    </button>
                                    <button
                                        className="btn btn-delete"
                                        onClick={handleDelete}
                                    >
                                        Delete Space
                                    </button>
                                </>
                            )}
                            {canRent && (
                                <button
                                    className="btn btn-rent"
                                    onClick={handleRent}
                                >
                                    Rent This Space
                                </button>
                            )}
                            {!canEdit && !canRent && (
                                <p style={{color: '#7f8c8d', fontSize: '14px'}}>
                                    No actions available for your role.
                                </p>
                            )}
                        </div>

                        <div className="contact-card">
                            <h3>Contact Information</h3>
                            <div className="contact-details">
                                <p><strong>Owner:</strong> {space.ownerName || 'N/A'}</p>
                                <p><strong>Email:</strong> {space.ownerEmail || 'N/A'}</p>
                                <p><strong>Phone:</strong> {space.ownerPhone || 'N/A'}</p>
                                <p><strong>Company:</strong> {space.ownerCompanyName || 'N/A'}</p>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default SpaceDetailsPage;