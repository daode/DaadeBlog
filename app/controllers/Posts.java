package controllers;
 
import play.*;
import play.mvc.*;
import controllers.*;
 

@Check("admin")
@With(Secure.class)
public class Posts extends CRUD {
	
}
