package coffee.laeven.expocool.utils.structs;

/**
 * 
 * @author Laeven
 *
 */
public class Pair<V1,V2>
{
	private V1 value1;
	private V2 value2;
	
	public Pair(V1 val1,V2 val2)
	{
		this.value1 = val1;
		this.value2 = val2;
	}
	
	public V1 getValueA()
	{
		return value1;
	}
	
	public void setValueA(V1 val)
	{
		this.value1 = val;
	}
	
    public V2 getValueB()
    {
    	return value2;
    }
    
    public void setValueB(V2 val)
    {
    	this.value2 = val;
    }
    
    public static <V1,V2> Pair<V1,V2> of(V1 v1,V2 v2)
    {
    	return new Pair<V1,V2>(v1,v2);
    }
}
