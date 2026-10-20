package com.resuelveya.resuelve_api.business.api.controller;

import com.resuelveya.resuelve_api.business.api.dto.usuario.UsuarioRequestDTO;
import com.resuelveya.resuelve_api.business.data.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuarios")
public class UsuarioViewController {
    private final UsuarioRepository usuarioRepository;

    public UsuarioViewController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuarios"; // busca usuarios.html en templates/
    }

    @GetMapping("/nuevo")
    public String registrarUsuarios(Model model) {
        model.addAttribute("usuarioForm",new UsuarioRequestDTO(null, null, null, null, null, null, null, null));
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuarios"; // busca usuarios.html en templates/
    }
}
