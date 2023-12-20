package com.project.controller.user;

import com.project.payload.request.user.StudentRequest;
import com.project.payload.request.user.StudentRequestWithoutPassword;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.user.StudentResponse;
import com.project.service.user.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    @PostMapping("/save") // http://localhost:8080/students/save + POST + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<StudentResponse>> saveStudent(@RequestBody @Valid StudentRequest studentRequest) {
        return ResponseEntity.ok(studentService.saveStudent(studentRequest));
    }

    @PatchMapping("/update") // http://localhost:8080/students/update + PATCH + JSON
    @PreAuthorize("hasAnyAuthority('STUDENT')") // şifre hariç diğer değişkenleri setleme
    public ResponseEntity<String> updateStudent(@RequestBody @Valid StudentRequestWithoutPassword studentRequestWithoutPassword,
                                                HttpServletRequest request){
            return studentService.updateStudent(studentRequestWithoutPassword,request);
    }

    @PutMapping("/update/{id}")  // http://localhost:8080/students/update/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<StudentResponse> updateStudentForManager(@PathVariable Long userId, // öğrenci şifresini unutup idare ben şifremi unuttum derse
                                                                    @RequestBody @Valid StudentRequest studentRequest){

    }


}

