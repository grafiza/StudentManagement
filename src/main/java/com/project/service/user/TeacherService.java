package com.project.service.user;

import com.project.entity.concretes.user.User;
import com.project.entity.enums.RoleType;
import com.project.exception.ConflictException;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.user.TeacherRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.UserResponse;
import com.project.payload.response.user.StudentResponse;
import com.project.payload.response.user.TeacherResponse;
import com.project.repository.UserRepository;
import com.project.service.UserRoleService;
import com.project.service.helper.MethodHelper;
import com.project.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final MethodHelper methodHelper;

    public ResponseMessage<TeacherResponse> saveTeacher(TeacherRequest teacherRequest) {
        //TODO : LessonProgram eklenecek
        // unique kontrolü
        uniquePropertyValidator.checkDuplicate(teacherRequest.getUsername(),
                teacherRequest.getSsn(),
                teacherRequest.getPhoneNumber(),
                teacherRequest.getEmail());
        User teacher = userMapper.mapTeacherRequestToUser(teacherRequest);
        // pojoda olması gereken ama DTO da olmyaan verileri setliyoruz

        teacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));
        //TODO lesson program eklenecek
        //TODO password eklenecek
        teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        // advisor mı kontrol et
        if (teacherRequest.getIsAdvisorTeacher()) {
            teacher.setIsAdvisor(Boolean.TRUE);
        } else teacher.setIsAdvisor(Boolean.FALSE);
        User savedTeacher = userRepository.save(teacher);
        return ResponseMessage.<TeacherResponse>builder()
                .message(SuccessMessages.TEACHER_SAVE)
                .status(HttpStatus.CREATED)
                .object(userMapper.mapUserToTeacherResponse(savedTeacher))
                .build();
    }

    public ResponseMessage<TeacherResponse> updateTeacherForManagers(TeacherRequest teacherRequest, Long userId) {
        User user = methodHelper.isUserExist(userId);
        methodHelper.checkRole(user, RoleType.TEACHER);
        //TODO lesson program eklenecek
        //unique kontrolü
        uniquePropertyValidator.checkUniqueProperties(user, teacherRequest);
        //dto --> pojo
        User updatedTeacher = userMapper.mapTeacherRequestToUpdatedUser(teacherRequest, userId);
        //password encode
        updatedTeacher.setPassword(passwordEncoder.encode(teacherRequest.getPassword()));
        //TODO lesson program eklenecek

        //rolü setliyoruz
        updatedTeacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));

        User savedTeacher = userRepository.save(updatedTeacher);
        return ResponseMessage.<TeacherResponse>builder()
                .message(SuccessMessages.TEACHER_UPDATE)
                .object(userMapper.mapUserToTeacherResponse(savedTeacher))
                .status(HttpStatus.OK)
                .build();


    }

    public List<StudentResponse> getAllStudentByAdvisorUsername(String userName) {
        User teacher = methodHelper.isUserExistByUsername(userName);
        // isAdvisor
        methodHelper.checkAdvisor(teacher);
        return userRepository.findByAdvisorTeacherId(teacher.getId())
                .stream()
                .map(userMapper::mapUserToStudentResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage<UserResponse> saveAdvisorTeacher(Long teacherId) {
        User teacher = methodHelper.isUserExist(teacherId);
        // teacher mı kontrolü
        methodHelper.checkRole(teacher, RoleType.TEACHER);
        // id ile gelen teacher zaten advisor mı
        if (Boolean.TRUE.equals(teacher.getIsAdvisor())) {
            throw new ConflictException(String.format(ErrorMessages.ALREADY_EXIST_ADVISOR_MESSAGE, teacherId));
        }
        teacher.setIsAdvisor(Boolean.TRUE);
        userRepository.save(teacher);
        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.ADVISOR_TEACHER_SAVE)
                .object(userMapper.mapUserToUserResponse(teacher))
                .status(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<UserResponse> deleteAdvisorTeacherById(Long teacherId) {
        User teacher = methodHelper.isUserExist(teacherId);
        methodHelper.checkRole(teacher, RoleType.TEACHER);
        methodHelper.checkAdvisor(teacher);
        teacher.setIsAdvisor(Boolean.FALSE);
        userRepository.save(teacher);
        List<User> list = userRepository.findByAdvisorTeacherId(teacher.getId());
        if (!list.isEmpty()) {
            list.forEach(student -> student.setAdvisorTeacherId(null));
        }
        //TODO meet
        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.ADVISOR_TEACHER_DELETE)
                .object(userMapper.mapUserToUserResponse(teacher))
                .status(HttpStatus.OK)
                .build();
    }

    public List<UserResponse> getAllAdvisorTeacher() {
        return userRepository.findAllByAdvisor(Boolean.TRUE).stream().map(userMapper::mapUserToUserResponse).collect(Collectors.toList());
    }
}
