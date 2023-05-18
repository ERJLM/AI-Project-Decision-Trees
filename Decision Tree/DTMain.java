import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class DTMain {

  
    

    public static void printTree(Node node, String indent) {
        System.out.println(indent + "<" + node.getLabel() + ">");
                
        for (Node child : node.getChildren()){
            System.out.print(indent + "    " + child.getBranch() + ": ");
            if (child.getChildren().isEmpty()) {
                System.out.println(child.getLabel() + " (" + (node.countAttributeValues(node.getExamples(), node.getLabel(), child.getBranch())) + ")");
            } else {
                System.out.println();
                printTree(child, indent + "    ");
            }
        }
    }
    
    
    public static void main(String[] args) throws FileNotFoundException {
          System.out.println("Enter the path for the dataset: ");
          Scanner scan = new Scanner(System.in);
          String f = scan.nextLine();
      
        File file = new File(f);

        if (!file.exists()) {
            System.out.println("The specified file does not exist.");
            scan.close();
            return;
        }

        Map<String, Map<String, String>> m = new LinkedHashMap<>();
        Scanner sc = new Scanner(file);
        sc.useDelimiter(",|\r\n");

        String[] headers = null;
        while (sc.hasNext()) {
            if (headers == null) {
                headers = sc.nextLine().split("[\\s,]+");
                //System.out.println(headers);
                continue;
            }
             
            String[] values = sc.nextLine().split("[\\s,]+");
            String id = values[0];
            Map<String, String> rowData = new LinkedHashMap<>();
            for (int i = 1; i < headers.length; i++) {
                rowData.put(headers[i], values[i]);
            }
            m.put(id, rowData);
        }

        sc.close();
        scan.close();
        // Print the map m for verification
        for (Map.Entry<String, Map<String, String>> entry : m.entrySet()) {
            //System.out.println("ID: " + entry.getKey());
            Map<String, String> rowData = entry.getValue();
            for (Map.Entry<String, String> rowEntry : rowData.entrySet()) {
                //System.out.println(rowEntry.getKey() + ": " + rowEntry.getValue());
            }
           // System.out.println("------------------------");
        }
        Queue<String> attributes = new ArrayDeque<>();
       
        LinkedList<String> list = new LinkedList<>();
       // System.out.println(queue);
        for(String key: m.keySet()){
            list = new LinkedList<>(m.get(key).keySet());
        for(String x: m.get(key).keySet()){
           
            if(!x.equals(list.peekLast())) attributes.add(x);
        }
        break;
    }
        String targetAttribute = list.peekLast();
      
        
        Tree t = new Tree(m, targetAttribute, attributes);
        Node root = t.getRoot();
        printTree(root, "");
       
    }
}
