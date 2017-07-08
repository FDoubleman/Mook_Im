package net.qiujuer.web.italker.push.service;

import net.qiujuer.web.italker.push.entity.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by fmm on 2017/7/8.
 */
//127.0.0.0/api/account
@Path("/account")
public class AccountService {

    @GET
    @Path("/login")
    //http://localhost:8080/api/account/login
    public String get(){
        return " you get api !";
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User post(){
        User user =  new User();
        user.setName("小米");
        user.setAge("6");

        return user;
    }

}
