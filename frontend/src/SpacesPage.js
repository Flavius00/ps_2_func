import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate, useLocation } from 'react-router-dom';
import './SpacesPage.css';

function SpacesPage() {
    const [spaces, setSpaces] = useState([]);
    const [filteredSpaces, setFilteredSpaces] = useState([]);
    const [user, setUser] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [successMessage, setSuccessMessage] = useState('');

    const [minPrice, setMinPrice] = useState('');
    const [maxPrice, setMaxPrice] = useState('');
    const [minArea, setMinArea] = useState('');
    const [maxArea, setMaxArea] = useState('');
    const [locations, setLocations] = useState([]);
    const [spaceTypes, setSpaceTypes] = useState([]);
    const [onlyAvailable, setOnlyAvailable] = useState(false); // Schimbat: pentru OWNER să vadă toate spațiile
    const [sortOption, setSortOption] = useState('');
    const [buildingFilter, setBuildingFilter] = useState(null);

    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        setUser(storedUser);

        console.log('Current user:', storedUser); // DEBUG

        // Verifică dacă există un mesaj de succes în starea locației
        if (location.state?.message) {
            setSuccessMessage(location.state.message);
            // Curăță URL-ul pentru a evita afișarea mesajului după reîncărcare
            window.history.replaceState({}, document.title);

            setTimeout(() => {
                setSuccessMessage('');
            }, 5000);
        }

        if (location.state?.buildingFilter) {
            setBuildingFilter(location.state.buildingFilter);
        }

        const fetchSpaces = async () => {
            setIsLoading(true);
            try {
                const response = await axios.get('http://localhost:8080/spaces/getAll');

                // VERIFICARE CRITICĂ: Asigură-te că response.data este un array
                const spacesData = Array.isArray(response.data) ? response.data : [];
                console.log('Date primite de la server:', spacesData);
                console.log('Număr de spații primite:', spacesData.length);

                // Log pentru debugging - vezi ce spații au owner-ul setat
                spacesData.forEach(space => {
                    console.log(`Space ID: ${space.id}, Name: ${space.name}, Owner ID: ${space.ownerId}, Owner Name: ${space.ownerName}`);
                });

                // Dacă un spațiu nou a fost adăugat, îl includem în listă
                if (location.state?.newSpace) {
                    const existingSpace = spacesData.find(s => s.id === location.state.newSpace.id);
                    if (!existingSpace) {
                        spacesData.push(location.state.newSpace);
                    }
                }

                setSpaces(spacesData);

                // Filtrare inițială
                let initialFiltered = [...spacesData]; // Copiază array-ul

                // Aplicare filtru pentru clădire dacă există
                if (location.state?.buildingFilter) {
                    initialFiltered = initialFiltered.filter(space =>
                        space.buildingId && space.buildingId === location.state.buildingFilter
                    );
                }

                // CORECTARE CRITICĂ: Filtrare pe baza rolului utilizatorului
                if (storedUser?.role === 'OWNER') {
                    console.log('Filtering for OWNER with ID:', storedUser.id); // DEBUG

                    // Filtrează spațiile care aparțin owner-ului curent
                    initialFiltered = initialFiltered.filter(space => {
                        const isOwner = space.ownerId && space.ownerId === storedUser.id;
                        console.log(`Space ${space.name}: ownerId=${space.ownerId}, user.id=${storedUser.id}, isOwner=${isOwner}`); // DEBUG
                        return isOwner;
                    });

                    console.log('Spaces after owner filter:', initialFiltered.length); // DEBUG
                }
                else if (storedUser?.role === 'TENANT') {
                    // Pentru tenant, afișează doar spațiile disponibile
                    initialFiltered = initialFiltered.filter(space => space.available);
                    // Setează filtrul pentru spații disponibile implicit pentru tenants
                    setOnlyAvailable(true);
                }

                console.log('Date după filtrarea inițială:', initialFiltered);
                console.log('Număr de spații după filtrare:', initialFiltered.length);

                setFilteredSpaces(initialFiltered);
            } catch (error) {
                console.error('Error fetching spaces:', error);
                // În caz de eroare, setează arrays goale pentru a evita crash-ul
                setSpaces([]);
                setFilteredSpaces([]);
            } finally {
                setIsLoading(false);
            }
        };

        fetchSpaces();
    }, [location.state]);

    const handleViewDetails = (space) => {
        navigate(`/space-details/${space.id}`, { state: { spaceData: space } });
    };

    const handleFilter = () => {
        // VERIFICARE CRITICĂ: Asigură-te că spaces este un array
        if (!Array.isArray(spaces)) {
            console.error('Spaces is not an array:', spaces);
            setFilteredSpaces([]);
            return;
        }

        let filtered = [...spaces]; // Copiază array-ul

        if (buildingFilter) {
            filtered = filtered.filter(space =>
                space.buildingId && space.buildingId === buildingFilter
            );
        }

        // CORECTARE CRITICĂ: Aplică filtrul de owner ÎNTOTDEAUNA pentru owner-i
        if (user?.role === 'OWNER') {
            filtered = filtered.filter(space => space.ownerId && space.ownerId === user.id);
        }

        filtered = filtered.filter(space => {
            const price = space.pricePerMonth;
            const area = space.area;
            const location = space.buildingName;
            const type = space.spaceType;

            const matchesPrice = (!minPrice || price >= minPrice) && (!maxPrice || price <= maxPrice);
            const matchesArea = (!minArea || area >= minArea) && (!maxArea || area <= maxArea);
            const matchesLocation = locations.length === 0 || (location && locations.includes(location));
            const matchesType = spaceTypes.length === 0 || (type && spaceTypes.includes(type));
            const matchesAvailability = !onlyAvailable || space.available;

            return matchesPrice && matchesArea && matchesLocation && matchesType && matchesAvailability;
        });

        if (sortOption === 'priceAsc') {
            filtered.sort((a, b) => a.pricePerMonth - b.pricePerMonth);
        } else if (sortOption === 'priceDesc') {
            filtered.sort((a, b) => b.pricePerMonth - a.pricePerMonth);
        } else if (sortOption === 'areaAsc') {
            filtered.sort((a, b) => a.area - b.area);
        } else if (sortOption === 'areaDesc') {
            filtered.sort((a, b) => b.area - a.area);
        }

        setFilteredSpaces(filtered);
    };

    const handleReset = () => {
        setMinPrice('');
        setMaxPrice('');
        setMinArea('');
        setMaxArea('');
        setLocations([]);
        setSpaceTypes([]);

        // Pentru OWNER, nu seta onlyAvailable la true implicit
        if (user?.role === 'TENANT') {
            setOnlyAvailable(true);
        } else {
            setOnlyAvailable(false);
        }

        setSortOption('');
        setBuildingFilter(null);

        // VERIFICARE CRITICĂ: Asigură-te că spaces este un array
        if (!Array.isArray(spaces)) {
            console.error('Spaces is not an array during reset:', spaces);
            setFilteredSpaces([]);
            return;
        }

        // Resetează la lista inițială bazată pe rolul utilizatorului
        let resetFiltered = [...spaces];
        if (user?.role === 'OWNER') {
            resetFiltered = spaces.filter(space => space.ownerId && space.ownerId === user.id);
        } else if (user?.role === 'TENANT') {
            resetFiltered = spaces.filter(space => space.available);
        }

        setFilteredSpaces(resetFiltered);

        // Elimină parametrul de filtru din starea locației
        navigate(location.pathname, { replace: true });
    };

    const handleCreateSpace = () => {
        navigate('/spaces/create');
    };

    // VERIFICARE CRITICĂ: Asigură-te că spaces este un array înainte de a folosi .map()
    const uniqueLocations = Array.isArray(spaces)
        ? [...new Set(spaces
            .filter(s => user?.role !== 'OWNER' || (s.ownerId === user.id)) // Filtrează și aici pentru owner
            .map(s => s.buildingName)
            .filter(Boolean))]
        : [];
    const spaceTypeOptions = ['OFFICE', 'RETAIL', 'WAREHOUSE'];

    if (isLoading) {
        return <div className="loading-container">Se încarcă spațiile comerciale...</div>;
    }

    // VERIFICARE CRITICĂ: Asigură-te că filteredSpaces este un array
    const spacesToRender = Array.isArray(filteredSpaces) ? filteredSpaces : [];

    return (
        <div className="spaces-container">
            {successMessage && (
                <div className="success-message">
                    <p>{successMessage}</p>
                    <button className="close-message" onClick={() => setSuccessMessage('')}>×</button>
                </div>
            )}

            <div className="spaces-header">
                <h2>
                    Spații Comerciale
                    {user?.role === 'OWNER' && (
                        <span style={{fontSize: '16px', color: '#7f8c8d', marginLeft: '10px'}}>
                            (Spațiile mele)
                        </span>
                    )}
                </h2>
                {user?.role === 'OWNER' && (
                    <button className="btn btn-create" onClick={handleCreateSpace}>
                        + Adaugă Spațiu Nou
                    </button>
                )}
            </div>

            {buildingFilter && (
                <div className="building-filter-notice">
                    <p>
                        Se afișează spațiile din clădirea selectată.
                        <button className="btn-clear-filter" onClick={handleReset}>
                            Șterge filtrul de clădire
                        </button>
                    </p>
                </div>
            )}

            <div className="filter-section">
                <div className="filter-group">
                    <label>Preț Minim (€/lună):</label>
                    <input
                        type="number"
                        value={minPrice}
                        onChange={(e) => setMinPrice(e.target.value)}
                    />
                </div>
                <div className="filter-group">
                    <label>Preț Maxim (€/lună):</label>
                    <input
                        type="number"
                        value={maxPrice}
                        onChange={(e) => setMaxPrice(e.target.value)}
                    />
                </div>
                <div className="filter-group">
                    <label>Suprafață Min (m²):</label>
                    <input
                        type="number"
                        value={minArea}
                        onChange={(e) => setMinArea(e.target.value)}
                    />
                </div>
                <div className="filter-group">
                    <label>Suprafață Max (m²):</label>
                    <input
                        type="number"
                        value={maxArea}
                        onChange={(e) => setMaxArea(e.target.value)}
                    />
                </div>
                <div className="filter-group">
                    <label>Sortare:</label>
                    <select value={sortOption} onChange={(e) => setSortOption(e.target.value)}>
                        <option value="">Fără sortare</option>
                        <option value="priceAsc">Preț: Mic la Mare</option>
                        <option value="priceDesc">Preț: Mare la Mic</option>
                        <option value="areaAsc">Suprafață: Mic la Mare</option>
                        <option value="areaDesc">Suprafață: Mare la Mic</option>
                    </select>
                </div>
                {/* Afișează filtrul de disponibilitate doar pentru tenants sau admins */}
                {user?.role !== 'OWNER' && (
                    <div className="filter-group checkbox-group">
                        <label>
                            <input
                                type="checkbox"
                                checked={onlyAvailable}
                                onChange={(e) => setOnlyAvailable(e.target.checked)}
                            /> Arată doar spații disponibile
                        </label>
                    </div>
                )}

                <div className="filter-expanded">
                    <div className="filter-section-inner">
                        <div className="filter-column">
                            <label>Filtrare după Locație:</label>
                            <div className="checkbox-list">
                                {uniqueLocations.map(loc => (
                                    <label key={loc} className="checkbox-item">
                                        <input
                                            type="checkbox"
                                            value={loc}
                                            checked={locations.includes(loc)}
                                            onChange={(e) => {
                                                if (e.target.checked) {
                                                    setLocations(prev => [...prev, loc]);
                                                } else {
                                                    setLocations(prev => prev.filter(item => item !== loc));
                                                }
                                            }}
                                        /> {loc}
                                    </label>
                                ))}
                            </div>
                        </div>

                        <div className="filter-column">
                            <label>Filtrare după Tip Spațiu:</label>
                            <div className="checkbox-list">
                                {spaceTypeOptions.map(type => (
                                    <label key={type} className="checkbox-item">
                                        <input
                                            type="checkbox"
                                            value={type}
                                            checked={spaceTypes.includes(type)}
                                            onChange={(e) => {
                                                if (e.target.checked) {
                                                    setSpaceTypes(prev => [...prev, type]);
                                                } else {
                                                    setSpaceTypes(prev => prev.filter(item => item !== type));
                                                }
                                            }}
                                        /> {type === 'OFFICE' ? 'Birou' :
                                        type === 'RETAIL' ? 'Spațiu Comercial' :
                                            type === 'WAREHOUSE' ? 'Depozit' : type}
                                    </label>
                                ))}
                            </div>
                        </div>
                    </div>

                    <div className="button-group">
                        <button className="btn" onClick={handleFilter}>Aplică Filtrele</button>
                        <button className="btn btn-secondary" onClick={handleReset}>Resetează Filtrele</button>
                    </div>
                </div>
            </div>

            {spacesToRender.length === 0 ? (
                <div className="no-spaces-message">
                    {user?.role === 'OWNER'
                        ? "Nu aveți încă spații comerciale. Folosiți butonul 'Adaugă Spațiu Nou' pentru a adăuga primul spațiu."
                        : "Nu s-au găsit spații care să corespundă criteriilor de căutare."
                    }
                </div>
            ) : (
                <div className="spaces-grid">
                    {spacesToRender.map((space) => (
                        <div key={space.id} className="space-card">
                            <div className="space-card-header">
                                <h3>{space.name}</h3>
                                <span className={`space-badge ${space.spaceType ? space.spaceType.toLowerCase() : 'unknown'}`}>
                                    {space.spaceType === 'OFFICE' ? 'Birou' :
                                        space.spaceType === 'RETAIL' ? 'Spațiu Comercial' :
                                            space.spaceType === 'WAREHOUSE' ? 'Depozit' :
                                                space.spaceType || 'Necunoscut'}
                                </span>
                            </div>
                            <div className="space-card-body">
                                <p className="space-description">{space.description}</p>
                                <div className="space-details">
                                    <div className="detail-item">
                                        <span className="detail-label">Suprafață:</span>
                                        <span className="detail-value">{space.area} m²</span>
                                    </div>
                                    <div className="detail-item">
                                        <span className="detail-label">Preț:</span>
                                        <span className="detail-value">{space.pricePerMonth} €/lună</span>
                                    </div>
                                    <div className="detail-item">
                                        <span className="detail-label">Locație:</span>
                                        <span className="detail-value">{space.buildingName || 'N/A'}</span>
                                    </div>
                                    <div className="detail-item">
                                        <span className="detail-label">Status:</span>
                                        <span className={`detail-value ${space.available ? 'available' : 'rented'}`}>
                                            {space.available ? 'Disponibil' : 'Închiriat'}
                                        </span>
                                    </div>
                                    {/* Pentru owner, afișează info despre contracte */}
                                    {user?.role === 'OWNER' && (
                                        <div className="detail-item">
                                            <span className="detail-label">Contracte:</span>
                                            <span className="detail-value">{space.contractsCount || 0}</span>
                                        </div>
                                    )}
                                </div>
                            </div>
                            <div className="space-card-footer">
                                <button
                                    className="btn btn-details"
                                    onClick={() => handleViewDetails(space)}
                                >
                                    Vezi Detalii
                                </button>
                                {user?.role === 'TENANT' && space.available && (
                                    <button
                                        className="btn btn-action"
                                        onClick={() => navigate('/payment', { state: { selectedSpace: space } })}
                                    >
                                        Închiriază
                                    </button>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default SpacesPage;