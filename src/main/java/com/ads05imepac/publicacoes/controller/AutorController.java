package com.ads05imepac.publicacoes.controller;

import com.ads05imepac.publicacoes.entity.Autor;
import com.ads05imepac.publicacoes.repository.AutorRepository;
import com.ads05imepac.publicacoes.services.AutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.ResponseSupportConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/autores")
@Tag(name = "Cadastro de Autores")
public class AutorController {
    @Autowired
    AutorService autorService;

    @Autowired
    AutorRepository autorRepository;
    private ResponseSupportConverter responseSupportConverter;

    @Operation(description = "Busca todos autores cadastrados", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna os dados"),
            @ApiResponse(responseCode = "500", description = "Falha no servidor / Falha na requisição")
    })
    @GetMapping("/todos")
    private List<Autor> getAll(){
        return autorRepository.findAll();
    }

    @GetMapping("/autor/{id}")
    private Autor getById(@PathVariable Long id){
        return autorRepository.findById(id).get();
    }

    /*@PostMapping("/autor")
    private Autor save(@RequestBody AutorDto autorDto){
        return autorService.cadastrar(autorDto);
    }

    @PostMapping(value = "/autor")
    private Autor save(@RequestBody Autor autor){
        return autorRepository.save(autor);
    }*/

    @PostMapping(value = "/autor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "Realiza cadastro de autor a partir de um form-data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastro Realizado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Falha no processamento da requisição"),
            @ApiResponse(responseCode = "409", description = "Autor já cadastrado com o email informado")
    })
    public ResponseEntity<?> cadastrarAutor(@RequestParam("nome") String nome,
                                @RequestParam("email") String email,
                                @RequestParam("imgPerfil") MultipartFile imgPerfil
                                ){
            Autor autor = new Autor();

        try{
            autor = autorService.buscarPorEmail(email);
            if(Objects.nonNull(autor.getId())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Autor já cadastrado com este e-mail");
            }

            // salvar foto localmente
            //autor = autorService.cadastrarComFoto(nome, email, imgPerfil);

            // upload para Amazon S3
            autor = autorService.cadastrarComFotoS3(nome, email, imgPerfil);
            return ResponseEntity.ok(autor);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("autor/{id}")
    public void delete(@PathVariable Long id){
        autorRepository.deleteById(id);
    }

}
