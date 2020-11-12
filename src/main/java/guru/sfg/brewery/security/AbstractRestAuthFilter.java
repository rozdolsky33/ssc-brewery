package guru.sfg.brewery.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Setting up a Filter
 */
@Slf4j
public abstract class  AbstractRestAuthFilter extends AbstractAuthenticationProcessingFilter {

    public AbstractRestAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Request is to process authentication");
        }
        try {
            Authentication authResult = attemptAuthentication(request, response);

            if (authResult != null) {
                successfulAuthentication(request, response, chain, authResult);
            }else {
                chain.doFilter(request, response);
            }
        }catch (AuthenticationException e){
            log.error("Authentication Failed ", e);
            unsuccessfulAuthentication(request, response, e);
        }
    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        if (log.isDebugEnabled()) {
            log.debug("Authentication request failed: " + failed.toString(), failed);
            log.debug("Updated SecurityContextHolder to contain null Authentication");

        }
        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    @Override //  Authorization Filter
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        //extracting user name and password form the request
        String userName = getUsername(request);
        String password = getPassword(request);

        if (userName == null){
            userName ="";
        }
        if (password == null){
            password ="";
        }

        log.debug("Authenticating User: " + userName);

        // creating user name authentication token
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);

        if (!StringUtils.isEmpty(userName)) {
            // passing into authentication manager for it to do it's work - as far as authenticating
            return this.getAuthenticationManager().authenticate(token); //inMemory authentication
        }else {
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
        }

        SecurityContextHolder.getContext().setAuthentication(authResult); // getting the context setting the auth result - > establishes
        // authorization within the context of spring security - after we go through successful authentication

    }
    protected abstract String getPassword(HttpServletRequest request);


    protected abstract String getUsername(HttpServletRequest request);
}

