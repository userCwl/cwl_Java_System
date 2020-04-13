package cwl.kill.server.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author long
 * @Date 2020/3/8 14:56
 */
@Data
@ToString
public class KillDto implements Serializable {

    @NotNull
    private Integer killId;

    private Integer userId;
}
