package xmps.androiddebugtool.factorytest.chain;

import android.util.Log;


/**
 * java class object link list.
 * 
 * @author enjack.
 * */
public class ObjectLinkList {
	private Node head;
	private Node tail;
	private Node current;
	private int pos;
	
	/**
	 * Node of the link list.
	 * */
	public class Node{
		public Node pre;
		public Node next;
		public Object obj;
		
		public Node(Object obj){
			this.obj = obj;
			this.next = null;
			this.pre = null;
		}
		
		public String toString(){
			return this.obj.getClass().getName();
		}
	}
	
	public ObjectLinkList(){this.head = this.current = this.tail = null;}
	
	
	/**get head node.*/
	public Node getHead(){return head;}
	/**get tail node.*/
	public Node getTail(){return tail;}
	/**get current node*/
	public Node getCurrent(){return current;}
	
	
	
	/**
	 * list is empty or not.
	 * 
	 * @return	true means empty.
	 * */
	public boolean isEmpty(){
		if(head==null && tail==null)
			return true;
		else
			return false;
	}
	
	
	/**
	 * get count of node in the list.
	 * 
	 * @return	
	 * 	number of node.
	 * */
	public int length(){
		int cnt = 0;
		if(isEmpty())
			cnt = 0;
		else{
			Node tmp;
			tmp = head;
			cnt = 1;
			while(tmp.next!=null){
				tmp = tmp.next;
				cnt++;
			}
		}
		
		return cnt;
	}
	
	
	/**
	 * add first node in list.
	 * 
	 * @return	true means success.
	 * */
	public boolean addFirstNode(Object obj){
		if(!isEmpty())
			return false;
		Node n = new Node(obj);
		n.next = n.pre = null;
		head = tail = current = n;
		return true;
	}
	
	
	
	/**
	 * delete last node in the node.
	 * 
	 * @return
	 * 	true indicate success.If list has more than one node return false;
	 * */
	private boolean removeLastNode(){
		if(length()>1)
			return false;
		else{
			head.next = null;
			head.pre = null;
			head = tail = current = null;
			return true;
		}
	}
	
	
	/**
	 * add node as head of the list.
	 * 
	 * @param	obj
	 * 	Object in the node.
	 * 
	 * @return 
	 * 	if success return head node otherwise return null.
	 * */
	public Node addHead(Object obj){
		if(isEmpty()){
			if(addFirstNode(obj))
				return head;
			else
				return null;
		}
		
		Node n = new Node(obj);
		n.pre = null;
		n.next = head;
		head.pre = n;
		head = n;
		
		return head;
	}
	
	
	
	/**
	 * add node to tail of list.
	 * 
	 * @return
	 * 	return tail node if success, null means operate failed.
	 * */
	public Node addTail(Object obj){
		if(isEmpty()){
			if(addFirstNode(obj))
				return tail;
			else
				return null;
		}
		Node n = new Node(obj);
		n.next = null;
		n.pre = tail;
		tail.next = n;
		tail = n;

		return tail;
	}
	
	
	/**
	 * remove head node.
	 * 
	 * @return	true indicate success.
	 * */
	public boolean removeHead(){
		if(!isEmpty()){
			if(length()==1){
				removeLastNode();
			}
			else{
				head = head.next;
				head.pre.next = head.pre.pre = null;
				head.pre = null;
			}
		}
		
		return true;
	}
	
	
	/**
	 * remove tail node.
	 * 
	 * @return true indicate success.
	 * */
	public boolean removeTail(){
		if(!isEmpty()){
			if(length()==1){
				removeLastNode();
			}
			else{
				tail = tail.pre;
				tail.next.next = tail.next.pre = null;
				tail.next = null;
			}
		}
		
		return true;
	}
	
	
	/**
	 * find the node at the index of list.
	 * 
	 * @return
	 * 	Node found.null means failed.
	 * */
	public Node findAt(int index){
		if(isEmpty() || index>(length()-1) || index<0)
			return null;
		
		if(length()==1)
			return head;
		
		Node tmp = head;
		while(index!=0){
			tmp = tmp.next;
			index--;
		}
		return tmp;
	}
	
	
	/**
	 * add node behind index.
	 * 
	 * @return
	 * 	true indicate success.
	 * */
	public boolean addAt(int index, Object obj){
		if(isEmpty() || index<0 || index>(length()-1))
			return false;
		
		Node n = new Node(obj);
		if(length()==1){
			Log.i("LinkList", "aaa");
			head.next = n;
			n.pre = head;
			n.next = null;
			return true;
		}
		
		if(index==(length()-1)){//add to tail
			Log.i("LinkList", "bbb");
			addTail(obj);
			return true;
		}
		
		Log.i("LinkList", "ccc");
		Node tmp = findAt(index);
		n.next = tmp.next;
		n.next.pre = n;
		tmp.next = n;
		n.pre = tmp;
		return true;
	}
	
	
	/**
	 * remove node at index.
	 * 
	 * @return true if success.
	 * */
	public boolean removeAt(int index){
		if(isEmpty() || index<0 || index>(length()-1))
			return false;
		if(1==length()){
			head.next = head.pre = null;
			head = tail = current = null;
			return true;
		}
		
		if(index ==0)
			return removeHead();
		
		if(index == length()-1)
			return removeTail();
		
		Node tmp = findAt(index);
		tmp.pre.next = tmp.next;
		tmp.next.pre = tmp.pre;
		tmp.next = tmp.pre = null;
		return true;
	}
	
	
	/**
	 * remove all nodes.
	 * */
	public void removeAll(){
		int len = length();
		while(length()!=0)
			removeHead();
	}
	
	
	/**
	 * print out information to string.
	 * */
	public String toString(){
		String info = "cnt:"+length()+". ";
		for(int i=0; i<length(); i++){
			Object obj = findAt(i).obj;
			info += (obj.getClass().getName()+"("+obj.hashCode()+")-->");
		}
		return info;
	}
}
