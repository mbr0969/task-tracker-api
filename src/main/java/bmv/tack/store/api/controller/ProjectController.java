package bmv.tack.store.api.controller;

import bmv.tack.store.api.dto.AckDto;
import bmv.tack.store.api.dto.ProjectDto;
import bmv.tack.store.api.exceptions.BadRequestException;
import bmv.tack.store.api.exceptions.NotFoundException;
import bmv.tack.store.api.factories.ProjectDtoFactory;
import bmv.tack.store.entities.ProjectEntity;
import bmv.tack.store.repositories.ProjectRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RestController
public class ProjectController {
    ProjectDtoFactory projectDtoFactory;
    ProjectRepository projectRepository;

    public static final String FETCH_PROJECT ="/api/projects";
    public static final String CREATE_PROJECT ="/api/projects";
    public static final String EDIT_PROJECT ="/api/projects/{project_id}";
    public static final String DELETE_PROJECT ="/api/projects/{project_id}";

    public static final String CREATE_OR_UPDATE_PROJECT ="/api/projects";

    @GetMapping(FETCH_PROJECT)
    public List<ProjectDto> fetchProjects(@RequestParam(value = "prefix_name", required = false) Optional<String> optionalPrefixName){

       optionalPrefixName = optionalPrefixName.filter(prefixName -> !prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectStream  = optionalPrefixName
        .map(projectRepository::streamAllByNameStartsWithIgnoreCase)
        .orElseGet(projectRepository::streamAll);


        return  projectStream
                .map(projectDtoFactory::makeProjectDto)
                .collect(Collectors.toList());

    }


    @PostMapping(CREATE_PROJECT)
    public ProjectDto createProject(@RequestParam String projectName){

        if(projectName.trim().isEmpty()){
            throw new BadRequestException("Project name can not be empty  ");
        }

        projectRepository.findByName(projectName).ifPresent(project ->{
                throw new BadRequestException(String.format("Project \"%s\" already exist ", projectName));
        });

        ProjectEntity project =  projectRepository.saveAndFlush(
                ProjectEntity.builder().name(projectName).build()
        );
        return  projectDtoFactory.makeProjectDto(project);
    }

    @PatchMapping(EDIT_PROJECT)
    public ProjectDto editProject(@PathVariable("project_id") Long projectId, @RequestParam String projectName){

        if(projectName.trim().isEmpty()){
             throw new BadRequestException("Project name can not be empty  ");
        }

        ProjectEntity  project = getProjectOrException(projectId);

        projectRepository
                .findByName(projectName)
                .filter(anotherProject -> !Objects.equals(anotherProject.getId(), projectId))
                .ifPresent(anotherProject ->{
                    throw new BadRequestException(String.format("Project \"%s\" already exist ", projectName));
                });

        project.setName(projectName);
        project = projectRepository.saveAndFlush(project);
        return projectDtoFactory.makeProjectDto(project);
    }


    @PutMapping(CREATE_OR_UPDATE_PROJECT)
    public ProjectDto createOrUpdateProject(
            @RequestParam(value = "project_id", required = false) Optional<Long> optionalProjectId,
            @RequestParam(value = "project_name", required = false) Optional<String> optionalProjectName) {

        optionalProjectName = optionalProjectName.filter(projectName -> !projectName.trim().isEmpty());

        boolean isCreate = !optionalProjectId.isPresent();
        if(isCreate && !optionalProjectName.isPresent()){
            throw   new BadRequestException("Project name can not be empty!");
        }

        final ProjectEntity project = optionalProjectId
                .map(this::getProjectOrException)
                .orElseGet(()->ProjectEntity.builder().build());

        optionalProjectName.ifPresent(projectName -> {

            projectRepository
                    .findByName(projectName)
                    .filter(anotherProject -> !Objects.equals(anotherProject.getId(), project.getId()))
                    .ifPresent(anotherProject ->{
                        throw new BadRequestException(String.format("Project \"%s\" already exist ", projectName));
                    });
            project.setName(projectName);

        });

        final ProjectEntity savedProject = projectRepository.saveAndFlush(project);
        return projectDtoFactory.makeProjectDto(savedProject);
    }


    @DeleteMapping(EDIT_PROJECT)
    public AckDto deleteProject(@PathVariable("project_id") Long projectId){

        getProjectOrException(projectId);

        projectRepository.deleteById(projectId);
         return AckDto.makeDefault(true);
    }

    private ProjectEntity getProjectOrException(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new NotFoundException(String.format("Project with \"%s\" does not exist", projectId)));
    }


}
