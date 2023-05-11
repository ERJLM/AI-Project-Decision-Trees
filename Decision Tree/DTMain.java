import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class DTMain {
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

        Map<String, Map<String, String>> m = new LinkedHashMap<>();
        Scanner sc = new Scanner(file);
        sc.useDelimiter(",|\r\n");

        String[] headers = null;
        while (sc.hasNext()) {
            if (headers == null) {
                headers = sc.nextLine().split(",");
                //System.out.println(headers);
                continue;
            }
             
            String[] values = sc.nextLine().split(",");
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
            System.out.println("ID: " + entry.getKey());
            Map<String, String> rowData = entry.getValue();
            for (Map.Entry<String, String> rowEntry : rowData.entrySet()) {
                System.out.println(rowEntry.getKey() + ": " + rowEntry.getValue());
            }
            System.out.println("------------------------");
        }
        Set<String> attributes = new HashSet<>();
       
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
        //System.out.println(m);
       // System.out.println(attributes);
        
        Tree t = new Tree(m, targetAttribute, attributes);
        Node n = t.getRoot();
        System.out.println(n.getLabel() + " ROOT ");
        for(Node x: n.children){
            System.out.println(x.getLabel());
            for(Node y: x.children){
                System.out.println(x.getLabel() + " Child " + y.getLabel());
                for(Node z: y.children){
                    System.out.println(y.getLabel() + " Child " + z.getLabel());
                   
                }
            }
        } 
        //System.out.println(m);
    }
}
