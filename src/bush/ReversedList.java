/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bush;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 *
 * @author michaellevin
 */
public class ReversedList<E> extends ArrayList<E> {
    private boolean backwards;
    
    public ReversedList(){
    }
    

    public Iterator<E> iterator(){
        return new ReversedIterator<E>(super.listIterator());
    }
    
    public ListIterator<E> listIterator(){
        return new ReversedIterator<E>(super.listIterator());
    }
    
    public String toString(){
        String output = "[";
        
        int size = size();
        for(int i = size-1; i >= 0; i--){
            output += get(i);
            if(i > 0){
                output += ", ";
            }
        }
        output += "]";
        
        return output;
    }
    
    class ReversedIterator<E> implements ListIterator<E>
    {
        private ListIterator<E> internal;
        
        public ReversedIterator(ListIterator<E> internal){
            this.internal = internal;
        }
        
        public void add(E e){
            internal.add(e);
        }
        
        public void remove(){
            internal.remove();
        }
        
        public void set(E e){
            internal.set(e);
        }
        
        public E next(){
            return internal.previous();
        }
        
        public boolean hasNext(){
            return internal.hasPrevious();
        }
        
        
        public int nextIndex(){
            return internal.previousIndex();
        }
        
        public int previousIndex(){
            return internal.nextIndex();
        }
        
        public E previous(){
            return internal.next();
        }
        
        public boolean hasPrevious(){
            return internal.hasNext();
        }
    }
    
}
