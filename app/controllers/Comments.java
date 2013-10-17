package controllers;



import play.*;
import play.mvc.*;
import controllers.*;

@Check("admin")
@With(Secure.class)
public class Comments extends CRUD {

}
