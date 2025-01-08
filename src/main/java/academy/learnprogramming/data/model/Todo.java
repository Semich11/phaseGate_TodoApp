package academy.learnprogramming.data.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class Todo {
    @Id
    private String id;
    private String title;
    private String Description;
    private Date createdAt;
    private boolean isCompleted;
    private String userId;

}
