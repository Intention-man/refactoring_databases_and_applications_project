package com.example.prac.controller;

import com.example.prac.data.DTO.data.ProjectDTO;
import com.example.prac.service.data.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import com.example.prac.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@AllArgsConstructor
@Tag(name = "Projects", description = "API для управления проектами")
@SecurityRequirement(name = "Bearer Authentication")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "Создать новый проект", description = "Создает новый проект в системе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Проект успешно создан",
                    content = @Content(schema = @Schema(implementation = ProjectDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        ProjectDTO savedProject = projectService.save(projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProject);
    }

    @Operation(summary = "Получить все проекты", description = "Возвращает список всех проектов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список проектов успешно получен"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.findAll();
        return ResponseEntity.ok(projects);
    }

    @Operation(summary = "Получить проект по ID", description = "Возвращает проект по указанному идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проект найден",
                    content = @Content(schema = @Schema(implementation = ProjectDTO.class))),
            @ApiResponse(responseCode = "404", description = "Проект не найден"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        ProjectDTO project = projectService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        return ResponseEntity.ok(project);
    }

    @Operation(summary = "Найти проекты по имени", description = "Возвращает проекты, содержащие указанную подстроку в названии")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список проектов успешно получен"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    @GetMapping("/find_by_name_containing")
    public ResponseEntity<List<ProjectDTO>> getByNameContaining(@RequestBody String substring) {
        List<ProjectDTO> projects = projectService.findByNameContaining(substring);
        return ResponseEntity.ok(projects);
    }

    @Operation(summary = "Частичное обновление проекта", description = "Обновляет указанные поля проекта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проект успешно обновлен",
                    content = @Content(schema = @Schema(implementation = ProjectDTO.class))),
            @ApiResponse(responseCode = "404", description = "Проект не найден"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProjectPartially(@PathVariable Long id, @RequestBody ProjectDTO projectDTO) {
        ProjectDTO updatedProject = projectService.partialUpdate(id.intValue(), projectDTO);
        return ResponseEntity.ok(updatedProject);
    }

    @Operation(summary = "Удалить проект", description = "Удаляет проект по указанному идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Проект успешно удален"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
