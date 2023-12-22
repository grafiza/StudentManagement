package com.project.service.business;

import com.project.entity.concretes.business.EducationTerm;
import com.project.entity.concretes.business.Lesson;
import com.project.entity.concretes.business.StudentInfo;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.Note;
import com.project.entity.enums.RoleType;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.StudentInfoDto;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.StudentInfoRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.StudentInfoResponse;
import com.project.repository.business.StudentInfoRepository;
import com.project.service.UserService;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import com.project.service.user.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

    private final StudentInfoRepository studentInfoRepository;
    private final MethodHelper methodHelper;
    private final UserService userService;
    private final LessonService lessonService;
    private final EducationTermService educationTermService;
    private final StudentInfoDto studentInfoDto;
    private final PageableHelper pageableHelper;

    @Value("${midterm.exam.impact.percentage}")
    private Double midtermExamPercentage;

    @Value("${final.exam.impact.percentage}")
    private Double finalExamPercentage;


    public ResponseMessage<StudentInfoResponse> saveStudentInfo(HttpServletRequest httpServletRequest,
                                                                StudentInfoRequest studentInfoRequest) {
        String teacherUsername = (String) httpServletRequest.getAttribute("username");
        User student = methodHelper.isUserExist(studentInfoRequest.getStudentId());
        methodHelper.checkRole(student, RoleType.STUDENT);

        User teacher = methodHelper.isUserExistByUsername(teacherUsername);
        Lesson lesson = lessonService.isLessonExistById(studentInfoRequest.getLessonId());
        EducationTerm educationTerm =
                educationTermService.findEducationTermById(studentInfoRequest.getEducationTermId());

        //TODO brans kontrolu
        //!!! ayni ders icin duclicate kontrolu
        checkSameLesson(studentInfoRequest.getStudentId(), lesson.getLessonName());
        Note note = checkLetterGrade(calculateExamAverage(studentInfoRequest.getMidtermExam(),
                studentInfoRequest.getFinalExam()));

        //!!! DTO --> POJO
        StudentInfo studentInfo = studentInfoDto.mapStudentInfoRequestToStudentInfo(studentInfoRequest,
                note,
                calculateExamAverage(studentInfoRequest.getMidtermExam(), studentInfoRequest.getFinalExam()));

        studentInfo.setStudent(student);
        studentInfo.setTeacher(teacher);
        studentInfo.setEducationTerm(educationTerm);
        studentInfo.setLesson(lesson);

        StudentInfo savedStudentInfo = studentInfoRepository.save(studentInfo);

        return ResponseMessage.<StudentInfoResponse>builder()
                .message(SuccessMessages.STUDENT_INFO_SAVE)
                .status(HttpStatus.CREATED)
                .object(studentInfoDto.mapStudentInfoToStudentInfoResponse(savedStudentInfo))
                .build();
    }

    private void checkSameLesson(Long studentId, String lessonName) {
        boolean isLessonDuplicationExist = studentInfoRepository.getAllByStudentId_Id(studentId)
                .stream()
                .anyMatch(e -> e.getLesson().getLessonName().equalsIgnoreCase(lessonName));

        if (isLessonDuplicationExist) {
            throw new ConflictException(String.format(ErrorMessages.ALREADY_EXIST_LESSON_WITH_LESSON_NAME_MESSAGE, lessonName));
        }
    }

    private Double calculateExamAverage(Double midtermExam, Double finalExam) {
        return ((midtermExam * midtermExamPercentage) + (finalExam * finalExamPercentage));
    }

    private Note checkLetterGrade(Double average) {
        if (average < 50.0) {
            return Note.FF;
        } else if (average < 60) {
            return Note.DD;
        } else if (average < 65) {
            return Note.CC;
        } else if (average < 70) {
            return Note.CB;
        } else if (average < 75) {
            return Note.BB;
        } else if (average < 80) {
            return Note.BA;
        } else {
            return Note.AA;
        }
    }

    public ResponseMessage deleteStudentInfoById(Long studentInfoId) {
        StudentInfo studentInfo = isStudentInfoExistById(studentInfoId);
        studentInfoRepository.delete(studentInfo);
        return ResponseMessage.builder()
                .message(SuccessMessages.STUDENT_INFO_DELETED)
                .status(HttpStatus.OK)
                .build();
    }

    public StudentInfo isStudentInfoExistById(Long id) {
        if (!studentInfoRepository.existsByIdEquals(id)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.STUDENT_INFO_NOT_FOUND, id));
        } else return studentInfoRepository.findById(id).get();
    }

    public Page<StudentInfoResponse> getAllStudentInfoByPage(int page, int size, String sort, String type) {
       Pageable pageable= pageableHelper.getPageableWithProperties(page, size, sort, type);
       return studentInfoRepository.findAll(pageable).map(studentInfoDto::mapStudentInfoToStudentInfoResponse);
    }

    public StudentInfoResponse getStudentInfoById(Long studentInfoId) {
        return studentInfoDto.mapStudentInfoToStudentInfoResponse(isStudentInfoExistById(studentInfoId));
    }
}