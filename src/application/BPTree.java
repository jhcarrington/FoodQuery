package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Implementation of a B+ tree to allow efficient access to many different indexes of a large data
 * set. BPTree objects are created for each type of index needed by the program. BPTrees provide an
 * efficient range search as compared to other types of data structures due to the ability to
 * perform log_m N lookups and linear in-order traversals of the data items.
 * 
 * @author sapan (sapan@cs.wisc.edu), Jason Carrington, Sarah Ostermeier, Cristian Espinoza, Brian
 *         O'Loughlin
 *
 * @param <K> key - expect a string that is the type of id for each item
 * @param <V> value - expect a user-defined type that stores all data for a food item
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

    // Root of the tree
    private Node root;

    // Branching factor is the number of children nodes
    // for internal nodes of the tree
    private int branchingFactor;


    /**
     * Public constructor
     * 
     * @param branchingFactor
     */
    public BPTree(int branchingFactor) {
        if (branchingFactor <= 2) {
            throw new IllegalArgumentException("Illegal branching factor: " + branchingFactor);
        }
        root = new LeafNode();
        this.branchingFactor = branchingFactor;
    }


    /*
     * (non-Javadoc)
     * 
     * @see BPTreeADT#insert(java.lang.Object, java.lang.Object)
     */
    @Override
    public void insert(K key, V value) {
        Node newRoot = insertHelper(root, key, value);// calls insertHelper
        if (newRoot != null) {
            root = newRoot;
        }
    }

    /**
     * helper method to decide where to go from an interalNode
     * 
     * @param node InternalNode
     * @param key key
     * @param value value
     * @return null or InternalNode depending if it split
     */
    @SuppressWarnings("unchecked")
    private Node insertHelper(InternalNode node, K key, V value) {
        int i = 0;
        for (i = 0; i < node.keys.size(); i++) {// iterates through this nodes keys to find out
                                                // where we need to go next
            if (node.keys.get(i).compareTo(key) >= 0) {
                break;
            }
        }
        // calls insert helper on the node that is located at the location we found to search
        InternalNode recur = (InternalNode) insertHelper(node.children.get(i), key, value);

        if (recur == null) {// if nothing was returned that means it didn't split so return null
            return null;
        }
        node.keys.add(i, recur.keys.get(0));// add the key if it did split
        node.children.remove(i);// remove the child that was split
        for (int j = 0; j < recur.children.size(); j++) {// iterate through the new children and add
                                                         // them to our current children
            Node temp = recur.children.get(j);
            node.children.add(i + j, temp);
        }
        if (node.isOverflow()) {// if it's unbalanced now then return a split version of it
            return node.split();
        }

        return null;
    }

    /**
     * helper method that deals with a LeafNode
     * 
     * @param node Leaf node
     * @param key key
     * @param value value
     * @return null or an Internal node depending if it split
     */
    @SuppressWarnings("unchecked")
    private Node insertHelper(LeafNode node, K key, V value) {// baseCase of recursion
        node.insert(key, value);
        if (node.isOverflow()) {// split up and return the split version
            InternalNode temp = (InternalNode) node.split();
            return temp;
        } else {
            return null;// if it doesn't split then shouldn't return anything new to add to the tree
                        // recursively
        }
    }

    /**
     * Decents which type node is and then calls the specific helper method
     * 
     * @param node
     * @param key
     * @param value
     * @return Recursive, will be the root node at the end
     */
    @SuppressWarnings("unchecked")
    private Node insertHelper(Node node, K key, V value) {
        if (node.getClass().equals(LeafNode.class)) {
            return insertHelper((LeafNode) node, key, value);
        } else if (node.getClass().equals(InternalNode.class)) {
            return insertHelper((InternalNode) node, key, value);
        }
        return null;
    }


    /*
     * (non-Javadoc)
     * 
     * @see BPTreeADT#rangeSearch(java.lang.Object, java.lang.String)
     */
    @Override
    public List<V> rangeSearch(K key, String comparator) {
        if (!comparator.contentEquals(">=") && !comparator.contentEquals("==")
            && !comparator.contentEquals("<="))
            return new ArrayList<V>();

        return root.rangeSearch(key, comparator);// recursively finds a list by finding the leaf
                                                 // node and storing the comparator along the way to
                                                 // be used from this leaf node
    }



    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    @SuppressWarnings("unchecked")
    public String toString() {
        Queue<List<Node>> queue = new LinkedList<List<Node>>();
        queue.add(Arrays.asList(root));
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
            while (!queue.isEmpty()) {
                List<Node> nodes = queue.remove();
                sb.append('{');
                Iterator<Node> it = nodes.iterator();
                while (it.hasNext()) {
                    Node node = it.next();
                    sb.append(node.toString());
                    if (it.hasNext())
                        sb.append(", ");
                    if (node instanceof BPTree.InternalNode)
                        nextQueue.add(((InternalNode) node).children);
                }
                sb.append('}');
                if (!queue.isEmpty())
                    sb.append(", ");
                else {
                    sb.append('\n');
                }
            }
            queue = nextQueue;
        }
        return sb.toString();
    }

    /**
     * This abstract class represents any type of node in the tree This class is a super class of
     * the LeafNode and InternalNode types.
     * 
     * @author sapan, Jason Carrington, Sarah Ostermeier, Cristian Espinoza, Brian O'Loughlin
     */
    private abstract class Node {

        // List of keys
        List<K> keys;

        /**
         * Package constructor
         */
        Node() {
            keys = new ArrayList<K>();
        }

        /**
         * Inserts key and value in the appropriate leaf node and balances the tree if required by
         * splitting
         * 
         * @param key
         * @param value
         */
        abstract void insert(K key, V value);

        /**
         * Gets the first leaf key of the tree
         * 
         * @return key
         */
        abstract K getFirstLeafKey();

        /**
         * Gets the new sibling created after splitting the node
         * 
         * @return Node
         */
        abstract Node split();

        /*
         * (non-Javadoc)
         * 
         * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
         */
        abstract List<V> rangeSearch(K key, String comparator);

        /**
         * 
         * @return boolean
         */
        abstract boolean isOverflow();

        public String toString() {
            return keys.toString();
        }

    } // End of abstract class Node

    /**
     * This class represents an internal node of the tree. This class is a concrete sub class of the
     * abstract Node class and provides implementation of the operations required for internal
     * (non-leaf) nodes.
     * 
     * @author sapan, Jason Carrington, Sarah Ostermeier, Cristian Espinoza, Brian O'Loughlin
     */
    private class InternalNode extends Node {

        // List of children nodes
        List<Node> children;

        /**
         * Package constructor
         */
        InternalNode() {
            super();
            children = new ArrayList<Node>();
        }

        /**
         * (non-Javadoc)
         * 
         * @see BPTree.Node#getFirstLeafKey()
         */
        @SuppressWarnings("unchecked")
        K getFirstLeafKey() {
            InternalNode current = this;
            while (current.children.get(0).getClass().equals(InternalNode.class)) {// iterates to a
                                                                                   // leaf node
                current = (InternalNode) current.children.get(0);
            }

            return current.getFirstLeafKey();// calls getFirstLeafKey on the leafNode
        }

        /**
         * (non-Javadoc)
         * 
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            return keys.size() >= branchingFactor;
        }

        /**
         * (non-Javadoc)
         * 
         * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
         */
        void insert(K key, V value) {
            this.keys.add(key);
        }

        /**
         * (non-Javadoc)
         * 
         * @see BPTree.Node#split()
         */
        Node split() {
            InternalNode leftSide = new InternalNode();
            int i = 0;
            while (i <= this.children.size() / 2) {// takes the first half of the children and adds
                                                   // it to the leftSide
                leftSide.children.add(this.children.get(0));
                this.children.remove(0);// possible error with the loop
                i++;
            }

            for (int j = 0; j < --i; j++) {// decrements i because the while added 1 at the end,
                                           // loops through and sets the keys
                leftSide.keys.add(this.keys.get(0));
                this.keys.remove(0);
            }

            InternalNode parent = new InternalNode();// creates the new parent
            parent.children.add(leftSide);
            parent.keys.add(this.keys.get(0));// adds the left most key of the right side, in order
                                              // successor
            this.keys.remove(0);// removes the key from the current because we know that 'this' is
                                // an internalNode
            parent.children.add(this);
            return parent;
        }

        /**
         * (non-Javadoc)
         * 
         * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
         */
        List<V> rangeSearch(K key, String comparator) {
            int i = 0;
            for (i = 0; i < keys.size(); i++) {// finds the next child node to recursively go to
                if (keys.get(i).compareTo(key) >= 0) {// when the keys value is greater than the key
                    break;
                }
            }

            return children.get(i).rangeSearch(key, comparator);// recursively calls rangesearch on
                                                                // the found child
        }

    } // End of class InternalNode


    /**
     * This class represents a leaf node of the tree. This class is a concrete sub class of the
     * abstract Node class and provides implementation of the operations that required for leaf
     * nodes.
     * 
     * @author sapan, Jason Carrington, Sarah Ostermeier, Cristian Espinoza, Brian O'Loughlin
     */
    private class LeafNode extends Node {

        // List of values
        List<V> values;

        // Reference to the next leaf node
        LeafNode next;

        // Reference to the previous leaf node
        LeafNode previous;

        /**
         * Package constructor
         */
        LeafNode() {
            super();
            values = new ArrayList<V>();
        }


        /**
         * (non-Javadoc)
         * 
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return keys.get(0);
        }

        /**
         * (non-Javadoc)
         * 
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            return keys.size() >= branchingFactor;
        }

        /**
         * (non-Javadoc)
         * 
         * @see BPTree.Node#insert(Comparable, Object)
         */
        void insert(K key, V value) {
            int i = 0;
            for (i = 0; i < keys.size(); i++) {// finds exactly where it should be inserted
                if (keys.get(i).compareTo(key) >= 0) {// break when we have gotten to a point where
                                                      // the already stored key is greater than or
                                                      // equal to the
                                                      // key
                    break;
                }
            }
            keys.add(i, key);
            values.add(i, value);

        }

        /**
         * (non-Javadoc)
         * 
         * @see BPTree.Node#split()
         */
        Node split() {
            LeafNode leftSide = new LeafNode();// stores the left side of the new parent
            int i = 0;
            while (i < keys.size() / 2) {// add the first half of the values and keys
                leftSide.values.add(values.get(0));
                leftSide.keys.add(keys.get(0));
                values.remove(0);
                keys.remove(0);// removes values and keys from the current so they arnt duplicated
                i++;
            }
            InternalNode parent = new InternalNode();
            parent.children.add(leftSide);
            parent.children.add(this);

            // updates pointers
            leftSide.previous = this.previous;
            if (this.previous != null) {
                this.previous.next = leftSide;
            }
            this.previous = leftSide;
            leftSide.next = this;

            parent.keys.add(this.getFirstLeafKey());
            return parent;
        }

        /**
         * (non-Javadoc)
         * 
         * @see BPTree.Node#rangeSearch(Comparable, String)
         */
        List<V> rangeSearch(K key, String comparator) {
            ArrayList<V> finalList = new ArrayList<V>();
            LeafNode current = this;
            if (comparator.equals(">=")) {
                int i = 0;
                for (i = 0; i < keys.size(); i++) {// finds the correct location
                    if (keys.get(i).compareTo(key) >= 0) {
                        break;
                    }
                }
                finalList.addAll(current.values.subList(i, current.values.size()));// add a sublist
                                                                                   // from i to the
                                                                                   // end
                current = current.next;// go to the next node

                while (current != null) {// while there is another node add all items
                    finalList.addAll(current.values);
                    current = current.next;
                }
            }

            else if (comparator.equals("<=")) {
                LeafNode current2 = current;// stores the found node to be used to go to the right
                while (current2 != null) {// iterates to the end of the equal to values
                    for (int k = 0; k < current2.keys.size(); k++) {
                        if (current2.keys.get(k).compareTo(key) <= 0) {
                            finalList.add(current2.values.get(k));// add all values that are the
                                                                  // same
                        } else {
                            break;
                        }
                    }
                    current2 = current2.next;
                }
                current = current.previous;
                while (current != null) {// add all previous values
                    finalList.addAll(0, current.values);
                    current = current.previous;
                }
            }

            else {
                int k = 0;
                for (k = 0; k < current.keys.size(); k++) {// iterates through found node
                    if (current.keys.get(k).compareTo(key) == 0) {
                        finalList.add(current.values.get(k));// if keys match then add the value
                    }
                }
                current = current.next;
                boolean breakLoop = false;
                while (current != null && !breakLoop) {// iterates to the right
                    for (int j = 0; j < current.keys.size(); j++) {// iterates through the keys
                        if (current.keys.get(j).compareTo(key) == 0) {// adds all matching keys
                            finalList.add(current.values.get(j));
                            continue;
                        }
                        breakLoop = true;
                        break;// breaks both loops when it reaches a key that is not equal to 'key'
                    }
                    current = current.next;
                }

            }

            return finalList;
        }

    } // End of class LeafNode


    /**
     * Contains a basic test scenario for a BPTree instance. It shows a simple example of the use of
     * this class and its related types.
     * 
     * @param args
     */
    public static void main(String[] args) {
        // create empty BPTree with branching factor of 3
        BPTree<Double, Double> bpTree = new BPTree<>(3);

        // create a pseudo random number generator
        Random rnd1 = new Random();

        // some value to add to the BPTree
        Double[] dd = {0.0d, 0.5d, 0.2d, 0.8d};

        // build an ArrayList of those value and add to BPTree also
        // allows for comparing the contents of the ArrayList
        // against the contents and functionality of the BPTree
        // does not ensure BPTree is implemented correctly
        // just that it functions as a data structure with
        // insert, rangeSearch, and toString() working.
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Double j = dd[rnd1.nextInt(4)];
            list.add(j);
            System.out.println(j);
            bpTree.insert(j, j);
            System.out.println("\n\nTree structure:\n" + bpTree.toString());
        }
        List<Double> filteredValues = bpTree.rangeSearch(0.8d, "<=");
        System.out.println("Filtered values: " + filteredValues.toString());
    }

} // End of class BPTree
