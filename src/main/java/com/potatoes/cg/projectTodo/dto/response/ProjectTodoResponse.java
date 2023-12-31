package com.potatoes.cg.projectTodo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.potatoes.cg.jwt.CustomUser;
import com.potatoes.cg.project.domain.ProjectReply;
import com.potatoes.cg.projectTodo.domain.ProjectTodo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class ProjectTodoResponse {

    private final Long todoCode;
    private final String todoTitle;
    private final String memberName;
    private final List<ProjectTodoListResponse> todoList;
    private final String memberId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDateTime createdDate;
    private final List<Map<String, Object>> replies;
    private final Long infoCode;

    public static ProjectTodoResponse from(ProjectTodo projectTodo, List<ProjectTodoListResponse> projectTodolists, List<ProjectReply> replies, CustomUser customUser) {

        List<Map<String, Object>> repliesMap = replies.stream().map(reply -> {
            Map<String, Object> map = new HashMap<>();
            map.put("memberName", reply.getMember().getInfoName());
            map.put("replyBody", reply.getReplyBody());
            map.put("replyCreatedDate", reply.getReplyCreatedDate());
            map.put("replyCode", reply.getReplyCode());
            map.put("replyModifyDate", reply.getReplyModifyDate());
            map.put("infoCode", reply.getMember().getInfoCode());
            return map;
        }).collect(Collectors.toList());

        return new ProjectTodoResponse(
                projectTodo.getTodoCode(),
                projectTodo.getTodoTitle(),
                projectTodo.getMember().getMemberInfo().getInfoName(),
                projectTodolists,
                projectTodo.getMember().getMemberId(),
                projectTodo.getTodoModifyDate(),
                repliesMap,
                customUser.getInfoCode()
        );
    }
}
