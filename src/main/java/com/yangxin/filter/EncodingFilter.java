package com.yangxin.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

/**
 * 字符编码过滤器：统一把请求和响应设置为 UTF-8，彻底解决中文乱码问题。
 *
 * <p>{@code @WebFilter("/*")} 表示拦截本项目下所有请求，
 * 这样每个 Servlet 里就不必再重复写 {@code setCharacterEncoding} 了。</p>
 *
 * @author 杨鑫（学号：25040500）
 */
@WebFilter(filterName = "encodingFilter", urlPatterns = "/*")
public class EncodingFilter implements Filter {

    /** 统一使用的字符集 */
    private static final String ENCODING = "UTF-8";

    /**
     * 过滤器初始化（本项目无需额外处理）。
     *
     * @param filterConfig 过滤器配置
     */
    @Override
    public void init(FilterConfig filterConfig) {
        // 无需初始化参数
    }

    /**
     * 在执行 Servlet 之前，先把请求与响应的编码都设为 UTF-8。
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param chain    过滤器链
     * @throws IOException      IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // ★ 设置请求编码：影响 getParameter() 读取中文表单值的解码方式
        request.setCharacterEncoding(ENCODING);
        // ★ 设置响应编码：影响输出到浏览器的中文编码
        response.setCharacterEncoding(ENCODING);

        // 放行，交给下一个过滤器或目标 Servlet
        chain.doFilter(request, response);
    }

    /**
     * 过滤器销毁（本项目无需额外处理）。
     */
    @Override
    public void destroy() {
        // 无需释放资源
    }
}
