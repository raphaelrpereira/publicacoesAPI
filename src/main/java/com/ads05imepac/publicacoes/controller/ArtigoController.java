package com.ads05imepac.publicacoes.controller;

import com.ads05imepac.publicacoes.entity.Artigo;
import com.ads05imepac.publicacoes.repository.ArtigoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/artigos")
public class ArtigoController {

    @Autowired
    private ArtigoRepository artigoRepository;

    @GetMapping("/todos")
    public List<Artigo> getArtigos() {
        return artigoRepository.findAll();
    }

    @GetMapping("/artigo/{id}")
    public Optional<Artigo> getArtigoById(@PathVariable Long id) {
        return artigoRepository.findById(id);
    }

    @PostMapping("/artigo")
    public Artigo saveArtigo(@RequestBody Artigo artigo) {
        return artigoRepository.save(artigo);
    }

    @DeleteMapping("/artigo/{id}")
    public void deleteArtigo(@PathVariable Long id) {
        artigoRepository.deleteById(id);
    }





}
