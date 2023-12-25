package com.project.service.business;

import com.project.entity.concretes.business.LessonProgram;
import com.project.entity.concretes.business.Meet;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.RoleType;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.MeetMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.MeetRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.MeetResponse;
import com.project.repository.business.MeetRepository;
import com.project.service.UserService;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import com.project.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MeetService {
    private final MeetRepository meetRepository;
    private final MethodHelper methodHelper;
    private final DateTimeValidator dateTimeValidator;
    private final UserService userService;
    private final MeetMapper meetMapper;
    private final PageableHelper pageableHelper;

    public ResponseMessage<MeetResponse> saveMeet(HttpServletRequest request, MeetRequest meetRequest) {
        String username = (String) request.getAttribute("username");
        User advisorTeacher = methodHelper.isUserExistByUsername(username);
        methodHelper.checkAdvisor(advisorTeacher);
        dateTimeValidator.checkTimeWithException(meetRequest.getStartTime(), meetRequest.getStopTime());
        checkMeetConflict(advisorTeacher.getId(), meetRequest.getDate(), meetRequest.getStartTime(), meetRequest.getStopTime());
// student için meet conflict kontrolü
        for (Long studentId : meetRequest.getStudentIds()) {
            User student = methodHelper.isUserExist(studentId);
            methodHelper.checkRole(student, RoleType.STUDENT);
            checkMeetConflict(studentId, meetRequest.getDate(), meetRequest.getStartTime(), meetRequest.getStopTime());
        }
// meet e katılacak student lar getiriliyor
        List<User> students = userService.getStudentById(meetRequest.getStudentIds());
       Meet meet= meetMapper.mapMeetRequestToMeet(meetRequest);
        meet.setStudentList(students);
        meet.setAdvisoryTeacher(advisorTeacher);
        Meet savedMeet=meetRepository.save(meet);
        return ResponseMessage.<MeetResponse>builder()
                .message(SuccessMessages.MEET_SAVE)
                .object(meetMapper.mapMeetToMeetResponse(savedMeet))
                .status(HttpStatus.OK)
                .build();
    }

    private void checkMeetConflict(Long userId, LocalDate date, LocalTime startTime, LocalTime stopTime) {

        List<Meet> meets;
        if (Boolean.TRUE.equals(userService.getUserByUserId(userId).getIsAdvisor())) {
            meets = meetRepository.getByAdvisorTeacher_IdEquals(userId);
        } else meets = meetRepository.findByStudentList_IdEquals(userId);

        for (Meet meet : meets) {
            LocalTime existingStartTime = meet.getStartTime();
            LocalTime existingStopTime = meet.getStopTime();

            if (meet.getDate().equals(date) &&
                    (
                            (startTime.isAfter(existingStartTime) && startTime.isBefore(existingStopTime)) ||
                                    (stopTime.isAfter(existingStartTime) && stopTime.isBefore(existingStopTime)) ||
                                    (startTime.isBefore(existingStartTime) && stopTime.isAfter(existingStopTime)) ||
                                    (startTime.equals(existingStartTime) || stopTime.equals(existingStopTime))
                    )
            ) {
                throw new ConflictException(ErrorMessages.MEET_HOURS_CONFLICT);
            }
        }
    }

    public List<MeetResponse> getAllMeet() {
        return meetRepository.findAll().stream().map(meetMapper::mapMeetToMeetResponse).collect(Collectors.toList());
    }

    public MeetResponse getByMeetId(Long meetId) {
        return meetMapper.mapMeetToMeetResponse(isMeetExistById(meetId));

    }


    private Meet isMeetExistById(Long id) {
        return meetRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_PROGRAM_MESSAGE, id)));
    }


    public ResponseMessage delete(Long id) {
        meetRepository.delete(isMeetExistById(id));
        return ResponseMessage.builder()
                .message(SuccessMessages.MEET_DELETE_MESSAGE)
                .status(HttpStatus.OK)
                .build();
    }

    public Page<MeetResponse> getAllWithPage(int page, int size) {
        Pageable pageable=pageableHelper.getPageableWithProperties(page,size);
        return meetRepository.findAll(pageable).map(meetMapper::mapMeetToMeetResponse);
    }
}
