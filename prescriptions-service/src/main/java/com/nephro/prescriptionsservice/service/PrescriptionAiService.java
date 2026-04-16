package com.nephro.prescriptionsservice.service;

import com.nephro.prescriptionsservice.dto.AiExplanationResponseDTO;
import com.nephro.prescriptionsservice.entity.PrescribedMedication;
import com.nephro.prescriptionsservice.entity.Prescription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionAiService {

    private final PrescriptionService prescriptionService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    public PrescriptionAiService(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    public AiExplanationResponseDTO explainPrescriptionForElderly(Long prescriptionId) {
        Prescription prescription = prescriptionService.getById(prescriptionId);
        List<PrescribedMedication> medications = prescriptionService.listMedications(prescriptionId);

        String prompt = buildPrompt(prescription, medications);
        String explanation = callOllama(prompt);

        return AiExplanationResponseDTO.builder()
                .prescriptionId(prescriptionId)
                .model(ollamaModel)
                .explanation(explanation)
                .warning("This is a simplified explanation and does not replace the doctor or pharmacist.")
                .build();
    }

    private String buildPrompt(Prescription prescription, List<PrescribedMedication> medications) {
        StringBuilder sb = new StringBuilder();

        sb.append("You are a medical assistant for elderly patients.\n");
        sb.append("Explain the prescription in very simple, calm, short, reassuring language.\n");
        sb.append("Use short sentences.\n");
        sb.append("Use bullet points.\n");
        sb.append("Do not scare the patient.\n");
        sb.append("Do not invent information that is not provided.\n");
        sb.append("At the end, add one short reminder that this does not replace the doctor.\n\n");

        sb.append("Prescription data:\n");
        sb.append("Doctor name: ").append(textOrDefault(prescription.getDoctorName())).append("\n");
        sb.append("Patient ID: ").append(prescription.getPatientId()).append("\n");
        sb.append("Prescription date: ").append(prescription.getPrescriptionDate()).append("\n");
        sb.append("Notes: ").append(textOrDefault(prescription.getNotes())).append("\n\n");

        sb.append("Medications:\n");
        if (medications == null || medications.isEmpty()) {
            sb.append("- No medications found in this prescription.\n");
        } else {
            for (PrescribedMedication med : medications) {
                sb.append("- Medication name: ").append(textOrDefault(med.getMedicationName())).append("\n");
                sb.append("  Dosage: ").append(textOrDefault(med.getDosage())).append("\n");
                sb.append("  Frequency: ").append(textOrDefault(med.getFrequency())).append("\n");
                sb.append("  Duration in days: ")
                        .append(med.getDurationDays() == null ? "Not specified" : med.getDurationDays())
                        .append("\n");
            }
        }

        sb.append("\nNow explain this prescription for an elderly patient in a friendly and very easy way.");

        return sb.toString();
    }

    private String callOllama(String prompt) {
        String url = ollamaBaseUrl + "/api/chat";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new HashMap<>();
        body.put("model", ollamaModel);
        body.put("stream", false);
        body.put("messages", List.of(message));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getBody() == null) {
                return "AI service returned an empty response.";
            }

            Object messageObj = response.getBody().get("message");
            if (messageObj instanceof Map<?, ?> messageMap) {
                Object content = messageMap.get("content");
                if (content != null) {
                    return content.toString();
                }
            }

            return "AI response format was unexpected.";
        } catch (Exception e) {
            return "Could not get AI explanation. Please make sure Ollama is running locally.";
        }
    }

    private String textOrDefault(String value) {
        return value == null || value.isBlank() ? "Not provided" : value;
    }
}