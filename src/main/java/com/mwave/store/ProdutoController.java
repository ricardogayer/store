package com.mwave.store;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/")
public class ProdutoController {

    ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/produtos")
    public ResponseEntity<List<Produto>> getProdutos() {
        List<Produto> produtos = produtoRepository.findByOrderByNomeAsc();
        return ResponseEntity.ok(produtos);
    }

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

    @PostMapping("/produtos")
    public ResponseEntity<Produto> saveProduto(@RequestBody Produto produto) {
        System.out.println("Incluindo o produto: " + produto.getNome());
        Produto p = produtoRepository.save(produto);
        return new ResponseEntity<>(p, HttpStatus.CREATED);
    }

    @PutMapping("/produtos")
    public ResponseEntity<Produto> updateProduto(@RequestBody Produto produto) {

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
