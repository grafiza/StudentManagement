package com.project.payload.mappers;

import com.project.entity.concretes.user.User;
import com.project.payload.request.abstracts.BaseUserRequest;
import com.project.payload.request.user.UserRequest;
import com.project.payload.response.UserResponse;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.payload.response.user.StudentResponse;
import com.project.payload.response.user.TeacherResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse mapUserToUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .surname(user.getSurname())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .birthDay(user.getBirthDay())
                .birthPlace(user.getBirthPlace())
                .ssn(user.getSsn())
                .email(user.getEmail())
                .userRole(user.getUserRole().getRoleType().name)
                .build();

    }

    public User mapUserRequestToUser(BaseUserRequest userRequest) {
        // burada polymorphism kullandık
        return User.builder()
                .username(userRequest.getUsername())
                .name(userRequest.getName())
                .surname(userRequest.getSurname())
                .password(userRequest.getPassword())
                .ssn(userRequest.getSsn())
                .birthDay(userRequest.getBirthDay())
                .birthPlace(userRequest.getBirthPlace())
                .phoneNumber(userRequest.getPhoneNumber())
                .gender(userRequest.getGender())
                .email(userRequest.getEmail())
                .built_in(userRequest.getBuiltIn())
                .build();
    }

    public StudentResponse mapUserToStudentResponse(User student) {
        return StudentResponse.builder()
                .userId(student.getId())
                .username(student.getUsername())
                .name(student.getName())
                .surname(student.getSurname())
                .birthDay(student.getBirthDay())
                .birthPlace(student.getBirthPlace())
                .phoneNumber(student.getPhoneNumber())
                .gender(student.getGender())
                .email(student.getEmail())
                .fatherName(student.getFatherName())
                .motherName(student.getMotherName())
                .studentNumber(student.getStudentNumber())
                .isActive(student.isActive())
                .build();
    }

    public TeacherResponse mapUserToTeacherResponse(User teacher) {
        return TeacherResponse.builder()
                .userId(teacher.getId())
                .username(teacher.getUsername())
                .name(teacher.getName())
                .surname(teacher.getSurname())
                .birthDay(teacher.getBirthDay())
                .birthPlace(teacher.getBirthPlace())
                .ssn(teacher.getSsn())
                .phoneNumber(teacher.getPhoneNumber())
                .gender(teacher.getGender())
                .email(teacher.getEmail())
                .lessonPrograms(teacher.getLessonProgramList())
                .isAdvisorTeacher(teacher.getIsAdvisor())
                .build();


    }



    public User mapUserRequestToUpdatedUser(BaseUserRequest userRequest,Long userId) {
        // burada polymorphism kullandık
        return User.builder()
                .id(userId)
                .username(userRequest.getUsername())
                .name(userRequest.getName())
                .surname(userRequest.getSurname())
                .password(userRequest.getPassword())
                .ssn(userRequest.getSsn())
                .birthDay(userRequest.getBirthDay())
                .birthPlace(userRequest.getBirthPlace())
                .phoneNumber(userRequest.getPhoneNumber())
                .gender(userRequest.getGender())
                .email(userRequest.getEmail())
                .build();
    }
}