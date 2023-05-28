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
    
    public static Map<String, Map<String, String>> fileToMap(File file) throws FileNotFoundException{
        Map<String, Map<String, String>> m = new HashMap<String, Map<String, String>>();
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
       
        
      return m;
    }
    
    public static void main(String[] args) throws FileNotFoundException {
          System.out.println("Enter the path for the dataset: ");
          Scanner scan = new Scanner(System.in);
          String f = scan.nextLine();
       // String filePath = args[0];
        File file = new File(f);

        if (!file.exists()) {
            System.out.println("The specified file does not exist.");
            scan.close();
            return;
        }
        long startTime = System.currentTimeMillis();
        Map<String, Map<String, String>> m = new LinkedHashMap<>();
        m = fileToMap(file);
        scan.close();
       
        Queue<String> attributes = new ArrayDeque<>();
       
        LinkedList<String> list = new LinkedList<>();
       
        for(String key: m.keySet()){
            list = new LinkedList<>(m.get(key).keySet());
        for(String x: m.get(key).keySet()){
           
            if(!x.equals(list.peekLast())) attributes.add(x);
        }
        break;
    }
        String targetAttribute = list.peekLast();
        
        Tree t = new Tree(m, targetAttribute, attributes);
        m = t.getData();
        Node root = t.getRoot();
        printTree(root, "");
        System.out.println();
        //The testTree method returns the percentage of success of the tree based in the examples
        System.out.println("Percentage of success: ");
        System.out.println(t.testTree(m));
        /*
        If you want to test the success rate of the tree with another examples 
        call the method "t.testTree(fileToMap(file))" where file is a csv file with the examples.
        */
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: \n" + elapsedTime + "ms");
    }
}

