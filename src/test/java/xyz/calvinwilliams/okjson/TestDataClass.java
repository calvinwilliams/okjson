package xyz.calvinwilliams.okjson;

import java.util.*;

public class TestDataClass {
	public String		str1 ;
	public byte			byte1 ;
	public short		short1 ;
	public int			int1 ;
	public Long			long1 ;
	public Float		float1 ;
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
	
	public LinkedList<String>	array4 ;
	public ArrayList<Integer>	array5 ;
	public LinkedList<Boolean>	array6 ;
	public LinkedList<String>	array65 ;
	
	public ArrayList<Branch7>	array7 ;
	
	public Strings8				strings8 ;
	public Numbers9				numbers9 ;
	
}

class Branch2 {
	public String		str2 ;
	public byte			byte2 ;
	public short		short2 ;
	public Integer		int2 ;
	public long			long2 ;
	public float		float2 ;
	public Double		double2 ;
	public Boolean		boolean2 ;
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

class Strings8 {
	private String		string81 ;
	private String		string82 ;
	private String		string83 ;
	private String		string84 ;
	private String		string85 ;
	private String		string86 ;
	private String		string87 ;
	private String		string88 ;
	private String		string89 ;
	
	public String getString81() {
		return string81;
	}
	public void setString81(String string81) {
		this.string81 = string81;
	}
	public String getString82() {
		return string82;
	}
	public void setString82(String string82) {
		this.string82 = string82;
	}
	public String getString83() {
		return string83;
	}
	public void setString83(String string83) {
		this.string83 = string83;
	}
	public String getString84() {
		return string84;
	}
	public void setString84(String string84) {
		this.string84 = string84;
	}
	public String getString85() {
		return string85;
	}
	public void setString85(String string85) {
		this.string85 = string85;
	}
	public String getString86() {
		return string86;
	}
	public void setString86(String string86) {
		this.string86 = string86;
	}
	public String getString87() {
		return string87;
	}
	public void setString87(String string87) {
		this.string87 = string87;
	}
	public String getString88() {
		return string88;
	}
	public void setString88(String string88) {
		this.string88 = string88;
	}
	public String getString89() {
		return string89;
	}
	public void setString89(String string89) {
		this.string89 = string89;
	}
	
}

class Numbers9 {
	private long		long91 ;
	private long		long92 ;
	private long		long93 ;
	private float		floatE94 ;
	private float		floatE95 ;
	private float		floatE96 ;
	private double		doubleE97 ;
	private double		doubleE98 ;
	private double		doubleE99 ;
	public long getLong91() {
		return long91;
	}
	public void setLong91(long long91) {
		this.long91 = long91;
	}
	public long getLong92() {
		return long92;
	}
	public void setLong92(long long92) {
		this.long92 = long92;
	}
	public long getLong93() {
		return long93;
	}
	public void setLong93(long long93) {
		this.long93 = long93;
	}
	public float getFloatE94() {
		return floatE94;
	}
	public void setFloatE94(float floatE94) {
		this.floatE94 = floatE94;
	}
	public float getFloatE95() {
		return floatE95;
	}
	public void setFloatE95(float floatE95) {
		this.floatE95 = floatE95;
	}
	public float getFloatE96() {
		return floatE96;
	}
	public void setFloatE96(float floatE96) {
		this.floatE96 = floatE96;
	}
	public double getDoubleE97() {
		return doubleE97;
	}
	public void setDoubleE97(double doubleE97) {
		this.doubleE97 = doubleE97;
	}
	public double getDoubleE98() {
		return doubleE98;
	}
	public void setDoubleE98(double doubleE98) {
		this.doubleE98 = doubleE98;
	}
	public double getDoubleE99() {
		return doubleE99;
	}
	public void setDoubleE99(double doubleE99) {
		this.doubleE99 = doubleE99;
	}

}
