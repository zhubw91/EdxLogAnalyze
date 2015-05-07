# EdxLogAnalyze

This is the algorithm part for the edx log data analyzing.

## EdxLogAnalyze.java 
This is the main java file
### Some paramaters
**file_path**   : the path for log file  (this one should be reset according to your path)  
**url_map_path**  : the path for url map  

### Some functions and algorithms
**EditDistance( )**  : Compute the editdistance for two users 

**FindSimilarStu( )** : Find the neighbors for a user according to similarity between learning paths   

**GetBarStat( )** : Get the statistics for barchart. This function will firstly find the unique path for the given user ( e.g. : if L1->L3->L1 , then the unique path would be L3->L1 ). Then count how many users are there on the previous status. Next count how many students are "one more action" and "two more action" ahead the given user.   

**GetArrowStat( )** : Get the statistics for arrowchart. This function will firstly filter the neighbors by the length of path ( e.g. : if the delta = 2, and the given user is currently in lecture 5, then we just consider the student whose path has two more actions before L5 and two more actions after L5). Then using hashtable to count the frequency of those path in the neighbors. Next choose the top k popular paths and put them into the required format.    

## Event.java
This is the class for every event.   
It stores the event type and the event number for each event.   

## Student.java
This is the class for every student.   
It stores the eventlist and a hashtable which can be used to check the last index in the eventlist for every event.   
