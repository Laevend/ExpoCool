package coffee.laeven.expocool.utils.structs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * 
 * @author Laeven
 *
 */
public class TabTree
{
	private UUID treeId;
	private Node root;
	
	public TabTree()
	{
		this.root = new Node(null,"root");
		this.treeId = UUID.randomUUID();
	}
	
	public Node getRoot()
	{
		return root;
	}
	
	public UUID getTreeId()
	{
		return treeId;
	}

	public static final class Node implements Iterable<Node>
	{
		public TabTree tree;
		public Node parent;
		public Map<String,Node> branches = new HashMap<>();
		public final String key;
		
		/**
		 * Creates a new node
		 * @param parent Parent node that this node can be traversed from, can be null
		 * @param key Key used to traverse to this node from the parent
		 * @param isRoot If this node is the root node
		 */
		protected Node(Node parent,String key)
		{
			this.parent = parent;
			this.key = key;
			
			if(this.parent == null) { return; }
			
			// Add this new node branch to parents map of branches
			this.parent.branches.put(key,this);
		}
		
		/**
		 * Adds a new branch to this nodes branch map and returns the new branch
		 * @param key Key used to traverse to this node from the parent
		 * @param value The value this new branch will hold
		 * @return New branch
		 */
		public Node addBranch(String key)
		{
			if(this.branches == null) { this.branches = new HashMap<>(); }
			new Node(this,key);
			return branches.get(key);
		}
		
		/**
		 * Flag for if this node is a leaf
		 * 
		 * <p>A node is a leaf if it has no branches to traverse to
		 * @return True if this node is considered a leaf, false otherwise
		 */
		public boolean isLeaf()
		{
			return branches == null ? true : branches.isEmpty() ? true : false;
		}
		
		/**
		 * Get they key of this branch node
		 */
		public String getKey()
		{
			return key;
		}

		private void print(StringBuilder buffer,String prefix,String childrenPrefix)
		{
			buffer.append(prefix);
			buffer.append(this.key != null ? this.key.toString() : "null" );
			buffer.append("\n");
			
			for(Iterator<TabTree.Node> it = this.iterator(); it.hasNext();)
			{
				Node next = it.next();
				
				if(it.hasNext())
				{
					next.print(buffer,childrenPrefix + "├── ",childrenPrefix + "│   ");
				}
				else
				{
					next.print(buffer,childrenPrefix + "└── ",childrenPrefix + "    ");
				}
			}
		}
		
		@Override
		public Iterator<Node> iterator()
		{
			return new NodeIterator(this);
		}
	}
	
	private static final class NodeIterator implements Iterator<Node>
	{
		private TabTree.Node current;
		private Iterator<Node> it;
		
		NodeIterator(TabTree.Node node)
		{
			this.current = node;
			this.it = current.branches == null ? Collections.emptyIterator() : current.branches.values().iterator();
		}
		
		@Override
		public boolean hasNext()
		{
			return it.hasNext();
		}
		
		public Node next()
		{
			return it.next();
		}
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder(50);
		this.root.print(sb,"","");
		return sb.toString();
	}
}