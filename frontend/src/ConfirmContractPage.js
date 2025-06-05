import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './ConfirmContractPage.css';
import { jsPDF } from 'jspdf';

function ConfirmContractPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const [contract, setContract] = useState(null);
    const [space, setSpace] = useState(null);
    const [isDataMissing, setIsDataMissing] = useState(false);
    const [countdown, setCountdown] = useState(5); // Am schimbat la 5 pentru că în UI ai 5 secunde
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        // Verifică dacă există date necesare în state
        if (!location.state || !location.state.contract || !location.state.space) {
            setIsDataMissing(true);
            setIsLoading(false);
            return;
        }

        setContract(location.state.contract);
        setSpace(location.state.space);
        setIsLoading(false);
    }, [location.state]); // Am păstrat doar location.state în dependențe

    // useEffect separat pentru countdown când datele lipsesc
    useEffect(() => {
        if (isDataMissing && countdown > 0) {
            const timer = setTimeout(() => {
                setCountdown(countdown - 1);
            }, 1000);
            return () => clearTimeout(timer);
        } else if (isDataMissing && countdown === 0) {
            navigate('/'); // sau unde vrei să redirecționezi
        }
    }, [isDataMissing, countdown, navigate]);

    const handleViewContracts = () => {
        navigate('/contracts');
    };

    const handleDownloadContract = () => {
        if (!contract || !space) {
            alert("Informațiile contractului nu sunt disponibile.");
            return;
        }

        console.log(contract.paymentMethod);
        try {
            const doc = new jsPDF();

            doc.setFontSize(20);
            doc.setTextColor(0, 51, 102);
            doc.text("CONTRACT DE ÎNCHIRIERE", 105, 15, { align: "center" });
            doc.setFontSize(14);
            doc.text("Nr. " + contract.contractNumber, 105, 25, { align: "center" });

            doc.setFontSize(12);
            doc.setTextColor(0, 0, 0);
            doc.text("DETALII SPAȚIU ÎNCHIRIAT:", 20, 40);

            doc.setFontSize(10);
            doc.text("Nume: " + space.name, 25, 50);
            doc.text("Suprafață: " + space.area + " m²", 25, 57);

            // Adăugarea informațiilor despre contract
            doc.setFontSize(12);
            doc.text("DETALII CONTRACT:", 20, 70);

            doc.setFontSize(10);
            doc.text("Numărul Contract: " + contract.contractNumber, 25, 80);
            doc.text("Perioadă: " + contract.startDate + " - " + contract.endDate, 25, 87);
            doc.text("Chirie Lunară: " + contract.monthlyRent + " €", 25, 94);
            doc.text("Garanție: " + contract.securityDeposit + " €", 25, 101);

            const paymentMethodText =
                contract.paymentMethod === 'card' ? 'Card de credit/debit' :
                    contract.paymentMethod === 'transfer' ? 'Transfer bancar' :
                        contract.paymentMethod === 'cash' ? 'Numerar' : contract.paymentMethod;

            doc.text("Metodă de Plată: " + paymentMethodText, 25, 108);

            doc.setFontSize(12);
            doc.text("SEMNĂTURI:", 20, 125);

            doc.setFontSize(10);
            doc.text("Proprietar: ______________________", 25, 140);
            doc.text("Chiriaș: ________________________", 25, 150);

            const currentDate = new Date().toLocaleDateString('ro-RO');
            doc.text("Document generat la data: " + currentDate, 20, 180);

            doc.setFontSize(8);
            doc.text("Acest document reprezintă o confirmare a contractului de închiriere. Pentru informații suplimentare, contactați support@spatii-comerciale.ro.", 20, 270);

            doc.save("Contract_" + contract.contractNumber + ".pdf");

        } catch (error) {
            console.error("Eroare la generarea PDF-ului:", error);
            alert("A apărut o eroare la generarea PDF-ului. Vă rugăm să încercați din nou.");
        }
    };

    if (isDataMissing) {
        return (
            <div className="contract-confirmation-container error">
                <div className="error-message">
                    <i className="error-icon">⚠️</i>
                    <h2>Informații lipsă</h2>
                    <p>Nu s-au găsit informațiile necesare pentru afișarea confirmării contractului.</p>
                    <p>Veți fi redirecționat în {countdown} secunde...</p>
                </div>
            </div>
        );
    }

    if (isLoading) {
        return (
            <div className="contract-confirmation-container">
                <div className="loading-message">
                    <p>Se încarcă informațiile contractului...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="contract-confirmation-container">
            <div className="confirmation-card">
                <div className="success-icon">✓</div>
                <h2>Contract Semnat cu Succes!</h2>
                <p className="confirmation-message">
                    Contractul de închiriere pentru spațiul <strong>{space?.name}</strong> a fost semnat și înregistrat cu succes.
                </p>

                <div className="contract-details">
                    <div className="detail-item">
                        <span className="detail-label">Număr Contract:</span>
                        <span className="detail-value">{contract?.contractNumber}</span>
                    </div>
                    <div className="detail-item">
                        <span className="detail-label">Spațiu Închiriat:</span>
                        <span className="detail-value">{space?.name} ({space?.area} m²)</span>
                    </div>
                    <div className="detail-item">
                        <span className="detail-label">Perioadă:</span>
                        <span className="detail-value">{contract?.startDate} - {contract?.endDate}</span>
                    </div>
                    <div className="detail-item">
                        <span className="detail-label">Chirie Lunară:</span>
                        <span className="detail-value">{contract?.monthlyRent} €</span>
                    </div>
                    <div className="detail-item">
                        <span className="detail-label">Garanție:</span>
                        <span className="detail-value">{contract?.securityDeposit} €</span>
                    </div>
                    <div className="detail-item">
                        <span className="detail-label">Metodă de Plată:</span>
                        <span className="detail-value">
                            {contract?.paymentMethod === 'card' ? 'Card de credit/debit' :
                                contract?.paymentMethod === 'transfer' ? 'Transfer bancar' :
                                    contract?.paymentMethod === 'cash' ? 'Numerar' : contract?.paymentMethod}
                        </span>
                    </div>
                </div>

                <div className="confirmation-actions">
                    <button className="btn btn-primary" onClick={handleViewContracts}>
                        Vezi Contractele Mele
                    </button>
                    <button className="btn btn-secondary" onClick={handleDownloadContract}>
                        Descarcă Contract PDF
                    </button>
                </div>

                <div className="contact-info">
                    <p>Aveți întrebări despre contractul dumneavoastră?</p>
                    <p>Contactați-ne la <a href="mailto:support@spatii-comerciale.ro">support@spatii-comerciale.ro</a> sau sunați la <strong>0712 345 678</strong>.</p>
                </div>
            </div>
        </div>
    );
}

export default ConfirmContractPage;