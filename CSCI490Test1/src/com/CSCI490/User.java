package com.CSCI490;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * This class is used for the user model and it can be used for different layers
 * @author Junkai
 *
 */

public class User implements Serializable{

	private static final long serialVersionUID=1L;
	
	private Long id;
	private String email;
	private String password;
	private String firstname;
	private String lastname;
	
	public Long getId(){
		return id;
	}
	
	public void setId(Long id){
		
		this.id=id;
	}
	
	
	public String getEmail() {
        return email;
    }

	public void setEmail(String email){
		this.email=email;
	}
	
	public String getPassword(){
		return password;
	}
	
	public void setPassword(String password){
		this.password=password;
		
	}
	
	public String getFirstname(){
		return firstname;
	}
	
	public void setFirstname(String firstname){
		this.firstname=firstname;
	}
	
	public String getLastname(){
		return lastname;
	}
	
	public void setLastname(String lastname){
		this.lastname=lastname;
	}
	/**
	 * 	overrides
	 *	each user have unique user id and email address
	 */
	
	@Override
	public boolean equals(Object other){
		
		return (other instanceof User) && (id!=null)
				?id.equals(((User)other).id)
				:(other==this);
	}
	
	/**
	 * The user if is unique. The same user ID should return the same hashCode
	 */
	
	@Override
	public int hashCode(){
		return (id!=null)
				?(this.getClass().hashCode()+id.hashCode())
				:super.hashCode();
	}
	
	/**
	 * return the string representation of the user
	 */
	@Override 
	public String toString(){
		
		return String.format("User[id=%d,emal=%s,firstname=%s,lastname=%s]",id,email,firstname,lastname);
	}
	
	
}
