package academy.learnprogramming.dto.request;


import lombok.Data;

@Data
public class TodoRequestDto {
    private String id;
    private String title;
    private String description;
    private boolean isCompleted;
}

