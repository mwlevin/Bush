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
    public static double bush_gap = 0.01;
    public static double pas_cost_mu = 0.05;
    public static double pas_flow_mu = 0.025;
    public static double flow_epsilon = 0.01;
    
    
    public static double tapas_equilibrate_iter = 3;
    
    //public static double epsilon = 0.1;
    
    public static final double INFTY = 1.0e7;
    
    public static final boolean printBushEquilibrate = false;
    public static final boolean printReducedCosts = false;
}
