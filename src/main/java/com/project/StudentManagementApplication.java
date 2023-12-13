package com.project;

import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.Gender;
import com.project.entity.enums.RoleType;
import com.project.payload.request.user.UserRequest;
import com.project.repository.UserRepository;
import com.project.repository.UserRoleRepository;
import com.project.service.UserRoleService;
import com.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
@RequiredArgsConstructor
public class StudentManagementApplication implements CommandLineRunner {
    private final UserRoleService userRoleService;
    private final UserRoleRepository userRoleRepository;
    private final UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // role tablosu dolduruluyor
        if (userRoleService.getAllUserRole().isEmpty()) {
            UserRole admin = new UserRole();
            admin.setRoleType(RoleType.ADMIN);
            admin.setRoleName("SuperAdmin");
            userRoleRepository.save(admin);

            UserRole dean = new UserRole();
            dean.setRoleType(RoleType.MANAGER);
            dean.setRoleName("Dean");
            userRoleRepository.save(dean);

            UserRole viceDean = new UserRole();
            viceDean.setRoleType(RoleType.ASSISTANT_MANGER);
            viceDean.setRoleName("Vice Dean");
            userRoleRepository.save(viceDean);

            UserRole teacher = new UserRole();
            teacher.setRoleType(RoleType.TEACHER);
            teacher.setRoleName("Teacher");
            userRoleRepository.save(teacher);

            UserRole student = new UserRole();
            student.setRoleType(RoleType.STUDENT);
            student.setRoleName("Student");
            userRoleRepository.save(student);
        }

        // admin yoksa bir tane built in admin oluşturuluyor
        if (userService.countAllAdmins() == 0) {
            UserRequest adminRequest= new UserRequest();
            adminRequest.setUsername("SuperAdmin");
            adminRequest.setEmail("admin@admin.com");
            adminRequest.setSsn("111-11-1111");
            adminRequest.setPassword("12345678");
            adminRequest.setName("Zafer");
            adminRequest.setSurname("Kanbur");
            adminRequest.setPhoneNumber("555-555-5555");
            adminRequest.setGender(Gender.MALE);
            adminRequest.setBirthDay(LocalDate.of(1980,12,9));
            adminRequest.setBirthPlace("Rize");
            userService.saveUser(adminRequest,"Admin");
        }
    }
}
