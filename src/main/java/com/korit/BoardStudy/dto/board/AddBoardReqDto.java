package com.korit.BoardStudy.dto.board;

import com.korit.BoardStudy.entity.Board;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddBoardReqDto {
    private String title;
    private String content;
    private Integer userId;

    public Board toEntity() {
        return Board.builder()
                .title(title)
                .content(content)
                .userId(userId)
                .build();
    }
}