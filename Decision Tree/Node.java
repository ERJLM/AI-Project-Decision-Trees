import java.util.ArrayList;
import java.util.List;

public class Node {
    String label;
   
    List<Node> children;
    Node(String l){
     label = l;
     children = new ArrayList<>();
     
    }
    Node(){
        label = "";
        children = new ArrayList<>();
        
       }
    
    String getLabel(){
        return label;
    }

    void setLabel(String l){
        label = l;
    }
}
