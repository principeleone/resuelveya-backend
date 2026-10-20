package com.resuelveya.resuelve_api.business.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "usuario_id")
@AllArgsConstructor
public class Cliente extends Usuario {

    @Column(name = "puntos_acumulados")
    private Integer puntosAcumulados = 0;

    public Cliente() {
        super();
        this.puntosAcumulados = 0;
    }
}