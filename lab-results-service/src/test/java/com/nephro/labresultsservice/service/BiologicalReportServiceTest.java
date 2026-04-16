package com.nephro.labresultsservice.service;

import com.nephro.labresultsservice.dto.CalendarEventDTO;
import com.nephro.labresultsservice.entity.BiologicalReport;
import com.nephro.labresultsservice.entity.BiologicalResult;
import com.nephro.labresultsservice.repository.BiologicalReportRepository;
import com.nephro.labresultsservice.repository.BiologicalResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BiologicalReportServiceTest {

    @Mock
    private BiologicalReportRepository reportRepo;

    @Mock
    private BiologicalResultRepository resultRepo;

    @InjectMocks
    private BiologicalReportService service;

    @Test
    void shouldGenerateNormalFollowUpWhenReportHasNoAbnormalResults() {
        BiologicalResult normalResult = BiologicalResult.builder()
                .id(1L)
                .parameterName("Creatinine")
                .value(1.0)
                .unit("mg/dL")
                .normalMinValue(0.7)
                .normalMaxValue(1.3)
                .build();

        BiologicalReport report = BiologicalReport.builder()
                .id(10L)
                .patientId(100L)
                .reportDate(LocalDate.of(2026, 4, 15))
                .analysisType("Blood Test")
                .laboratoryName("Lab A")
                .comment("Normal report")
                .results(List.of(normalResult))
                .build();

        normalResult.setBiologicalReport(report);

        when(reportRepo.findAll()).thenReturn(List.of(report));

        List<CalendarEventDTO> events = service.getCalendarEvents();

        assertEquals(2, events.size());

        CalendarEventDTO reportEvent = events.get(0);
        CalendarEventDTO followUpEvent = events.get(1);

        assertEquals("REPORT", reportEvent.getType());
        assertEquals(LocalDate.of(2026, 4, 15), reportEvent.getDate());

        assertEquals("NORMAL", followUpEvent.getType());
        assertEquals(LocalDate.of(2026, 5, 15), followUpEvent.getDate());
        assertEquals(0.0, followUpEvent.getSeverity());
    }

    @Test
    void shouldGenerateUrgentFollowUpWhenSeverityIsHigh() {
        BiologicalResult abnormalResult = BiologicalResult.builder()
                .id(2L)
                .parameterName("Potassium")
                .value(10.0)
                .unit("mmol/L")
                .normalMinValue(3.5)
                .normalMaxValue(5.0)
                .build();

        BiologicalReport report = BiologicalReport.builder()
                .id(11L)
                .patientId(101L)
                .reportDate(LocalDate.of(2026, 4, 15))
                .analysisType("Emergency Lab")
                .laboratoryName("Lab B")
                .comment("Critical result")
                .results(List.of(abnormalResult))
                .build();

        abnormalResult.setBiologicalReport(report);

        when(reportRepo.findAll()).thenReturn(List.of(report));

        List<CalendarEventDTO> events = service.getCalendarEvents();

        assertEquals(2, events.size());

        CalendarEventDTO followUpEvent = events.get(1);

        assertEquals("URGENT", followUpEvent.getType());
        assertEquals(LocalDate.of(2026, 4, 17), followUpEvent.getDate());
        assertTrue(followUpEvent.getSeverity() >= 2.0);
    }

    @Test
    void shouldReturnDtoWithAbnormalStatusWhenResultIsOutOfRange() {
        BiologicalResult abnormalResult = BiologicalResult.builder()
                .id(3L)
                .parameterName("Hemoglobin")
                .value(5.0)
                .unit("g/dL")
                .normalMinValue(12.0)
                .normalMaxValue(16.0)
                .build();

        BiologicalReport report = BiologicalReport.builder()
                .id(12L)
                .patientId(102L)
                .reportDate(LocalDate.of(2026, 4, 15))
                .analysisType("CBC")
                .laboratoryName("Lab C")
                .comment("Abnormal hemoglobin")
                .results(List.of(abnormalResult))
                .build();

        abnormalResult.setBiologicalReport(report);

        when(reportRepo.findById(12L)).thenReturn(java.util.Optional.of(report));

        var dto = service.getDtoById(12L);

        assertEquals("ABNORMAL", dto.getReportStatus());
        assertEquals(1, dto.getAbnormalCount());
        assertEquals(1, dto.getResults().size());
        assertTrue(dto.getResults().get(0).getIsAbnormal());
        assertNotEquals("NORMAL", dto.getResults().get(0).getStatus().name());
    }
}