
package Common_pkg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Item {
    public String itemId;
    public String name;
    public String description;
    public String category;
    public String status;
    public String location;
    public String reporterId;
    public String contactInfo;
    public LocalDate dateReported;
    String mData_path="src\\Common_pkg\\items.txt";
    public String ownerId;

   public Item(){}; 
    public  Item(String itemId,String name,String description,String category,String status,
            String location,String reporterId,String contactInfo,LocalDate dateReported){
        this.itemId=itemId;
        this.name=name;
        this.description=description;
        this.category=category;
        this.status=status;
        this.location=location;
        this.reporterId=reporterId;
        this.contactInfo=contactInfo;
        this.dateReported = LocalDate.now();
    }

   public String InsertFormat(){
        return String.join(",",itemId,name,description,category,
                status,location,reporterId,contactInfo);
}
     public String DisplayFormat(){
        return "\tItemID: "+itemId+"\tName: "+name+"\tDescription: "+description
                +"\tCategory:  "+category+"\tStatus:   "+status+"\tLocation:  "
                +location+"\tReporterId: "+reporterId+"\tContact Info: "+contactInfo+"\tDate: "+LocalDate.now();
    }
      public void addItem(){
   
     try(BufferedWriter writer = new BufferedWriter(new FileWriter(mData_path,true))){
         
         writer.write(InsertFormat());
         writer.newLine();
     }
     catch(Exception e){}
      }
      
     public List<Item> loadAllItems(String path){
    List<Item> Itemsinfo = new ArrayList<>();  
     try{
         BufferedReader reader = new BufferedReader(new FileReader(path));
         String line = reader.readLine();
                  
          while (line!=null) {
             String mInfo[] =line.split(","); 

          Item user = new Item(mInfo[0], mInfo[1], mInfo[2],  mInfo[3],
                  mInfo[4],mInfo[5],mInfo[6],mInfo[7],LocalDate.now());
                
             line = reader.readLine();
             
             Itemsinfo.add(user);
         }
        }
     catch(Exception e){}

return Itemsinfo;
} 

       public void DeleteItem(String mID){
         List<Item> items = loadAllItems(mData_path);
         items.removeIf(item->item.itemId.equals(mID));
         
         saveItems(items,mData_path);
    }
     private void saveItems(List<Item> items, String mData_path) {
                   
     try(BufferedWriter writer = new BufferedWriter(new FileWriter(mData_path))){
         for(Item item:items){
         writer.write(item.InsertFormat());
         writer.newLine();
     }
    }
     catch(Exception e){}
     
    } 
     
      public void UpdateItem(String mID, String field, String fieldNewValue) {
          List<Item> items = loadAllItems(mData_path);  
          for(Item item:items){
              if(item.itemId.equalsIgnoreCase(mID)){
                  switch(field.toLowerCase()){
                        case"name": item.name =fieldNewValue;
                          break;
                        case"description": item.description =fieldNewValue;
                          break; 
                        case"category": item.category =fieldNewValue;
                          break;
                        case"status": item.status =fieldNewValue;
                          break;
                        case"location": item.location =fieldNewValue;
                          break;
                        case"reporterId": item.reporterId =fieldNewValue;
                          break;
                        case"contactInfo": item.contactInfo =fieldNewValue;
                          break;   
              }
      saveItems(items,mData_path);
              }
          }
      }
  
}


      
       
   
  



