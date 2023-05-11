import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tree {
    Node root;

    Tree(Map<String, Map<String,String>> examples, String targetAttribute, Set<String> attributes){
        root = ID3(examples, targetAttribute, attributes);
    }

    Node getRoot(){
        return root;
    }

    Node ID3(Map<String, Map<String,String>> examples, String targetAttribute, Set<String> attributes) {
        Node root = new Node();
        if(attributes.isEmpty()) return root;
        //System.out.println(examples);
        Set<String> l = examples.keySet();
        String x = l.iterator().next();
         //System.out.println(attributes);
        //System.out.println(calculateEntropy(examples, "Pat", targetAttribute) + " Hunny");
        String example = examples.get(x).get(targetAttribute);
        //System.out.println(calculateEntropy(examples, "Pat", targetAttribute) + " ENTRO");
        // Check if all types in the target attribute are the same
        if (isPositive(examples, targetAttribute)) {
            root.label = examples.get(x).get(targetAttribute);
            return root;
        }
        // Check if the list of attributes is empty
       
        if(attributes.isEmpty() || attributes.contains(null)){
            root.label = getMostCommonValue(examples,targetAttribute, targetAttribute, targetAttribute);
            return root;
        }
        else{
        // Find the attribute that best classifies examples
        Node bestAttribute = findBestAttribute(examples, attributes, targetAttribute);
        root = bestAttribute;
        //System.out.println(bestAttribute.getLabel() + "HHH");
        List<String> attributeValues = getAttributeValues(examples, bestAttribute.label);
       
        
        for (String value : attributeValues) {
            
            Map<String, Map<String, String>> subset = getSubset(examples, bestAttribute.label, value);
           // if(bestAttribute.label.equals("Hun") && value.equals("Yes")) System.out.println(subset);
            //If all the values are equal
            System.out.println(subset);
            if(isSame(examples, bestAttribute.label, value, targetAttribute)){
                Node leafNode = new Node(getMostCommonValue(examples, bestAttribute.label, value, targetAttribute));
                //leafNode.label = value;
                root.children.add(leafNode);
            } else {
                Set<String> remainingAttributes = new HashSet<>(attributes);
                remainingAttributes.remove(bestAttribute.label);
                Node subtree = ID3(subset, targetAttribute, remainingAttributes);
                subtree.label = findBestAttribute(subset, remainingAttributes, targetAttribute).getLabel();
                root.children.add(subtree);
            }
        }
    }
        return root;
    }

    private boolean isPositive(Map<String, Map<String, String>> subset, String target) {
        String c = "";
        for(String id: subset.keySet()){
         c = subset.get(id).get(target);
        }
       
          //System.out.println(c + "JJJJ");
        for(String id: subset.keySet()){
           
        
                if(!subset.get(id).get(target).equals(c)) return false;
             
            
          }
        return true;
    }

    private boolean isSame(Map<String, Map<String, String>> subset, String attribute, String value,String target) {
        String c = "";
       
        for(String id: subset.keySet()){
            String temp  = subset.get(id).get(attribute);
            if(temp.equals(value)) c = subset.get(id).get(target);
         }
          //System.out.println(c + "JJJJ");
        for(String id: subset.keySet()){
           
             if(subset.get(id).get(attribute).equals(value)){
                if(!subset.get(id).get(target).equals(c)) return false;
             }
            
          }
        return true;
    }

    private boolean onlyClass(Map<String, Map<String, String>> examples, String targetAttribute) {
        for(String id: examples.keySet()){
          for(String i : examples.get(id).keySet()){
                 if(!i.equals("Class")) return false;
          }
        }
        return true;
    }

    private String getMostCommonValue(Map<String, Map<String,String>> examples,String attribute, String value, String target) {
        Map<String, Integer> valueCount = new HashMap<>();
        for (String id: examples.keySet()) {
            String targetValue =  examples.get(id).get(target);
            if(examples.get(id).get(attribute).equals(value))valueCount.put(targetValue, valueCount.getOrDefault(targetValue, 0) + 1);
        }
        String mostCommonValue = "";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : valueCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostCommonValue = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        //System.out.println(mostCommonValue + " MOST");
        return mostCommonValue;
    }

    

    public Node findBestAttribute(Map<String, Map<String,String>> examples, Set<String> attributes, String target) {
        double minEntropy = Double.POSITIVE_INFINITY;
        Node bestAttribute = new Node();
    
        for (String attribute : attributes) {
            double attributeEntropy = calculateEntropy(examples, attribute, target);
            if (attributeEntropy < minEntropy) {
                minEntropy = attributeEntropy;
                bestAttribute.setLabel(attribute);
            }
            else if(attributeEntropy == minEntropy){
                if(getAttributeValues(examples, attribute).size() < getAttributeValues(examples, bestAttribute.label).size()) 
                           bestAttribute.setLabel(attribute);           
            }
        }
    
        return bestAttribute;
    }
    
    private double calculateEntropy(Map<String, Map<String, String>> examples, String attribute, String target) {
        int totalCount = examples.keySet().size();
        Map<String, Integer> attributeValueCounts = new HashMap<>();
        Map<String, Map<String, Integer>> attributeTargetCounts = new HashMap<>();
        
        for (String id : examples.keySet()) {
            String attributeValue = examples.get(id).get(attribute);
            String targetValue = examples.get(id).get(target);
            
            if (!attributeValueCounts.containsKey(attributeValue)) {
                attributeValueCounts.put(attributeValue, 0);
                attributeTargetCounts.put(attributeValue, new HashMap<>());
            }
            attributeValueCounts.put(attributeValue, attributeValueCounts.get(attributeValue) + 1);
            
            Map<String, Integer> targetValueCounts = attributeTargetCounts.get(attributeValue);
            if (!targetValueCounts.containsKey(targetValue)) {
                targetValueCounts.put(targetValue, 0);
            }
            targetValueCounts.put(targetValue, targetValueCounts.get(targetValue) + 1);
        }
        
        double entropy = 0.0;
        for (String attributeValue : attributeValueCounts.keySet()) {
            double attributeValueProbability = (double) attributeValueCounts.get(attributeValue) / totalCount;
            double targetValueProbability = 0.0;
            
            Map<String, Integer> targetValueCounts = attributeTargetCounts.get(attributeValue);
            for (String targetValue : targetValueCounts.keySet()) {
                double targetValueFraction = (double) targetValueCounts.get(targetValue) / attributeValueCounts.get(attributeValue);
                if (targetValueFraction != 0) {
                    targetValueProbability -= targetValueFraction * Math.log(targetValueFraction) / Math.log(2);
                }
            }
            
            entropy += attributeValueProbability * targetValueProbability;
        }
        
        return entropy;
    }
    

    private double calculateEntropy1(Map<String, Map<String, String>> examples, String attribute) {
        double p = 0, n = 0;
        double upYes = 0, upNo = 0, down = 0, newDown = 0;
        List<String> s = getAttributeValues(examples, attribute);
        double entropy = 0.0;
        for (String x : s) {
            upYes = 0; upNo = 0; down = 0; newDown = 0;
    
            for (String id : examples.keySet()) {
                if (examples.get(id).containsKey(attribute)) {
                    if (examples.get(id).get(attribute).equals(x)) {
                        if (examples.get(id).get("Class").equals("Yes")) upYes++;
                        else upNo++;
                        down++;
                    }
                }
            }
            if(down == 0) continue;
            //System.out.println(down + " Down " + upNo + " UpNO " + upYes );
            p = upYes / down;
            n = upNo / down;
            if (p != 0 && n != 0) {
                
                double temp = -p * Math.log(p) / Math.log(2) - n * Math.log(n) / Math.log(2);
                newDown = examples.keySet().size();
                entropy += (down / newDown) * temp;
                //System.out.println(entropy);
            }
        }
        
        return entropy;
    }
    
    
    private static List<String> getAttributeValues(Map<String, Map<String,String>> examples, String attribute) {
        List<String> values = new ArrayList<>();

        for ( String id : examples.keySet()) {
            String value = examples.get(id).get(attribute);
            if (!values.contains(value)) {
                values.add(value);
            }
        }
        return values;
    }

    private static Map<String, Map<String,String>> getSubset(Map<String, Map<String,String>> examples, String attribute, String value) {
        Map<String, Map<String,String>> subset = new HashMap<>();
        Map<String, Map<String,String>> aux = new HashMap<>();
        for (Map.Entry<String,Map<String,String>> entry : examples.entrySet()){
            aux.put(entry.getKey(), new HashMap<String,String>(entry.getValue()));
        }
        HashSet<String> h = new HashSet<>();
        //System.out.println(examples + " AA " + value);
        for (String id : examples.keySet()) {
          // System.out.println(examples + " WALLA " );
            //System.out.println(examples);
            
            //System.out.println(attribute + "HHHH");
            
            if(examples.get(id).get(attribute).equals(value)){ 
                Map<String,String> temp = aux.get(id);
                subset.put(id, temp);
            
            //subset.get(id).put(value, examples.get(id).get("Class"));
            
            //System.out.println(subset);
            subset.get(id).remove(attribute);
        }
        
             
        }
        //if(attribute.equals("Pat")) System.out.println(subset);
        return subset;
    }

}
