package org.eclipse.epsilon.emc.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.eclipse.epsilon.emc.plainxml.PlainXmlType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONElement {

	protected JSONElement parent;
	protected boolean array;
	protected boolean root;
	private String tag;
	private Object value;
	private String id;

	public JSONElement(JSONElement parent, String tag, Object value, String id) {
		if (parent == null){
			root = true;
			this.parent = null;
			this.tag= PlainXmlType.parse("t_root").getTagName();
		} else {
			this.root = false;
			this.parent = parent;
		}
		
		if (value instanceof JSONObject) {
			this.value = value;
			this.array = false;
		} else if (value instanceof JSONArray) {
			this.value = value;
			this.array = true;
		} else {
			this.value = value;
			this.array = false; 
		}
		
		if (tag == null || tag.isEmpty()){
			setTag();
		} else {
			this.tag = PlainXmlType.parse("t_" +tag).getTagName();
		}	
		
		if (id == null || id.isEmpty()) {
			this.id = this.tag;
		} else {
			this.id = id;
		}
	}
	
	public JSONElement(JSONElement parent, String tag, Object value) {
		this(parent, tag, value, null);
	}	
	public JSONElement(JSONElement parent, Object value) {
		this(parent, null, value);
	}

	public JSONElement(Object value) {
		this(null, null, value);
	}

	public String getId() {
		return id;
	}
	public JSONElement getParent() {
		return parent;
	}
	public void setParent(JSONElement parent) {
		this.parent = parent;
	}
	public String getTag() {
		return tag;
	}

	public void setTag() {
		if (parent != null){
			if (parent.isArray()) {
				
			} else {
				JSONObject parentObject = (JSONObject) parent.getValue();
				Iterator<?> iterator = parentObject.keySet().iterator();
				while (iterator.hasNext()){
					Object key = iterator.next();
					Object element = parentObject.get(key);
					if (this.isArray()) {
						if (((JSONArray) element).equals(this.value)) {
							this.tag = PlainXmlType.parse("t_"+String.valueOf(key)).getTagName();
						}
					} else if (!this.isArray()) {
						if (((JSONObject) element).equals(this.value)){
							this.tag = PlainXmlType.parse("t_"+String.valueOf(key)).getTagName();
						}
					}
				}
			}
		}
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isArray() {
		return array;
	}

	public void setArray(boolean array) {
		this.array = array;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public JSONElement getGet(String key) {
		if (!isArray() && ((JSONObject) value).containsKey(key)) {
			return new JSONElement(this, key, ((JSONObject) value).get(key));
		} else {
			return null;
		}
	}

	// GETS PROPERTIES OF JSON OBJECT
	public Collection<JSONElement> getProperties() {
		if (!isArray()) {
			JSONObject jsonObject = (JSONObject) value;
			Collection<?> keySet = jsonObject.keySet();	
			ArrayList<JSONElement> result = new ArrayList<>(keySet.size());
			for (Object key : keySet) {
				Object child = jsonObject.get(key);
				result.add(new JSONElement(this, Objects.toString(key), child));
			}
			return Collections.unmodifiableList(result);
		}
		return null;
	}

	// GETS ELEMENTS OF JSON ARRAY
	public Collection<JSONElement> getChildren() {
		ArrayList<JSONElement> result = new ArrayList<>(0);
		if (isArray()) {
			JSONArray array = (JSONArray) this.getValue();
			final int size = array.size();
			result.ensureCapacity(size);
			for (int i = 0; i < size; i++) {
				result.add(new JSONElement(this, this.id + i, array.get(i)));	
			}			
		} 
		return result;
	}

	public static List<JSONElement> cast(Object array) {
		if (array instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray) array;
			ArrayList<JSONElement> result = new ArrayList<>(jsonArray.size());
			for (Object element : jsonArray) {
				result.add(new JSONElement(element));
			}
			return result;
		}
		else {
			return Collections.emptyList();
		}
	}

	@Override
	public String toString() {
		return "JSONElement [tag=" + tag + " parent=" 
				+ ((parent == null) ? "" : parent.getTag()) 
				+ ", array=" + array + "]";
	}

}