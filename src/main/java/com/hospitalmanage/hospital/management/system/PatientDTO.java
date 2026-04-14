package com.hospitalmanage.hospital.management.system;

public class PatientDTO {
    private Long id;
    private String name;
    private int age;
    private String illness;
    private String phoneNumber;
    private String recordNumber;
    private String predictedDisease;
    private String doctorAssigned;
    private String doctorSpecialization;
    private String doctorQualification;
    private String aiSuggestedSpecialization;
    private int tokenNumber;
    private String consultancyFee;
    private String nextAppointmentDate;
    private String prescription;
    private String aiAnalysis;
    private String wellnessAdvice;
    private boolean isEmergency;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
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
    public String getAiSuggestedSpecialization() { return aiSuggestedSpecialization; }
    public void setAiSuggestedSpecialization(String aiSuggestedSpecialization) { this.aiSuggestedSpecialization = aiSuggestedSpecialization; }
    public int getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(int tokenNumber) { this.tokenNumber = tokenNumber; }
    public String getConsultancyFee() { return consultancyFee; }
    public void setConsultancyFee(String consultancyFee) { this.consultancyFee = consultancyFee; }
    public String getNextAppointmentDate() { return nextAppointmentDate; }
    public void setNextAppointmentDate(String nextAppointmentDate) { this.nextAppointmentDate = nextAppointmentDate; }
    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }
    public String getAiAnalysis() { return aiAnalysis; }
    public void setAiAnalysis(String aiAnalysis) { this.aiAnalysis = aiAnalysis; }
    public String getWellnessAdvice() { return wellnessAdvice; }
    public void setWellnessAdvice(String wellnessAdvice) { this.wellnessAdvice = wellnessAdvice; }
    public boolean isEmergency() { return isEmergency; }
    public void setEmergency(boolean emergency) { isEmergency = emergency; }

    // Static mapping methods
    public static PatientDTO fromEntity(Patient patient) {
        if (patient == null) return null;
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setName(patient.getName());
        dto.setAge(patient.getAge());
        dto.setIllness(patient.getIllness());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setRecordNumber(patient.getRecordNumber());
        dto.setPredictedDisease(patient.getPredictedDisease());
        dto.setDoctorAssigned(patient.getDoctorAssigned());
        dto.setDoctorSpecialization(patient.getDoctorSpecialization());
        dto.setDoctorQualification(patient.getDoctorQualification());
        dto.setAiSuggestedSpecialization(patient.getAiSuggestedSpecialization());
        dto.setTokenNumber(patient.getTokenNumber());
        dto.setConsultancyFee(patient.getConsultancyFee());
        dto.setNextAppointmentDate(patient.getNextAppointmentDate());
        dto.setPrescription(patient.getPrescription());
        dto.setAiAnalysis(patient.getAiAnalysis());
        dto.setWellnessAdvice(patient.getWellnessAdvice());
        dto.setEmergency(patient.isEmergency());
        return dto;
    }

    public Patient toEntity() {
        Patient entity = new Patient();
        entity.setId(this.id);
        entity.setName(this.name);
        entity.setAge(this.age);
        entity.setIllness(this.illness);
        entity.setPhoneNumber(this.phoneNumber);
        if (this.recordNumber != null) {
            entity.setRecordNumber(this.recordNumber);
        }
        entity.setPredictedDisease(this.predictedDisease);
        entity.setDoctorAssigned(this.doctorAssigned);
        entity.setDoctorSpecialization(this.doctorSpecialization);
        entity.setDoctorQualification(this.doctorQualification);
        entity.setAiSuggestedSpecialization(this.aiSuggestedSpecialization);
        entity.setTokenNumber(this.tokenNumber);
        entity.setConsultancyFee(this.consultancyFee);
        entity.setNextAppointmentDate(this.nextAppointmentDate);
        entity.setPrescription(this.prescription);
        entity.setAiAnalysis(this.aiAnalysis);
        entity.setWellnessAdvice(this.wellnessAdvice);
        entity.setEmergency(this.isEmergency);
        return entity;
    }
}
