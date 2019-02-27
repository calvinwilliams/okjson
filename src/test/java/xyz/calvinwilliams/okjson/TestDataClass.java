package xyz.calvinwilliams.okjson;

import java.util.*;

public class TestDataClass {
	public String		str1 ;
	public byte			byte1 ;
	public short		short1 ;
	public int			int1 ;
	public long			long1 ;
	public float		float1 ;
	public double		double1 ;
	public boolean		boolean1 ;
	public String		null1 ;
	public String		name1 ;
	
	public String getName1() {
		System.out.println( "getName1" );
		return name1;
	}
	public void setName1(String name1) {
		System.out.println( "setName1" );
		this.name1 = name1;
	}
	
	public Branch2		branch2 ;
	
	public Branch3		branch3 ;
	
	public LinkedList<String>	list4 ;
	public ArrayList<Integer>	list5 ;
	public LinkedList<Boolean>	list6 ;
	
	public ArrayList<Branch7>	list7 ;
}

class Branch2 {
	public String		str2 ;
	public byte			byte2 ;
	public short		short2 ;
	public int			int2 ;
	public long			long2 ;
	public float		float2 ;
	public double		double2 ;
	public boolean		boolean2 ;
	public String		null2 ;
}

class Branch3 {
	public String		str3 ;
	public byte			byte3 ;
	public short		short3 ;
	public int			int3 ;
	public long			long3 ;
	public float		float3 ;
	public double		double3 ;
	public boolean		boolean3 ;
	public String		null3 ;
	
	public Branch33		branch33 ;
}

class Branch33 {
	public String		str33 ;
	public byte			byte33 ;
	public short		short33 ;
	public int			int33 ;
	public long			long33 ;
	public float		float33 ;
	public double		double33 ;
	public boolean		boolean33 ;
	public String		null33 ;
	public String		name33 ;
	
	public String getName33() {
		System.out.println( "getName33" );
		return name33;
	}
	public void setName33(String name33) {
		System.out.println( "setName33" );
		this.name33 = name33;
	}
}

class Branch7 {
	public String		str7 ;
	public int			int7 ;
}
