package Common_pkg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Users {
public String UserID,username,password,UserType;

Paths DataPath = new Paths();


public Users(){}

public Users(String UserID,String uname, String pwd,String utype){
    this.UserID = UserID;
    this.username = uname;
    this.password = pwd;
    this.UserType = utype;
}

public String InsertFormat(){
    return String.join(",", UserID,username,password,UserType);
} 

public String DisplayFormat(){
    return "UserID:   "+UserID+"    Username:   "+username+"    Password: "+password + "    UserType:"+UserType;
}

public void addUser() {
     try(BufferedWriter writer = new BufferedWriter(new FileWriter(DataPath.users_path,true))){
         writer.write(InsertFormat());
         writer.newLine();
     }
     catch(Exception e){}
}

public List<Users> loadAllUsers(String path){
    List<Users> usersinfo = new ArrayList<>();  
     try{
         BufferedReader reader = new BufferedReader(new FileReader(path));
         String line = reader.readLine();
                  
          while (line!=null) {
             String userInfo[] =line.split(","); 

             Users user = new Users(userInfo[0],userInfo[1],userInfo[2],userInfo[3]);
                
             line = reader.readLine();
             
             usersinfo.add(user);
         }
        }
     catch(Exception e){}

return usersinfo;
}
}
