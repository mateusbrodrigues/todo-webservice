package dev.mateusb.todolist.task;

import dev.mateusb.todolist.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@Tag(name = "task-controller", description = "Método de inclusão de tarefas")
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    @Operation(summary = "Método de incluir tarefas", description = "Inclui a tarefa de acordo com os parâmetros desejados.",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início/fim deve ser maior do que a data atual.");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início deve ser menor do que a data término.");
        }

        var task = this.taskRepository.save(taskModel);

        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    @Operation(summary = "Método de recuperar tarefas", description = "Recupera tarefas do usuário autenticado.",security = @SecurityRequirement(name = "basicAuth"))
    public List<TaskModel> list(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");

        return this.taskRepository.findByIdUser((UUID) idUser);
    }
    @PutMapping("/{id}")
    @Operation(summary = "Método de atualizar tarefas", description = "Altera algum campo desejado.",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity update(@RequestBody TaskModel taskModel,@PathVariable UUID id, HttpServletRequest request){

        var task = this.taskRepository.findById(id).orElse(null);
        var idUser = request.getAttribute("idUser");

        if(task == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tarefa não encontrada.");
        }

        if(!task.getIdUser().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuário sem permissão para alterar essa tarefa.");
        }

        Utils.copyNonNullProperties(taskModel,task);

        var taskUpdated = this.taskRepository.save(task);

        return ResponseEntity.ok().body(taskUpdated);
    }
}
