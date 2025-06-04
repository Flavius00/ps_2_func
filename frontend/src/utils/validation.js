// frontend/src/utils/validation.js
// Funcții de validare pentru toate formularele din aplicație

// ============== VALIDĂRI GENERALE ==============

export const validateEmail = (email) => {
    if (!email) {
        return { isValid: false, message: 'Email-ul este obligatoriu' };
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        return { isValid: false, message: 'Format email invalid' };
    }

    if (email.length > 100) {
        return { isValid: false, message: 'Email-ul nu poate avea mai mult de 100 de caractere' };
    }

    return { isValid: true, message: '' };
};

export const validatePhone = (phone) => {
    if (!phone) {
        return { isValid: false, message: 'Numărul de telefon este obligatoriu' };
    }

    // Acceptă formate: +40123456789, 0123456789, 123456789
    const phoneRegex = /^(\+4|4|0)?[0-9]{9}$/;
    if (!phoneRegex.test(phone.replace(/\s/g, ''))) {
        return { isValid: false, message: 'Format număr telefon invalid (ex: 0712345678)' };
    }

    return { isValid: true, message: '' };
};

export const validateName = (name, fieldName = 'Numele') => {
    if (!name || !name.trim()) {
        return { isValid: false, message: `${fieldName} este obligatoriu` };
    }

    if (name.trim().length < 2) {
        return { isValid: false, message: `${fieldName} trebuie să aibă cel puțin 2 caractere` };
    }

    if (name.length > 100) {
        return { isValid: false, message: `${fieldName} nu poate avea mai mult de 100 de caractere` };
    }

    // Verifică că numele conține doar litere, spații și apostrofuri
    const nameRegex = /^[a-zA-ZăâîșțĂÂÎȘȚ\s'.-]+$/;
    if (!nameRegex.test(name)) {
        return { isValid: false, message: `${fieldName} poate conține doar litere, spații și apostrofuri` };
    }

    return { isValid: true, message: '' };
};

export const validatePassword = (password) => {
    if (!password) {
        return { isValid: false, message: 'Parola este obligatorie' };
    }

    if (password.length < 6) {
        return { isValid: false, message: 'Parola trebuie să aibă cel puțin 6 caractere' };
    }

    if (password.length > 100) {
        return { isValid: false, message: 'Parola nu poate avea mai mult de 100 de caractere' };
    }

    return { isValid: true, message: '' };
};

// ============== VALIDĂRI SPAȚII COMERCIALE ==============

export const validateSpaceName = (name) => {
    if (!name || !name.trim()) {
        return { isValid: false, message: 'Numele spațiului este obligatoriu' };
    }

    if (name.trim().length < 3) {
        return { isValid: false, message: 'Numele spațiului trebuie să aibă cel puțin 3 caractere' };
    }

    if (name.length > 200) {
        return { isValid: false, message: 'Numele spațiului nu poate avea mai mult de 200 de caractere' };
    }

    return { isValid: true, message: '' };
};

export const validateArea = (area) => {
    const numericArea = parseFloat(area);

    if (!area || isNaN(numericArea)) {
        return { isValid: false, message: 'Suprafața este obligatorie și trebuie să fie un număr valid' };
    }

    if (numericArea <= 0) {
        return { isValid: false, message: 'Suprafața trebuie să fie mai mare decât 0' };
    }

    if (numericArea > 10000) {
        return { isValid: false, message: 'Suprafața nu poate fi mai mare de 10,000 m²' };
    }

    // Verifică numărul de zecimale (maximum 2)
    if (area.toString().includes('.') && area.toString().split('.')[1].length > 2) {
        return { isValid: false, message: 'Suprafața poate avea maximum 2 zecimale' };
    }

    return { isValid: true, message: '' };
};

export const validatePrice = (price) => {
    const numericPrice = parseFloat(price);

    if (!price || isNaN(numericPrice)) {
        return { isValid: false, message: 'Prețul este obligatoriu și trebuie să fie un număr valid' };
    }

    if (numericPrice <= 0) {
        return { isValid: false, message: 'Prețul trebuie să fie mai mare decât 0' };
    }

    if (numericPrice > 100000) {
        return { isValid: false, message: 'Prețul nu poate fi mai mare de 100,000 €' };
    }

    // Verifică numărul de zecimale (maximum 2)
    if (price.toString().includes('.') && price.toString().split('.')[1].length > 2) {
        return { isValid: false, message: 'Prețul poate avea maximum 2 zecimale' };
    }

    return { isValid: true, message: '' };
};

export const validateDescription = (description, isRequired = false) => {
    if (isRequired && (!description || !description.trim())) {
        return { isValid: false, message: 'Descrierea este obligatorie' };
    }

    if (description && description.length > 1000) {
        return { isValid: false, message: 'Descrierea nu poate avea mai mult de 1000 de caractere' };
    }

    return { isValid: true, message: '' };
};

export const validateSpaceType = (spaceType) => {
    const validTypes = ['OFFICE', 'RETAIL', 'WAREHOUSE'];

    if (!spaceType) {
        return { isValid: false, message: 'Tipul spațiului este obligatoriu' };
    }

    if (!validTypes.includes(spaceType)) {
        return { isValid: false, message: 'Tipul spațiului selectat nu este valid' };
    }

    return { isValid: true, message: '' };
};

// ============== VALIDĂRI CLĂDIRI ==============

export const validateBuildingName = (name) => {
    if (!name || !name.trim()) {
        return { isValid: false, message: 'Numele clădirii este obligatoriu' };
    }

    if (name.trim().length < 3) {
        return { isValid: false, message: 'Numele clădirii trebuie să aibă cel puțin 3 caractere' };
    }

    if (name.length > 200) {
        return { isValid: false, message: 'Numele clădirii nu poate avea mai mult de 200 de caractere' };
    }

    return { isValid: true, message: '' };
};

export const validateAddress = (address) => {
    if (!address || !address.trim()) {
        return { isValid: false, message: 'Adresa este obligatorie' };
    }

    if (address.trim().length < 5) {
        return { isValid: false, message: 'Adresa trebuie să aibă cel puțin 5 caractere' };
    }

    if (address.length > 300) {
        return { isValid: false, message: 'Adresa nu poate avea mai mult de 300 de caractere' };
    }

    return { isValid: true, message: '' };
};

export const validateFloors = (floors) => {
    const numericFloors = parseInt(floors);

    if (!floors || isNaN(numericFloors)) {
        return { isValid: false, message: 'Numărul de etaje trebuie să fie un număr valid' };
    }

    if (numericFloors < 1) {
        return { isValid: false, message: 'Numărul de etaje trebuie să fie cel puțin 1' };
    }

    if (numericFloors > 100) {
        return { isValid: false, message: 'Numărul de etaje nu poate fi mai mare de 100' };
    }

    return { isValid: true, message: '' };
};

export const validateYear = (year) => {
    const numericYear = parseInt(year);
    const currentYear = new Date().getFullYear();

    if (!year || isNaN(numericYear)) {
        return { isValid: false, message: 'Anul construcției trebuie să fie un număr valid' };
    }

    if (numericYear < 1800) {
        return { isValid: false, message: 'Anul construcției nu poate fi mai mic de 1800' };
    }

    if (numericYear > currentYear) {
        return { isValid: false, message: 'Anul construcției nu poate fi în viitor' };
    }

    return { isValid: true, message: '' };
};

export const validateCoordinates = (latitude, longitude) => {
    const errors = {};

    if (latitude !== undefined && latitude !== '') {
        const numericLat = parseFloat(latitude);
        if (isNaN(numericLat) || numericLat < -90 || numericLat > 90) {
            errors.latitude = 'Latitudinea trebuie să fie între -90 și 90';
        }
    }

    if (longitude !== undefined && longitude !== '') {
        const numericLng = parseFloat(longitude);
        if (isNaN(numericLng) || numericLng < -180 || numericLng > 180) {
            errors.longitude = 'Longitudinea trebuie să fie între -180 și 180';
        }
    }

    return {
        isValid: Object.keys(errors).length === 0,
        errors
    };
};

// ============== VALIDĂRI CONTRACTE ==============

export const validateContractDuration = (duration) => {
    const numericDuration = parseInt(duration);

    if (!duration || isNaN(numericDuration)) {
        return { isValid: false, message: 'Durata contractului trebuie să fie un număr valid' };
    }

    if (numericDuration < 1) {
        return { isValid: false, message: 'Durata contractului trebuie să fie cel puțin 1 lună' };
    }

    if (numericDuration > 120) {
        return { isValid: false, message: 'Durata contractului nu poate fi mai mare de 120 luni (10 ani)' };
    }

    return { isValid: true, message: '' };
};

export const validateDateRange = (startDate, endDate) => {
    if (!startDate || !endDate) {
        return { isValid: false, message: 'Ambele date sunt obligatorii' };
    }

    const start = new Date(startDate);
    const end = new Date(endDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (start < today) {
        return { isValid: false, message: 'Data de început nu poate fi în trecut' };
    }

    if (end <= start) {
        return { isValid: false, message: 'Data de sfârșit trebuie să fie după data de început' };
    }

    // Verifică că diferența nu este prea mare (max 10 ani)
    const diffTime = Math.abs(end - start);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    const maxDays = 10 * 365; // 10 ani

    if (diffDays > maxDays) {
        return { isValid: false, message: 'Durata contractului nu poate fi mai mare de 10 ani' };
    }

    return { isValid: true, message: '' };
};

export const validateSignature = (signature) => {
    if (!signature || !signature.trim()) {
        return { isValid: false, message: 'Semnătura electronică este obligatorie' };
    }

    if (signature.trim().length < 2) {
        return { isValid: false, message: 'Semnătura trebuie să aibă cel puțin 2 caractere' };
    }

    if (signature.length > 100) {
        return { isValid: false, message: 'Semnătura nu poate avea mai mult de 100 de caractere' };
    }

    return { isValid: true, message: '' };
};

// ============== VALIDĂRI MESAJE ==============

export const validateMessage = (message) => {
    if (!message || !message.trim()) {
        return { isValid: false, message: 'Mesajul nu poate fi gol' };
    }

    if (message.trim().length < 1) {
        return { isValid: false, message: 'Mesajul trebuie să aibă cel puțin 1 caracter' };
    }

    if (message.length > 2000) {
        return { isValid: false, message: 'Mesajul nu poate avea mai mult de 2000 de caractere' };
    }

    return { isValid: true, message: '' };
};

// ============== FUNCȚII HELPER ==============

export const validateForm = (formData, validationRules) => {
    const errors = {};
    let isValid = true;

    Object.keys(validationRules).forEach(field => {
        const rules = validationRules[field];
        const value = formData[field];

        for (const rule of rules) {
            const result = rule(value);
            if (!result.isValid) {
                errors[field] = result.message;
                isValid = false;
                break; // Oprește la prima eroare pentru acest câmp
            }
        }
    });

    return { isValid, errors };
};

export const validateFieldOnChange = (fieldName, value, validationRules) => {
    if (!validationRules[fieldName]) {
        return { isValid: true, message: '' };
    }

    const rules = validationRules[fieldName];

    for (const rule of rules) {
        const result = rule(value);
        if (!result.isValid) {
            return result;
        }
    }

    return { isValid: true, message: '' };
};

// ============== REGULI DE VALIDARE PREDEFINITE ==============

export const userValidationRules = {
    name: [validateName],
    email: [validateEmail],
    phone: [validatePhone],
    password: [validatePassword]
};

export const spaceValidationRules = {
    name: [validateSpaceName],
    area: [validateArea],
    pricePerMonth: [validatePrice],
    spaceType: [validateSpaceType],
    description: [(desc) => validateDescription(desc, false)]
};

export const buildingValidationRules = {
    name: [validateBuildingName],
    address: [validateAddress],
    totalFloors: [validateFloors],
    yearBuilt: [validateYear]
};

export const contractValidationRules = {
    monthlyRent: [validatePrice],
    securityDeposit: [validatePrice],
    signature: [validateSignature]
};