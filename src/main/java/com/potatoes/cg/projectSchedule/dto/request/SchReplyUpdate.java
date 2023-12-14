package com.potatoes.cg.projectSchedule.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class SchReplyUpdate {

    @NotBlank
    private final String replyBody;

    @JsonCreator
    public SchReplyUpdate(@JsonProperty("replyBody") String replyBody) {
        this.replyBody = replyBody;
    }
}
