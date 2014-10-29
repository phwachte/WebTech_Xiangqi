package controllers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.mvc.Controller;
import play.mvc.Result;
import de.htwg.xiangqi.XiangqiGame;

public class Application extends Controller {
	
	/*cool!!*/
	private static XiangqiGame xg;
	private static int boardColSize = 9;
	private static int boardRowSize = boardColSize+1;
    
    public static Result index() {
    	xg = new XiangqiGame();
        return ok(views.html.index.render(transformStringToArrayList(xg.getTui().printBoard()), xg.getTui().printBoard()));
    }
    
    public static Result input(String s){
    	xg.getBm().inputMove(s);
    	return ok(views.html.index.render(transformStringToArrayList(xg.getTui().printBoard()), xg.getTui().printBoard()));
    }
    
    
    /*Addittional Logic for concerting Strings to Lists - needed so we dont have to screw around in scala*/
    public static ArrayList<ArrayList<String>> transformStringToArrayList(String s){
	    ArrayList<ArrayList<String>> returnVal = new ArrayList<ArrayList<String>>(boardRowSize);
	    
	    String rowRegex = "(?m)^[0-9] .*$";/*^ Stringanfang $ Stringende (?m) multiline*/
	    String colRegex = "[A-Z ]{2}";/*{i} i-times*/
	    Pattern rowPattern = Pattern.compile(rowRegex);
	    Pattern colPattern = Pattern.compile(colRegex);
	    
	    Matcher rowMatcher = rowPattern.matcher(s);
	    while(rowMatcher.find()) {
	    	ArrayList<String> tmpList = new ArrayList<String>();
	    	Matcher colMatcher = colPattern.matcher(rowMatcher.group());
	    	while(colMatcher.find()){
	    		tmpList.add(colMatcher.group());
	    	}
	    	returnVal.add(tmpList);
	    }
	    System.out.println("Parsed List:");
	    System.out.println(returnVal);
	    return returnVal;
    }
}
