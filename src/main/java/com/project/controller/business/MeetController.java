package com.project.controller.business;

import com.project.payload.request.business.MeetRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.MeetResponse;
import com.project.payload.response.business.StudentInfoResponse;
import com.project.service.business.MeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/meet")
@RequiredArgsConstructor
public class MeetController {
    private final MeetService meetService;

    @PostMapping("/save")  //http://localhost:8080/meet/save    + POST + JSON
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseMessage<MeetResponse> saveMeet(HttpServletRequest request, @Valid @RequestBody MeetRequest meetRequest) {

        return meetService.saveMeet(request, meetRequest);
    }

    // getAll
  // @GetMapping("getAll")   //http://localhost:8080/meet/getAll
  // @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
  // public ResponseEntity<List<MeetResponse>> getAll() {
  //     return ResponseEntity.ok(meetService.getAllMeet());
  // }

  // //getByMeetId
  // @GetMapping("/getByMeetId/{meetId}") //http://localhost:8080/meet/getByMeetId/2
  // @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
  // public ResponseEntity<MeetResponse> getByMeetId(@PathVariable Long meetId) {
  //     return ResponseEntity.ok(meetService.getByMeetId(meetId));
  // }

  // // delete
  // @DeleteMapping("/delete")    //http://localhost:8080/meet/delete/2
  // @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
  // public ResponseMessage delete(@PathVariable Long id) {
  //     return meetService.delete(id);
  // }


  // // getAllWithPage
  // @GetMapping("getAllWithPage")   //http://localhost:8080/meet/getAllWithPage
  // @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
  // public ResponseEntity<Page<MeetResponse>> getAllWithPage(
  //                                                                   @RequestParam (value = "page") int page,
  //                                                                   @RequestParam(value = "size")int size){
  //     return new ResponseEntity<>(meetService.getAllWithPage(page,size), HttpStatus.OK);
  // }

    //Not: getAll
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/getAll")  // http://localhost:8080/meet/getAll
    public List<MeetResponse> getAll(){
        return meetService.getAll();
    }

    // Not: getByMeetId
    @PreAuthorize("hasAnyAuthority( 'ADMIN')")
    @GetMapping("/getMeetById/{meetId}")  // http://localhost:8080/meet/getMeetById/1
    public ResponseMessage<MeetResponse> getMeetById(@PathVariable Long meetId){
        return meetService.getMeetById(meetId);
    }

    // Not: Delete
    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN' )")
    @DeleteMapping("/delete/{meetId}") // http://localhost:8080/meet/2
    public ResponseMessage delete(@PathVariable Long meetId, HttpServletRequest httpServletRequest){
        return meetService.delete(meetId, httpServletRequest);
    }

    // Not: getAllWithPage
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/getAllMeetByPage") // http://localhost:8080/meet/getAllMeetByPage?page=0&size=1
    public Page<MeetResponse> getAllMeetByPage(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ){
        return meetService.getAllMeetByPage(page,size);
    }

    @PutMapping("/update/{meetId}")// http://localhost:8080/meet/update/2 + PUT + JSON
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseMessage<MeetResponse> updateMeet(@RequestBody @Valid MeetRequest meetRequest,
                                                    @PathVariable Long meetId,
                                                    HttpServletRequest httpServletRequest){
        return meetService.updateMeet(meetRequest, meetId, httpServletRequest);
    }

    @GetMapping("/getAllMeetByAdvisorTeacherAsList")// http://localhost:8080/meet/getAllMeetByAdvisorTeacherAsList
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<List<MeetResponse>> getAllMeetByTeacher(HttpServletRequest httpServletRequest){
        return meetService.getAllMeetByTeacher(httpServletRequest);
    }

    @GetMapping("/getAllMeetByStudent")// http://localhost:8080/meet/getAllMeetByStudent
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    public List<MeetResponse> getAllMeetByStudent( HttpServletRequest httpServletRequest){
        return meetService.getAllMeetByStudent(httpServletRequest);
    }
}
