package com.hospitalmanage.hospital.management.system;

import jakarta.persistence.*;
import java.util.Random;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;
    private String illness;
    private String phoneNumber;
    private String recordNumber;

    // AI & Doctor Fields
    private String predictedDisease;
    private String doctorAssigned;
    private String doctorSpecialization;
    private String doctorQualification;
    private String aiSuggestedSpecialization;

    // Billing & Token Fields
    private int tokenNumber;
    private String consultancyFee;
    private String nextAppointmentDate;
    private String prescription;
    private String aiAnalysis;
    private String wellnessAdvice;

    // Emergency Flag
    @com.fasterxml.jackson.annotation.JsonProperty("isEmergency")
    private boolean isEmergency;

    // Lifecycle Status
    private String status = "REGISTERED"; // REGISTERED, UNDER_TREATMENT, TREATED
    private boolean isPaid = false;
    private String treatmentDate;

    // --- CONSTRUCTORS ---

    public Patient() {
        this.recordNumber = "PAT-" + (1000 + new Random().nextInt(9000));
    }

    public Patient(String name, int age, String illness, String phoneNumber, boolean isEmergency) {
        this();
        this.name = name;
        this.setAge(age);
        this.illness = illness;
        this.phoneNumber = phoneNumber;
        this.isEmergency = isEmergency;
    }

    // --- GETTERS AND SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = (age < 0) ? 0 : age; }

    public String getIllness() { return illness; }
    public void setIllness(String illness) { this.illness = illness; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRecordNumber() { return recordNumber; }
    public void setRecordNumber(String recordNumber) { this.recordNumber = recordNumber; }

    public String getPredictedDisease() { return predictedDisease; }
    public void setPredictedDisease(String predictedDisease) { this.predictedDisease = predictedDisease; }

    public String getDoctorAssigned() { return doctorAssigned; }
    public void setDoctorAssigned(String doctorAssigned) { this.doctorAssigned = doctorAssigned; }

    public String getDoctorSpecialization() { return doctorSpecialization; }
    public void setDoctorSpecialization(String doctorSpecialization) { this.doctorSpecialization = doctorSpecialization; }

    public String getDoctorQualification() { return doctorQualification; }
    public void setDoctorQualification(String doctorQualification) { this.doctorQualification = doctorQualification; }

    public int getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(int tokenNumber) { this.tokenNumber = tokenNumber; }

    public String getConsultancyFee() { return consultancyFee; }
    public void setConsultancyFee(String consultancyFee) { this.consultancyFee = consultancyFee; }

    public String getNextAppointmentDate() { return nextAppointmentDate; }
    public void setNextAppointmentDate(String nextAppointmentDate) { this.nextAppointmentDate = nextAppointmentDate; }

    public boolean isEmergency() { return isEmergency; }
    public void setEmergency(boolean emergency) { isEmergency = emergency; }

    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }

    public String getAiSuggestedSpecialization() { return aiSuggestedSpecialization; }
    public void setAiSuggestedSpecialization(String aiSuggestedSpecialization) { this.aiSuggestedSpecialization = aiSuggestedSpecialization; }

    public String getAiAnalysis() { return aiAnalysis; }
    public void setAiAnalysis(String aiAnalysis) { this.aiAnalysis = aiAnalysis; }

    public String getWellnessAdvice() { return wellnessAdvice; }
    public void setWellnessAdvice(String wellnessAdvice) { this.wellnessAdvice = wellnessAdvice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    public String getTreatmentDate() { return treatmentDate; }
    public void setTreatmentDate(String treatmentDate) { this.treatmentDate = treatmentDate; }
}