package com.ernandesventura.marketplace.service;

import com.ernandesventura.marketplace.dto.UsuarioCadastroRequest;
import com.ernandesventura.marketplace.dto.UsuarioRequest;
import com.ernandesventura.marketplace.dto.UsuarioResponse;
import com.ernandesventura.marketplace.exception.EmailJaCadastradoException;
import com.ernandesventura.marketplace.exception.RecursoNaoEncontradoException;
import com.ernandesventura.marketplace.model.Usuario;
import com.ernandesventura.marketplace.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponse criar(UsuarioCadastroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException("Email já cadastrado: " + request.email());
        }
        String senhaHash = passwordEncoder.encode(request.senha());
        Usuario usuario = new Usuario(request.nome(), request.email(), senhaHash);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(usuarioSalvo);
    }

    public List<UsuarioResponse> listarTodos() {
        List<UsuarioResponse> resposta = new ArrayList<>();
        for (Usuario usuario : usuarioRepository.findByAtivoTrue()) {
            resposta.add(UsuarioResponse.fromEntity(usuario));
        }
        return resposta;
    }

    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = buscarEntidadePorId(id);
        return UsuarioResponse.fromEntity(usuario);
    }

    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscarEntidadePorId(id);
        boolean emailMudou = !usuario.getEmail().equals(request.email());
        if (emailMudou && usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException("Email já cadastrado: " + request.email());
        }
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(usuarioAtualizado);
    }

    public void inativar(Long id) {
        Usuario usuario = buscarEntidadePorId(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    public Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + id));
    }
}
