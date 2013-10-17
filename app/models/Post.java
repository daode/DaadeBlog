package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Post extends Model {
	
	@Required
	public String title;
	@Required
	public Date postedAt;

	@Lob
    @Required
    @MaxSize(10000)
	public String content;

	@ManyToOne
	@Required
	public User author;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	public List<Comment> comments;

	@ManyToMany(cascade = CascadeType.PERSIST)
	public Set<Tag> tags;

	public Post(User author, String title, String content) {
		this.comments = new ArrayList<Comment>();
		this.tags = new TreeSet<Tag>();
		this.author = author;
		this.title = title;
		this.content = content;
		this.postedAt = new Date();
	}

	/**
	 * Retrieves the updates post with new comment.
	 * 
	 * @param author
	 *            the author of the comment.
	 * @param content
	 *            the content of the comment.
	 * @return updated post.
	 */
	public Post addComment(String author, String content) {
		Comment lComment = new Comment(this, author, content).save();
		this.comments.add(lComment);
		return this;
	}

	/**
	 * This method lets us navigate between pages
	 * 
	 * @return
	 */
	public Post previous() {
		return Post.find("postedAt < ? order by postedAt desc", postedAt)
				.first();
	}

	/**
	 * This method lets us navigate between pages
	 * 
	 * @return
	 */
	public Post next() {
		return Post.find("postedAt > ? order by postedAt asc", postedAt)
				.first();
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public Post tagItWith(String name) {
		tags.add(Tag.findOrCreateByName(name));
		return this;
	}
	
	/**
	 * 
	 * @param tag
	 * @return
	 */
	public static List<Post> findTaggedWith(String tag) {
		List<Post> taggedWith = Post
				.find("select distinct p from Post p join p.tags as t where t.name = ?",
						tag).fetch();
		return taggedWith;
	}
}
