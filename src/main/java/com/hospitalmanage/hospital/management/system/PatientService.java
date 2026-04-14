package com.hospitalmanage.hospital.management.system;

import java.util.List;

public interface PatientService {
    List<PatientDTO> findAll();
    PatientDTO findById(Long id);
    PatientDTO registerPatient(PatientDTO patientDTO);
    PatientDTO updatePatient(Long id, PatientDTO patientDetails);
    void deletePatient(Long id);
    List<PatientDTO> findByPhoneNumber(String phoneNumber);
    PatientDTO findByRecordNumber(String recordNumber);
    boolean authenticateDoctor(String password);
}
