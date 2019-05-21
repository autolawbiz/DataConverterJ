package jp.autolawbiz.DataConverter;

import java.util.ArrayList;
import java.util.List;

class Dbffile {
	private String fieldName;
	private String fieldType;
	private int fieldLength;
	private int fieldDecimalpartLength;
	private List<String> listValues;

	public Dbffile() {
		super();
		listValues = new ArrayList<String>();
	}
	public List<String> getListValues() {
		return listValues;
	}
	public void setListValues(List<String> listValues) {
		this.listValues = listValues;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public int getFieldLength() {
		return fieldLength;
	}
	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}
	public int getFieldDecimalpartLength() {
		return fieldDecimalpartLength;
	}
	public void setFieldDecimalpartLength(int fieldDecimalpartLength) {
		this.fieldDecimalpartLength = fieldDecimalpartLength;
	}
}
