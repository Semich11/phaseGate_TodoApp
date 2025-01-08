package academy.learnprogramming.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class TodoResponseDto {
    private String title;
    private String description;
    private boolean isCompleted;
    private Date createdAt;
}
