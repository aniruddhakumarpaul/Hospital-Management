package com.hospitalmanage.hospital.management.system;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("GP", patientRepository.countByDoctorSpecializationAndStatus("General Physician", "REGISTERED"));
        stats.put("Cardio", patientRepository.countByDoctorSpecializationAndStatus("Cardiologist", "REGISTERED"));
        stats.put("Peds", patientRepository.countByDoctorSpecializationAndStatus("Pediatrician", "REGISTERED"));
        return stats;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
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

    @GetMapping("/active/doctor/{name}")
    public List<PatientDTO> getActiveByDoctor(@PathVariable String name) {
        return patientService.findActiveByDoctor(name);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPatientQuery(@RequestParam(required = false) String phone, @RequestParam(required = false) String id) {
        if (id != null && id.startsWith("PAT-")) {
            PatientDTO patient = patientService.findByRecordNumber(id);
            return patient != null ? ResponseEntity.ok(patient) : ResponseEntity.notFound().build();
        } else if (phone != null) {
            List<PatientDTO> history = patientService.findByPhoneNumber(phone);
            return history.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(history);
        }
        return ResponseEntity.badRequest().body("Either 'phone' or 'id' required");
    }

    // Legacy support for path-based search
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

    @PostMapping
    public ResponseEntity<?> registerPatient(@RequestBody PatientDTO patientDTO) {
        try {
            PatientDTO registered = patientService.registerPatient(patientDTO);
            return new ResponseEntity<>(registered, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            if (e.getMessage().startsWith("UNPAID_BALANCE:")) {
                String balance = e.getMessage().split(":")[1];
                return ResponseEntity.status(402).body(Map.of(
                    "error", "UNPAID_BALANCE",
                    "balance", balance
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to register patient: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable Long id, @RequestBody PatientDTO patientDetails) {
        PatientDTO updated = patientService.updatePatient(id, patientDetails);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/settle")
    public ResponseEntity<PatientDTO> settleBill(@PathVariable Long id) {
        PatientDTO settled = patientService.settleBill(id);
        return settled != null ? ResponseEntity.ok(settled) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

}