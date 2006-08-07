/*
 * Copyright 2006 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.uportal.org/license.html
 */
package org.jasig.cas.client.web.filter;

import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.client.authorization.AuthorizationException;
import org.jasig.cas.client.authorization.CasAuthorizedDecider;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Simple filter that attempts to determine if someone is authorized to use the
 * system. Assumes that you are protecting the application with the
 * AuthenticationFilter such that the Assertion is set in the session.
 * <p>
 * If a user is not authorized to use the application, the response code of 403
 * will be sent to the browser.
 * <p>
 * This filter needs to be configured after both the authentication filter and
 * the validation filter.
 * 
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.0
 * @see CasAuthorizedDecider
 */
public final class CasAuthorizationFilter implements Filter {

    /**
     * Decider that determines whether a specified principal has access to the
     * resource or not.
     */
    private CasAuthorizedDecider decider;

    public void destroy() {
        // nothing to do here
    }

    public void doFilter(final ServletRequest servletRequest,
        final ServletResponse servletResponse, final FilterChain filterChain)
        throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final Assertion assertion = (Assertion) request.getSession()
            .getAttribute(AbstractCasFilter.CONST_ASSERTION);

        if (assertion == null) {
            throw new ServletException(
                "assertion session attribute expected but not found.");
        }

        final Principal principal = assertion.getPrincipal();

        final boolean authorized = this.decider
            .isAuthorizedToUseApplication(principal);

        if (!authorized) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            throw new AuthorizationException(principal.getId()
                + " is not authorized to use this application.");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void init(final FilterConfig filterConfig) throws ServletException {
        // nothing to do here

    }

    public void setDecider(final CasAuthorizedDecider decider) {
        this.decider = decider;
    }

    public void init() {
        CommonUtils.assertNotNull(this.decider,
            "the casAuthorizedDecider cannot be null.");
    }
}