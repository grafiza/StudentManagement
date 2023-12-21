package com.project.controller.business;

import com.project.payload.request.business.LessonProgramRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.LessonProgramResponse;
import com.project.service.business.LessonProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/lessonPrograms")
@RequiredArgsConstructor
public class LessonProgramController {

    private final LessonProgramService lessonProgramService;

    @PostMapping("/save") // http://localhost:8080/lessonPrograms/save + POST + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<LessonProgramResponse> saveLessonProgram(@RequestBody @Valid
                                                                    LessonProgramRequest lessonProgramRequest) {
        return lessonProgramService.saveLessonProgram(lessonProgramRequest);
    }

    @GetMapping("/getAll")   // http://localhost:8080/lessonPrograms/getAll  +GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllLessonPrograms() {
        return lessonProgramService.getAllLessonPrograms();
    }

    @GetMapping("/getById")   // http://localhost:8080/lessonPrograms/getById/5  +GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public LessonProgramResponse getLessonProgramById(@PathVariable Long id) {

        return lessonProgramService.getLessonProgramById(id);
    }
//Herhangi bir öğretmen ataması yapılmamış lesson programlar

    @GetMapping("/getAllUnassigned")   // http://localhost:8080/lessonPrograms/getAllUnassigned
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllUnassigned() {
        return lessonProgramService.getAllUnassigned();
    }

    @GetMapping("/getAllAssigned")   // http://localhost:8080/lessonPrograms/getAllAssigned
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllAssigned() {
        return lessonProgramService.getAllAssigned();
    }

    @DeleteMapping("/delete/{id}")  // http://localhost:8080/lessonPrograms/delete/3
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage deleteLessonProgramById(@PathVariable Long id) {
        return lessonProgramService.deleteLessonProgramById(id);
    }

    @GetMapping("/getAllLessonProgramWithPage")
    // http://localhost:8080/lessons/findLessonByPage?page=0&size=10&sort=lessonName&type=desc + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public Page<LessonProgramResponse> getAllLessonProgramWithPage(@RequestParam(value = "page") int page,
                                                                   @RequestParam(value = "size") int size,
                                                                   @RequestParam(value = "sort") String sort,
                                                                   @RequestParam(value = "type") String type) {
        return lessonProgramService.getAllLessonProgramWithPage(page, size, sort, type);
    }

    // Bir öğretmenin kendine ait ders programlarını getirme

    @GetMapping("/getAllLessonProgramByTeacher")   // http://localhost:8080/lessons/getAllLessonProgramByTeacher
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public Set<LessonProgramResponse> getAllLessonProgramByTeacher(HttpServletRequest request) {
        return lessonProgramService.getAllLessonProgramByUser(request);
    }

    @GetMapping("/getAllLessonProgramByStudent")   // http://localhost:8080/lessons/getAllLessonProgramByStudent
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    public Set<LessonProgramResponse> getAllLessonProgramByStudent(HttpServletRequest request) {
        return lessonProgramService.getAllLessonProgramByUser(request);
    }

    @GetMapping("/getLessonProgramsByTeacherId/{id}")  // http://localhost:8080/lessonPrograms/getLessonProgramsByTeacherId/3
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Set<LessonProgramResponse> getLessonProgramsByTeacherId(@RequestParam Long id) {
        return  lessonProgramService.getLessonProgramsByTeacherId(id);


    }

    @GetMapping("/getLessonProgramsByStudentId/{id}")  // http://localhost:8080/lessonPrograms/getLessonProgramsByStudentId/3
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Set<LessonProgramResponse> getLessonProgramsByStudentId(@RequestParam Long id) {
        return  lessonProgramService.getLessonProgramsByStudentId(id);

    }
}