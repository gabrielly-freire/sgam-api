package br.ufrn.imd.sgam.controller;

import br.ufrn.imd.sgam.dto.LoginDTO;
import br.ufrn.imd.sgam.dto.TokenDTO;
import br.ufrn.imd.sgam.model.UserInfo;
import br.ufrn.imd.sgam.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Operation(
        summary = "Realizar login", 
        description = "Autentica o usuário e retorna um token JWT para acesso aos demais endpoints."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Login realizado com sucesso",
            content = @Content(schema = @Schema(implementation = TokenDTO.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Credenciais inválidas (usuário ou senha incorretos)",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Requisição mal formatada",
            content = @Content
        )
    })
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        
        var token = tokenService.generateToken((UserInfo) auth.getPrincipal());

        return ResponseEntity.ok(new TokenDTO(token));
    }
}