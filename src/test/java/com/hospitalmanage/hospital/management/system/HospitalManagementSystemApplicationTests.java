package com.hospitalmanage.hospital.management.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
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

    @Test
    void testTokenIncrementsConsecutively() {
        patientRepository.deleteAll();

        // Register 10 patients with "chest pain" to assign them to Cardiology
        for (int i = 0; i < 10; i++) {
            PatientDTO p = new PatientDTO();
            p.setName("Patient " + i);
            p.setAge(25);
            p.setPhoneNumber("+91 999999900" + i);
            p.setIllness("chest pain"); // Critical symptom, triggers Cardiology specialty
            p.setEmergency(false);
            p.setPaid(true); // set paid true to avoid unpaid balance enforcement blocks

            PatientDTO registered = patientService.registerPatient(p);
            assertNotNull(registered);
            assertEquals(i + 1, registered.getTokenNumber(), "Token number should increment consecutively for the specialization");
            System.out.println("TEST LOG: Registered " + registered.getName() + " -> Doc: " + registered.getDoctorAssigned() + ", Token: " + registered.getTokenNumber());
        }

        // Assert that there is no doctor who has multiple patients with the same token number
        java.util.List<Patient> allPatients = patientRepository.findAll();
        java.util.Map<String, java.util.Set<Integer>> doctorTokens = new java.util.HashMap<>();
        for (Patient p : allPatients) {
            String doc = p.getDoctorAssigned();
            int tok = p.getTokenNumber();
            if (tok != 0) { // scheduled next-day patients have token 0
                doctorTokens.putIfAbsent(doc, new java.util.HashSet<>());
                boolean added = doctorTokens.get(doc).add(tok);
                assertTrue(added, "Duplicate token " + tok + " allocated to doctor " + doc);
            }
        }
    }

    @Test
    void testDoctorQueueCapacityAndNextDayScheduling() {
        patientRepository.deleteAll();

        // Roster GP has 3 doctors: House, Watson, Murphy.
        // Register 18 routine patients sequentially with GP (General Physician).
        // Since load-balancing is active, they will be distributed: each doctor gets 6 patients.
        for (int i = 0; i < 18; i++) {
            PatientDTO p = new PatientDTO();
            p.setName("GP Routine Patient " + i);
            p.setAge(30);
            p.setPhoneNumber("+91 98765431" + (i < 10 ? "0" + i : i));
            p.setIllness("cough"); // GP scope
            p.setEmergency(false);
            p.setPaid(true);

            PatientDTO registered = patientService.registerPatient(p);
            assertNotNull(registered);
            assertEquals("REGISTERED", registered.getStatus());
            assertNotEquals(0, registered.getTokenNumber());
        }

        // Verify each doctor has exactly 6 patients
        assertEquals(6, patientRepository.countByDoctorAssignedAndStatus("Dr. Gregory House", "REGISTERED"));
        assertEquals(6, patientRepository.countByDoctorAssignedAndStatus("Dr. John Watson", "REGISTERED"));
        assertEquals(6, patientRepository.countByDoctorAssignedAndStatus("Dr. Shaun Murphy", "REGISTERED"));

        // Register the 19th routine patient. Since all GP doctors have 6 waiting patients,
        // this patient should be scheduled for the next day.
        PatientDTO p19 = new PatientDTO();
        p19.setName("Kiosk Overflow Patient");
        p19.setAge(35);
        p19.setPhoneNumber("+91 9876543299");
        p19.setIllness("cough"); // GP scope
        p19.setEmergency(false);
        p19.setPaid(true);

        PatientDTO registered19 = patientService.registerPatient(p19);
        assertNotNull(registered19);
        assertEquals("SCHEDULED", registered19.getStatus());
        assertEquals(0, registered19.getTokenNumber());
        assertEquals(java.time.LocalDate.now().plusDays(1).format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")), registered19.getNextAppointmentDate());
    }
}
