import java.io.*;
import java.util.Comparator;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class Event {
	public enum EventType {
	    LECTURE, QUIZ, FORUM, OTHER
	}
	public EventType type;
	public int eventnum;          // the number of the event  e.g. Lecture 10, Quiz 5
	
	public Event(JSONObject event,String url)
	{
		eventnum = 0;
		String typestring = event.get("event_type").toString();
		if(typestring.contains("forum")) type = EventType.FORUM;
		else if(EdxLogAnalyze.UrlMap.containsKey(url))
		{
			//System.out.println("ssssss");
			String value = EdxLogAnalyze.UrlMap.get(url);
			if(value.startsWith("L")) type = EventType.LECTURE;
			else type = EventType.QUIZ;
			eventnum = Integer.valueOf(value.substring(1));
		}
		else type = EventType.OTHER;
		
	}
	public boolean equals(Event a)
	{
		if(this.type.equals(a.type) && this.eventnum == a.eventnum) return true;
		else return false;
	}
	public String toString()
	{
		String name = "";
		if(this.type == EventType.LECTURE) name += "L";
		else if(this.type == EventType.FORUM) name += "F";
		else if(this.type == EventType.QUIZ) name += "Q";
		else name += "O";
		name += this.eventnum;
		return name;
	}
}
