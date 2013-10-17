import java.util.List;
import java.util.Map;

import org.junit.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {
	
	/**
	 * Initialize DB before use.
	 */
	@Before
    public void setup(){
    	Fixtures.deleteAll();
    }
	
	
	/**
	 * This method tests the creation of user in DB.
	 */
    @Test
    public void createAndRetrieveUser(){
    	//Create a new user and save it
    	new User("bob@gmail.com", "secret", "Bob").save();
    	
    	//Retrieve the user with email address bob@gmail.com
    	User bob = User.find("byFullname", "Bob").first();
    	
    	//Test
    	assertNotNull(bob);
    	assertEquals("bob@gmail.com", bob.email);
    }
    
    
    /**
     * This method tests connection with created user.
     */
    @Test
    public void tryConnectAsUser() {
        // Create a new user and save it
        new User("bob@gmail.com", "secret", "Bob").save();
        
        // Test 
        assertNotNull(User.connect("bob@gmail.com", "secret"));
        assertNull(User.connect("bob@gmail.com", "badpassword"));
        assertNull(User.connect("tom@gmail.com", "secret"));
    }
    
    
    
    /**
     * This method creates and tests a User that posts a comment.
     */
    @Test
    public void createPost(){
    	
    	//Create a new user and save it
    	User bob = new User("bob@gmail.com", "secret", "Bob").save();
    	
    	// Create a new post
    	new Post(bob, "My First Post", "Hello world!").save();
    	
    	//Test that the post has been created
    	assertEquals(1,Post.count());
    	
    	//Retrieve all post created by Bob
    	List<Post> bobPosts = Post.find("byAuthor", bob).fetch();
    	
    	//TEsts
    	assertEquals(1, bobPosts.size());
    	Post firstPost = bobPosts.get(0);
    	assertNotNull(firstPost);
    	assertEquals(bob, firstPost.author);
    	assertEquals("My First Post", firstPost.title);
    	assertEquals("Hello world!", firstPost.content);
    	assertNotNull(firstPost.postedAt);
    	
    }
    
    
    @Test
    public void postComments(){
    	//Create a new user and save it
    	User bob = new User("bob@gmail.com", "secret", "Bob").save();
    	
    	//Create a new Post
    	Post bobPost = new Post(bob, "My first post", "Hello world").save();
    	
    	//Post a first comment
    	new Comment(bobPost, "Jeff", "Nice post").save();
    	new Comment(bobPost, "Tom", "I knew that!").save();
    	
    	//Retrieves all comments
    	List<Comment> bobPostComments = Comment.find("byPost", bobPost).fetch();
    	
    	//Tests
    	assertEquals(2, bobPostComments.size());
    	
    	Comment firstComment = bobPostComments.get(0);
    	assertNotNull(firstComment);
    	assertEquals("Jeff", firstComment.author);
    	assertEquals("Nice post", firstComment.content);
    	assertNotNull(firstComment.postedAt);
    	
    	Comment secondComment = bobPostComments.get(1);
    	assertNotNull(secondComment);
    	assertEquals("Tom", secondComment.author);
    	assertNotNull("I knew that!", secondComment.content);
    	assertNotNull(secondComment.postedAt);
    	
    	
    	
    }
    
    
    @Test
    public void useTheCommentsRelation(){
    	//Create a new user and save it
    	User bob = new User("bob@gmail.com", "secret", "Bob").save();
    	
    	//Create a new Post
    	Post bobPost = new Post(bob, "My first post", "Hello world").save();
    	
    	//Post a first comment
    	bobPost.addComment("Jeff", "Nice post");
    	bobPost.addComment("Tom", "I knew that!");
    	
    	// Count objetcs
    	assertEquals(1, User.count());
    	assertEquals(1, Post.count());
    	assertEquals(2, Comment.count());
    	
    	//Retrive Bob's Post
    	bobPost = Post.find("byAuthor", bob).first();
    	assertNotNull(bobPost);
    	
    	//Navigate to comments
    	assertEquals(2,bobPost.comments.size());
    	assertEquals("Jeff", bobPost.comments.get(0).author);
    	
    	//Delete the post
    	bobPost.delete();
    	
    	//Check that all comments have been deleted
    	assertEquals(1, User.count());
    	assertEquals(0, Post.count());
    	assertEquals(0, Comment.count());
    	
    }
    
    
    @Test
    public void fullTest(){
    	
    	Fixtures.loadModels("data.yml");
    	
    	// count things
    	assertEquals(2, User.count());
    	assertEquals(3, Post.count());
    	assertEquals(3, Comment.count());
    	
    	//Try to connect as users
    	assertNotNull(User.connect("bob@gmail.com", "secret"));
    	assertNotNull(User.connect("jeff@gmail.com", "secret"));
    	assertNull(User.connect("jeff@gmail.com", "badpwd"));
    	assertNull(User.connect("amine@gmail.com", "secret"));
    	
    	//Find all bob's posts
    	List<Post> bobPosts = Post.find("author.email", "bob@gmail.com").fetch();
    	assertEquals(2, bobPosts.size());
    	
    	//Find all comments related to bob's posts
    	List<Comment> bobComments = Comment.find("post.author.email", "bob@gmail.com").fetch();
    	assertEquals(3, bobComments.size());
    	
    	//Find the most recent post
    	Post frontPost = Post.find("order by postedAt desc").first();
    	assertNotNull(frontPost);
    	assertEquals("About the model layer", frontPost.title);
    	
    	//Check that this post has two comments
    	assertEquals(2, frontPost.comments.size());
    	
    	//Post a new comment
    	frontPost.addComment("Jim", "Hello guys");
    	assertEquals(3, frontPost.comments.size());
    	assertEquals(4, Comment.count());
    	
    	
    	
    }
    
    /**
     * 
     */
    @Test
    public void testTags(){
    	//Create User
    	User bob = new User("bob@gmail.com", "secret", "Bob").save();
    	
    	//Create Posts
    	Post bobPost = new Post(bob, "My first post","Hello world");
    	Post anotherBobPost = new Post(bob, "Hop", "Hello world");
    	
    	assertEquals(0, Post.findTaggedWith("red").size());
    	
    	bobPost.tagItWith("red").tagItWith("blue").save();
    	anotherBobPost.tagItWith("red").tagItWith("green").save();
    	
    	assertEquals(2, Post.findTaggedWith("red").size());
    	assertEquals(1, Post.findTaggedWith("blue").size());
    	assertEquals(1, Post.findTaggedWith("green").size());
    	
    	List<Map> cloud = Tag.getCloud();
    	assertEquals(
    	    "[{tag=Red, pound=2}, {tag=Blue, pound=1}, {tag=Green, pound=1}]", 
    	    cloud.toString()
    	);
    	
    	
    
    
    }
    
    
    

}
