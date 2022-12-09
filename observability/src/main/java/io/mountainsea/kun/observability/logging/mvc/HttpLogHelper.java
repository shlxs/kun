package io.mountainsea.kun.observability.logging.mvc;

import cn.hutool.core.io.IoUtil;
import io.mountainsea.kun.util.http.ContentTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 遇到404错误时，会再经过一次/error请求，而且返回JSON通过BasicErrorController渲染，无法统一在@RestControllerAdvice中处理
 *
 * 测试以下几类场景
 * 1、Content-Type错误
 * 2、× GET、POST方法错误 GET http://localhost:8080/company/getCompanyNameAndId?UserId=1
 * 3、√ 路径错误  GET http://localhost:8080/actuator2
 * 4、参数校验错误
 * 5、上传文件
 * @author : ShaoHongLiang
 * @date : 2022/11/15
 */
@Slf4j
public class HttpLogHelper {

    private final static String SHELL_START = "\t";
    private final static String SHELL_LINE = " \\\n\t";

    /**
     * 返回被打印的Body部分流
     * @param request
     * @return
     * @throws IOException
     */
    public static InputStream printRequestInfo(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        //
        sb.append(SHELL_START).append("curl -X ").append(request.getMethod());
        sb.append(SHELL_LINE).append("'").append(request.getRequestURL())
                .append(Objects.nonNull(request.getQueryString()) ? "?" + request.getQueryString() : "")
                .append("'");

        request.getHeaderNames().asIterator().forEachRemaining(headName -> {
            sb.append(SHELL_LINE).append("-H '").append(headName).append(":").append(request.getHeader(headName)).append("'");
        });


        ByteArrayInputStream bis = null;
        if(ContentTypeUtils.isNotCompatibleWith(request.getContentType(), MediaType.MULTIPART_FORM_DATA)){
            BufferedReader bufferedReader = IoUtil.getReader(request.getInputStream(), StandardCharsets.UTF_8);
            String line;
            StringBuilder body = new StringBuilder();
            do {
                line = bufferedReader.readLine();
                if(line != null){
                    if(body.length() > 0){
                        body.append("\n");
                    }
                    body.append(line);
                }
            } while (StringUtils.hasLength(line));

            // 上传超大文件（例如10G）时，会产生大对象，截止读取到第一个空行
            if(!ObjectUtils.isEmpty(body)){
                sb.append(SHELL_LINE).append("-d '").append(body).append("'");
                bis = new ByteArrayInputStream(body.toString().getBytes(StandardCharsets.UTF_8));
            }
        }

        log.info("\n当前请求: 来源IP {}\n{}", request.getRemoteAddr(), sb);

        return bis;
    }
}
