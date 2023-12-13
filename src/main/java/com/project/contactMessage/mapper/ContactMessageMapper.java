package com.project.contactMessage.mapper;

import com.project.contactMessage.dto.ContactMessageRequest;
import com.project.contactMessage.dto.ContactMessageResponse;
import com.project.contactMessage.entity.ContactMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ContactMessageMapper {
    // Request --> POJO
    public ContactMessage requestToContactMessage(ContactMessageRequest contactMessageRequest){
        return ContactMessage.builder()
                .name(contactMessageRequest.getMessage())
                .email(contactMessageRequest.getEmail())
                .subject(contactMessageRequest.getSubject())
                .message(contactMessageRequest.getMessage())
                .dateTime(LocalDateTime.now())
                .build();
    }

    public ContactMessageResponse contactMessageToResponse(ContactMessage contactMessage){
        return ContactMessageResponse.builder()
                .name(contactMessage.getName())
                .email(contactMessage.getEmail())
                .subject(contactMessage.getSubject())
                .message(contactMessage.getMessage())
                .dateTime(contactMessage.getDateTime())
                .build();
    }

}
