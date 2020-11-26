package th.ku.memo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Memo implements Serializable {
    private int id;
    private String text;
    private String colorBackground;
    private String colorText;
    private String alignment;
    private int textSize;
    private LocalDateTime timeCreate;
    private LocalDateTime timeUpdate;
}
