package io.mountainsea.kun.observability.tracing;

import com.google.common.base.Stopwatch;
import lombok.Data;

/**
 * @author : ShaoHongLiang
 * @date : 2022/11/15
 */
@Data
public class TracingStatus {

    private String contextId;

    private Stopwatch stopwatch;
}
