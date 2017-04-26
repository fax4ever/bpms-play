package it.redhat.demo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;

/**
 * Created by fabio.ercoli@redhat.com on 26/04/17.
 */

@Stateless
public class SecurityService {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityService.class);

    public void discoveryRoles() {

        Subject subject;

        try {
            subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
        } catch (PolicyContextException e) {
            throw new RuntimeException(e);
        }

        // no security context
        if (subject == null) {
            LOG.warn("no security context");
        }

        for (Principal principal : subject.getPrincipals()) {

            LOG.info("principal {}", principal);

            if (principal instanceof Group) {
                Group group = (Group) principal;
                Enumeration<? extends Principal> members = group.members();

                while (members.hasMoreElements()) {
                    Principal member = members.nextElement();
                    LOG.info("member {} of group {}", member, group);
                }
            }

        }

    }


}
