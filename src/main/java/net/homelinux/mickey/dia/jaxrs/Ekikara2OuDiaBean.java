package net.homelinux.mickey.dia.jaxrs;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface Ekikara2OuDiaBean {
    @POST
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("/getOuDia")
    public Response getOuDia(@DefaultValue("1") @FormParam("processTables") String processTables,
                             @FormParam("lineNumber") String lineNumber,
                             @DefaultValue("0300") @FormParam("startTime") String startTime,
                             @DefaultValue("") @FormParam("day") String day,
                             @FormParam("reverse") boolean reverse,
                             @HeaderParam("Referer") String referer);
}
