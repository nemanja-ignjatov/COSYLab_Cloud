package at.cosylab.cloud.acam.commons.filters;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CloudConstants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebAppCORSFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(WebAppCORSFilter.class);

    public WebAppCORSFilter() {
        log.debug("WebAppCORSFilter init");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        log.debug("WebAppCORSFilter");

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Expose-Headers", CloudConstants.HEADER_NAME_SET_TOKEN);
        response.setHeader("Access-Control-Allow-Headers",
                "Content-Type, Accept, " + CloudConstants.HEADER_NAME_TOKEN);

        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}
