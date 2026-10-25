package com.ernandesventura.marketplace.service;

import com.ernandesventura.marketplace.dto.UsuarioCadastroRequest;
import com.ernandesventura.marketplace.dto.UsuarioRequest;
import com.ernandesventura.marketplace.dto.UsuarioResponse;
import com.ernandesventura.marketplace.exception.AcessoNegadoException;
import com.ernandesventura.marketplace.exception.EmailJaCadastradoException;
import com.ernandesventura.marketplace.exception.RecursoNaoEncontradoException;
import com.ernandesventura.marketplace.model.Usuario;
import com.ernandesventura.marketplace.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public UsuarioResponse buscarPorId(Long id, Long usuarioLogadoId) {
        verificarProprioUsuario(id, usuarioLogadoId);
        Usuario usuario = buscarEntidadePorId(id);
        return UsuarioResponse.fromEntity(usuario);
    }

    public UsuarioResponse atualizar(Long id, UsuarioRequest request, Long usuarioLogadoId) {
        verificarProprioUsuario(id, usuarioLogadoId);
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

    public void inativar(Long id, Long usuarioLogadoId) {
        verificarProprioUsuario(id, usuarioLogadoId);
        Usuario usuario = buscarEntidadePorId(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    // Cada usuário só vê, edita ou inativa a própria conta.
    // Checado ANTES de buscar no banco: id de outra pessoa dá 403 exista ou não,
    // assim ninguém descobre quais ids existem.
    private void verificarProprioUsuario(Long id, Long usuarioLogadoId) {
        if (!id.equals(usuarioLogadoId)) {
            throw new AcessoNegadoException("Você só pode acessar a própria conta");
        }
    }

    public Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + id));
    }
}
