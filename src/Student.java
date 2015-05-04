import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;


public class Student {
	public String name;
	public ArrayList<Event> EventList;
	public HashMap<String,Integer> Eventhash;
	public Timestamp last_action_time;
	public Timestamp first_action_time;
	
	public Student(String n){
		EventList = new ArrayList<Event>();
		Eventhash = new HashMap<String,Integer>();
		name = n;
		last_action_time = new Timestamp(0);
		first_action_time = new Timestamp(0);
	}

	public void addEvent(JSONObject event,String url)
	{
		Event e = new Event(event,url);
		if(e.type == Event.EventType.OTHER) return;
		if(EventList.size() > 0 && EventList.get(EventList.size()-1).equals(e)) return;
		
		EventList.add(e);
		Eventhash.put(e.toString(), EventList.size()-1);
		
		//if(ts.after(last_action_time)) last_action_time = ts;
	}

}

