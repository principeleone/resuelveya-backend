
package com.resuelveya.resuelve_api.business.api.dto.usuario;

import com.resuelveya.resuelve_api.business.data.entity.enums.Rol;
import jakarta.validation.constraints.*;


public record UsuarioRequestDTO (

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80,message = "El nombre no debe superar los 80 caracteres")
    String nombre,

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Ingresa un email valido")
    @Size(max = 100,message = "El nombre no debe superar los 100 caracteres")
    String email,

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$",
            message = "La contraseña debe tener al menos una mayúscula, una minúscula y un número"
    )
    String password,


    @Size(max = 20, message = "El teléfono no debe superar los 20 caracteres")
    String telefono,

    @NotNull(message = "El rol es obligatorio")
    Rol rol,

    @Size(max = 255, message = "La dirección no debe superar los 255 caracteres")
    String direccion,

    Double latitud,

    Double longitud
){}
