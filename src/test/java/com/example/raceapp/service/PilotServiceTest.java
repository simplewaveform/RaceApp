package com.example.raceapp.service;

import com.example.raceapp.dto.PilotDto;
import com.example.raceapp.dto.PilotResponse;
import com.example.raceapp.exception.NotFoundException;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.repository.PilotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PilotServiceTest {

    @Mock
    private PilotRepository pilotRepository;

    @InjectMocks
    private PilotService pilotService;

    // Тест на успешное создание нескольких пилотов
    @Test
    void createPilotsBulk_ValidInput_ReturnsListOfResponses() {
        // Given
        PilotDto dto1 = new PilotDto();
        dto1.setName("Lewis Hamilton");
        dto1.setAge(38);
        dto1.setExperience(15);

        PilotDto dto2 = new PilotDto();
        dto2.setName("George Russell");
        dto2.setAge(25);
        dto2.setExperience(4);

        when(pilotRepository.save(any(Pilot.class))).thenAnswer(inv -> {
            Pilot pilot = inv.getArgument(0);
            pilot.setId(1L); // Эмулируем сохранение в БД
            return pilot;
        });

        // When
        List<PilotResponse> result = pilotService.createPilotsBulk(List.of(dto1, dto2));

        // Then
        assertEquals(2, result.size());
        assertEquals("Lewis Hamilton", result.get(0).getName());
        verify(pilotRepository, times(2)).save(any(Pilot.class));
    }

    // Тест на пустой список
    @Test
    void createPilotsBulk_EmptyList_ReturnsEmptyList() {
        List<PilotResponse> result = pilotService.createPilotsBulk(List.of());
        assertTrue(result.isEmpty());
    }

    // Тест на невалидные данные (например, возраст < 18)
    @Test
    void createPilotsBulk_InvalidAge_ThrowsException() {
        PilotDto invalidDto = new PilotDto();
        invalidDto.setName("Test");
        invalidDto.setAge(17); // Нарушение @Min(18)

        assertThrows(Exception.class, () ->
                pilotService.createPilotsBulk(List.of(invalidDto)));
    }

    // Тест получения пилота по ID (покрытие существующего метода)
    @Test
    void getPilotById_ExistingId_ReturnsPilot() {
        Long pilotId = 1L;
        Pilot pilot = new Pilot();
        pilot.setId(pilotId);
        pilot.setName("Max Verstappen");
        pilot.setAge(30);
        pilot.setExperience(5);

        when(pilotRepository.findById(pilotId)).thenReturn(Optional.of(pilot));

        Optional<PilotResponse> result = pilotService.getPilotById(pilotId);
        assertTrue(result.isPresent());
        assertEquals(pilotId, result.get().getId());
    }

    @Test
    void getPilotById_NonExistingId_ThrowsNotFoundException() {
        Long nonExistingId = 999L;
        when(pilotRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            pilotService.getPilotById(nonExistingId)
                    .orElseThrow(() -> new NotFoundException("Pilot not found"));
        });
    }
}