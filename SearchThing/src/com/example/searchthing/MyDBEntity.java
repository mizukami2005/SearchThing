package com.example.searchthing;

public class MyDBEntity {
	private int rowId;
	
	private String value;
	private String filename;
	private String x;
	private String y;
	private String thing;
	
	public void setRowId(int rowId){
		this.rowId = rowId;
	}
	
	public int getRowId(){
		return rowId;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	public void setFilename(String filename){
		this.filename = filename;
	}
	
	public String getFilename(){
		return filename;
	}
	
	public void setX(String x){
		this.x = x;
	}
	
	public String getX(){
		return x;
	}
	
	public void setY(String y){
		this.y = y;
	}
	
	public String getY(){
		return y;
	}
	
	public void setThing(String thing){
		this.thing = thing;
	}
	
	public String getThing(){
		return thing;
	}
}
