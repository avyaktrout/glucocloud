package com.glucocloud.api.service;

import com.glucocloud.api.dto.MedicationRequest;
import com.glucocloud.api.dto.MedicationResponse;
import com.glucocloud.api.entity.Medication;
import com.glucocloud.api.entity.User;
import com.glucocloud.api.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicationService {

    private final MedicationRepository medicationRepository;

    public MedicationResponse createMedication(User user, MedicationRequest request) {
        Medication medication = Medication.builder()
                .user(user)
                .name(request.getName())
                .dosage(request.getDosage())
                .medicationType(request.getMedicationType())
                .takenAt(request.getTakenAt())
                .notes(request.getNotes())
                .effectivenessRating(request.getEffectivenessRating())
                .sideEffects(request.getSideEffects())
                .build();

        Medication savedMedication = medicationRepository.save(medication);
        return MedicationResponse.fromEntity(savedMedication);
    }

    @Transactional(readOnly = true)
    public List<MedicationResponse> getUserMedications(User user, LocalDateTime startDate, LocalDateTime endDate) {
        List<Medication> medications;

        if (startDate != null && endDate != null) {
            medications = medicationRepository.findByUserAndTakenAtBetweenOrderByTakenAtDesc(user, startDate, endDate);
        } else {
            medications = medicationRepository.findByUserOrderByTakenAtDesc(user);
        }

        return medications.stream()
                .map(MedicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<MedicationResponse> getMedicationById(User user, UUID medicationId) {
        return medicationRepository.findByIdAndUser(medicationId, user)
                .map(MedicationResponse::fromEntity);
    }

    public MedicationResponse updateMedication(User user, UUID medicationId, MedicationRequest request) {
        Medication medication = medicationRepository.findByIdAndUser(medicationId, user)
                .orElseThrow(() -> new RuntimeException("Medication not found"));

        medication.setName(request.getName());
        medication.setDosage(request.getDosage());
        medication.setMedicationType(request.getMedicationType());
        medication.setTakenAt(request.getTakenAt());
        medication.setNotes(request.getNotes());
        medication.setEffectivenessRating(request.getEffectivenessRating());
        medication.setSideEffects(request.getSideEffects());

        Medication savedMedication = medicationRepository.save(medication);
        return MedicationResponse.fromEntity(savedMedication);
    }

    public void deleteMedication(User user, UUID medicationId) {
        Medication medication = medicationRepository.findByIdAndUser(medicationId, user)
                .orElseThrow(() -> new RuntimeException("Medication not found"));

        medicationRepository.delete(medication);
    }

    @Transactional(readOnly = true)
    public List<String> getUserMedicationNames(User user) {
        return medicationRepository.findDistinctMedicationNamesByUser(user);
    }
}