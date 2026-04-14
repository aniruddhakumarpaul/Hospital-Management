package com.hospitalmanage.hospital.management.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class DoctorAIService {

    @Autowired
    private PatientRepository patientRepository;

    private static final Map<String, String[]> KNOWLEDGE_BASE = new HashMap<>();
    private static final Map<String, String[]> DOCTOR_ROSTER = new HashMap<>();
    private static final Map<String, String> QUALIFICATIONS = new HashMap<>();

    private static final String[] FEES = {"400", "500", "600", "1000"};
    private static final int[] APPOINTMENT_DAYS = {7, 15, 30, 45};

    private static final Map<String, String> PRESCRIBER_DATABASE = new HashMap<>();
    private static final Map<String, String> WELLNESS_TIPS = new HashMap<>();
    private static final java.util.List<String> CRITICAL_SYMPTOMS = new java.util.ArrayList<>();
    private static final Map<String, String> EMERGENCY_ON_CALL = new HashMap<>();

    static {
        EMERGENCY_ON_CALL.put("Doctor", "Dr. Stephen Strange");
        EMERGENCY_ON_CALL.put("Specialization", "Senior Trauma Specialist");

        PRESCRIBER_DATABASE.put("General Physician", "Paracetamol 500mg, Amoxicillin, Cetirizine");
        PRESCRIBER_DATABASE.put("Cardiologist", "Aspirin, Atorvastatin, Metoprolol");
        PRESCRIBER_DATABASE.put("Gastroenterologist", "Omeprazole, Domperidone, Ondansetron");
        PRESCRIBER_DATABASE.put("Orthopedic Surgeon", "Ibuprofen, Naproxen, Calcium Supplements");
        PRESCRIBER_DATABASE.put("Dermatologist", "Hydrocortisone Cream, Isotretinoin, Mupirocin");
        PRESCRIBER_DATABASE.put("Psychiatrist", "Fluoxetine, Alprazolam, Sertraline");
        PRESCRIBER_DATABASE.put("Ophthalmologist", "Carboxymethylcellulose Drops, Tobramycin, Latanoprost");
        PRESCRIBER_DATABASE.put("ENT Specialist", "Azithromycin, Fluticasone Nasal Spray, Ear Drops");
        PRESCRIBER_DATABASE.put("Gynecologist", "Folic Acid, Iron Supplements, Metformin");
        PRESCRIBER_DATABASE.put("Urologist", "Tamsulosin, Finasteride, Nitrofurantoin");
        PRESCRIBER_DATABASE.put("Endocrinologist", "Metformin, Levothyroxine, Insulin");
        PRESCRIBER_DATABASE.put("Pulmonologist", "Salbutamol Inhaler, Budesonide, Montelukast");
        PRESCRIBER_DATABASE.put("Neurologist", "Gabapentin, Levetiracetam, Donepezil");
        PRESCRIBER_DATABASE.put("Nephrologist", "Furosemide, Enalapril, Erythropoietin");
        PRESCRIBER_DATABASE.put("Pediatrician", "Paracetamol Syrup, Zinc Drops, Vitamin D3");
        PRESCRIBER_DATABASE.put("Oncologist", "Ondansetron, Tamoxifen, Methotrexate");

        // Wellness Guidance Database
        PRESCRIBER_DATABASE.put("Wellness Default", "Maintain a balanced diet, stay hydrated, and ensure 7-8 hours of sleep.");
        
        WELLNESS_TIPS.put("General Physician", "Monitor temperature every 4 hours, stay hydrated, and rest completely.");
        WELLNESS_TIPS.put("Cardiologist", "Avoid salt and heavy exertion. Monitor blood pressure twice daily.");
        WELLNESS_TIPS.put("Pulmonologist", "Use a humidifier and avoid dust or smoke. Practice deep breathing exercises.");
        WELLNESS_TIPS.put("Gastroenterologist", "Stick to a bland diet (BRAT) and avoid spicy or oily foods.");
        WELLNESS_TIPS.put("Dermatologist", "Avoid scratching affected areas and use lukewarm water for bathing.");
        WELLNESS_TIPS.put("Psychiatrist", "Practice mindfulness and maintain a regular sleep schedule.");
        WELLNESS_TIPS.put("Orthopedic Surgeon", "Apply cold compress to swelling and avoid weight-bearing on the affected limb.");

        CRITICAL_SYMPTOMS.add("chest pain");
        CRITICAL_SYMPTOMS.add("difficulty breathing");
        CRITICAL_SYMPTOMS.add("unconscious");
        CRITICAL_SYMPTOMS.add("seizure");
        CRITICAL_SYMPTOMS.add("heavy bleeding");
        CRITICAL_SYMPTOMS.add("stroke");
        CRITICAL_SYMPTOMS.add("paralysis");

        // 1. General Physician
        KNOWLEDGE_BASE.put("General Physician", new String[]{"fever", "cough", "cold", "headache", "body pain", "flu", "sinus", "migraine", "weakness"});
        DOCTOR_ROSTER.put("General Physician", new String[]{"Dr. Gregory House", "Dr. John Watson", "Dr. Shaun Murphy"});
        QUALIFICATIONS.put("General Physician", "MBBS, MD (General Medicine)");

        // 2. Cardiologist
        KNOWLEDGE_BASE.put("Cardiologist", new String[]{"chest pain", "shortness of breath", "palpitations", "swelling in legs", "heart", "pulse"});
        DOCTOR_ROSTER.put("Cardiologist", new String[]{"Dr. Stephen Strange", "Dr. Cristina Yang"});
        QUALIFICATIONS.put("Cardiologist", "MBBS, MD, DM (Cardiology)");

        // 3. Gastroenterologist
        KNOWLEDGE_BASE.put("Gastroenterologist", new String[]{"stomach pain", "acidity", "vomiting", "diarrhea", "constipation", "indigestion"});
        DOCTOR_ROSTER.put("Gastroenterologist", new String[]{"Dr. Hannibal Lecter", "Dr. Miranda Bailey"});
        QUALIFICATIONS.put("Gastroenterologist", "MBBS, MD, DM (Gastroenterology)");

        // 4. Orthopedic Surgeon
        KNOWLEDGE_BASE.put("Orthopedic Surgeon", new String[]{"joint pain", "back pain", "fracture", "swelling", "arthritis", "bone", "knee"});
        DOCTOR_ROSTER.put("Orthopedic Surgeon", new String[]{"Dr. Callie Torres", "Dr. Seeley Booth"});
        QUALIFICATIONS.put("Orthopedic Surgeon", "MBBS, MS (Orthopedics)");

        // 5. Dermatologist
        KNOWLEDGE_BASE.put("Dermatologist", new String[]{"rash", "acne", "fungal", "hair fall", "eczema", "skin", "itch"});
        DOCTOR_ROSTER.put("Dermatologist", new String[]{"Dr. Mark Sloan", "Dr. Jackson Avery"});
        QUALIFICATIONS.put("Dermatologist", "MBBS, MD (Dermatology)");

        // 6. Psychiatrist
        KNOWLEDGE_BASE.put("Psychiatrist", new String[]{"stress", "anxiety", "depression", "mood swings", "sleep", "insomnia"});
        DOCTOR_ROSTER.put("Psychiatrist", new String[]{"Dr. Frasier Crane", "Dr. Phil"});
        QUALIFICATIONS.put("Psychiatrist", "MBBS, MD (Psychiatry)");

        // 7. Ophthalmologist
        KNOWLEDGE_BASE.put("Ophthalmologist", new String[]{"blurred vision", "eye pain", "itching eye", "redness", "cataract"});
        DOCTOR_ROSTER.put("Ophthalmologist", new String[]{"Dr. Iris West", "Dr. Cyclops"});
        QUALIFICATIONS.put("Ophthalmologist", "MBBS, MS (Ophthalmology)");

        // 8. ENT Specialist
        KNOWLEDGE_BASE.put("ENT Specialist", new String[]{"ear pain", "hearing loss", "sore throat", "tonsils", "nose"});
        DOCTOR_ROSTER.put("ENT Specialist", new String[]{"Dr. Hawkeye Pierce", "Dr. Sheldon Cooper"});
        QUALIFICATIONS.put("ENT Specialist", "MBBS, MS (ENT)");

        // 9. Gynecologist
        KNOWLEDGE_BASE.put("Gynecologist", new String[]{"irregular periods", "cramps", "pregnancy", "vaginal", "menopause"});
        DOCTOR_ROSTER.put("Gynecologist", new String[]{"Dr. Addison Montgomery", "Dr. Carina DeLuca"});
        QUALIFICATIONS.put("Gynecologist", "MBBS, MS (Obs & Gyn)");

        // 10. Urologist
        KNOWLEDGE_BASE.put("Urologist", new String[]{"erectile", "infertility", "urinary", "testicle", "bladder"});
        DOCTOR_ROSTER.put("Urologist", new String[]{"Dr. Catherine Avery", "Dr. Turk"});
        QUALIFICATIONS.put("Urologist", "MBBS, MS, MCh (Urology)");

        // 11. Endocrinologist
        KNOWLEDGE_BASE.put("Endocrinologist", new String[]{"weight change", "fatigue", "thyroid", "diabetes", "hormone"});
        DOCTOR_ROSTER.put("Endocrinologist", new String[]{"Dr. Banner", "Dr. Jekyll"});
        QUALIFICATIONS.put("Endocrinologist", "MBBS, MD, DM (Endocrinology)");

        // 12. Pulmonologist
        KNOWLEDGE_BASE.put("Pulmonologist", new String[]{"difficulty breathing", "asthma", "chronic cough", "wheezing", "lungs"});
        DOCTOR_ROSTER.put("Pulmonologist", new String[]{"Dr. Teddy Altman", "Dr. Manhattan"});
        QUALIFICATIONS.put("Pulmonologist", "MBBS, MD (Pulmonary Medicine)");

        // 13. Neurologist
        KNOWLEDGE_BASE.put("Neurologist", new String[]{"seizure", "paralysis", "numbness", "tingling", "memory", "brain"});
        DOCTOR_ROSTER.put("Neurologist", new String[]{"Dr. Derek Shepherd", "Dr. Amelia Shepherd"});
        QUALIFICATIONS.put("Neurologist", "MBBS, MD, DM (Neurology)");

        // 14. Nephrologist
        KNOWLEDGE_BASE.put("Nephrologist", new String[]{"kidney", "renal", "dialysis", "urination changes"});
        DOCTOR_ROSTER.put("Nephrologist", new String[]{"Dr. House", "Dr. Wilson"});
        QUALIFICATIONS.put("Nephrologist", "MBBS, MD, DM (Nephrology)");

        // 15. Pediatrician
        KNOWLEDGE_BASE.put("Pediatrician", new String[]{"child", "baby", "infant", "growth", "toddler"});
        DOCTOR_ROSTER.put("Pediatrician", new String[]{"Dr. Arizona Robbins", "Dr. Doogie Howser"});
        QUALIFICATIONS.put("Pediatrician", "MBBS, MD (Pediatrics)");

        // 16. Oncologist
        KNOWLEDGE_BASE.put("Oncologist", new String[]{"lump", "tumor", "cancer", "chemo", "radiation"});
        DOCTOR_ROSTER.put("Oncologist", new String[]{"Dr. Wilson", "Dr. Izzie Stevens"});
        QUALIFICATIONS.put("Oncologist", "MBBS, MD, DM (Oncology)");
    }

    public synchronized void assignDoctor(Patient patient) {
        String bestSpecialty = "General Physician";
        
        if (patient.getAiSuggestedSpecialization() != null && 
            !patient.getAiSuggestedSpecialization().trim().isEmpty() && 
            DOCTOR_ROSTER.containsKey(patient.getAiSuggestedSpecialization())) {
            
            bestSpecialty = patient.getAiSuggestedSpecialization();
        } else {
            String symptoms = (patient.getIllness() != null ? patient.getIllness() : "").toLowerCase();
            int maxScore = 0;
            for (Map.Entry<String, String[]> entry : KNOWLEDGE_BASE.entrySet()) {
                String specialty = entry.getKey();
                String[] keywords = entry.getValue();
                int currentScore = 0;
                for (String keyword : keywords) {
                    if (symptoms.contains(keyword)) currentScore++;
                }
                if (currentScore > maxScore) {
                    maxScore = currentScore;
                    bestSpecialty = specialty;
                }
            }
        }

        patient.setDoctorSpecialization(bestSpecialty);
        patient.setDoctorQualification(QUALIFICATIONS.get(bestSpecialty));

        // --- NEW: AI Health Analysis & Criticality Logic ---
        String symptomsLower = (patient.getIllness() != null ? patient.getIllness() : "").toLowerCase();
        boolean isCriticalDetected = false;
        for (String crit : CRITICAL_SYMPTOMS) {
            if (symptomsLower.contains(crit)) {
                isCriticalDetected = true;
                break;
            }
        }

        String analysis = "Analysis: Patient presents with symptoms matching the " + bestSpecialty + " scope.";
        if (isCriticalDetected) {
            analysis += " [ALERT: High severity indicators detected]";
            patient.setEmergency(true); // Force emergency flag if critical symptoms detected
        }
        patient.setAiAnalysis(analysis);
        patient.setPredictedDisease("Suspected " + bestSpecialty + " related condition");

        String wellness = WELLNESS_TIPS.getOrDefault(bestSpecialty, PRESCRIBER_DATABASE.get("Wellness Default"));
        patient.setWellnessAdvice(wellness);

        String assignedDoctor = getRandomDoctorForSpecialty(bestSpecialty);
        patient.setDoctorAssigned(assignedDoctor);

        int tokenNumber = patientRepository.findFirstByDoctorAssignedOrderByTokenNumberDesc(assignedDoctor)
                .map(Patient::getTokenNumber)
                .orElse(0) + 1;
        patient.setTokenNumber(tokenNumber);

        Random random = new Random();
        String randomFee = FEES[random.nextInt(FEES.length)];
        patient.setConsultancyFee("₹" + randomFee);

        int daysToAdd = APPOINTMENT_DAYS[random.nextInt(APPOINTMENT_DAYS.length)];
        LocalDate futureDate = LocalDate.now().plusDays(daysToAdd);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        patient.setNextAppointmentDate(futureDate.format(formatter));

        // --- Conditional AI Prescription Logic ---
        long existingCount = patientRepository.countByDoctorAssigned(assignedDoctor);
        if (existingCount >= 5 || isCriticalDetected) {
            String medSuggestions = PRESCRIBER_DATABASE.getOrDefault(bestSpecialty, "Standard symptomatic treatment");
            String prefix = isCriticalDetected ? "⚠️ URGENT CARE ADVISED: " : "⚠️ AI SUGGESTED (Doctor Busy): ";
            patient.setPrescription(prefix + medSuggestions);
        } else {
            patient.setPrescription("Pending Doctor Consultation");
        }
    }

    public Map<String, String> getEmergencyOnCall() {
        return EMERGENCY_ON_CALL;
    }

    private String getRandomDoctorForSpecialty(String specialty) {
        String[] doctors = DOCTOR_ROSTER.getOrDefault(specialty, DOCTOR_ROSTER.get("General Physician"));
        return doctors[new Random().nextInt(doctors.length)];
    }
}