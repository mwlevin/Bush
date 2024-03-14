/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

import java.io.IOException;

/**
 *
 * @author micha
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException
    {
        
        Network test = new Network("grid3");

        
        Tree minTree = test.getSPTree(test.findNode(1));
        
        System.out.println(minTree.getPath(test.findNode(2)));
        
        Bush bush = new Bush((Zone)test.findNode(1), test);
        
        test.printLinkFlows();
        
        bush.checkPAS();
        
        
        PAS p52 = bush.getRelevantPAS().get(test.findLink(52)).iterator().next();
        
        System.out.println(p52);
        
        p52.flowShift();
        
        test.printLinkFlows();
        
        System.out.println(p52.getTT(0));
        /*
        long time = System.nanoTime();
        Network test = new Network("Winnipeg");
        
        //test.frankWolfe(10, 0);
        test.algorithmB(20, 0);
        
        
        System.out.println("Time: "+String.format("%.2f", (System.nanoTime() - time)/1.0e9));
        */
        
        
    }
    
}
