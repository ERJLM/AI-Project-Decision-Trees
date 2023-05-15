import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
    Node parent;
    String label;
    String branch;
    List<Node> children;
    Map<String, Map<String,String>> examples = new HashMap<>();

    Node(String l, String b, Node p){
        label = l;
        children = new ArrayList<>();
        parent = p;
        branch = b;
       }
       Node(List<Node> c, String l){
        label = l;
        children = c;
        parent = null;
        branch = "";
       }

    Node(String l){
     label = l;
     children = new ArrayList<>();
     parent = null;
     branch = "";
    }
    Node(){
        label = "";
        children = new ArrayList<>();
        parent = null;
        branch = "";
       }
    
    String getLabel(){
        return label;
    }
    String getBranch(){
        return branch;
    }

    List<Node> getChildren(){
        return children;
    }

    Map<String, Map<String,String>> getExamples(){
        return examples;
    }
    Node getParent(){
        return parent;
    }
    

    void setLabel(String l){
        label = l;
    }

    void setExamples(Map<String, Map<String,String>> ex){
        examples = ex;
    }

    void setChildren(List<Node> c){
        children = c;
    }

    void setBranch(String b){
        branch = b;
    }

    void setParent(Node p){
        parent = p;
    }

    
}
