package com.hospitalmanage.hospital.management.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @GetMapping("/stats")
    public java.util.Map<String, Long> getStats() {
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("GP", patientRepository.countByDoctorSpecialization("General Physician"));
        stats.put("Cardio", patientRepository.countByDoctorSpecialization("Cardiologist"));
        stats.put("Peds", patientRepository.countByDoctorSpecialization("Pediatrician"));
        return stats;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody java.util.Map<String, String> credentials) {
        String password = credentials.get("password");
        if (patientService.authenticateDoctor(password)) {
            return ResponseEntity.ok("Success");
        }
        return ResponseEntity.status(401).body("Unauthorized");
    }

    @GetMapping
    public List<PatientDTO> getAllPatients() {
        return patientService.findAll();
    }

    @GetMapping("/search/{idOrPhone}")
    public ResponseEntity<?> searchPatient(@PathVariable String idOrPhone) {
        if (idOrPhone.startsWith("PAT-")) {
            PatientDTO patient = patientService.findByRecordNumber(idOrPhone);
            return patient != null ? ResponseEntity.ok(patient) : ResponseEntity.notFound().build();
        } else {
            List<PatientDTO> history = patientService.findByPhoneNumber(idOrPhone);
            return history.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(history);
        }
    }

    @GetMapping("/history/{phoneNumber}")
    public List<PatientDTO> getPatientHistory(@PathVariable String phoneNumber) {
        return patientService.findByPhoneNumber(phoneNumber);
    }

    @PostMapping
    public PatientDTO createPatient(@RequestBody PatientDTO patientDTO) {
        return patientService.registerPatient(patientDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable Long id, @RequestBody PatientDTO patientDetails) {
        PatientDTO updated = patientService.updatePatient(id, patientDetails);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        if (patientService.findById(id) != null) {
            patientService.deletePatient(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}