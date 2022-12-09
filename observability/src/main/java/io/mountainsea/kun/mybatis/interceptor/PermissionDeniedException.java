package io.mountainsea.kun.mybatis.interceptor;

/**
 * 权限错误异常类
 *
 * @author : ShaoHongLiang
 * @date : 2022/11/18
 */
public class PermissionDeniedException extends RuntimeException{

    public PermissionDeniedException(String message) {
        super(message);
    }
}
