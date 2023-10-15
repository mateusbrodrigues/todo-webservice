package dev.mateusb.todolist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@Tag(name = "exemplo-controller", description = "Controller")
@RequestMapping("/primeiraRota")
public class MinhaPrimeiraController {

    @GetMapping("/primeiroMetodo")
    @Operation(summary = "Método de exemplo", description = "Retorna se a api está funcionando.")
    public String primeiraMensagem(){
            return  "API Funcionando! ;)";
    }
}
