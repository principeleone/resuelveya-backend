package com.resuelveya.resuelve_api.business.data.entity;

import com.resuelveya.resuelve_api.business.data.entity.enums.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tecnico")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Getter
@Setter
@AllArgsConstructor
public class Tecnico extends Usuario {

    @Column(name = "presentacion", length = 1000)
    private String presentacion;

    @Column(name = "anios_experiencia")
    private Integer aniosExperiencia = 0;

    @Column(name = "validacion")
    private Boolean validacion = false;

    @Column(name = "yape_numero", length = 20)
    private String yapeNumero;

    @Column(name = "plin_numero", length = 20)
    private String plinNumero;

    @Column(name = "titular_pago", length = 120)
    private String titularPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidad_id")
    private Categoria especialidad;

    @OneToMany(mappedBy = "tecnico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Servicio> servicios = new ArrayList<>();

    @OneToMany(mappedBy = "tecnico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resenia> resenias = new ArrayList<>();

    public Tecnico() {
        super();
        this.setRol(Rol.TECNICO);
        this.aniosExperiencia = 0;
        this.setCalificacionPromedio(0.0);
    }

    public Tecnico(String nombre, String email, String password, String telefono, Integer aniosExperiencia,
            Double calificacionPromedio, Categoria especialidad) {
        super(nombre, email, telefono, password, Rol.TECNICO);
        this.aniosExperiencia = aniosExperiencia != null ? aniosExperiencia : 0;
        this.setCalificacionPromedio(calificacionPromedio != null ? calificacionPromedio : 0.0);
        this.especialidad = especialidad;
    }
}