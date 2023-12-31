package dev.mateusb.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "user-controller", description = "Cria usuário")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")

    @Operation(summary = "Método de criação de usuário", description = "Cria o usuário para autenticação.")
    public ResponseEntity create(@RequestBody UserModel userModel){
        var user = this.userRepository.findByUsername(userModel.getUsername());

        if(user != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe!");
        }
        var passwordHashed = BCrypt.withDefaults().hashToString(12,userModel.getPassword().toCharArray());

        userModel.setPassword(passwordHashed);

        var userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.OK).body(userCreated);
    }
}
