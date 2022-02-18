
package Tree;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
    private List<Node<T>> children = new ArrayList<Node<T>>();
    private Node<T> parent = null;
    private T data = null;
    
    //Constructors
    public Node(T data) {
        this.data = data;
    }
    
    public Node(T data, Node<T> parent) {
        this.data = data;
        this.parent = parent;
        parent.children.add(this);
    }

    //Basic methods
    //Check root
    public boolean isRoot() {
        return (this.parent == null);
    }
    //Check leaf
    public boolean isLeaf() {
        return this.children.size() == 0;
    }
    
    //Node data get & set
    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
    
    //Parent 
    public void setParent(Node<T> parent) {
    	this.parent = parent;
        parent.children.add(this);
  }
    
    public void removeParent() {
        this.parent = null;
    }
    
    //Child 
    public void addChild(T data) {
        Node<T> child = new Node<T>(data);
        this.children.add(child);
        child.parent = this;
    }
    public void addChild(Node<T> child) {
        this.children.add(child);
        child.parent = this;
    }
    
    //Children 
    public List<Node<T>> getChildren() {
        return children;
    }
    
    public void addChildren(List<Node<T>> children) {
        for(Node<T> t : children) {
            t.setParent(this);
        }
    }
    
    //Traverse
//    public void traverse(Node<T> n) {
//        if (n != null) {
//            for (int i = 0; i < n.children.size(); i++) {
//                System.out.println(n.children.get(i).data);
//                traverse(n.children.get(i));
//            }
//        }
//    }
    public static <T> void traverse(Node<T> n) {
        if (n != null) {
            for (int i = 0; i < n.children.size(); i++) {
                System.out.println(n.children.get(i).data);
                traverse(n.children.get(i));
            }
        }
    }

}
