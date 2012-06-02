package net.homelinux.mickey.dia.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteHostFilter implements Filter {
    protected static Logger log =
        LoggerFactory.getLogger(RemoteHostFilter.class);
    private static final String COMMUFA = "commufa.jp",
        COMMUFA_14_132 = "14.132.", COMMUFA_115_36 = "115.36.",
        COMMUFA_115_37 = "115.37.", COMMUFA_115_38 = "115.38.",
        COMMUFA_115_39 = "115.39.", COMMUFA_118_104 = "118.104.",
        COMMUFA_118_105 = "118.105.", COMMUFA_118_106 = "118.106.",
        COMMUFA_123_1 = "123.1.", COMMUFA_123_48 = "123.48.",
        COMMUFA_124_18 = "124.18.", COMMUFA_180_196 = "180.196.",
        COMMUFA_180_197 = "180.197.", COMMUFA_180_198 = "180.198.",
        COMMUFA_180_199 = "180.199.";

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
        throws IOException, ServletException {
        String remoteHost = ((HttpServletRequest) request).getRemoteHost();

        log.info("RemoteHost: {}", remoteHost);
        if ((remoteHost.endsWith(COMMUFA)
             || remoteHost.startsWith(COMMUFA_14_132)
             || remoteHost.startsWith(COMMUFA_115_36)
             || remoteHost.startsWith(COMMUFA_115_37)
             || remoteHost.startsWith(COMMUFA_115_38)
             || remoteHost.startsWith(COMMUFA_115_39)
             || remoteHost.startsWith(COMMUFA_118_104)
             || remoteHost.startsWith(COMMUFA_118_105)
             || remoteHost.startsWith(COMMUFA_118_106)
             || remoteHost.startsWith(COMMUFA_123_1)
             || remoteHost.startsWith(COMMUFA_123_48)
             || remoteHost.startsWith(COMMUFA_124_18)
             || remoteHost.startsWith(COMMUFA_180_196)
             || remoteHost.startsWith(COMMUFA_180_197)
             || remoteHost.startsWith(COMMUFA_180_198)
             || remoteHost.startsWith(COMMUFA_180_199))
            && Math.random() < .85) {
            throw new ServletException();
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
