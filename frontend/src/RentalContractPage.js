import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import './RentalContractPage.css';

// ============ STRATEGY PATTERN IMPLEMENTATION ============

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
        console.log(`🔄 Strategy changed to: ${strategy.getStrategyName()}`);
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
        console.log(`🏭 Creating strategy for ${duration} months`);

        if (duration < 12) {
            return new ShortTermStrategy();
        } else if (duration === 12) {
            return new MediumTermStrategy();
        } else {
            return new LongTermStrategy();
        }
    }
}

// ============ REACT COMPONENT ============

function RentalContractPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [space, setSpace] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [contractDuration, setContractDuration] = useState(12);
    const [termsAccepted, setTermsAccepted] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [paymentMethod, setPaymentMethod] = useState('');
    const [signatureData, setSignatureData] = useState('');

    // Strategy Pattern: Context și pricing
    const [pricingContext] = useState(new PricingContext());
    const [pricing, setPricing] = useState(null);

    const startDate = new Date();
    const formattedStartDate = startDate.toISOString().split('T')[0];

    const endDate = new Date(startDate);
    endDate.setMonth(endDate.getMonth() + parseInt(contractDuration));
    const formattedEndDate = endDate.toISOString().split('T')[0];

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

    // Strategy Pattern: Recalculează prețurile când se schimbă durata
    useEffect(() => {
        if (space && contractDuration) {
            console.log(`💰 Calculating pricing for ${contractDuration} months`);

            // Creează și setează strategia
            const strategy = PricingStrategyFactory.createStrategy(parseInt(contractDuration));
            pricingContext.setStrategy(strategy);

            // Calculează prețurile
            const result = pricingContext.calculatePrice(space.pricePerMonth, parseInt(contractDuration));
            setPricing(result);

            console.log('📊 Pricing result:', result);
        }
    }, [space, contractDuration, pricingContext]);

    const handleContractDurationChange = (e) => {
        const newDuration = e.target.value;
        console.log(`📅 Duration changed from ${contractDuration} to ${newDuration} months`);
        setContractDuration(newDuration);
    };

    const handleTermsAccepted = (e) => {
        setTermsAccepted(e.target.checked);
    };

    const handlePaymentMethodChange = (e) => {
        setPaymentMethod(e.target.value);
    };

    const handleSignatureChange = (e) => {
        setSignatureData(e.target.value);
    };

    const handleCancel = () => {
        navigate('/spaces');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!termsAccepted) {
            alert('Trebuie să acceptați termenii și condițiile contractului înainte de a continua.');
            return;
        }

        if (!paymentMethod) {
            alert('Vă rugăm să selectați o metodă de plată.');
            return;
        }

        if (!signatureData.trim()) {
            alert('Vă rugăm să adăugați semnătura electronică.');
            return;
        }

        setIsSubmitting(true);

        try {
            const contractData = {
                spaceId: space.id,
                tenantId: user.id,
                startDate: formattedStartDate,
                endDate: formattedEndDate,
                monthlyRent: pricing.monthlyRent, // Folosește prețul calculat de strategii
                securityDeposit: pricing.securityDeposit,
                status: "ACTIVE",
                isPaid: true,
                dateCreated: formattedStartDate,
                contractNumber: `RENT-${Date.now()}`,
                notes: `Contract încheiat electronic. Metodă de plată: ${paymentMethod}. Durata: ${contractDuration} luni. Discount aplicat: ${pricing.discount}%. Economii: ${pricing.discountAmount}€. Strategie folosită: ${pricing.strategyName}. Semnătură: ${signatureData}`
            };

            console.log('📝 Creating contract with pricing strategy data:', contractData);

            const response = await axios.post('http://localhost:8080/contracts/create', contractData, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            console.log('✅ Contract created successfully:', response.data);

            try {
                await axios.post('http://localhost:8080/spaces/update', {
                    id: space.id,
                    name: space.name,
                    description: space.description,
                    area: space.area,
                    pricePerMonth: space.pricePerMonth,
                    address: space.address,
                    available: false,
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
            } catch (spaceUpdateError) {
                console.warn('Failed to update space availability:', spaceUpdateError);
            }

            const contractForConfirmation = {
                ...contractData,
                id: response.data.id,
                paymentMethod: paymentMethod,
                signature: signatureData,
                appliedDiscount: pricing.discount,
                savings: pricing.discountAmount,
                strategyUsed: pricing.strategyName
            };

            navigate('/payment/confirm', {
                state: {
                    contract: contractForConfirmation,
                    space: space
                }
            });

        } catch (error) {
            console.error('❌ Error creating contract:', error);
            if (error.response) {
                if (error.response.status === 400) {
                    if (error.response.data.errors) {
                        const validationErrors = error.response.data.errors;
                        const errorMessages = Object.entries(validationErrors)
                            .map(([field, message]) => `${field}: ${message}`)
                            .join('\n');
                        setError(`Erori de validare:\n${errorMessages}`);
                    } else if (error.response.data.message) {
                        setError(`Eroare: ${error.response.data.message}`);
                    } else {
                        setError('Datele contractului nu sunt valide. Vă rugăm verificați informațiile introduse.');
                    }
                } else if (error.response.status === 404) {
                    setError('Spațiul sau chiriașul nu a fost găsit. Vă rugăm reîncărcați pagina și încercați din nou.');
                } else {
                    setError(`Nu s-a putut crea contractul: ${error.response.data.message || 'Eroare de server'}`);
                }
            } else if (error.request) {
                setError('Nu s-a putut conecta la server. Verificați conexiunea la internet.');
            } else {
                setError('A apărut o eroare neașteptată. Vă rugăm încercați din nou.');
            }
        } finally {
            setIsSubmitting(false);
        }
    };

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
                                onChange={handleContractDurationChange}
                                disabled={isSubmitting}
                                style={{
                                    padding: '8px',
                                    borderRadius: '4px',
                                    border: '2px solid #2196f3'
                                }}
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
                            <label className="payment-option">
                                <input
                                    type="radio"
                                    name="paymentMethod"
                                    value="card"
                                    checked={paymentMethod === 'card'}
                                    onChange={handlePaymentMethodChange}
                                    disabled={isSubmitting}
                                />
                                <span className="payment-label">Card de credit/debit</span>
                            </label>
                            <label className="payment-option">
                                <input
                                    type="radio"
                                    name="paymentMethod"
                                    value="transfer"
                                    checked={paymentMethod === 'transfer'}
                                    onChange={handlePaymentMethodChange}
                                    disabled={isSubmitting}
                                />
                                <span className="payment-label">Transfer bancar</span>
                            </label>
                            <label className="payment-option">
                                <input
                                    type="radio"
                                    name="paymentMethod"
                                    value="cash"
                                    checked={paymentMethod === 'cash'}
                                    onChange={handlePaymentMethodChange}
                                    disabled={isSubmitting}
                                />
                                <span className="payment-label">Numerar la sediul companiei</span>
                            </label>
                        </div>
                    </div>

                    <div className="signature-section">
                        <h3>Semnătură Electronică</h3>
                        <p className="signature-info">
                            Introduceți numele complet pentru a semna electronic acest contract.
                        </p>
                        <input
                            type="text"
                            className="signature-input"
                            placeholder="Nume și prenume"
                            value={signatureData}
                            onChange={handleSignatureChange}
                            disabled={isSubmitting}
                        />
                    </div>

                    <div className="terms-section">
                        <label className="terms-checkbox">
                            <input
                                type="checkbox"
                                checked={termsAccepted}
                                onChange={handleTermsAccepted}
                                disabled={isSubmitting}
                            />
                            <span>
                                Am citit și sunt de acord cu termenii și condițiile contractului de închiriere.
                            </span>
                        </label>
                    </div>

                    <div className="contract-actions">
                        <button
                            className="btn btn-sign"
                            onClick={handleSubmit}
                            disabled={isSubmitting || !termsAccepted || !paymentMethod || !signatureData}
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