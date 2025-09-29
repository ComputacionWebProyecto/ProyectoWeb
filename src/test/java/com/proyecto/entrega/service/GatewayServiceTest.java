package com.proyecto.entrega.service;

import com.proyecto.entrega.dto.GatewayDTO;
import com.proyecto.entrega.entity.Gateway;
import com.proyecto.entrega.repository.GatewayRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GatewayServiceTest {

    @Mock
    GatewayRepository gatewayRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    GatewayService gatewayService;

    @Test
    void createGateway_ok() {
        GatewayDTO inDto = new GatewayDTO(null, "accepted", "exclusive", 99L);
        Gateway entityBefore = new Gateway(null, "accepted", "exclusive", null);
        Gateway entitySaved  = new Gateway(1L,   "accepted", "exclusive", null);
        GatewayDTO outDto    = new GatewayDTO(1L, "accepted", "exclusive", 99L);

        when(modelMapper.map(inDto, Gateway.class)).thenReturn(entityBefore);
        when(gatewayRepository.save(entityBefore)).thenReturn(entitySaved);
        when(modelMapper.map(entitySaved, GatewayDTO.class)).thenReturn(outDto);

        GatewayDTO result = gatewayService.createGateway(inDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo("accepted");
        assertThat(result.getType()).isEqualTo("exclusive");
        verify(gatewayRepository).save(entityBefore);
    }

    @Test
    void updateGateway_ok() {
        GatewayDTO inDto = new GatewayDTO(10L, "rejected", "parallel", 77L);
        Gateway entity = new Gateway(10L, "rejected", "parallel", null);

        when(modelMapper.map(inDto, Gateway.class)).thenReturn(entity);
        when(gatewayRepository.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, GatewayDTO.class)).thenReturn(inDto);

        GatewayDTO result = gatewayService.updateGateway(inDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getType()).isEqualTo("parallel");
        verify(gatewayRepository).save(entity);
    }

    @Test
    void findGateway_found() {
        Gateway entity = new Gateway(2L, "accepted", "inclusive", null);
        GatewayDTO dto = new GatewayDTO(2L, "accepted", "inclusive", 50L);

        when(gatewayRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, GatewayDTO.class)).thenReturn(dto);

        GatewayDTO result = gatewayService.findGateway(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getStatus()).isEqualTo("accepted");
        assertThat(result.getType()).isEqualTo("inclusive");
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
        gatewayService.deleteGateway(7L);
        verify(gatewayRepository).deleteById(7L);
    }

    @Test
    void findGateways_ok() {
        Gateway a = new Gateway(1L, "accepted", "exclusive", null);
        when(gatewayRepository.findAll()).thenReturn(List.of(a));

        GatewayDTO dto = new GatewayDTO(1L, "accepted", "exclusive", 10L);
        when(modelMapper.map(a, GatewayDTO.class)).thenReturn(dto);

        List<GatewayDTO> result = gatewayService.findGateways();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo("exclusive");
        verify(gatewayRepository).findAll();
        verify(modelMapper, atLeastOnce()).map(any(Gateway.class), eq(GatewayDTO.class));
    }
}
