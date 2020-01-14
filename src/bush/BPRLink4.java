/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bush;

/**
 * This works for BPR links with beta=4
 * @author micha
 */
public class BPRLink4 extends Link
{
    public BPRLink4(Node source, Node dest, double fftime, double capacity, double alpha, double beta, double length)
    {
        super(source, dest, fftime, capacity, alpha, beta, length);
        
    }
    
    public BPRLink4(int id, Node source, Node dest, double fftime, double capacity, double alpha, double beta, double length)
    {
        super(id, source, dest, fftime, capacity, alpha, beta, length);
    }
    
    public double getTT(double x)
    {
        double vc = x/getCapacity();
        double vc2 = vc * vc;
        
        return getFFTime() * (1 + getAlpha() * vc2* vc2);
    }
    
    public double getDeriv_TT(double x)
    {
        double vc = x/getCapacity();
        return getFFTime() * getAlpha() * 4 / getCapacity() * vc * vc * vc;
    }
}
