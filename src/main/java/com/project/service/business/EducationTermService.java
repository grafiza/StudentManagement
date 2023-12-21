package com.project.service.business;

import com.project.entity.concretes.business.EducationTerm;
import com.project.exception.BadRequestException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.EducationTermMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.EducationTermRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.EducationTermResponse;
import com.project.repository.business.EducationTermRepository;
import com.project.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationTermService {

    private final EducationTermRepository educationTermRepository;
    private final EducationTermMapper educationTermMapper;
    private final PageableHelper pageableHelper;

    public ResponseMessage<EducationTermResponse> saveEducationTerm(EducationTermRequest educationTermRequest) {
        validateEducationTermDates(educationTermRequest);
        EducationTerm savedEducationTerm =
                educationTermRepository.save(educationTermMapper.mapEducationTermRequestToEducationTerm(educationTermRequest));

        return ResponseMessage.<EducationTermResponse>builder()
                .message(SuccessMessages.EDUCATION_TERM_SAVE)
                .object(educationTermMapper.mapEducationTermToEducationTermResponse(savedEducationTerm))
                .status(HttpStatus.CREATED)
                .build();

    }

    //!!! Yrd Methd -1
    private void validateEducationTermDatesForRequest(EducationTermRequest educationTermRequest){
        // registration > start
        if(educationTermRequest.getLastRegistrationDate().isAfter(educationTermRequest.getStartDate())){
            throw new ResourceNotFoundException(ErrorMessages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE);
        }

        // end > start
        if(educationTermRequest.getEndDate().isBefore(educationTermRequest.getStartDate())) {
            throw new ResourceNotFoundException(ErrorMessages.EDUCATION_END_DATE_IS_EARLIER_THAN_START_DATE);
        }
    }
    //!!! Yrd Method-2
    private void validateEducationTermDates(EducationTermRequest educationTermRequest){

        validateEducationTermDatesForRequest(educationTermRequest);

        //!!! Bir yil icinde bir tane Guz donemi olmali
        if(educationTermRepository.existsByTermAndYear(educationTermRequest.getTerm(),
                educationTermRequest.getStartDate().getYear())){
            throw new ResourceNotFoundException(ErrorMessages.EDUCATION_TERM_IS_ALREADY_EXIST_BY_TERM_AND_YEAR_MESSAGE);
        }

        //!!! yil icinde eklenecek educationTerm, DB'deki mevcuttakilerin tarihleri ile cakismamali
        if(educationTermRepository.findByYear(educationTermRequest.getStartDate().getYear())
                .stream()
                .anyMatch(educationTerm->
                        (
                                educationTerm.getStartDate().equals(educationTermRequest.getStartDate()) // ilk kontrol baslama tarihlerinin ayni olma durumu
                                        || (educationTerm.getStartDate().isBefore(educationTermRequest.getStartDate())//2.kontrol: eklenecek olan ET un startDatei mevcuttakilerin arasina denk geliyor mu
                                        && educationTerm.getEndDate().isAfter(educationTermRequest.getStartDate()))
                                        || (educationTerm.getStartDate().isBefore(educationTermRequest.getEndDate())//3.kontrol : yeni eklenecek ET un EndDate bilgisinin mevcuttakilerin arasinda olma durumu
                                        && educationTerm.getEndDate().isAfter(educationTermRequest.getEndDate()))
                                        || (educationTerm.getStartDate().isAfter(educationTermRequest.getStartDate())//4.kontrol : yeni eklenecek ET . ,mevcuttakileri tamamen kapsiyorsa
                                        && educationTerm.getEndDate().isBefore(educationTermRequest.getEndDate()))

                        ))
        ){
            throw new BadRequestException(ErrorMessages.EDUCATION_TERM_CONFLICT_MESSAGE);
        }

    }

    public EducationTermResponse getEducationTermById(Long id) {
        EducationTerm term = isEducationTermExist(id);
        return educationTermMapper.mapEducationTermToEducationTermResponse(term);
    }

    private EducationTerm isEducationTermExist(Long id){
        return educationTermRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.EDUCATION_TERM_NOT_FOUND_MESSAGE,id)));
    }

    public List<EducationTermResponse> getAllEducationTerms() {

        return educationTermRepository.findAll()
                .stream()
                .map(educationTermMapper::mapEducationTermToEducationTermResponse)
                .collect(Collectors.toList());
    }

    public Page<EducationTermResponse> getAllEducationTermByPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return educationTermRepository.findAll(pageable)
                .map(educationTermMapper::mapEducationTermToEducationTermResponse);
    }

    public ResponseMessage<?> deleteEducationTermById(Long id) {
        isEducationTermExist(id);
        educationTermRepository.deleteById(id);

        return ResponseMessage.builder()
                .message(SuccessMessages.EDUCATION_TERM_DELETE)
                .status(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<EducationTermResponse> updateEducationTerm(Long id, EducationTermRequest educationTermRequest) {

        isEducationTermExist(id);
        validateEducationTermDates(educationTermRequest);
        EducationTerm educationTerm2 =
                educationTermMapper.mapEducationTermRequestToUpdatedEducationTerm(id,educationTermRequest);

        EducationTerm educationTermUpdated = educationTermRepository.save(educationTerm2);

        return ResponseMessage.<EducationTermResponse>builder()
                .message(SuccessMessages.EDUCATION_TERM_UPDATE)
                .status(HttpStatus.OK)
                .object(educationTermMapper.mapEducationTermToEducationTermResponse(educationTermUpdated))
                .build();
    }

    //!!! LessonProgram icin yazildi
    public EducationTerm findEducationTermById(Long id){
        return isEducationTermExist(id);
    }
}