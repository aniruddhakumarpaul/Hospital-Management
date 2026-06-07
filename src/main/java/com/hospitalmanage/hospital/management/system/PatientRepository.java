package com.hospitalmanage.hospital.management.system;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Find patient by PAT-XXXX ID
    Optional<Patient> findByRecordNumber(String recordNumber);

    // Find patient history by phone number (Flexible suffix matching for user convenience)
    @org.springframework.data.jpa.repository.Query("SELECT p FROM Patient p WHERE p.phoneNumber LIKE %:phoneNumber")
    java.util.List<Patient> findByPhoneNumber(String phoneNumber);

    // Count existing patients for a doctor (Used for Token Number 1, 2, 3...)
    long countByDoctorAssigned(String doctorAssigned);

    // Count active waiting patients in a doctor's queue
    long countByDoctorAssignedAndStatus(String doctorAssigned, String status);

    // Find highest token number for a doctor safely avoiding deleted overlaps
    Optional<Patient> findFirstByDoctorAssignedOrderByTokenNumberDesc(String doctorAssigned);

    // Get the maximum token number assigned to a doctor currently
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(MAX(p.tokenNumber), 0) FROM Patient p WHERE p.doctorAssigned = :doctorAssigned")
    int findMaxTokenNumberByDoctorAssigned(@org.springframework.data.repository.query.Param("doctorAssigned") String doctorAssigned);

    // Get the maximum token number assigned to a specialization currently
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(MAX(p.tokenNumber), 0) FROM Patient p WHERE p.doctorSpecialization = :doctorSpecialization")
    int findMaxTokenNumberByDoctorSpecialization(@org.springframework.data.repository.query.Param("doctorSpecialization") String doctorSpecialization);

    // DB-level sorting for efficient dashboard rendering
    java.util.List<Patient> findAllByOrderByIsEmergencyDescTokenNumberAsc();

    // Query to filter active vs treated patients for specific doctors
    java.util.List<Patient> findByDoctorAssignedAndStatus(String doctorAssigned, String status);

    // Stats counter for dashboard
    long countByDoctorSpecialization(String doctorSpecialization);

    // Stats counter for dashboard (Waiting patients only)
    long countByDoctorSpecializationAndStatusNot(String doctorSpecialization, String status);

    // Count patients in queue by specialization and status
    long countByDoctorSpecializationAndStatus(String doctorSpecialization, String status);

    // Dynamic Queue Logic: Count patients IN FRONT of this token who are not yet treated
    long countByDoctorSpecializationAndStatusNotAndTokenNumberLessThan(String doctorSpecialization, String status, int tokenNumber);
}