package ru.netology.patient.service.medical;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedicalServiceImplTest {


    private PatientInfoRepository repositoryMock;
    private SendAlertService alertServiceMock;
    private MedicalServiceImpl medicalService;
    private final String patientId = "patient123";
    private PatientInfo testPatient;

    @BeforeEach
    void setUp() {
        repositoryMock = mock(PatientInfoRepository.class);
        alertServiceMock = mock(SendAlertService.class);
        medicalService = new MedicalServiceImpl(repositoryMock, alertServiceMock);

        testPatient = new PatientInfo(patientId,
                "Иван",
                "Петров",
                LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80)));
    }

    @Test
    void checkBloodPressure_HighPressure_SendsAlert() {
        when(repositoryMock.getById(patientId)).thenReturn(testPatient);

        BloodPressure highPressure = new BloodPressure(150, 100);
        medicalService.checkBloodPressure(patientId, highPressure);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(alertServiceMock, times(1)).send(messageCaptor.capture());
        assertEquals("Warning, patient with id: patient123, need help", messageCaptor.getValue());
    }

    @Test
    void checkTemperature_LowTemperature_SendsAlert() {
        when(repositoryMock.getById(patientId)).thenReturn(testPatient);

        BigDecimal lowTemp = new BigDecimal("34.0");
        medicalService.checkTemperature(patientId, lowTemp);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(alertServiceMock, times(1)).send(captor.capture());
        assertEquals("Warning, patient with id: patient123, need help", captor.getValue());
    }

    @Test
    void check_WhenNormalValues_NoAlertSent() {
        when(repositoryMock.getById(patientId)).thenReturn(testPatient);

        medicalService.checkBloodPressure(patientId, new BloodPressure(120, 80));
        medicalService.checkTemperature(patientId, new BigDecimal("36.0"));

        verify(alertServiceMock, never()).send(anyString());
    }
}