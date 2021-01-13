package com.mwave.store;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/")
public class ProdutoController {

    private int contador = 0;

    ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // UPLOAD
    @PostMapping(
            path = "image/{imageId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void uploadFile(@PathVariable("imageId") Long id,
                           @RequestParam("image") MultipartFile file) throws IOException {

        UUID uuid = UUID.randomUUID();

        System.out.println("Image ID = " + id);

        // String filePath = "/Users/rrgayer/" + file.getOriginalFilename();
        String filePath = "/Users/rrgayer/" + uuid.toString() + ".jpg";
        file.transferTo(new File(filePath));

    }

    // DOWNLOAD
    @GetMapping(
        path = "/image/download/{id}",
        produces = MediaType.IMAGE_PNG_VALUE
    )
    public byte[] downloadFile(@PathVariable("id") Long id) throws IOException {
        System.out.println("Realizando download..." + id);
        String filePath = "/Users/rrgayer/" + id + ".png";
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        return bytes;
    }


   // LIST
    @GetMapping("/produtos")
    public ResponseEntity<List<Produto>> getProdutos() {
        List<Produto> produtos = produtoRepository.findByOrderByNomeAsc();
        // Simulando um erro p/ uso do Retry no iOS - Alamofire
        if (contador <= 5) {
            System.out.println("Contador: " + contador);
            contador++;
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(produtos);
    }


   // LIST filtro
    @GetMapping("/produtos/{id}")
    public ResponseEntity<Produto> getProduto(@PathVariable("id") Long id) {

        Produto produto = new Produto();
        Optional<Produto> opt = produtoRepository.findById(id);

        if (opt.isPresent()) {
            produto = opt.get();
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        System.out.println("Recuperando o produto: " + produto.getNome());
        return ResponseEntity.ok(produto);
    }

    // SAVE
    @PostMapping("/produtos")
    public ResponseEntity<Produto> saveProduto(@RequestBody Produto produto) {
        System.out.println("Incluindo o produto: " + produto.getNome());
        Produto p = produtoRepository.save(produto);
        return new ResponseEntity<>(p, HttpStatus.CREATED);
    }

    // UPDATE
    @PutMapping("/produtos")
    public ResponseEntity<Produto> updateProduto(@RequestBody Produto produto) {

        System.out.println("Chamada da atualização do produto");
        System.out.println("Produto Id: " + produto.getId());
        System.out.println("Produto Nome: " + produto.getNome());

        Produto p = new Produto();
        Optional<Produto> opt = produtoRepository.findById(produto.getId());

        if (opt.isPresent()) {
            p = opt.get();
            p.setCategoria(produto.getCategoria());
            p.setNome(produto.getNome());
            System.out.println("Atualizando o produto: " + produto.getNome());
            // Validaçao...
            if (p.getCategoria().equals("wearable")) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }

            p = produtoRepository.save(p);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(p);
    }

    // DELETE
    @DeleteMapping("/produtos/{id}")
    public ResponseEntity deleteProdutoById(@PathVariable("id") Long id) {
        Optional<Produto> opt = produtoRepository.findById(id);
        if (opt.isPresent()) {
            Produto produto = opt.get();
            System.out.println("Excluindo o produto: " + produto.getNome());
            produtoRepository.delete(produto);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
