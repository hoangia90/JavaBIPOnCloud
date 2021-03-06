// BDDTestCase.java, created Jul 28, 2004 3:00:14 AM by joewhaley
// Copyright (C) 2004 John Whaley <jwhaley@alum.mit.edu>
// Licensed under the terms of the GNU LGPL; see COPYING for details.
package bdd;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import junit.framework.TestCase;
import net.sf.javabdd.BDDFactory;

/**
 * BDDTestCase
 * 
 * @author John Whaley
 * @version $Id: BDDTestCase.java,v 1.6 2005/01/31 00:09:12 joewhaley Exp $
 */
public abstract class BDDTestCase extends TestCase implements Iterator {
    
    public static final String[] factoryNames = {
        "net.sf.javabdd.BuDDyFactory",
        "net.sf.javabdd.JFactory",
        "net.sf.javabdd.MicroFactory",
        //"net.sf.javabdd.CUDDFactory",
        //"net.sf.javabdd.CALFactory",
        //"net.sf.javabdd.JDDFactory",
    };
    
    protected static Collection factories;
    protected Iterator i;
    protected int nodenum, cachesize;
    
    protected void initFactories() {
        if (factories != null) return;
        Collection f = new LinkedList();
        for (int k = 0; k < factoryNames.length; ++k) {
            String bddpackage = factoryNames[k];
            try {
                Class c = Class.forName(bddpackage);
                Method m = c.getMethod("init", new Class[] { int.class, int.class });
                BDDFactory b = (BDDFactory) m.invoke(null, new Object[] { new Integer(nodenum), new Integer(cachesize) });
                f.add(b);
            }
            catch (Throwable _) {
                if (_ instanceof InvocationTargetException)
                    _ = ((InvocationTargetException)_).getTargetException();
                System.out.println("Failed initializing "+bddpackage+": "+_);
            }
        }
        factories = f;
    }
    
    protected void destroyFactories() {
        if (factories == null) return;
        for (Iterator i = factories.iterator(); i.hasNext(); ) {
            BDDFactory f = (BDDFactory) i.next();
            f.done();
        }
        factories = null;
    }
    
    public BDDTestCase(int nodenum, int cachesize) {
        this.nodenum = nodenum;
        this.cachesize = cachesize;
    }
    public BDDTestCase() {
        this(1000, 1000);
    }
    
    protected void setUp() {
        //System.out.println("Doing setUp()");
        initFactories();
        reset();
    }
    
    public BDDFactory nextFactory() {
        BDDFactory f = (BDDFactory) i.next();
        f.reset();
        return f;
    }
    
    public Object next() {
        return nextFactory();
    }
    
    public boolean hasNext() {
        return i.hasNext();
    }
    
    public void remove() {
        i.remove();
    }
    
    public void reset() {
        i = factories.iterator();
    }
    
    protected void tearDown() {
        //System.out.println("Doing tearDown()");
        //destroyFactories();
    }
    
}
