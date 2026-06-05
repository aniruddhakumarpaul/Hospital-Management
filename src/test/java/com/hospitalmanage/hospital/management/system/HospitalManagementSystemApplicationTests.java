package com.hospitalmanage.hospital.management.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HospitalManagementSystemApplicationTests {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void testCountryCodeAwareBillingSeparation() {
        // Clear potential existing test data to start fresh
        patientRepository.deleteAll();

        // 1. Register Patient A with +91 9876543210 (Unpaid)
        PatientDTO pA = new PatientDTO();
        pA.setName("Patient India");
        pA.setAge(30);
        pA.setPhoneNumber("+91 9876543210");
        pA.setIllness("Mild fever");
        pA.setEmergency(false);
        pA.setPaid(false);

        PatientDTO registeredA = patientService.registerPatient(pA);
        assertNotNull(registeredA);
        assertFalse(registeredA.isPaid());
        
        // Settle bill check
        PatientDTO foundA = patientService.findById(registeredA.getId());
        assertFalse(foundA.isPaid());

        // 2. Register Patient B with +1 9876543210.
        // It shares the same 10-digit local number (9876543210) but has a different country code.
        // Since we fixed the bug, Patient B should register successfully without throwing UNPAID_BALANCE!
        PatientDTO pB = new PatientDTO();
        pB.setName("Patient US");
        pB.setAge(40);
        pB.setPhoneNumber("+1 9876543210");
        pB.setIllness("Headache");
        pB.setEmergency(false);
        pB.setPaid(false);

        // This call should NOT throw an IllegalStateException!
        PatientDTO registeredB = null;
        try {
            registeredB = patientService.registerPatient(pB);
        } catch (IllegalStateException e) {
            fail("Should not throw unpaid balance exception for different country code: " + e.getMessage());
        }

        assertNotNull(registeredB);
        assertEquals("Patient US", registeredB.getName());
    }
}
