import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Tree {
    Node root;
    Map<String, Map<String,String>> data;
    Queue<String> attrib;
    String target;
    Tree(Map<String, Map<String,String>> examples, String targetAttribute, Queue<String> attributes){
        String id = "";
        //This loop gets the first id of the examples map
        for(String x : examples.keySet()){
            id = x;
            break;
        }
        //We have chosen 4 because it has a pretty good balance between percentage of sucess and size of the tree.
      if(examples.size() >= 100 || examples.get(id).size() >= 100)  examples = discretize(examples, attributes, 4);
        data = examples;
        attrib = attributes;
        target = targetAttribute;
       
        root = ID3(examples, attributes, targetAttribute);
        root.setExamples(examples);
    }


    Node getRoot(){
        return root;
    }

    Map<String, Map<String,String>> getData(){
        return data;
    }

    
    private Node ID3(Map<String, Map<String, String>> examples, Queue<String> attributes, String targetAttribute) {
        // check if all examples have the same classification
        if (isSame(examples, targetAttribute)) {
            return new Node(new ArrayList<>(), getMostCommonValue(examples, targetAttribute));
        }
        // check if the list of attributes is empty
        else if (attributes.isEmpty()) {
            return new Node(new ArrayList<>(), getMostCommonValue(examples, targetAttribute));
        }
        // else split by best attribute and handle subsets (including empty)
        else {
            Node bestAttribute = findBestAttribute(examples, attributes, targetAttribute);
            List<String> attributeValues = getAttributeValues(data, bestAttribute.getLabel());
            List<Node> children = new ArrayList<>();
           
           
            for (String value : attributeValues) {
                Node child = new Node();
                Map<String, Map<String, String>> subset = getSubset(examples, bestAttribute.getLabel(), value);
                if (subset.isEmpty()) {
                    child = new Node(new ArrayList<>(), getMostCommonValue(examples, targetAttribute));
                } else {
                    Queue<String> remainingAttributes = new ArrayDeque<>(attributes);
                    remainingAttributes.remove(bestAttribute.getLabel());
                    child.setExamples(subset);
                    
                    child = ID3(subset, remainingAttributes, targetAttribute);
                  
                }
                child.setBranch(value);
                children.add(child);
            }
            Node n = new Node(children, bestAttribute.getLabel());
            n.setExamples(examples);
            return n;
        }
    }

    public Map<String, Map<String, String>> discretize(Map<String, Map<String, String>> examples, Queue<String> attributes, int bins) {
        Map<String, Map<String, String>> discretizedData = new HashMap<>();
    
        // Iterate over each record in the dataset
        for (Map.Entry<String, Map<String, String>> recordEntry : examples.entrySet()) {
            String recordId = recordEntry.getKey();
            Map<String, String> originalAttributes = recordEntry.getValue();
            Map<String, String> discretizedAttributes = new HashMap<>();
    
            // Iterate over each attribute in the record
            for (Map.Entry<String, String> attributeEntry : originalAttributes.entrySet()) {
                String attributeName = attributeEntry.getKey();
                String attributeValue = attributeEntry.getValue();
                String discretizedValue;
    
                if (isNumeric(attributeValue) && allValuesNumeric(examples, attributeName)) {
                    // Discretize the attribute value
                    discretizedValue = discretizeValue(attributeValue, bins);
                } else {
                    discretizedValue = attributeValue;
                }
    
                // Store the discretized value in the map
                discretizedAttributes.put(attributeName, discretizedValue);
            }
    
            // Store the discretized attributes for the record
            discretizedData.put(recordId, discretizedAttributes);
        }
        //System.out.println(discretizedData);
        return discretizedData;
    }
    
    // Helper method to check if all values of an attribute are numeric
    private boolean allValuesNumeric(Map<String, Map<String, String>> examples, String attributeName) {
        for (String id : examples.keySet()) {
         String value = examples.get(id).get(attributeName);
            if (!isNumeric(value)) {
                return false;
            }
        }
        return true;
    }
    

    private static String discretizeValue(String value, int numBins) {
        double numericValue = Double.parseDouble(value);
        double binWidth = 10.0 / numBins;
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        double roundedBinWidth = Double.parseDouble(decimalFormat.format(binWidth));
        int binIndex = (int) Math.floor(numericValue / binWidth);

        // Calculate the interval for the discretized value
        float lowerBound = (float) (binIndex * roundedBinWidth);
        float upperBound = (float) ((binIndex + 1) * roundedBinWidth);
        String interval = "[" + lowerBound + ", " + upperBound + ")";
       // System.out.println(interval);
        return interval;
    }

    public String findAnswer(Map<String, String> example){
        if(root == null) return null;
        Node node = root;
       
        while(!node.getChildren().isEmpty()){
            for(Node x: node.getChildren()){
               
                if(example.get(node.getLabel()).equals(x.getBranch())){
                    node = x;
                    break;
                }
            }
        }
        return node.getLabel();
    }

    public String testTree(Map<String, Map<String,String>> examples){
        String result = "0%";
        int counter = 0, total = 0;
        double r = 0;
        for(String x: examples.keySet()){
            Map<String,String> m = examples.get(x);
         if(!m.get(target).equals(findAnswer(m))){

         }
         else{
            counter++;
         }
         total++;
        }
        r = (double) counter/total;
        r *= 100;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        r = Double.parseDouble(decimalFormat.format(r));
        result = Double.toString(r);
        result += "%";
        return result;
    }
    

    public boolean containsNumericalValues(Map<String, Map<String, String>> examples, String attribute) {
        for (Map<String, String> example : examples.values()) {
            String value = example.get(attribute);
            if (isNumeric(value)) {
                return true;
            }
        }
        return false;
    }
    
    
   
    public boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        //System.out.println(str);
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c) && c != '.' && c != '-') {
                return false;
            }
        }
        return true;
    }

    private boolean isSame(Map<String, Map<String, String>> subset, String target) {
        String c = "";
        for(String id: subset.keySet()){
         c = subset.get(id).get(target);
         break;
        }
       
         
        for(String id: subset.keySet()){
                if(!subset.get(id).get(target).equals(c)) return false;
          }
        return true;
    }

    public static String getMostCommonValue(Map<String, Map<String, String>> examples, String attribute) {
        Map<String, Integer> valueCounts = new HashMap<>();
        for (String key : examples.keySet()) {
            Map<String, String> example = examples.get(key);
            if (example.containsKey(attribute)) {
                String value = example.get(attribute);
                int count = valueCounts.containsKey(value) ? valueCounts.get(value) : 0;
                valueCounts.put(value, count + 1);
            }
        }
        String mostCommonValue = null;
        int maxCount = 0;
        for (String key : valueCounts.keySet()) {
            int count = valueCounts.get(key);
            if (count > maxCount) {
                maxCount = count;
                mostCommonValue = key;
            }
        }
        return mostCommonValue;
    }

    

    private Node findBestAttribute(Map<String, Map<String, String>> examples, Queue<String> attributes, String target) {
        double maxGain = Double.NEGATIVE_INFINITY;
        Node attributeWithMaxGain = new Node();
        
        double targetEntropy = calculateEntropy(examples, target);
        for (String attribute : attributes) {
            double attributeEntropy = calculateEntropy(examples, attribute, target);
            double gain = targetEntropy - attributeEntropy;
        
            if (gain > maxGain) {
                maxGain = gain;
                attributeWithMaxGain.setLabel(attribute);
            }
            else if(gain == maxGain){
                if(getAttributeValues(examples, attribute).size() < getAttributeValues(examples, attributeWithMaxGain.label).size()) 
                           attributeWithMaxGain.setLabel(attribute);  
            }
        }
        
       
        return attributeWithMaxGain;
    }

    
    public static double calculateEntropy(Map<String, Map<String, String>> examples, String attribute, String target) {
        double totalEntropy = 0.0;
        Map<String, Integer> attributeValueCounts = new HashMap<>();
        Map<String, Map<String, Integer>> attributeValueTargetCounts = new HashMap<>();
    
        // Count the frequency of each attribute value and each target value for each attribute value
        for (String id : examples.keySet()) {
            Map<String, String> example = examples.get(id);
            String attributeValue = example.get(attribute);
            String targetValue = example.get(target);
    
            if (!attributeValueCounts.containsKey(attributeValue)) {
                attributeValueCounts.put(attributeValue, 0);
                attributeValueTargetCounts.put(attributeValue, new HashMap<>());
            }
            attributeValueCounts.put(attributeValue, attributeValueCounts.get(attributeValue) + 1);
    
            Map<String, Integer> targetValueCounts = attributeValueTargetCounts.get(attributeValue);
            if (!targetValueCounts.containsKey(targetValue)) {
                targetValueCounts.put(targetValue, 0);
            }
            targetValueCounts.put(targetValue, targetValueCounts.get(targetValue) + 1);
        }
    
        // Calculate the entropy of each attribute value and add it to the total entropy
        for (String attributeValue : attributeValueCounts.keySet()) {
            int attributeValueCount = attributeValueCounts.get(attributeValue);
            double attributeValueProbability = (double) attributeValueCount / examples.size();
            double attributeValueEntropy = calculateAttributeValueEntropy(examples, attribute, attributeValue, target);
            totalEntropy += attributeValueProbability * attributeValueEntropy;
        }
         
        return totalEntropy;
    }

    public static double calculateAttributeValueEntropy(Map<String, Map<String, String>> examples, String attribute, String attributeValue, String target) {
        int totalCount = 0;
        Map<String, Integer> targetValueCounts = new HashMap<>();
        Map<String, Double> targetValueProbabilities = new HashMap<>();
    
        for (String id : examples.keySet()) {
            Map<String, String> example = examples.get(id);
            String exampleAttributeValue = example.get(attribute);
            String exampleTargetValue = example.get(target);
    
            if (exampleAttributeValue != null && exampleAttributeValue.equals(attributeValue)) {
                totalCount++;
    
                if (targetValueCounts.containsKey(exampleTargetValue)) {
                    targetValueCounts.put(exampleTargetValue, targetValueCounts.get(exampleTargetValue) + 1);
                } else {
                    targetValueCounts.put(exampleTargetValue, 1);
                }
            }
        }
    
        if (totalCount == 0) {
            return 0.0;
        }
    
        double entropy = 0.0;
        //if(attribute.equals("temp")) System.out.println(targetValueCounts + "EFH");
        for (String targetValue : targetValueCounts.keySet()) {
            int targetValueCount = targetValueCounts.get(targetValue);
            double targetValueProbability = (double) targetValueCount / totalCount;
            targetValueProbabilities.put(targetValue, targetValueProbability);
            entropy -= targetValueProbability * Math.log(targetValueProbability) / Math.log(2);
        }
        
        return entropy;
    }
    
    
    
    
    
    

    public static double calculateEntropy(Map<String, Map<String, String>> examples, String targetAttribute) {
        int totalExamples = examples.size();
        Map<String, Integer> targetCounts = new HashMap<>();
    
        // Count the frequency of each target value
        for (Map<String, String> example : examples.values()) {
            String targetValue = example.get(targetAttribute);
            if (!targetCounts.containsKey(targetValue)) {
                targetCounts.put(targetValue, 0);
            }
            targetCounts.put(targetValue, targetCounts.get(targetValue) + 1);
        }
    
        double entropy = 0.0;
        for (int count : targetCounts.values()) {
            double probability = (double) count / totalExamples;
            entropy -= probability * Math.log(probability) / Math.log(2);
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
       
        for (String id : examples.keySet()) {
          
            
            if(examples.get(id).get(attribute).equals(value)){ 
                Map<String,String> temp = aux.get(id);
                subset.put(id, temp);
            
            
            subset.get(id).remove(attribute);
        }
        
             
        }
        //if(attribute.equals("Pat")) System.out.println(subset);
        return subset;
    }

   
  
    

}
