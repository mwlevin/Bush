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
        long time = System.nanoTime();
        Network test = new Network("Winnipeg");
        
        //test.frankWolfe(10, 0);
        test.algorithmB(10, 0);
        
        
        System.out.println("Time: "+String.format("%.2f", (System.nanoTime() - time)/1.0e9));
        
    }
    
}
