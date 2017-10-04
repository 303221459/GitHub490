import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseAccess {

	public static void main(String[] args) throws SQLException, ClassNotFoundException{
		// TODO Auto-generated method stub
		System.out.println("Testing database access");
		String connectStr="jdbc:mysql://localhost:3306/fooddb";
		//database Username
		String username = "root";
		//database Password
		String password="csci490pass";
		String driver="com.mysql.jdbc.Driver";
		Connection con=DriverManager.getConnection(connectStr,username,password);
		Statement stmt = con.createStatement();
		ResultSet rs=stmt.executeQuery("SELECT id,name,category FROM ingredient");
		
		
		String result="";
		while(rs.next()){
			int theID=rs.getInt("id");
			String theName=rs.getString("name");
			String theCategory=rs.getString("category");
			result+="id:"+theID+",name:"+theName+"("+theCategory+")"+"\n";
		}
		System.out.println("Results:\n\n"+result);
		System.out.println("Testing database...Finished");
	}

}
