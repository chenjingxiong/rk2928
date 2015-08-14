package xmps.androiddebugtool.factorytest.chain;

import java.util.ArrayList;


/**
 * Object array list store data like link list.
 * 
 * @author enjack
 * */
public class ObjectList {
	
	private ArrayList<Object> list = null;
	
	public ObjectList(){
		list = new ArrayList<Object>();
	}
	
	
	/**
	 * copy array.from src to dest.Note:the data of destList will be clear.
	 * 
	 * @param destList
	 * 	destination array list.
	 * 
	 * @param	srcList
	 * 	source array list.
	 * 
	 * @return
	 * true if success.
	 * */
	private boolean arrayCopy(ArrayList<Object> destList, ArrayList<Object>srcList){
		if(destList!=null && srcList!=null && srcList.size()!=0){
			int len = srcList.size();
			destList.clear();
			for(int i=0; i<len; i++)
				destList.add(srcList.get(i));
			return true;
		}
		else
			return false;
	}
	
	
	/**
	 * copy data from src to dest list.All data from destStartIdx in destList will be removed.
	 * 
	 * @param	destList
	 * 	destination list.
	 * @param	srcList.
	 * 	source list.
	 * @param	destStartIdx
	 * 	the index of destList for copy in.
	 * @param	srcStartIdx
	 * 	this index of srcList to start copy.
	 * @param cnt
	 * 	number of element to copy.
	 * 
	 * @return
	 * 	true if success.
	 * */
	private boolean arrayCopy(ArrayList<Object> destList,
			ArrayList<Object> srcList,
			int destStartIdx,
			int srcStartIdx,
			int cnt){
		if(destList!=null && srcList!=null && 
				destStartIdx>=0 && srcStartIdx>=0 &&
				cnt!=0 &&
				(destStartIdx+cnt)<=destList.size()&&
				srcStartIdx<=destList.size()){
			
			//remove element from srcStartIdx
			while(destList.size()>destStartIdx){
				destList.remove(destStartIdx);
			}
			for(int i=0; i<cnt; i++)
				destList.add(srcList.get(i+srcStartIdx));
			return true;
		}
		else
			return false;
	}
	
	
	/**
	 * is list empty or not.
	 * 
	 * @return	true if empty.
	 * */
	public boolean isEmpty(){
		return (list.size()==0)?true:false;
	}
	
	
	/**
	 * get count of object in list.
	 * 
	 * @return
	 * 	number of object.
	 * */
	public int length(){
		return list.size();
	}
	
	
	
	/**
	 * get object in index.
	 * 
	 * @return
	 * 	object.null indicate failed.
	 * */
	public Object findAt(int index){
		Object obj = null;
		int len = length();
		if(isEmpty() || index<0 || index>(len-1))
			obj = null;
		else{
			obj = list.get(index);
		}
		
		return obj;
	}
	
	
	/**find all object in list.*/
	public Object[] findAll(){
		int len = list.size();
		if(0==len)
			return null;
		//
		Object obj[] = new Object[len];
		for(int i=0; i<len; i++)
			obj[i] = list.get(i);
		return obj;
	}
	
	/**
	 * get first object in list.
	 * 
	 * @return	object.
	 * */
	public Object getHead(){
		Object obj = null;
		if(isEmpty())
			obj = null;
		else{
			obj = list.get(0);
		}
		return obj;
	}
	
	
	/**get last object in list*/
	public Object getTail(){
		return findAt(length()-1);
	}
	
	
	/**add object to head*/
	public void addHead(Object obj){
		list.add(0, obj);
	}
	
	
	/**add object to tail*/
	public void addTail(Object obj){
		list.add(obj);
	}
	
	/**insert object in specified index.*/
	public void addAt(int index, Object obj){
		list.add(index, obj);
	}
	
	/**remove all object.*/
	public void removeAll(){
		list.clear();
	}
	
	/**remove object at specified index.*/
	public void removeAt(int index){
		list.remove(index);
	}
	
	/**remove head.*/
	public void removeHead(){
		list.remove(0);
	}
	
	/**remove tail.*/
	public void removeTail(){
		list.remove(list.size()-1);
	}
	
	
	public String toString(){
		String info = "cnt="+length()+". ";
		for(int i=0; i<length(); i++)
			info +=(list.get(i).getClass().getName()+"("+
					list.get(i).getClass().hashCode()+")->");
		return info;
	}
}
