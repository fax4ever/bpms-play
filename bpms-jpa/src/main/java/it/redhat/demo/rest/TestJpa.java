package it.redhat.demo.rest;

import it.redhat.demo.entity.MemoryEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Date;

/**
 * Created by fabio.ercoli@redhat.com on 04/05/17.
 */

@Path("")
@Stateless
public class TestJpa {

    @PersistenceContext(unitName = "test-jpa")
    private EntityManager em;

    @GET
    public String ciao() {
        return "ciao " + em;
    }

    @POST
    public void createMemory() {

        MemoryEntity memo = new MemoryEntity();
        memo.setLocation("LOC");
        memo.setMoment(new Date());
        memo.setNote("Notes");
        memo.setSubject("SUB");

        em.persist(memo);

    }


}
