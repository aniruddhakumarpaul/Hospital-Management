package com.hospitalmanage.hospital.management.system;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Find patient by PAT-XXXX ID
    Optional<Patient> findByRecordNumber(String recordNumber);

    // Find patient history by phone number
    java.util.List<Patient> findByPhoneNumber(String phoneNumber);

    // Count existing patients for a doctor (Used for Token Number 1, 2, 3...)
    long countByDoctorAssigned(String doctorAssigned);

    // Find highest token number for a doctor safely avoiding deleted overlaps
    Optional<Patient> findFirstByDoctorAssignedOrderByTokenNumberDesc(String doctorAssigned);

    // DB-level sorting for efficient dashboard rendering
    java.util.List<Patient> findAllByOrderByIsEmergencyDescTokenNumberAsc();

    // Stats counter for dashboard
    long countByDoctorSpecialization(String doctorSpecialization);
}