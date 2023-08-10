package net.optifine.util;

import java.util.Iterator;

public class LinkedList<T>
{
    private LinkedList.Node<T> first;
    private LinkedList.Node<T> last;
    private int size;

    public void addFirst(LinkedList.Node<T> node)
    {
        this.checkNoParent(node);

        if (this.isEmpty())
        {
            this.first = node;
            this.last = node;
        }
        else
        {
            LinkedList.Node<T> nodenext = this.first;
            node.setNext(nodenext);
            nodenext.setPrev(node);
            this.first = node;
        }

        node.setParent(this);
        ++this.size;
    }

    public void addLast(LinkedList.Node<T> node)
    {
        this.checkNoParent(node);

        if (this.isEmpty())
        {
            this.first = node;
            this.last = node;
        }
        else
        {
            LinkedList.Node<T> nodeprev = this.last;
            node.setPrev(nodeprev);
            nodeprev.setNext(node);
            this.last = node;
        }

        node.setParent(this);
        ++this.size;
    }

    public void addAfter(LinkedList.Node<T> nodePrev, LinkedList.Node<T> node)
    {
        if (nodePrev == null)
        {
            this.addFirst(node);
        }
        else if (nodePrev == this.last)
        {
            this.addLast(node);
        }
        else
        {
            this.checkParent(nodePrev);
            this.checkNoParent(node);
            LinkedList.Node<T> nodenext = nodePrev.getNext();
            nodePrev.setNext(node);
            node.setPrev(nodePrev);
            nodenext.setPrev(node);
            node.setNext(nodenext);
            node.setParent(this);
            ++this.size;
        }
    }

    public LinkedList.Node<T> remove(LinkedList.Node<T> node)
    {
        this.checkParent(node);
        LinkedList.Node<T> prev = node.getPrev();
        LinkedList.Node<T> node1 = node.getNext();

        if (prev != null)
        {
            prev.setNext(node1);
        }
        else
        {
            this.first = node1;
        }

        if (node1 != null)
        {
            node1.setPrev(prev);
        }
        else
        {
            this.last = prev;
        }

        node.setPrev((LinkedList.Node<T>)null);
        node.setNext((LinkedList.Node<T>)null);
        node.setParent((LinkedList<T>)null);
        --this.size;
        return node;
    }

    public void moveAfter(LinkedList.Node<T> nodePrev, LinkedList.Node<T> node)
    {
        this.remove(node);
        this.addAfter(nodePrev, node);
    }

    public boolean find(LinkedList.Node<T> nodeFind, LinkedList.Node<T> nodeFrom, LinkedList.Node<T> nodeTo)
    {
        this.checkParent(nodeFrom);

        if (nodeTo != null)
        {
            this.checkParent(nodeTo);
        }

        LinkedList.Node<T> node;

        for (node = nodeFrom; node != null && node != nodeTo; node = node.getNext())
        {
            if (node == nodeFind)
            {
                return true;
            }
        }

        if (node != nodeTo)
        {
            throw new IllegalArgumentException("Sublist is not linked, from: " + nodeFrom + ", to: " + nodeTo);
        }
        else
        {
            return false;
        }
    }

    private void checkParent(LinkedList.Node<T> node)
    {
        if (node.parent != this)
        {
            throw new IllegalArgumentException("Node has different parent, node: " + node + ", parent: " + node.parent + ", this: " + this);
        }
    }

    private void checkNoParent(LinkedList.Node<T> node)
    {
        if (node.parent != null)
        {
            throw new IllegalArgumentException("Node has different parent, node: " + node + ", parent: " + node.parent + ", this: " + this);
        }
    }

    public boolean contains(LinkedList.Node<T> node)
    {
        return node.parent == this;
    }

    public Iterator<LinkedList.Node<T>> iterator()
    {
        return new Iterator<LinkedList.Node<T>>()
        {
            LinkedList.Node<T> node = LinkedList.this.getFirst();
            public boolean hasNext()
            {
                return this.node != null;
            }
            public LinkedList.Node<T> next()
            {
                LinkedList.Node<T> node = this.node;

                if (this.node != null)
                {
                    this.node = this.node.next;
                }

                return node;
            }
        };
    }

    public LinkedList.Node<T> getFirst()
    {
        return this.first;
    }

    public LinkedList.Node<T> getLast()
    {
        return this.last;
    }

    public int getSize()
    {
        return this.size;
    }

    public boolean isEmpty()
    {
        return this.size <= 0;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        for (Iterator<Node<T>> it = iterator(); it.hasNext();)
        {
          Node<T> node = (Node)it.next();
          if (sb.length() > 0) {
            sb.append(", ");
          }
          sb.append(node.getItem());
        }
        return "" + this.size + " [" + sb.toString() + "]";
    }
    
    public static class Node<T>
    {
        private final T item;
        private LinkedList.Node<T> prev;
        private LinkedList.Node<T> next;
        private LinkedList<T> parent;

        public Node(T item)
        {
            this.item = item;
        }

        public T getItem()
        {
            return this.item;
        }

        public LinkedList.Node<T> getPrev()
        {
            return this.prev;
        }

        public LinkedList.Node<T> getNext()
        {
            return this.next;
        }

        private void setPrev(LinkedList.Node<T> prev)
        {
            this.prev = prev;
        }

        private void setNext(LinkedList.Node<T> next)
        {
            this.next = next;
        }

        private void setParent(LinkedList<T> parent)
        {
            this.parent = parent;
        }

        public String toString()
        {
            return "" + this.item;
        }
    }
}
