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
        Network test = new Network("SiouxFalls");
        
        //test.frankWolfe(100, 0.1);
        test.algorithmB(100);
        
    }
    
}
