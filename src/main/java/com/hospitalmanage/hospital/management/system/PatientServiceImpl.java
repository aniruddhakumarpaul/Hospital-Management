package com.hospitalmanage.hospital.management.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorAIService aiService;

    @Override
    @Transactional(readOnly = true)
    public List<PatientDTO> findAll() {
        return patientRepository.findAllByOrderByIsEmergencyDescTokenNumberAsc().stream()
                .map(PatientDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDTO findById(Long id) {
        return patientRepository.findById(id)
                .map(PatientDTO::fromEntity)
                .orElse(null);
    }

    @Override
    @Transactional
    public PatientDTO registerPatient(PatientDTO patientDTO) {
        Patient patient = patientDTO.toEntity();
        // Move core business logic here
        aiService.assignDoctor(patient);
        Patient savedPatient = patientRepository.save(patient);
        return PatientDTO.fromEntity(savedPatient);
    }

    @Override
    @Transactional
    public PatientDTO updatePatient(Long id, PatientDTO patientDetails) {
        return patientRepository.findById(id).map(p -> {
            if (patientDetails.getPredictedDisease() != null) {
                p.setPredictedDisease(patientDetails.getPredictedDisease());
            }
            if (patientDetails.getPrescription() != null) {
                p.setPrescription(patientDetails.getPrescription());
            }
            if (patientDetails.getIllness() != null) {
                p.setIllness(patientDetails.getIllness());
            }
            Patient updated = patientRepository.save(p);
            return PatientDTO.fromEntity(updated);
        }).orElse(null);
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDTO> findByPhoneNumber(String phoneNumber) {
        return patientRepository.findByPhoneNumber(phoneNumber).stream()
                .map(PatientDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDTO findByRecordNumber(String recordNumber) {
        return patientRepository.findByRecordNumber(recordNumber)
                .map(PatientDTO::fromEntity)
                .orElse(null);
    }

    @Override
    public boolean authenticateDoctor(String password) {
        // Professional mock implementation
        return "doc-pass-2026".equals(password);
    }
}
