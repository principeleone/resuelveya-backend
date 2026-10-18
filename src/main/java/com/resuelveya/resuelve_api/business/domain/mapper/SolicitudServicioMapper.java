package com.resuelveya.resuelve_api.business.domain.mapper;

import com.resuelveya.resuelve_api.business.api.dto.solicitud.CrearSolicitudRequestDto;
import com.resuelveya.resuelve_api.business.api.dto.solicitud.SolicitudResponseDto;
import com.resuelveya.resuelve_api.business.data.entity.SolicitudServicio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudServicioMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente.nombre", target = "clienteNombre")
    @Mapping(source = "cliente.telefono", target = "clienteTelefono")
    @Mapping(source = "cliente.email", target = "clienteEmail")
    @Mapping(source = "cliente.fotoUrl", target = "clienteFotoUrl")
    @Mapping(source = "tecnico.id", target = "tecnicoId")
    @Mapping(source = "tecnico.nombre", target = "tecnicoNombre")
    @Mapping(source = "tecnico.telefono", target = "tecnicoTelefono")
    @Mapping(source = "tecnico.email", target = "tecnicoEmail")
    @Mapping(source = "tecnico.fotoUrl", target = "tecnicoFotoUrl")
    @Mapping(source = "servicio.id", target = "servicioId")
    @Mapping(source = "servicio.titulo", target = "servicioTitulo")
    SolicitudResponseDto toResponseDto(SolicitudServicio solicitud);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "tecnico", ignore = true)
    @Mapping(target = "servicio", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "motivoCancelacion", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SolicitudServicio toEntity(CrearSolicitudRequestDto dto);
}
