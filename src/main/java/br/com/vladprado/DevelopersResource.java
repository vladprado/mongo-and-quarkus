package br.com.vladprado;

import java.time.LocalDate;
import java.util.List;

import br.com.vladprado.Developer.Developer;
import jakarta.inject.Inject;
import jakarta.nosql.document.DocumentTemplate;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("developers")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class DevelopersResource {
    
    @Inject
    DocumentTemplate template;

    @GET
    public List<Developer> listAll(@QueryParam("name") String name) {
        if(name == null) {
            return template.select(Developer.class).result();
        }

        return template.select(Developer.class)
                .where("name")
                .like(name)
                .result();
    }

    public record NewDeveloperRequest(String name, LocalDate birthday) {
    }

    @POST
    public Developer add(NewDeveloperRequest request) {
        var newDeveloper = Developer.newDeveloper(request.name(), request.birthday());
        return template.insert(newDeveloper);
    }

    @Path("{id}")
    @GET
    public Developer get(@PathParam("id") String id) {
        return template.find(Developer.class, id)
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
    }

    public record UpdateDeveloperRequest(String name, LocalDate birthday) {
    }

    @Path("{id}")
    @PUT
    public Developer update(@PathParam("id") String id, UpdateDeveloperRequest request) {
        var developer = template.find(Developer.class, id)
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));

        var updateDeveloper = developer.update(request.name(), request.birthday());
        return template.update(updateDeveloper);
    }

    @Path("{id}")
    @DELETE
    public void delete(@PathParam("id") String id) {
        template.delete(Developer.class, id);
    }
}
