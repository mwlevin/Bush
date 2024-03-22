/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

/**
 *
 * @author mlevin
 */
public class Params 
{
 
    
    public static double bush_gap;
    public static double pas_cost_mu;
    public static double pas_flow_mu;
    public static double flow_epsilon;
    public static double line_search_gap;
    public static double tapas_equilibrate_iter;
    
    public static void resetParams(){
        bush_gap = 0.001;
        pas_cost_mu = 0.05;
        pas_flow_mu = 0.025;
        flow_epsilon = 0.0001;
        line_search_gap = 0.1;
        tapas_equilibrate_iter = 3;
    }
    
    
    public static final boolean DEBUG_CHECKS = true;
    
    public static final boolean PRINT_PAS_INFO = false;
    
    
    
    
    //public static double epsilon = 0.1;
    
    public static final double INFTY = 1.0e7;
    
    public static final boolean printBushEquilibrate = false;
    public static final boolean printReducedCosts = false;
    
    
    
    
}
