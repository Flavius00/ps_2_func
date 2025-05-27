import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import './RentalContractPage.css';

function RentalContractPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [space, setSpace] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [contractDuration, setContractDuration] = useState(12); // Default 12 months
    const [termsAccepted, setTermsAccepted] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [paymentMethod, setPaymentMethod] = useState('');
    const [signatureData, setSignatureData] = useState('');

    // Calcularea datelor de contract
    const startDate = new Date();
    const formattedStartDate = startDate.toISOString().split('T')[0];

    const endDate = new Date(startDate);
    endDate.setMonth(endDate.getMonth() + parseInt(contractDuration));
    const formattedEndDate = endDate.toISOString().split('T')[0];

    useEffect(() => {
        // Verifică dacă utilizatorul este autentificat și este chiriaș
        const storedUser = JSON.parse(localStorage.getItem('user'));
        if (!storedUser || storedUser.role !== 'TENANT') {
            navigate('/spaces');
            return;
        }
        setUser(storedUser);

        // Verifică dacă există informații despre spațiu
        if (!location.state || !location.state.selectedSpace) {
            setError('Informații despre spațiu lipsă.');
            setLoading(false);
            return;
        }

        setSpace(location.state.selectedSpace);
        setLoading(false);
    }, [location, navigate]);

    const calculateTotal = () => {
        if (!space) return { monthlyRent: 0, securityDeposit: 0, totalValue: 0, initialPayment: 0 };

        const monthlyRent = space.pricePerMonth;
        const securityDeposit = monthlyRent * 2; // Garanție de două luni
        const totalValue = monthlyRent * contractDuration;
        const initialPayment = monthlyRent + securityDeposit; // Prima lună + garanție

        return { monthlyRent, securityDeposit, totalValue, initialPayment };
    };

    const { monthlyRent, securityDeposit, totalValue, initialPayment } = calculateTotal();

    const handleContractDurationChange = (e) => {
        setContractDuration(e.target.value);
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
            // CORECTARE CRITICĂ: Trimite datele în formatul corect pentru backend
            const contractData = {
                // Trimite doar ID-urile pentru space și tenant
                spaceId: space.id,
                tenantId: user.id,
                startDate: formattedStartDate,
                endDate: formattedEndDate,
                monthlyRent: monthlyRent,
                securityDeposit: securityDeposit,
                status: "ACTIVE",
                isPaid: true,
                dateCreated: formattedStartDate,
                contractNumber: `RENT-${Date.now()}`,
                notes: `Contract încheiat electronic. Metodă de plată: ${paymentMethod}. Durata: ${contractDuration} luni. Semnătură: ${signatureData}`,
                paymentMethod: paymentMethod,
                signature: signatureData
            };

            console.log('Sending contract data:', contractData);

            // Trimite datele către backend
            const response = await axios.post('http://localhost:8080/contracts/create', contractData);

            console.log('Contract created successfully:', response.data);

            // Actualizează spațiul ca fiind închiriat
            await axios.post('http://localhost:8080/spaces/update', {
                ...space,
                available: false
            });

            // Creează obiectul pentru pagina de confirmare
            const contractForConfirmation = {
                ...contractData,
                id: response.data.id,
                space: space,
                tenant: user
            };

            // Navigare către pagina de confirmare
            navigate('/payment/confirm', {
                state: {
                    contract: contractForConfirmation,
                    space: space
                }
            });

        } catch (error) {
            console.error('Eroare la crearea contractului:', error);
            if (error.response) {
                console.error('Server response:', error.response.data);
                setError(`Nu s-a putut crea contractul: ${error.response.data}`);
            } else {
                setError('Nu s-a putut crea contractul. Vă rugăm încercați din nou mai târziu.');
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
                                <h5>II. OBIECTUL CONTRACTULUI</h5>
                                <p>
                                    2.1 PROPRIETARUL închiriază CHIRIAȘULUI spațiul comercial situat la adresa {space.address || space.buildingAddress},
                                    având o suprafață de {space.area} m², destinat pentru activități comerciale.
                                </p>
                                <p>
                                    2.2 Spațiul va fi utilizat de CHIRIAȘ exclusiv pentru activitatea sa
                                    comercială, respectiv {user.businessType || 'activitatea comercială a chiriașului'}.
                                </p>
                            </div>

                            <div className="contract-article">
                                <h5>III. DURATA CONTRACTULUI</h5>
                                <p>
                                    3.1 Prezentul contract se încheie pe o perioadă de {contractDuration} luni,
                                    începând cu data de {formattedStartDate} și până la data de {formattedEndDate}.
                                </p>
                                <p>
                                    3.2 La expirarea termenului, contractul poate fi prelungit prin acordul scris al ambelor părți.
                                </p>
                            </div>

                            <div className="contract-article">
                                <h5>IV. PREȚUL ÎNCHIRIERII</h5>
                                <p>
                                    4.1 Chiria lunară este de {monthlyRent} Euro, plătibilă în lei la cursul BNR din ziua plății,
                                    în primele 5 zile ale fiecărei luni.
                                </p>
                                <p>
                                    4.2 CHIRIAȘUL se obligă să plătească o garanție în valoare de {securityDeposit} Euro,
                                    echivalentul a două chirii lunare, care se va restitui la încetarea contractului,
                                    mai puțin sumele datorate pentru eventualele daune.
                                </p>
                                <p>
                                    4.3 Valoarea totală a contractului pentru întreaga perioadă este de {totalValue} Euro.
                                </p>
                            </div>

                            <div className="contract-article">
                                <h5>V. OBLIGAȚIILE PROPRIETARULUI</h5>
                                <p>
                                    5.1 Să predea spațiul în stare corespunzătoare utilizării pentru care a fost închiriat.
                                </p>
                                <p>
                                    5.2 Să asigure folosința liniștită și utilă a spațiului pe toată durata contractului.
                                </p>
                                <p>
                                    5.3 Să efectueze reparațiile majore necesare menținerii spațiului în stare corespunzătoare.
                                </p>
                            </div>

                            <div className="contract-article">
                                <h5>VI. OBLIGAȚIILE CHIRIAȘULUI</h5>
                                <p>
                                    6.1 Să folosească spațiul conform destinației stabilite prin contract.
                                </p>
                                <p>
                                    6.2 Să plătească chiria la termenele și în condițiile stabilite.
                                </p>
                                <p>
                                    6.3 Să efectueze reparațiile locative și de întreținere curentă.
                                </p>
                                <p>
                                    6.4 Să nu subînchirieze sau să cedeze folosința spațiului unui terț fără acordul scris al PROPRIETARULUI.
                                </p>
                                <p>
                                    6.5 Să restituie spațiul la încetarea contractului în starea în care l-a primit, luând în considerare uzura normală.
                                </p>
                            </div>

                            <div className="contract-article">
                                <h5>VII. ÎNCETAREA CONTRACTULUI</h5>
                                <p>
                                    7.1 Contractul încetează la expirarea termenului pentru care a fost încheiat.
                                </p>
                                <p>
                                    7.2 Contractul poate înceta înainte de termen prin acordul scris al părților.
                                </p>
                                <p>
                                    7.3 PROPRIETARUL poate rezilia contractul dacă CHIRIAȘUL nu respectă obligațiile asumate,
                                    în special neachitarea chiriei timp de două luni consecutive.
                                </p>
                                <p>
                                    7.4 CHIRIAȘUL poate rezilia contractul dacă PROPRIETARUL nu respectă obligațiile asumate
                                    privind asigurarea folosinței spațiului.
                                </p>
                            </div>

                            <div className="contract-article">
                                <h5>VIII. FORȚA MAJORĂ</h5>
                                <p>
                                    8.1 Niciuna dintre părți nu răspunde pentru neexecutarea la termen sau/și de
                                    executarea în mod necorespunzător a oricărei obligații care îi revine în baza
                                    prezentului contract, dacă neexecutarea sau executarea necorespunzătoare a fost
                                    cauzată de forța majoră, așa cum este definită de lege.
                                </p>
                            </div>

                            <div className="contract-article">
                                <h5>IX. LITIGII</h5>
                                <p>
                                    9.1 Litigiile de orice fel decurgând din executarea prezentului contract vor fi
                                    soluționate pe cale amiabilă. În cazul în care acest lucru nu este posibil,
                                    litigiile vor fi supuse instanțelor judecătorești competente din România.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="contract-sidebar">
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
                            <span className="summary-label">Chirie lunară:</span>
                            <span className="summary-value">{monthlyRent} €</span>
                        </div>
                        <div className="summary-item">
                            <span className="summary-label">Garanție:</span>
                            <span className="summary-value">{securityDeposit} €</span>
                        </div>
                        <div className="summary-item duration">
                            <span className="summary-label">Durată contract:</span>
                            <select
                                value={contractDuration}
                                onChange={handleContractDurationChange}
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
                        <div className="summary-item total-value">
                            <span className="summary-label">Valoare totală:</span>
                            <span className="summary-value">{totalValue} €</span>
                        </div>
                        <div className="summary-item payment-total">
                            <span className="summary-label">Plată inițială:</span>
                            <span className="summary-value">{initialPayment} €</span>
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