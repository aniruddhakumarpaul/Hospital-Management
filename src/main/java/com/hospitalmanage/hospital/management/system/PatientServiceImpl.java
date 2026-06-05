package com.hospitalmanage.hospital.management.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${app.doctor.password:doc-pass-2026}")
    private String doctorPassword;

    private int getPatientsAhead(Patient p) {
        if (p == null || "TREATED".equals(p.getStatus())) return 0;
        return (int) patientRepository.countByDoctorSpecializationAndStatusNotAndTokenNumberLessThan(
                p.getDoctorSpecialization(), "TREATED", p.getTokenNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDTO> findAll() {
        return patientRepository.findAllByOrderByIsEmergencyDescTokenNumberAsc().stream()
                .map(p -> {
                    PatientDTO dto = PatientDTO.fromEntity(p);
                    dto.setPatientsAhead(getPatientsAhead(p));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDTO findById(Long id) {
        return patientRepository.findById(id)
                .map(p -> {
                    PatientDTO dto = PatientDTO.fromEntity(p);
                    dto.setPatientsAhead(getPatientsAhead(p));
                    return dto;
                })
                .orElse(null);
    }

    @Override
    @Transactional
    public PatientDTO registerPatient(PatientDTO patientDTO) {
        // Sanitization Layer
        if (patientDTO.getName() != null) patientDTO.setName(patientDTO.getName().trim());
        if (patientDTO.getPhoneNumber() != null) patientDTO.setPhoneNumber(patientDTO.getPhoneNumber().trim());
        if (patientDTO.getIllness() != null) patientDTO.setIllness(patientDTO.getIllness().trim());

        // Validation Layer
        if (patientDTO.getName() == null || patientDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Patient name cannot be empty");
        }
        if (patientDTO.getIllness() == null || patientDTO.getIllness().isEmpty()) {
            throw new IllegalArgumentException("Symptoms/Illness cannot be empty");
        }
        Patient patient = patientDTO.toEntity();
        
        // Ensure recordNumber is unique
        while (patientRepository.findByRecordNumber(patient.getRecordNumber()).isPresent()) {
            patient.setRecordNumber("PAT-" + (1000 + new java.util.Random().nextInt(9000)));
        }
        
        // --- NEW: Payment Enforcement Loop ---
        String rawPhone = patient.getPhoneNumber();
        String normalizedPhone = rawPhone.replaceAll("[^0-9]", "");
        if (normalizedPhone.length() > 10) {
            normalizedPhone = normalizedPhone.substring(normalizedPhone.length() - 10);
        }
        
        System.out.println("DEBUG: Investigating Payment Loop for normalized: " + normalizedPhone + " (Raw: " + rawPhone + ")");
        
        List<Patient> history = patientRepository.findByPhoneNumber(normalizedPhone);
        
        int unpaidBalance = 0;
        String cleanCurrent = rawPhone.replaceAll("[^0-9]", "");
        for (Patient p : history) {
            String cleanHistory = p.getPhoneNumber().replaceAll("[^0-9]", "");
            if (cleanHistory.equals(cleanCurrent) && !p.isPaid()) {
                int amount = extractNumericValue(p.getConsultancyFee());
                unpaidBalance += amount;
            }
        }

        if (unpaidBalance > 0) {
            if (!patient.isEmergency()) {
                // Block registration and send balance info
                throw new IllegalStateException("UNPAID_BALANCE:" + unpaidBalance);
            } else {
                // Emergency Carry-over: Add balance to new fee
                aiService.assignDoctor(patient); // This sets the base fee
                int currentFee = extractNumericValue(patient.getConsultancyFee());
                int totalFee = currentFee + unpaidBalance;
                
                // DEBT TRANSFER: Mark previous unpaid records as paid (transferred to this new record)
                for (Patient p : history) {
                    String cleanHistory = p.getPhoneNumber().replaceAll("[^0-9]", "");
                    if (cleanHistory.equals(cleanCurrent) && !p.isPaid()) {
                        p.setPaid(true);
                        patientRepository.save(p);
                    }
                }
                
                patient.setConsultancyFee("₹" + totalFee + " (Inc. O/S ₹" + unpaidBalance + ")");
                System.out.println("DEBUG: Carry-over processed. Total: " + totalFee + " (O/S: " + unpaidBalance + ")");
            }
        } else {
            aiService.assignDoctor(patient);
        }

        // Safety Override: Ensure status is REGISTERED
        if (patient.getStatus() == null) {
            patient.setStatus("REGISTERED");
        }
        
        Patient savedPatient = patientRepository.save(patient);
        PatientDTO registeredDto = PatientDTO.fromEntity(savedPatient);
        registeredDto.setPatientsAhead(getPatientsAhead(savedPatient));
        return registeredDto;
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
                // Logic: If prescription is added, patient is now "TREATED"
                p.setStatus("TREATED");
                p.setTreatmentDate(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")));
            }
            if (patientDetails.getIllness() != null) {
                p.setIllness(patientDetails.getIllness());
            }
            if (patientDetails.getNextAppointmentDate() != null) {
                p.setNextAppointmentDate(patientDetails.getNextAppointmentDate());
            }
            
            Patient updated = patientRepository.save(p);
            PatientDTO dto = PatientDTO.fromEntity(updated);
            dto.setPatientsAhead(getPatientsAhead(updated));
            return dto;
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
                .map(p -> {
                    PatientDTO dto = PatientDTO.fromEntity(p);
                    dto.setPatientsAhead(getPatientsAhead(p));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDTO findByRecordNumber(String recordNumber) {
        return patientRepository.findByRecordNumber(recordNumber)
                .map(p -> {
                    PatientDTO dto = PatientDTO.fromEntity(p);
                    dto.setPatientsAhead(getPatientsAhead(p));
                    return dto;
                })
                .orElse(null);
    }

    @Override
    public boolean authenticateDoctor(String password) {
        // Professional implementation using external config
        return doctorPassword.equals(password);
    }

    @Override
    @Transactional
    public PatientDTO settleBill(Long id) {
        return patientRepository.findById(id).map(p -> {
            p.setPaid(true);
            Patient saved = patientRepository.save(p);
            return PatientDTO.fromEntity(saved);
        }).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDTO> findActiveByDoctor(String doctorName) {
        return patientRepository.findByDoctorAssignedAndStatus(doctorName, "REGISTERED").stream()
                .map(p -> {
                    PatientDTO dto = PatientDTO.fromEntity(p);
                    dto.setPatientsAhead(getPatientsAhead(p));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private int extractNumericValue(String feeStr) {
        if (feeStr == null || feeStr.isEmpty()) return 0;
        // Match only the first sequence of digits (handles "₹1400 (Inc...)" correctly)
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)").matcher(feeStr);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}
