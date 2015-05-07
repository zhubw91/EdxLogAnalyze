import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class EdxLogAnalyze {
	
	public static HashMap<String,String> UrlMap = new HashMap<String,String>();
	public static HashMap<String,Student> StudentList = new HashMap<String,Student>();
	
	public static void main(String[] args) throws Exception {
		
		String file_path = "../../Documents/CMU/MOOC/LabStudy/log.txt";
		String url_map_path = "urlmap.txt";
		String username_output = "username.txt";
		
		// Read the URLMAP file first
		
		Scanner urlscan = new Scanner(new File(url_map_path));
		while(urlscan.hasNext())
		{
			String line = urlscan.nextLine();
			String[] s = line.split(" ");
			UrlMap.put(s[0], s[1]);
		}
		
		urlscan.close();
		
		Scanner scan = new Scanner(new File(file_path));
		JSONParser parser=new JSONParser();
		while(scan.hasNext())
		{
			String line = scan.nextLine();
			Object obj = parser.parse(line);
			JSONObject jsonobj = (JSONObject)obj;
			
			

			if(jsonobj.get("page") == null) continue;
			String url = jsonobj.get("page").toString();
			
			String username = jsonobj.get("username").toString();
			
			if(!StudentList.containsKey(username))
			{
				Student student = new Student(username);
				StudentList.put(username, student);
			}
			StudentList.get(username).addEvent(jsonobj, url);
			
			
		}
		scan.close();
		
		
		// Output the list of username (For Debug Only)
		
		/*   
		FileWriter fw = new FileWriter(username_output);
		for(String user:StudentList.keySet())
		{
			fw.write(user+"\n");
		}
		fw.close();
		*/
		
		int cnt = 0;
		for(String user:StudentList.keySet())
		{
			
			//ShowLearningPath(user);
			
			ArrayList<String> neighbors =  FindSimilarStu(user, 3);
			
			
			// Get the arrowstat
			
			ArrayList<String[]> arrowstat = GetArrowStat(user, neighbors, 1);
			ShowArrowStat(arrowstat);
			
			
			// Get the barstat
			
			/*
			ArrayList<String[]> barstat = GetBarStat(user,neighbors);
			ShowBarStat(barstat);
			*/
			
			
		}
	}
	public static int minimum(int a, int b, int c) {                            
        return Math.min(Math.min(a, b), c);                                      
    }
	
	
	/** Compute the Editdistance between two students' learning paths
	 * 
	 * @param a : Student a
	 * @param b : Student b
	 * @return : An integer which means the editdistance between student a and student b
	 */
	public static int EditDistance(Student a, Student b)
	{
		int[][] distance = new int[a.EventList.size() + 1][b.EventList.size() + 1];        
		 
        for (int i = 0; i <= a.EventList.size(); i++)                                 
            distance[i][0] = i;                                                  
        for (int j = 1; j <= b.EventList.size(); j++)                                 
            distance[0][j] = j;                                                  
 
        for (int i = 1; i <= a.EventList.size(); i++)                                 
            for (int j = 1; j <= b.EventList.size(); j++)                             
                distance[i][j] = minimum(                                        
                        distance[i - 1][j] + 1,                                  
                        distance[i][j - 1] + 1,                                  
                        distance[i - 1][j - 1] + ((a.EventList.get(i - 1).equals(b.EventList.get(j - 1))) ? 0 : 1));
 
        return distance[a.EventList.size()][b.EventList.size()];    
	}
	
	/**  Find the neighbors for a user according to similarity between learning paths 
	 * 
	 * @param username : the username for finding similarity
	 * @param threshold : the threshold for Editdistance. Judge two paths similar only when the editdistance < threshold 
	 * @return 
	 */
	public static ArrayList<String> FindSimilarStu(String username, int threshold)
	{
		ArrayList<String> result_list = new ArrayList<String>();
		if(StudentList.get(username).EventList.size() == 0) return result_list;
		for(String key : StudentList.keySet())
		{
			if(key.equals(username)) continue;
			if(StudentList.get(key).EventList.size() == 0) continue;
			
			//if(StudentList.get(key).EventList.get(StudentList.get(key).EventList.size()-1).equals(StudentList.get(username).EventList.get(StudentList.get(username).EventList.size()-1)) == false) continue;
			
			if(EditDistance(StudentList.get(key),StudentList.get(username)) < threshold)
			{
				result_list.add(key);
			}
		}
		return result_list;
	}
	
	/** Get the statistics for barchart
	 * 
	 * @param user : the username for the barchart
	 * @param neighbors : the list of neighbors for the user
	 * @return : the list of barstat, for each item in the list, item[0] is the name of the action eg: "L1" "Q3", item[1] is the height of bar.
	 */
	public static ArrayList<String[]> GetBarStat(String user,ArrayList<String> neighbors)
	{
		ArrayList<String[]> barstat = new ArrayList<String[]>();
		
		if(StudentList.get(user).EventList.size() == 0) return barstat;
		
		String[] path = new String[3];
		int j = 0;
		HashMap<String,Integer> eventhash = new HashMap<String,Integer>();
		for(int i = StudentList.get(user).EventList.size()-1;i >=0;i--)
		{
			if(j >= 3) break;
			if(eventhash.containsKey(StudentList.get(user).EventList.get(i).toString()))
			{
				continue;
			}
			eventhash.put(StudentList.get(user).EventList.get(i).toString(), 1);
			path[j++] = StudentList.get(user).EventList.get(i).toString();
			
		}
		
		for(int i = j-1;i>=0;i--)
		{
			String[] item = new String[2];
			item[0] = path[i];
			int cnt = 0;
			for(int k=0;k<=neighbors.size()-1;k++)
			{
				int l = StudentList.get(neighbors.get(k)).EventList.size();
				if(StudentList.get(neighbors.get(k)).EventList.get(l-1).toString().equals(path[i]))
				{
					cnt ++;
				}
				
			}
			item[1] = "" + cnt;
			barstat.add(item);
		}
		
		String lastevent = StudentList.get(user).EventList.get(StudentList.get(user).EventList.size()-1).toString();
		int cnt1 = 0,cnt2 = 0;
		for(int k=0;k<=neighbors.size()-1;k++)
		{
			if(StudentList.get(neighbors.get(k)).Eventhash.containsKey(lastevent))
			{
				if(StudentList.get(neighbors.get(k)).Eventhash.get(lastevent) + 2 == StudentList.get(neighbors.get(k)).EventList.size())
				{
					cnt2 ++;
				}
				else if(StudentList.get(neighbors.get(k)).Eventhash.get(lastevent) + 1 == StudentList.get(neighbors.get(k)).EventList.size())
				{
					cnt1 ++;
				}
			}
		}
		String[] item = new String[2];
		item[0] = "One +";item[1] = "" + cnt1;
		barstat.add(item);
		item = new String[2];
		item[0] = "Two +";item[1] = "" + cnt2;
		barstat.add(item);
		
		return barstat;
	}
	
	/**  Get the statistics for arrowchart
	 * 
	 * @param user : the username for the arrowchart
	 * @param neighbors : the list of neighbors for the user
	 * @param delta : the length each learning path would be 2*delta+1
	 * @return : the list of edges, which item[0] is the source node and item[1] is the target node
	 */
	public static ArrayList<String[]> GetArrowStat(String user,ArrayList<String> neighbors,int delta)
	{
		ArrayList<String[]> arrowstat = new ArrayList<String[]>();
		if (StudentList.get(user).EventList.size() == 0) return arrowstat;
		String lastevent = StudentList.get(user).EventList.get(StudentList.get(user).EventList.size()-1).toString();
		
		int cnt = 0;
		// Choose the path satisify the length requirement 
		
		HashMap<String,Integer> pathcnt = new HashMap<String,Integer>(); 
		for(int i=0;i<=neighbors.size()-1;i++)
		{
			String candidate = neighbors.get(i);
			if(StudentList.get(candidate).Eventhash.containsKey(lastevent) == false) continue;
			int index = StudentList.get(candidate).Eventhash.get(lastevent);
			if(index <=delta-1 || index >= StudentList.get(candidate).EventList.size()-delta) continue;
			cnt++;
			
			// cat the string of event as the key for hash
			String key = "";
			for(int j = index-delta;j<=index+delta;j++)
			{
				key += StudentList.get(candidate).EventList.get(j).toString() + ",";
			}
			
			// Update the hashtable for learning paths
			if(pathcnt.containsKey(key)==false) pathcnt.put(key, 1);
			else pathcnt.put(key, pathcnt.get(key)+1);
			
		}
		
		// Choose the top k paths
		int k = 2, kcnt = 0;
		
		while(kcnt < k)
		{
			if(pathcnt.size() == 0) break;
			
			String maxpath = "";
			int maxnum = -1;
			for(String key:pathcnt.keySet())
			{
				if(pathcnt.get(key) > maxnum)
				{
					maxpath = key;
					maxnum = pathcnt.get(key);
				}
				
			}
			
			// Add this path into arrowstat
			String[] can_path = maxpath.split(","); 
			for(int s = 0;s<=can_path.length-2;s++)
			{
				String[] edge = new String[2];
				edge[0] = can_path[s];
				edge[1] = can_path[s+1];
				arrowstat.add(edge);
			}
			
			pathcnt.remove(maxpath);
			kcnt ++;
		}
		//System.out.println(cnt);
		return arrowstat;
	}
	
	/**   Print the barstat
	 * 
	 * @param barstat
	 */
	public static void ShowBarStat(ArrayList<String[]> barstat)
	{
		for(int i=0;i<=barstat.size()-1;i++)
		{
			System.out.print(barstat.get(i)[0] + ":" + barstat.get(i)[1]+" ");
		}
		System.out.println("");
	}
	/** Print the arrowstat
	 * 
	 * @param arrowstat
	 */
	public static void ShowArrowStat(ArrayList<String[]> arrowstat)
	{
		for(int i=0;i<arrowstat.size()-1;i++)
		{
			System.out.print(arrowstat.get(i)[0]+"-->"+arrowstat.get(i)[1]+" ");
		}
		System.out.println("");
	}
	/** Print the learning path
	 * 
	 * @param username
	 */
	public static void ShowLearningPath(String username)
	{
		for(int i=0;i<=StudentList.get(username).EventList.size()-1;i++)
		{
			System.out.print(StudentList.get(username).EventList.get(i).eventnum + " ");
		}
		System.out.println("");
	}
}
