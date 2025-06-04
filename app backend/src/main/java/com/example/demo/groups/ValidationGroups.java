package com.example.demo.groups;

/**
 * Toate grupurile de validare pentru aplicația de managementul spațiilor comerciale.
 * Acestea sunt marker interfaces folosite pentru a grupa validările în scenarii specifice.
 */
public final class ValidationGroups {

    // Constructor privat pentru a preveni instanțierea
    private ValidationGroups() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    // ===== OPERAȚII CRUD =====
    public interface CreateValidation {}
    public interface UpdateValidation {}
    public interface DeleteValidation {}

    // ===== ROLURI UTILIZATORI =====
    public interface AdminValidation {}
    public interface UserValidation {}
    public interface OwnerValidation {}
    public interface TenantValidation {}

    // ===== ETAPE DE PROCESARE =====
    public interface BasicValidation {}
    public interface DetailedValidation {}
    public interface FinalValidation {}

    // ===== TIPURI DE SPAȚII =====
    public interface OfficeSpaceValidation {}
    public interface RetailSpaceValidation {}
    public interface WarehouseSpaceValidation {}

    // ===== CONTEXTE BUSINESS =====
    public interface ContractValidation {}
    public interface PaymentValidation {}
    public interface ReportingValidation {}
}