package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.GatewayDTO;
import com.proyecto.entrega.entity.Gateway;
import com.proyecto.entrega.entity.Process;
import com.proyecto.entrega.repository.GatewayRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GatewayServiceTest {

    @Mock ModelMapper modelMapper;
    @Mock GatewayRepository gatewayRepository;
    @Mock ProcessService processService; // <- ahora el service depende de ProcessService

    @InjectMocks GatewayService gatewayService;

    // -------- helpers
    private Gateway gw(Long id, String status, String type, Double x, Double y, Process p) {
        Gateway g = new Gateway();
        g.setId(id);
        g.setStatus(status);
        g.setType(type);
        g.setX(x);
        g.setY(y);
        g.setProcess(p);
        return g;
    }

    private GatewayDTO dto(Long id, String type, String status, Double x, Double y, Long processId) {
        GatewayDTO d = new GatewayDTO();
        d.setId(id);
        d.setType(type);
        d.setStatus(status);
        d.setX(x);
        d.setY(y);
        d.setProcessId(processId);
        return d;
    }

    // =================== CREATE ===================

    @Test
    void createGateway_ok_conProcessYCoords_statusPorDefectoActive() {
        GatewayDTO inDto = dto(null, "exclusive", null, 12.5, 34.5, 99L);

        Process proc = new Process(); proc.setId(99L);

        Gateway mapped = gw(null, null, "exclusive", null, null, null); // ModelMapper pone campos básicos
        Gateway saved  = gw(1L, "active", "exclusive", 12.5, 34.5, proc);
        GatewayDTO out = dto(1L, "exclusive", "active", 12.5, 34.5, 99L);

        when(modelMapper.map(inDto, Gateway.class)).thenReturn(mapped);
        when(processService.findProcessEntity(99L)).thenReturn(proc);
        when(gatewayRepository.save(any(Gateway.class))).thenReturn(saved);
        when(modelMapper.map(saved, GatewayDTO.class)).thenReturn(out);

        GatewayDTO result = gatewayService.createGateway(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo("exclusive");
        assertThat(result.getStatus()).isEqualTo("active");
        assertThat(result.getX()).isEqualTo(12.5);
        assertThat(result.getY()).isEqualTo(34.5);
        assertThat(result.getProcessId()).isEqualTo(99L);

        ArgumentCaptor<Gateway> captor = ArgumentCaptor.forClass(Gateway.class);
        verify(gatewayRepository).save(captor.capture());
        Gateway toSave = captor.getValue();
        assertThat(toSave.getStatus()).isEqualTo("active"); // default aplicado
        assertThat(toSave.getX()).isEqualTo(12.5);
        assertThat(toSave.getY()).isEqualTo(34.5);
        assertThat(toSave.getProcess()).isSameAs(proc);
    }

    @Test
    void createGateway_ok_sinProcessId_noRevienta() {
        GatewayDTO inDto = dto(null, "inclusive", null, 1.0, 2.0, null);

        Gateway mapped = gw(null, null, "inclusive", null, null, null);
        Gateway saved  = gw(2L, "active", "inclusive", 1.0, 2.0, null);
        GatewayDTO out = dto(2L, "inclusive", "active", 1.0, 2.0, null);

        when(modelMapper.map(inDto, Gateway.class)).thenReturn(mapped);
        when(gatewayRepository.save(any(Gateway.class))).thenReturn(saved);
        when(modelMapper.map(saved, GatewayDTO.class)).thenReturn(out);

        GatewayDTO result = gatewayService.createGateway(inDto);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getProcessId()).isNull();
        verifyNoInteractions(processService);
    }

    // =================== UPDATE ===================

    @Test
    void updateGateway_ok_actualizaTipoCoordsYProceso() {
        GatewayDTO inDto = dto(10L, "parallel", null, 5.5, 6.5, 77L);

        Process proc = new Process(); proc.setId(77L);
        Gateway existing = gw(10L, "active", "oldType", 0.0, 0.0, null);
        Gateway saved    = gw(10L, "active", "parallel", 5.5, 6.5, proc);
        GatewayDTO out   = dto(10L, "parallel", "active", 5.5, 6.5, 77L);

        when(gatewayRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(processService.findProcessEntity(77L)).thenReturn(proc);
        when(gatewayRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, GatewayDTO.class)).thenReturn(out);

        GatewayDTO result = gatewayService.updateGateway(inDto);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(existing.getType()).isEqualTo("parallel");
        assertThat(existing.getX()).isEqualTo(5.5);
        assertThat(existing.getY()).isEqualTo(6.5);
        assertThat(existing.getProcess()).isSameAs(proc);
        verify(gatewayRepository).save(existing);
    }

    @Test
    void updateGateway_idNull_lanzaIllegalArgument() {
        GatewayDTO inDto = dto(null, "x", null, 0.0, 0.0, 1L);

        assertThatThrownBy(() -> gatewayService.updateGateway(inDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id del Gateway");

        verifyNoInteractions(gatewayRepository, processService, modelMapper);
    }

    @Test
    void updateGateway_noExiste_lanzaEntityNotFound() {
        GatewayDTO inDto = dto(99L, "x", null, 0.0, 0.0, null);
        when(gatewayRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gatewayService.updateGateway(inDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Gateway 99");

        verify(gatewayRepository).findById(99L);
        verifyNoMoreInteractions(gatewayRepository);
        verifyNoInteractions(processService, modelMapper);
    }

    // =================== FIND ===================

    @Test
    void findGateway_found() {
        Gateway entity = gw(3L, "active", "inclusive", 7.0, 8.0, null);
        GatewayDTO dto = dto(3L, "inclusive", "active", 7.0, 8.0, 50L);

        when(gatewayRepository.findById(3L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, GatewayDTO.class)).thenReturn(dto);

        GatewayDTO result = gatewayService.findGateway(3L);

        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getType()).isEqualTo("inclusive");
        assertThat(result.getStatus()).isEqualTo("active");
        assertThat(result.getX()).isEqualTo(7.0);
        assertThat(result.getY()).isEqualTo(8.0);
        assertThat(result.getProcessId()).isEqualTo(50L);
    }

    @Test
    void findGateway_notFound() {
        when(gatewayRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gatewayService.findGateway(404L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("404");
    }

    @Test
    void findGatewayEntity_found_y_notFound() {
        Gateway e = gw(5L, "active", "exclusive", null, null, null);
        when(gatewayRepository.findById(5L)).thenReturn(Optional.of(e));
        assertThat(gatewayService.findGatewayEntity(5L).getId()).isEqualTo(5L);

        when(gatewayRepository.findById(6L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> gatewayService.findGatewayEntity(6L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("6");
    }

    // =================== DELETE (soft) ===================

    @Test
    void deleteGateway_ok_softDelete() {
        Gateway existing = gw(7L, "active", "exclusive", 1.0, 2.0, null);
        when(gatewayRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(gatewayRepository.save(existing)).thenReturn(existing);

        gatewayService.deleteGateway(7L);

        assertThat(existing.getStatus()).isEqualTo("inactive");
        ArgumentCaptor<Gateway> captor = ArgumentCaptor.forClass(Gateway.class);
        verify(gatewayRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("inactive");
    }

    @Test
    void deleteGateway_notFound() {
        when(gatewayRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gatewayService.deleteGateway(123L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("123");

        verify(gatewayRepository).findById(123L);
        verifyNoMoreInteractions(gatewayRepository);
    }

    // =================== LIST ===================

    @Test
    void findGateways_ok() {
        Gateway a = gw(1L, "active", "exclusive", 10.0, 20.0, null);
        Gateway b = gw(2L, "active", "parallel", 30.0, 40.0, null);
        when(gatewayRepository.findAll()).thenReturn(List.of(a, b));

        GatewayDTO da = dto(1L, "exclusive", "active", 10.0, 20.0, null);
        GatewayDTO db = dto(2L, "parallel", "active", 30.0, 40.0, null);
        when(modelMapper.map(a, GatewayDTO.class)).thenReturn(da);
        when(modelMapper.map(b, GatewayDTO.class)).thenReturn(db);

        List<GatewayDTO> result = gatewayService.findGateways();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getType()).isEqualTo("exclusive");
        assertThat(result.get(0).getX()).isEqualTo(10.0);
        assertThat(result.get(1).getY()).isEqualTo(40.0);

        verify(gatewayRepository).findAll();
        verify(modelMapper, atLeast(2)).map(any(Gateway.class), eq(GatewayDTO.class));
    }
}
