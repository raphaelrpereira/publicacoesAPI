package com.ads05imepac.publicacoes.repository;

import com.ads05imepac.publicacoes.entity.Artigo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtigoRepository  extends JpaRepository<Artigo, Long> {

}
