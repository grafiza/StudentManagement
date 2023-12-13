package com.project.payload.request.user;

import com.project.payload.request.abstracts.BaseUserRequest;
import com.project.payload.response.abstracts.BaseUserResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.bytebuddy.implementation.bind.annotation.Super;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class TeacherRequest extends BaseUserRequest {
    @NotNull(message = "Please select lesson")
    private Set<Long> lessonIdList;

    @NotNull(message = "Please select isAdvisor teacher")
    private Boolean isAdvisorTeacher;


}
