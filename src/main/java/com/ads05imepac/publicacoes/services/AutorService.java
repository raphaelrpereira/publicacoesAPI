package com.ads05imepac.publicacoes.services;

import com.ads05imepac.publicacoes.dto.AutorDto;
import com.ads05imepac.publicacoes.entity.Autor;
import com.ads05imepac.publicacoes.repository.AutorRepository;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
public class AutorService {
    private static final String BASE_URL = "http://localhost/photos/";
    private static final String PATH_IMAGE_PROFILE = "/uploads/photos/";
    private static final String UPLOAD_IMAGE_DIR = System.getProperty("user.dir") + "/uploads/photos/";

    @Value("${aws.bucketName}")
    private String bucketName;

    @Autowired
    AutorRepository autorRepository;

    @Autowired
    AmazonS3 s3Client;

    public Autor buscarPorEmail(String email){
            return autorRepository.findByEmail(email).orElse(new Autor());
    }

    public Autor cadastrar(AutorDto dto){
        Autor autor = new Autor();
        autor.setNome(dto.nome());
        autor.setEmail(dto.email());
        autor.setFoto_url(dto.foto_url());

        autorRepository.save(autor);

        return autor;
    }

    public Autor cadastrarComFoto_OLD(String nome, String email, MultipartFile imgPerfil) {
        File path = new File(PATH_IMAGE_PROFILE);

        if(!path.exists()){
            path.mkdirs();
        }

        try {
            //Gerando nome do arquivo
            String nomeArquivo = UUID.randomUUID()+"_"+imgPerfil.getOriginalFilename();
            String originalPath = PATH_IMAGE_PROFILE+nomeArquivo;

            //Salvando a imagem
            File arquivoDestino = new File(originalPath);
            imgPerfil.transferTo(arquivoDestino);

            //Gerar url para exibição do arquivo
            String urlImage = "/files/photos/"+nomeArquivo;

            // salvar no banco
            AutorDto dto = new AutorDto(nome, email, urlImage);
            return cadastrar(dto);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Autor cadastrarComFoto(String nome, String email, MultipartFile imgPerfil) {
        try {
            Files.createDirectories(Paths.get(UPLOAD_IMAGE_DIR));

            String nomeArquivo = UUID.randomUUID() + "_"+imgPerfil.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_IMAGE_DIR + nomeArquivo);

            imgPerfil.transferTo(filePath);

            AutorDto dto = new AutorDto(nome, email, BASE_URL + nomeArquivo);
            return cadastrar(dto);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Autor cadastrarComFotoS3(String nome, String email, MultipartFile imgPerfil) throws IOException {
        String nomeArquivo = UUID.randomUUID() + "_"+imgPerfil.getOriginalFilename();

        try{
            File imgFile = this.convertMultipartToFile(imgPerfil);
            s3Client.putObject(bucketName, nomeArquivo, imgFile);
            String imgUrl = s3Client.getUrl(bucketName, nomeArquivo).toString();

            AutorDto dto = new AutorDto(nome, email, imgUrl);
            return cadastrar(dto);
        }catch(Exception e){
            throw e;
        }
    }

    private File convertMultipartToFile(MultipartFile imgPerfil) throws IOException {
        File convFile = new File(Objects.requireNonNull(imgPerfil.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(imgPerfil.getBytes());
        fos.close();
        return convFile;
    }
}
