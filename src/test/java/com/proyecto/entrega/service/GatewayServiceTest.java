package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.GatewayDTO;
import com.proyecto.entrega.entity.Gateway;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.repository.GatewayRepository;
import com.proyecto.entrega.repository.ProcessRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GatewayServiceTest {

    @Mock GatewayRepository gatewayRepository;
    @Mock ProcessRepository processRepository;   // <- NECESARIO ahora
    @Mock ModelMapper modelMapper;

    @InjectMocks GatewayService gatewayService;

    @Test
    void createGateway_ok() {
        // DTO con processId requerido
        GatewayDTO inDto = new GatewayDTO(null, "exclusive", 99L);

        // Proceso existente
        Process proc = new Process(); proc.setId(99L);

        // Entity antes y después de guardar
        Gateway entityBefore = new Gateway(null, "active", "exclusive", proc);
        Gateway entitySaved  = new Gateway(1L,   "active", "exclusive", proc);

        GatewayDTO outDto = new GatewayDTO(1L, "exclusive", 99L);

        // Stubs
        when(processRepository.findById(99L)).thenReturn(Optional.of(proc));
        // En tu servicio probablemente construyes el entity y luego lo guardas;
        // si mapeas desde el DTO, dejamos este map por compatibilidad.
        when(modelMapper.map(inDto, Gateway.class)).thenReturn(entityBefore);
        when(gatewayRepository.save(any(Gateway.class))).thenReturn(entitySaved);
        when(modelMapper.map(entitySaved, GatewayDTO.class)).thenReturn(outDto);

        // Act
        GatewayDTO result = gatewayService.createGateway(inDto);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo("exclusive");
        assertThat(result.getProcessId()).isEqualTo(99L);

        verify(processRepository).findById(99L);
        verify(gatewayRepository).save(any(Gateway.class));
    }

    @Test
    void updateGateway_ok() {
        GatewayDTO inDto = new GatewayDTO(10L, "parallel", 77L);

    Process proc = new Process(); proc.setId(77L);
    Gateway existing = new Gateway(10L, "active", "oldType", null);
    Gateway saved    = new Gateway(10L, "active", "parallel", proc);

    GatewayDTO outDto = new GatewayDTO(10L, "parallel", 77L);

    when(gatewayRepository.findById(10L)).thenReturn(Optional.of(existing));
    when(processRepository.findById(77L)).thenReturn(Optional.of(proc));

    // ⬇️ Stub del map DTO -> entity existente (lo que hace tu service)
    doAnswer(inv -> {
        GatewayDTO src = inv.getArgument(0);
        Gateway tgt    = inv.getArgument(1);
        // simula el copy de campos que haría ModelMapper
        tgt.setType(src.getType());
        tgt.setProcess(proc); // el servicio luego setea el process; aquí lo dejamos ya consistente
        return null;
    }).when(modelMapper).map(eq(inDto), same(existing));

    when(gatewayRepository.save(existing)).thenReturn(saved);
    when(modelMapper.map(saved, GatewayDTO.class)).thenReturn(outDto);

    GatewayDTO result = gatewayService.updateGateway(inDto);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(10L);
    assertThat(result.getType()).isEqualTo("parallel");
    assertThat(result.getProcessId()).isEqualTo(77L);

    verify(gatewayRepository).findById(10L);
    verify(processRepository).findById(77L);
    verify(modelMapper).map(eq(inDto), same(existing));   // verifica el mapeo DTO->entity
    verify(gatewayRepository).save(existing);
    }

    @Test
    void findGateway_found() {
        Gateway entity = new Gateway(2L, "active", "inclusive", null);
        GatewayDTO dto = new GatewayDTO(2L, "inclusive", 50L);

        when(gatewayRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, GatewayDTO.class)).thenReturn(dto);

        GatewayDTO result = gatewayService.findGateway(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getType()).isEqualTo("inclusive");
        assertThat(result.getProcessId()).isEqualTo(50L);
    }

    @Test
    void findGateway_notFound_throwsEntityNotFound() {
        when(gatewayRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gatewayService.findGateway(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(gatewayRepository).findById(999L);
        verifyNoMoreInteractions(gatewayRepository);
    }

    @Test
    void deleteGateway_ok() {
        // Soft delete: findById -> setStatus("inactive") -> save
        Gateway existing = new Gateway(7L, "active", "exclusive", null);
        when(gatewayRepository.findById(7L)).thenReturn(Optional.of(existing));

        gatewayService.deleteGateway(7L);

        assertThat(existing.getStatus()).isEqualTo("inactive");
        verify(gatewayRepository).findById(7L);
        verify(gatewayRepository).save(existing);
    }

    @Test
    void findGateways_ok() {
        Gateway a = new Gateway(1L, "active", "exclusive", null);
        when(gatewayRepository.findAll()).thenReturn(List.of(a));

        GatewayDTO dto = new GatewayDTO(1L, "exclusive", 10L);
        when(modelMapper.map(a, GatewayDTO.class)).thenReturn(dto);

        List<GatewayDTO> result = gatewayService.findGateways();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo("exclusive");
        assertThat(result.get(0).getProcessId()).isEqualTo(10L);

        verify(gatewayRepository).findAll();
        verify(modelMapper, atLeastOnce()).map(eq(a), eq(GatewayDTO.class));
    }
}
