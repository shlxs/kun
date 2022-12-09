package io.mountainsea.kun.util.http;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;

/**
 * @author : ShaoHongLiang
 * @date : 2022/11/18
 */
public class ContentTypeUtils {
    public static boolean isCompatibleWith(String contentType, MediaType compatibleMediaType) {
        if(StringUtils.isEmpty(contentType)){
            return false;
        }else {
            MediaType mediaType = MediaType.parseMediaType(contentType);
            return compatibleMediaType.isCompatibleWith(mediaType);
        }
    }

    public static boolean isNotCompatibleWith(String contentType, MediaType compatibleMediaType) {
        return !isCompatibleWith(contentType, compatibleMediaType);
    }

}
