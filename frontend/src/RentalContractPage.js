import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import './RentalContractPage.css';

// Strategy interface
class PricingStrategy {
    calculateTotal(basePrice, duration) {
        throw new Error('Method must be implemented');
    }

    getDiscountDescription() {
        throw new Error('Method must be implemented');
    }

    getStrategyName() {
        throw new Error('Method must be implemented');
    }
}

// Concrete strategies
class ShortTermStrategy extends PricingStrategy {
    calculateTotal(basePrice, duration) {
        return {
            monthlyRent: basePrice,
            totalValue: basePrice * duration,
            discount: 0,
            discountAmount: 0,
            originalTotal: basePrice * duration
        };
    }

    getDiscountDescription() {
        return "Fără discount pentru contracte sub 12 luni";
    }

    getStrategyName() {
        return "Short Term";
    }
}

class MediumTermStrategy extends PricingStrategy {
    calculateTotal(basePrice, duration) {
        const discount = 0.05; // 5% discount
        const discountedPrice = basePrice * (1 - discount);
        const originalTotal = basePrice * duration;
        const discountedTotal = discountedPrice * duration;

        return {
            monthlyRent: discountedPrice,
            totalValue: discountedTotal,
            discount: discount * 100,
            discountAmount: originalTotal - discountedTotal,
            originalTotal: originalTotal
        };
    }

    getDiscountDescription() {
        return "🎉 5% discount pentru contracte de 12 luni!";
    }

    getStrategyName() {
        return "Medium Term";
    }
}

class LongTermStrategy extends PricingStrategy {
    calculateTotal(basePrice, duration) {
        let discount = 0.10; // 10% pentru 24 luni
        if (duration >= 36) {
            discount = 0.15; // 15% pentru 36+ luni
        }

        const discountedPrice = basePrice * (1 - discount);
        const originalTotal = basePrice * duration;
        const discountedTotal = discountedPrice * duration;

        return {
            monthlyRent: discountedPrice,
            totalValue: discountedTotal,
            discount: discount * 100,
            discountAmount: originalTotal - discountedTotal,
            originalTotal: originalTotal
        };
    }

    getDiscountDescription() {
        return "🏆 Discount special pentru contracte lungi!";
    }

    getStrategyName() {
        return "Long Term";
    }
}

// Context class
class PricingContext {
    constructor() {
        this.strategy = null;
    }

    setStrategy(strategy) {
        this.strategy = strategy;
    }

    calculatePrice(basePrice, duration) {
        if (!this.strategy) {
            throw new Error('Pricing strategy not set');
        }

        const pricing = this.strategy.calculateTotal(basePrice, duration);
        const securityDeposit = basePrice * 2;
        const initialPayment = pricing.monthlyRent + securityDeposit;

        return {
            ...pricing,
            securityDeposit,
            initialPayment,
            description: this.strategy.getDiscountDescription(),
            strategyName: this.strategy.getStrategyName()
        };
    }
}

// Factory pentru strategii
class PricingStrategyFactory {
    static createStrategy(duration) {
        if (duration < 12) {
            return new ShortTermStrategy();
        } else if (duration === 12) {
            return new MediumTermStrategy();
        } else {
            return new LongTermStrategy();
        }
    }
}

function RentalContractPage() {
    const location = useLocation();
    const navigate = useNavigate();

    // State
    const [user, setUser] = useState(null);
    const [space, setSpace] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [contractDuration, setContractDuration] = useState(12);
    const [paymentMethod, setPaymentMethod] = useState('');
    const [signatureData, setSignatureData] = useState('');
    const [termsAccepted, setTermsAccepted] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [formErrors, setFormErrors] = useState({});

    // Pricing context using ref to ensure stability
    const pricingContextRef = useRef(new PricingContext());
    const [pricing, setPricing] = useState(null);

    // Date calculation
    const startDate = new Date();
    const formattedStartDate = startDate.toISOString().split('T')[0];

    const endDate = new Date(startDate);
    endDate.setMonth(endDate.getMonth() + parseInt(contractDuration));
    const formattedEndDate = endDate.toISOString().split('T')[0];

    // Initialize data on load
    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        if (!storedUser || storedUser.role !== 'TENANT') {
            navigate('/spaces');
            return;
        }

        setUser(storedUser);

        if (!location.state || !location.state.selectedSpace) {
            setError('Informații despre spațiu lipsă.');
            setLoading(false);
            return;
        }

        setSpace(location.state.selectedSpace);
        setLoading(false);
    }, [location, navigate]);

    // Calculate pricing when needed data changes
    useEffect(() => {
        if (space && contractDuration) {
            try {
                // Create and set strategy
                const strategy = PricingStrategyFactory.createStrategy(parseInt(contractDuration));
                pricingContextRef.current.setStrategy(strategy);

                // Calculate prices
                const result = pricingContextRef.current.calculatePrice(
                    space.pricePerMonth,
                    parseInt(contractDuration)
                );

                setPricing(result);
            } catch (error) {
                console.error('Error calculating pricing:', error);
                setError('Eroare la calcularea prețurilor. Vă rugăm reîncărcați pagina.');
            }
        }
    }, [space, contractDuration]);

    // Validate fields
    const validateFields = () => {
        const errors = {};

        if (!paymentMethod) {
            errors.paymentMethod = 'Selectați o metodă de plată';
        }

        if (!signatureData || signatureData.trim().length < 3) {
            errors.signatureData = 'Semnătura electronică trebuie să aibă cel puțin 3 caractere';
        }

        if (!termsAccepted) {
            errors.termsAccepted = 'Trebuie să acceptați termenii și condițiile';
        }

        setFormErrors(errors);
        return Object.keys(errors).length === 0;
    };

    // Handle navigation
    const handleCancel = () => {
        navigate('/spaces');
    };

    // Handle contract submission
    // Fix pentru RentalContractPage.js - handleSubmit method

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateFields()) {
            return;
        }

        setIsSubmitting(true);

        try {
            // DEBUGGING: Verifică datele înainte de trimitere
            console.log("=== FRONTEND CONTRACT DEBUG ===");
            console.log("User:", user);
            console.log("Space:", space);
            console.log("User ID type:", typeof user.id, "Value:", user.id);
            console.log("Space ID type:", typeof space.id, "Value:", space.id);

            // CRITICAL FIX: Asigură-te că ID-urile sunt numere întregi
            const spaceId = parseInt(space.id);
            const tenantId = parseInt(user.id);

            console.log("Space ID (parsed):", spaceId);
            console.log("Tenant ID (parsed):", tenantId);

            // Validează că parsing-ul a reușit
            if (!spaceId || isNaN(spaceId)) {
                throw new Error("Invalid space ID: " + space.id);
            }

            if (!tenantId || isNaN(tenantId)) {
                throw new Error("Invalid tenant ID: " + user.id);
            }

            // Verifică că space-ul este disponibil
            if (!space.available) {
                throw new Error("Spațiul nu mai este disponibil pentru închiriere");
            }

            const contractData = {
                spaceId: spaceId,           // Trimite ca număr întreg
                tenantId: tenantId,         // Trimite ca număr întreg
                startDate: formattedStartDate,
                endDate: formattedEndDate,
                monthlyRent: parseFloat(pricing.monthlyRent.toFixed(2)),
                securityDeposit: parseFloat(pricing.securityDeposit.toFixed(2)),
                status: "ACTIVE",
                isPaid: true,
                dateCreated: formattedStartDate,
                contractNumber: `RENT-${Date.now()}`,
                notes: `Contract încheiat electronic. Metodă de plată: ${paymentMethod}. Durata: ${contractDuration} luni. Discount aplicat: ${pricing.discount}%. Economii: ${pricing.discountAmount}€. Strategie folosită: ${pricing.strategyName}. Semnătură: ${signatureData}`
            };

            console.log("Final contract data to send:", contractData);
            console.log("=== END FRONTEND DEBUG ===");

            const response = await axios.post('http://localhost:8080/contracts/create', contractData, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            console.log("Contract creation response:", response.data);

            // Actualizează space-ul local să fie indisponibil
            const updatedSpace = { ...space, available: false };

            // Încearcă să actualizezi și space-ul pe server (non-blocking)
            try {
                await axios.post('http://localhost:8080/spaces/update', {
                    id: space.id,
                    name: space.name,
                    description: space.description,
                    area: space.area,
                    pricePerMonth: space.pricePerMonth,
                    address: space.address,
                    available: false, // Marchează ca indisponibil
                    latitude: space.latitude,
                    longitude: space.longitude,
                    amenities: space.amenities,
                    floors: space.floors,
                    numberOfRooms: space.numberOfRooms,
                    hasReception: space.hasReception,
                    shopWindowSize: space.shopWindowSize,
                    hasCustomerEntrance: space.hasCustomerEntrance,
                    maxOccupancy: space.maxOccupancy,
                    ceilingHeight: space.ceilingHeight,
                    hasLoadingDock: space.hasLoadingDock,
                    securityLevel: space.securityLevel
                });
                console.log("Space availability updated successfully");
            } catch (spaceUpdateError) {
                console.warn('Failed to update space availability on frontend, but contract was created:', spaceUpdateError);
                // Nu oprește flow-ul pentru că contractul a fost creat cu succes
            }

            // Pregătește datele pentru pagina de confirmare
            const contractForConfirmation = {
                ...contractData,
                id: response.data.id,
                contractNumber: response.data.contractNumber || contractData.contractNumber,
                paymentMethod: paymentMethod,
                signature: signatureData,
                appliedDiscount: pricing.discount,
                savings: pricing.discountAmount,
                strategyUsed: pricing.strategyName
            };

            // Navighează la pagina de confirmare
            navigate('/payment/confirm', {
                state: {
                    contract: contractForConfirmation,
                    space: updatedSpace
                }
            });

        } catch (error) {
            console.error('=== FRONTEND CONTRACT CREATION ERROR ===');
            console.error('Error type:', error.constructor.name);
            console.error('Error message:', error.message);
            console.error('Full error object:', error);

            if (error.response) {
                console.error('Server response status:', error.response.status);
                console.error('Server response data:', error.response.data);
                console.error('Server response headers:', error.response.headers);
            }
            console.error('=== END FRONTEND ERROR ===');

            // Enhanced error handling
            if (error.response) {
                const status = error.response.status;
                const data = error.response.data;

                if (status === 500) {
                    setError('Eroare de server. Vă rugăm încercați din nou sau contactați suportul. Detalii: ' + (data.message || 'Eroare internă de server'));
                } else if (status === 400) {
                    if (data.errors) {
                        const validationErrors = Object.entries(data.errors)
                            .map(([field, message]) => `${field}: ${message}`)
                            .join('\n');
                        setError(`Erori de validare:\n${validationErrors}`);
                    } else if (data.message) {
                        setError(`Eroare: ${data.message}`);
                    } else {
                        setError('Datele contractului nu sunt valide. Vă rugăm verificați informațiile introduse.');
                    }
                } else if (status === 404) {
                    setError('Spațiul sau chiriașul nu a fost găsit în baza de date. Vă rugăm reîncărcați pagina.');
                } else if (status === 409) {
                    setError('Spațiul este deja închiriat sau există un conflict cu un alt contract.');
                } else {
                    setError(`Eroare server (${status}): ${data.message || 'Eroare nespecificată'}`);
                }
            } else if (error.request) {
                setError('Nu s-a putut conecta la server. Verificați conexiunea la internet și încercați din nou.');
            } else {
                setError('A apărut o eroare la pregătirea cererii: ' + error.message);
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    // Loading and error states
    if (loading) {
        return <div className="loading-container">Se încarcă...</div>;
    }

    if (error) {
        return (
            <div className="error-container">
                <p>{error}</p>
                <button onClick={() => navigate('/spaces')}>Înapoi la Spații</button>
            </div>
        );
    }

    if (!pricing) {
        return <div className="loading-container">Se calculează prețurile...</div>;
    }

    // Render component
    return (
        <div className="contract-page-container">
            <div className="contract-header">
                <h2>Contract de Închiriere Spațiu Comercial</h2>
                <button className="btn-back" onClick={handleCancel}>
                    ← Înapoi la Spații
                </button>
            </div>

            <div className="contract-content">
                <div className="contract-main">
                    <div className="contract-section">
                        <h3>Contract de Închiriere</h3>
                        <div className="contract-document">
                            <h4 className="document-title">CONTRACT DE ÎNCHIRIERE SPAȚIU COMERCIAL</h4>
                            <p className="document-date">Încheiat azi, {new Date().toLocaleDateString('ro-RO')}</p>

                            <div className="contract-parties">
                                <div className="party">
                                    <h5>I. PĂRȚILE CONTRACTANTE</h5>
                                    <p>
                                        <strong>1.1 Proprietar:</strong> {space.ownerName || 'Proprietarul spațiului'},
                                        cu adresa în {space.ownerAddress || 'adresa proprietarului'},
                                        având CUI {space.ownerTaxId || 'CUI proprietar'},
                                        denumit în continuare "PROPRIETAR"
                                    </p>
                                    <p>
                                        <strong>1.2 Chiriaș:</strong> {user.name},
                                        cu adresa în {user.address || 'adresa chiriașului'},
                                        având CUI {user.taxId || 'CUI chiriaș'},
                                        denumit în continuare "CHIRIAȘ"
                                    </p>
                                </div>
                            </div>

                            <div className="contract-article">
                                <h5>IV. PREȚUL ÎNCHIRIERII</h5>
                                <p>
                                    4.1 Chiria lunară este de {pricing.monthlyRent.toFixed(2)} Euro, plătibilă în lei la cursul BNR din ziua plății,
                                    în primele 5 zile ale fiecărei luni.
                                </p>
                                {pricing.discount > 0 && (
                                    <p style={{color: '#27ae60', fontWeight: 'bold'}}>
                                        4.1.1 S-a aplicat un discount de {pricing.discount}% pentru durata contractului de {contractDuration} luni,
                                        rezultând o economie totală de {pricing.discountAmount.toFixed(2)} Euro.
                                    </p>
                                )}
                                <p>
                                    4.2 CHIRIAȘUL se obligă să plătească o garanție în valoare de {pricing.securityDeposit.toFixed(2)} Euro,
                                    echivalentul a două chirii lunare, care se va restitui la încetarea contractului,
                                    mai puțin sumele datorate pentru eventualele daune.
                                </p>
                                <p>
                                    4.3 Valoarea totală a contractului pentru întreaga perioadă este de {pricing.totalValue.toFixed(2)} Euro
                                    {pricing.discount > 0 && (
                                        <span style={{color: '#27ae60'}}>
                                            {' '}(față de {pricing.originalTotal.toFixed(2)} Euro fără discount)
                                        </span>
                                    )}.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="contract-sidebar">
                    {/* Strategy Pattern Visualization */}
                    <div style={{
                        backgroundColor: '#e3f2fd',
                        border: '2px solid #2196f3',
                        borderRadius: '8px',
                        padding: '15px',
                        marginBottom: '1.5rem'
                    }}>
                        <h4 style={{margin: '0 0 10px 0', color: '#1976d2'}}>
                            🔧 Strategy Pattern Active
                        </h4>
                        <p style={{margin: '5px 0', fontSize: '14px'}}>
                            <strong>Current Strategy:</strong> {pricing.strategyName}
                        </p>
                        <p style={{margin: '5px 0', fontSize: '14px', color: '#27ae60'}}>
                            {pricing.description}
                        </p>
                        {pricing.discount > 0 && (
                            <p style={{margin: '5px 0', fontSize: '14px', color: '#27ae60', fontWeight: 'bold'}}>
                                💰 Economisești: {pricing.discountAmount.toFixed(2)} €
                            </p>
                        )}
                    </div>

                    <div className="contract-summary">
                        <h3>Sumar Contract</h3>
                        <div className="summary-item">
                            <span className="summary-label">Spațiu:</span>
                            <span className="summary-value">{space.name}</span>
                        </div>
                        <div className="summary-item">
                            <span className="summary-label">Adresă:</span>
                            <span className="summary-value">{space.address || space.buildingAddress}</span>
                        </div>
                        <div className="summary-item">
                            <span className="summary-label">Suprafață:</span>
                            <span className="summary-value">{space.area} m²</span>
                        </div>
                        <div className="summary-item">
                            <span className="summary-label">Preț original:</span>
                            <span className="summary-value" style={pricing.discount > 0 ? {textDecoration: 'line-through', color: '#95a5a6'} : {}}>
                                {space.pricePerMonth} €
                            </span>
                        </div>
                        {pricing.discount > 0 && (
                            <div className="summary-item">
                                <span className="summary-label">Preț cu discount:</span>
                                <span className="summary-value" style={{color: '#27ae60', fontWeight: 'bold'}}>
                                    {pricing.monthlyRent.toFixed(2)} € (-{pricing.discount}%)
                                </span>
                            </div>
                        )}
                        <div className="summary-item">
                            <span className="summary-label">Garanție:</span>
                            <span className="summary-value">{pricing.securityDeposit.toFixed(2)} €</span>
                        </div>
                        <div className="summary-item duration">
                            <span className="summary-label">Durată contract:</span>
                            <select
                                value={contractDuration}
                                onChange={(e) => setContractDuration(parseInt(e.target.value))}
                                className="duration-select"
                                disabled={isSubmitting}
                            >
                                <option value="6">6 luni</option>
                                <option value="12">12 luni</option>
                                <option value="24">24 luni</option>
                                <option value="36">36 luni</option>
                            </select>
                        </div>
                        <div className="summary-item">
                            <span className="summary-label">Perioadă:</span>
                            <span className="summary-value">
                                {formattedStartDate} - {formattedEndDate}
                            </span>
                        </div>
                        {pricing.discount > 0 && (
                            <div className="summary-item" style={{backgroundColor: '#e8f5e9', padding: '10px', borderRadius: '4px', marginBottom: '10px'}}>
                                <span className="summary-label">Economie totală:</span>
                                <span className="summary-value" style={{color: '#27ae60', fontWeight: 'bold'}}>
                                    {pricing.discountAmount.toFixed(2)} €
                                </span>
                            </div>
                        )}
                        <div className="summary-item total-value">
                            <span className="summary-label">Valoare totală:</span>
                            <span className="summary-value">{pricing.totalValue.toFixed(2)} €</span>
                        </div>
                        <div className="summary-item payment-total">
                            <span className="summary-label">Plată inițială:</span>
                            <span className="summary-value">{pricing.initialPayment.toFixed(2)} €</span>
                            <span className="summary-note">(prima lună + garanție)</span>
                        </div>
                    </div>

                    <div className="payment-section">
                        <h3>Metodă de Plată</h3>
                        <div className="payment-options">
                            {[
                                { value: 'card', label: 'Card de credit/debit' },
                                { value: 'transfer', label: 'Transfer bancar' },
                                { value: 'cash', label: 'Numerar la sediul companiei' }
                            ].map(option => (
                                <label key={option.value} className="payment-option">
                                    <input
                                        type="radio"
                                        name="paymentMethod"
                                        value={option.value}
                                        checked={paymentMethod === option.value}
                                        onChange={(e) => setPaymentMethod(e.target.value)}
                                        disabled={isSubmitting}
                                    />
                                    <span className="payment-label">{option.label}</span>
                                </label>
                            ))}
                        </div>
                        {formErrors.paymentMethod && (
                            <div className="validated-input-error">
                                <span className="error-icon">⚠️</span>
                                {formErrors.paymentMethod}
                            </div>
                        )}
                    </div>

                    <div className="signature-section">
                        <h3>Semnătură Electronică</h3>
                        <p className="signature-info">
                            Introduceți numele complet pentru a semna electronic acest contract.
                        </p>
                        <input
                            type="text"
                            value={signatureData}
                            onChange={(e) => setSignatureData(e.target.value)}
                            placeholder="Nume și prenume"
                            disabled={isSubmitting}
                            className={`signature-input-field ${formErrors.signatureData ? 'error' : ''}`}
                        />
                        {formErrors.signatureData && (
                            <div className="validated-input-error">
                                <span className="error-icon">⚠️</span>
                                {formErrors.signatureData}
                            </div>
                        )}
                    </div>

                    <div className="terms-section">
                        <label className="terms-checkbox">
                            <input
                                type="checkbox"
                                checked={termsAccepted}
                                onChange={(e) => setTermsAccepted(e.target.checked)}
                                disabled={isSubmitting}
                            />
                            <span>
                                Am citit și sunt de acord cu termenii și condițiile contractului de închiriere.
                            </span>
                        </label>
                        {formErrors.termsAccepted && (
                            <div className="validated-input-error">
                                <span className="error-icon">⚠️</span>
                                {formErrors.termsAccepted}
                            </div>
                        )}
                    </div>

                    <div className="contract-actions">
                        <button
                            className="btn btn-sign"
                            onClick={handleSubmit}
                            disabled={isSubmitting}
                        >
                            {isSubmitting ? 'Se procesează...' : 'Semnează și Finalizează Contractul'}
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
            </div>
        </div>
    );
}

export default RentalContractPage;