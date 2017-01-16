import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

class Fibonaccinode
{
    //degree indicates the number of children of each node
    public int degree = 0;   
    //Initially, every node is marked as false    
    public boolean mark = false; 
    
    public Fibonaccinode left;
    public Fibonaccinode right;
    public Fibonaccinode child;
    public Fibonaccinode parent;
    public String hash;
    public int key;
    
    public Fibonaccinode(String hash,int key)
    {
        
        this.right = null;
        this.parent = null;
        this.hash = hash;
        this.key = key;
        this.degree = 0; 
        this.left = null;
    }
    
}


class hashtagcounter
{
    public static void main(String[] args) throws Exception 
    {
        //taking user input using scanner
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a file name: ");
        System.out.flush();
        String filename = scanner.nextLine();
        File file1 = new File(filename);
        /*using a hashmap function with key(hashtags) as a string value
        and mapped value as the node*/ 
        HashMap<String,Fibonaccinode> table = new HashMap();
        FHeap obj = new FHeap();
        String output1 = "output_file.txt";
        //Keeping the output file in Output.txt
        File file = new File("Output.txt");
        BufferedWriter output=null;
        try 
        {
            //Filereader is used to read stream of characers
            FileReader sourcefile = new FileReader(file1);
            BufferedReader br = new BufferedReader(sourcefile);
            String s = br.readLine();

            //pattern is a compiled representation of regular expressions
            //[#?] is used for hashtags
            //[a-z_] is used for alphabets
            //(\\s) is used for blank spaces
            //(\\d+) is used for digits
            //making objects of diffent pattern types
            Pattern q = Pattern.compile("([#?])([a-z_]+)(\\s)(\\d+)");
            Pattern q1 = Pattern.compile("(\\d+)");
            Pattern q2 = Pattern.compile("STOP");
            output = new BufferedWriter( new FileWriter(file));
            
            //if we encounter STOP, we have to terminate the process
            while (s != "STOP") 
            {
                //Matcher matches the character sequences against the regular expression
                //making objects of matcher comparing different characters
                Matcher n = q.matcher(s);
                Matcher n1 = q1.matcher(s);
                Matcher n2 = q2.matcher(s);
                
                if (n.find()) 
                {
                    //store the alphabets in the data field
                    String data = n.group(2);
                    //store the digit value as key 
                    int key = Integer.parseInt(n.group(4));
                    if ( table.containsKey(n.group(2)))
                    {
                        //if similar objects are found, increase the key by the digit value
                        obj.increaseKey(table.get(n.group(2)),key);
                    }
                    else
                    {
                        //creating the node and inserting in the table
                        Fibonaccinode node = new Fibonaccinode(n.group(2),key);
                        obj.insert(node);
                        table.put(n.group(2),node);
                    }
       
                } 
                //writing output to a file
                //remove all the max elements and store them in a list
                //reinsert the elements again
                else if (!n.find() && n1.find()) 
                {
                    int keystop = Integer.parseInt(n1.group(1));
                    ArrayList<Fibonaccinode> removed_nodes = new ArrayList<Fibonaccinode>(keystop);
                    for ( int i=0;i<keystop;i++)
                    {
                        Fibonaccinode node = obj.removeMax();
                        table.remove(node.hash);
                        Fibonaccinode node1 = new Fibonaccinode(node.hash,node.key);
                        removed_nodes.add(node1);
                        if ( i <keystop-1) 
                        {
                            output.write(node.hash + ", ");
                        }
                        else 
                        {
                            output.write(node.hash);
                        }
                    }
                    for ( Fibonaccinode iteration : removed_nodes)
                    {
                        obj.insert(iteration);
                        table.put(iteration.hash,iteration);
                    }
                    output.newLine();
                }
                else if (n2.find()) 
                {
                    break;
                }
                s = br.readLine();
            }
        }
        catch(Exception e)
        {
            //diagnosing an exception, if any occurs
            e.printStackTrace();
        }
        finally 
        {
            if ( output != null ) 
            {
                try 
                {
                    output.close();
                } 
                catch (IOException ioe2) 
                {
                    
                }
            }
        }
        System.out.println(" ");
    }
}


class FHeap
{
    Fibonaccinode maximumNode;
    int nNodes;
    
    public void insert(Fibonaccinode node)
    {

        if (maximumNode != null) 
        {
            node.left = maximumNode;
            node.right = maximumNode.right;
            maximumNode.right = node;
            if (node.right!=null) 
            {                                
                node.right.left = node;
            }
            /*this occurs when max node is pointing to a null value
            and when there is only one max node */
            if (node.right==null)
            {
                node.right = maximumNode;
                maximumNode.left = node;
            }
            if (node.key > maximumNode.key) 
            {
                maximumNode = node;
            }
        } 
        else 
        {
            //creating a root list containing only node
            maximumNode = node;
            node.left = node;
            node.right = node;
        }
        
        nNodes++;
    }
    
    public Fibonaccinode removeMax()
    {
        Fibonaccinode q = maximumNode;
        
        if (q != null) 
        {
            int numKids = q.degree;
            Fibonaccinode a = q.child;
            Fibonaccinode tempRight;
            //for each child a of q
            while (numKids > 0) 
            {
                tempRight = a.right;
                
                //removing a from the childlist
                a.left.right = a.right;
                a.right.left = a.left;
                
                //adding a to root list of heap
                a.left = maximumNode;
                a.right = maximumNode.right;
                maximumNode.right = a;
                a.right.left = a;
                
                //setting the parent of the removed a node to null
                a.parent = null;

                //setting a to sibling to continue the process
                a = tempRight;
                
                numKids--;
            }
            
            
            //removing q from the heap
            q.left.right = q.right;
            q.right.left = q.left;
            
            //when the heap contains only one node
            if (q == q.right) 
            {
                maximumNode = null;
            } 
            else 
            {
                maximumNode = q.right;
                consolidate();
            }
            
            //decreasing the size of heap since max node is removed
            nNodes--;

            //returning the maximum element
            return q;
        }
        
        //return null if the heap is empty
        return null;
    }
    
    
    public void increaseKey(Fibonaccinode m, int k)
    {
        if (k < m.key) 
        {
            //it is an error 
        }
        
        m.key = k;
        
        Fibonaccinode b = m.parent;
        
        /*checking if parent is not null and the value 
        of the node is greater than its parent*/
        if ((b != null) && (m.key > b.key)) 
        {
            cut(m, b);
            cascadingCut(b);
        }
        
        //make m.key the max node if it has higher value than maximumnode
        if (m.key > maximumNode.key) 
        {
            maximumNode = m;
        }
    }


    public void cut(Fibonaccinode p, Fibonaccinode q)
    {
        /*removing p from the child list and decrementing the degree of q*/
        p.left.right = p.right;
        p.right.left = p.left;
        q.degree--;
        
        //restting the value of q.child
        if (q.child == p) 
        {
            q.child = p.right;
        }
        
        if (q.degree == 0) 
        {
            q.child = null;
        }
        
        //adding p to the root list
        //setting the parent of p to null
        //initializing the mark of p to be false
        p.left = maximumNode;
        p.right = maximumNode.right;
        maximumNode.right = p;
        p.right.left = p;
        p.parent = null;
        p.mark = false;
    }

    public void cascadingCut(Fibonaccinode s)
    {
        Fibonaccinode w = s.parent;
        
        //To check for the parent
        //If there is no parent, s is the root node
        if (w != null) 
        {
            // If there is no cut earlier, s will be marked false
            // Mark it as true
            if (s.mark == false) 
            {
                s.mark = true;
            } 
            else 
            {
                //If s is true, it has to be cut
                cut(s, w);
                
                //reapeat the process
                cascadingCut(w);
            }
        }
    }
    
    
    public void consolidate()
    {
        /*create an array to keep the track of roots
        according to their degrees*/
        int arraySize = 100;
        List<Fibonaccinode> array = new ArrayList<Fibonaccinode>(arraySize);
        
        //initializing the array
        for (int i = 0; i < arraySize; i++) 
        {
            array.add(null);
        }
        
        // Find the number of root nodes.
        int numRoots = 0;
        Fibonaccinode q = maximumNode;
        
        if (q != null) 
        {
            numRoots++;
            q = q.right;       
            
            while (q != maximumNode) 
            {
                //incrementing the counter until we dont come back to the max node
                numRoots++;
                q = q.right;
            }
        }
        
        //for each node in the root list of the heap
        while (numRoots > 0) 
        {
            //storing the degree of the current max node pointer in d
            int d = q.degree;
            Fibonaccinode next = q.right;
            
            //checking if the degree of some other node is same 
            for (;;) 
            {
                Fibonaccinode p = array.get(d);
                if (p == null) 
                {
                    break;
                }
                
                /*If there are nodes having same degree, make one 
                the child of another based on the key values*/
                if (q.key < p.key) 
                {
                    //swap the values
                    Fibonaccinode temp = p;
                    p = q;
                    q = temp;
                }
                
                /*Removing q from the root list of x and
                making it a child of p*/
                link(p, q);
                
                array.set(d, null);
                //continue to the next degree
                d++;
            }
            
            array.set(d, q);
            q = next;

            //the number of roots starts decreasing 
            numRoots--;
        }
        
        
        //Reconstructing the array again
        maximumNode = null;                                                   
        
        for (int i = 0; i < arraySize; i++) 
        {
            Fibonaccinode p = array.get(i);
            if (p == null) 
            {
                continue;
            }
            
            //adding to the root list if there is a node
            if (maximumNode != null) {
                
                //Removing and adding node to the root list
                p.left.right = p.right;
                p.right.left = p.left;
                
                p.left = maximumNode;
                p.right = maximumNode.right;
                maximumNode.right = p;
                p.right.left = p;
                
                if (p.key > maximumNode.key) 
                {
                    maximumNode = p;
                }
            } 
        else 
            {
                //initialize max node if it was null
                maximumNode = p;
            }
        }
    }
    

    public void link(Fibonaccinode a, Fibonaccinode b)
    {
        //removing a from the root list
        a.left.right = a.right;
        a.right.left = a.left;
        
        //make a child of b
        a.parent = b;
        
        if (b.child == null) {
            b.child = a;
            a.right = a;
            a.left = a;
        } 
        else 
        {
            a.left = b.child;
            a.right = b.child.right;
            b.child.right = a;
            a.right.left = a;
        }
        
        /*increase the degree of b
        and set the mark value of a as false*/
        b.degree++;
        a.mark = false;
    }
    
}
