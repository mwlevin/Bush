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
        
        
        //Bush bush = new Bush((Zone)test.findNode(1), test);
        
        test.frankWolfe(100, 1E-4);
 
        
        
        /*
        //bush.addFlow(test.findLink(13), -50);
        bush.addFlow(test.findLink(34), -50);
        bush.addFlow(test.findLink(45), -50);
        bush.addFlow(test.findLink(52), -20);
        
        bush.addFlow(test.findLink(65), 30);
        bush.addFlow(test.findLink(36), 50);
        bush.addFlow(test.findLink(69), 20);
        bush.addFlow(test.findLink(92), 20);
        
        test.printLinkFlows();
        test.getSPTree(bush.getOrigin());
        bush.checkPAS();
        bush.branchShifts();
        
        
        */
        
        
        /*
        bush.addFlow(test.findLink(34), 10);
        bush.addFlow(test.findLink(45), 10);
        bush.addFlow(test.findLink(56), 10);
        bush.addFlow(test.findLink(63), 10);
        
        
        bush.addFlow(test.findLink(13), 10);
        bush.addFlow(test.findLink(36), 10);
        bush.addFlow(test.findLink(67), 10);
        bush.addFlow(test.findLink(71), 10);
        
        test.printLinkFlows();
        bush.removeCycles();
        test.printLinkFlows();
        */
        
        
        
        test.tapas(100, 1E-4);
        
        //System.out.println("flow conservation "+bush.validateFlowConservation());

        
        //test.printLinkFlows();
        
        /*
        bush.checkPAS();
        test.printPAS();
        
        
        System.out.println("Equilibrated? "+test.equilibratePAS());
        //System.out.println("Equilibrated? "+test.equilibratePAS());
        //System.out.println("Equilibrated? "+test.equilibratePAS());

        
        test.printLinkFlows();
        System.out.println("Flow conservation "+bush.validateFlowConservation());
        
        System.out.println(test.getTSTT()+" "+test.getSPTT());
        
        for(PAS p : bush.getRelevantPAS()){
            System.out.println(p+" "+p.getTT(0));
        }
        */
        /*
        bush.checkPAS();
        test.printPAS();
        
        
        System.out.println("Equilibrated? "+test.equilibratePAS());
        System.out.println("Equilibrated? "+test.equilibratePAS());
        System.out.println("Equilibrated? "+test.equilibratePAS());

        
        
        
        test.printLinkFlows();
        System.out.println(test.getTSTT()+" "+test.getSPTT());
        
        
        for(PAS p : bush.getRelevantPAS()){
            System.out.println(p+" "+p.getTT(0));
        }
        
        
        Params.pas_cost_mu = 0.00001;
        
        bush.checkPAS();
        
        
        System.out.println("Equilibrated? "+test.equilibratePAS());
        System.out.println("Equilibrated? "+test.equilibratePAS());
        System.out.println("Equilibrated? "+test.equilibratePAS());
        
        test.printPAS();
        
        test.printLinkFlows();
        
        System.out.println(test.getTSTT()+" "+test.getSPTT());
        
        
        for(PAS p : bush.getRelevantPAS()){
            System.out.println(p+" "+p.getTT(0));
        }
        */
        
        /*
        PAS p52 = bush.getRelevantPAS().get(test.findLink(52)).iterator().next();
        PAS p45 = bush.getRelevantPAS().get(test.findLink(45)).iterator().next();
        
        System.out.println(p52);
        
        p52.flowShift();
        System.out.println("p52 tt "+p52.getTT(0));
        System.out.println("p45 tt "+p45.getTT(0));
        
        test.printLinkFlows();
        
        System.out.println(test.getTSTT()+" "+test.getSPTT());
        
        p45.flowShift();
        System.out.println("p52 tt "+p52.getTT(0));
        System.out.println("p45 tt "+p45.getTT(0));
        
        test.printLinkFlows();
        
        System.out.println(test.getTSTT()+" "+test.getSPTT());
        */
        
        
        
        /*
        long time = System.nanoTime();
        Network test = new Network("Winnipeg");
        
        //test.frankWolfe(10, 0);
        test.algorithmB(20, 0);
        
        
        System.out.println("Time: "+String.format("%.2f", (System.nanoTime() - time)/1.0e9));
        */
        
        
    }
    
}
